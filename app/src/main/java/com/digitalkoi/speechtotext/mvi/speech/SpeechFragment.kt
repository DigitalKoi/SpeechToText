package com.digitalkoi.speechtotext.mvi.speech

import android.Manifest.permission
import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.View
import com.digitalkoi.speechtotext.mvi.MviView
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import kotlin.LazyThreadSafetyMode.NONE
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.speech.SpeechRecognizer
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.text.Editable
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.TextView.BufferType.EDITABLE
import android.widget.Toast
import com.digitalkoi.speechtotext.R
import com.digitalkoi.speechtotext.R.layout
import com.digitalkoi.speechtotext.R.string
import com.digitalkoi.speechtotext.drawing.DrawActivity
import com.digitalkoi.speechtotext.mvi.speech.SpeechIntent.InitialIntent
import com.digitalkoi.speechtotext.mvi.speech.SpeechIntent.PausePressedIntent
import com.digitalkoi.speechtotext.mvi.speech.SpeechIntent.PlayPressedIntent
import com.digitalkoi.speechtotext.mvi.speech.SpeechIntent.ShowDialogConfirmIntent
import com.digitalkoi.speechtotext.mvi.speech.SpeechIntent.ShowDialogGoodnessIntent
import com.digitalkoi.speechtotext.mvi.speech.SpeechIntent.ShowDialogIdIntent
import com.digitalkoi.speechtotext.mvi.speech.SpeechIntent.ShowKeyboardIntent
import com.digitalkoi.speechtotext.mvi.speech.SpeechIntent.StopPressedIntent
import com.digitalkoi.speechtotext.mvi.speech.SpeechIntent.ZoomInIntent
import com.digitalkoi.speechtotext.mvi.speech.SpeechIntent.ZoomOutIntent
import com.digitalkoi.speechtotext.util.Constants
import com.digitalkoi.speechtotext.util.ViewModelFactory
import com.hsalf.smilerating.SmileRating
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.speech_frag.*
import kotlin.properties.Delegates
import com.tbruyelle.rxpermissions2.RxPermissions
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import net.yslibrary.android.keyboardvisibilityevent.Unregistrar
import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil
import java.util.concurrent.TimeUnit.SECONDS

/**
 * @author Taras Zhupnyk (akka DigitalKoi) on 09/03/18.
 */

class SpeechFragment : Fragment(),
    MviView<SpeechIntent, SpeechViewState> {

  private val disposable = CompositeDisposable()
  private val viewModel: SpeechViewModel by lazy(NONE) {
    ViewModelProviders
        .of(this, ViewModelFactory.getInstance(activity!!))
        .get(SpeechViewModel::class.java)
  }
  private val rxPermissions: RxPermissions by lazy { RxPermissions(activity) }

  private val playPressedSubject = PublishSubject.create<PlayPressedIntent>()
  private val stopPressedSubject = PublishSubject.create<StopPressedIntent>()
  private val pausePressedSubject = PublishSubject.create<PausePressedIntent>()
  private val zoomOutSubject = PublishSubject.create<ZoomOutIntent>()
  private val zoomInSubject = PublishSubject.create<ZoomInIntent>()
  private val showDialogIdSubject = PublishSubject.create<ShowDialogIdIntent>()
  private val showDialogConfirmSubject = PublishSubject.create<ShowDialogConfirmIntent>()
  private val showDialogGoodnessSubject = PublishSubject.create<ShowDialogGoodnessIntent>()
  private val showKeyboardSubject = PublishSubject.create<ShowKeyboardIntent>()

  private val builder: AlertDialog.Builder by lazy { AlertDialog.Builder(activity) }
  private val dialogId: AlertDialog by lazy { builder.create() }
  private val dialogConfirm: AlertDialog by lazy { builder.create() }
  private val dialogGoodness: AlertDialog by lazy { builder.create() }
  private lateinit var unregistrar: Unregistrar

  private var recSpeechStatus: Int by Delegates.notNull()
  private var keyboardIsOpen = false
  private var idPatient: String? = null
  private var text: String = ""


  override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    return inflater?.inflate(layout.speech_frag, container, false)
  }

  override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    bind()
  }

  override fun onPause() {
    super.onPause()
    showKeyboard(false)
  }

  override fun onDestroy() {
    super.onDestroy()
    disposable.dispose()
    unregistrar.unregister()
    showDialogs(false, false, false)
  }

  override fun intents(): Observable<SpeechIntent> {
    return Observable.merge(
        listOf(
            initialIntent(),
            playPressedIntent(),
            stopPressedIntent(),
            pausePressedIntent(),
            zoomInIntent(),
            zoomOutIntent(),
            showDialogIdIntent(),
            showDialogConfirmIntent(),
            showDialogGoodnessIntent(),
            showKeyboardIntent()
            )
    )
  }

  @SuppressLint("LogNotTimber")
  override fun render(state: SpeechViewState) {
    recSpeechStatus = state.recSpeechStatus
    idPatient = state.idPatient

    if (!TextUtils.isEmpty(state.text)) {
      speechTextField.setText(state.text, EDITABLE)
    } else {
      speechTextField.setText("", EDITABLE)
    }

    if (state.error != null) {
      val error = state.error.message
      when (error) {
        SpeechRecognizer.ERROR_NO_MATCH.toString() -> {
          showToast("No recognition result matched")
          pausePressedSubject.onNext(PausePressedIntent)
          Observable.just(
          playPressedSubject.onNext(PlayPressedIntent(idPatient!!, speechTextField.text.toString()))).delay(1, SECONDS)
        }
      }
      Log.e("error", state.error.toString())
      showToast(state.error.toString()) //return for fatal error
    }

    speechTextField.textSize = state.fontSize

    if (state.showDialogId || state.showDialogConfirmation || state.showDialogGoodness) {
      showDialogs(state.showDialogId, state.showDialogConfirmation, state.showDialogGoodness)
    } else { showDialogs(false, false, false)    }

    if (keyboardIsOpen != state.showKeyboard) {
      keyboardIsOpen = state.showKeyboard
      showKeyboard(keyboardIsOpen)
    }

    Log.d("Fragment: ", state.toString())
  }

  private fun bind() {
    initialDialogId()
    initialDialogConfirm()
    initialDialogGoodness()
    disposable.add(viewModel.states().subscribe(this::render))
    viewModel.processIntents(intents())
    initialClickListeners()
    unregistrar = KeyboardVisibilityEvent.registerEventListener(
        activity, { if (!it) { showKeyboardSubject.onNext(ShowKeyboardIntent(false)) }}
    )
  }

  private fun initialIntent(): Observable<InitialIntent> = Observable.just(InitialIntent)
  private fun playPressedIntent(): Observable<PlayPressedIntent> = playPressedSubject
  private fun stopPressedIntent(): Observable<StopPressedIntent> = stopPressedSubject
  private fun pausePressedIntent(): Observable<PausePressedIntent> = pausePressedSubject
  private fun zoomInIntent(): Observable<ZoomInIntent> = zoomInSubject
  private fun zoomOutIntent(): Observable<ZoomOutIntent> = zoomOutSubject
  private fun showDialogIdIntent(): Observable<ShowDialogIdIntent> = showDialogIdSubject
  private fun showDialogConfirmIntent(): Observable<ShowDialogConfirmIntent> = showDialogConfirmSubject
  private fun showDialogGoodnessIntent(): Observable<ShowDialogGoodnessIntent> = showDialogGoodnessSubject
  private fun showKeyboardIntent(): Observable<ShowKeyboardIntent> = showKeyboardSubject

  private fun initialClickListeners() {
    speechMicPlay.setOnClickListener {
      when (recSpeechStatus) {
        Constants.REC_STATUS_STOP -> {
          speechMicPlay.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.shape_pause_button))
          checkPermissions()
          }
        Constants.REC_STATUS_PLAY -> {
          speechMicPlay.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.shape_play_button))
          pausePressedSubject.onNext(PausePressedIntent)
        }
        Constants.REC_STATUS_PAUSE -> {
          speechMicPlay.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.shape_pause_button))
          checkPermissions()
        }
      }
    }
    speechStopBt.setOnClickListener {
      if  (Constants.REC_STATUS_STOP != recSpeechStatus) {
        stopPressedSubject.onNext(StopPressedIntent(idPatient!!, speechTextField.text.toString())) }
        speechMicPlay.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.shape_play_button))
    }
    speechPlusBt.setOnClickListener { zoomInSubject.onNext(ZoomInIntent) }
    speechMinusBt.setOnClickListener { zoomOutSubject.onNext(ZoomOutIntent) }
    speechPaintBt.setOnClickListener { showDrawActivity() }
    speechGoodnessBt.setOnClickListener { showDialogGoodnessSubject.onNext(
        ShowDialogGoodnessIntent(true)
    ) }
    speechQuestionBt.setOnClickListener { showDialogConfirmSubject.onNext(ShowDialogConfirmIntent(true)) }
    speechKeyboardBt.setOnClickListener { showKeyboardSubject.onNext(ShowKeyboardIntent(!keyboardIsOpen)) }
  }

  private fun initialDialogId() {
    val view = layoutInflater.inflate(R.layout.dialog_patient_id, null)
    builder.setView(view)
    dialogId.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    dialogId.setCancelable(false)

    val dialogPatientOk = view.findViewById<View>(R.id.dialogPatientOk)
    val dialogPatientCancel = view.findViewById<View>(R.id.dialogPatientCancel)
    val dialogPatientIdEdit = view.findViewById<EditText>(R.id.dialogPatientIdEd)
    //TODO: clear text field
    dialogPatientIdEdit.setText("", TextView.BufferType.EDITABLE)
    dialogPatientOk.setOnClickListener {
      playPressedSubject.onNext(PlayPressedIntent(dialogPatientIdEdit.text.toString(), speechTextField.text.toString()))
      showDialogIdSubject.onNext(ShowDialogIdIntent(false))
    }
    dialogPatientCancel.setOnClickListener {
      showDialogIdSubject.onNext(ShowDialogIdIntent(false))
    }
  }

  private fun initialDialogConfirm() {
    val view = layoutInflater.inflate(R.layout.dialog_confirmation, null)
    builder.setView(view)
    dialogConfirm.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    val windowManager = dialogConfirm.window.attributes
    windowManager.verticalMargin = 0.1f
    windowManager.gravity = Gravity.BOTTOM
    dialogConfirm.setCancelable(false)
    val dialogConfirmYes = view.findViewById<View>(R.id.dialogConfirmYes)
    dialogConfirmYes.setOnClickListener {
      showDialogConfirmSubject.onNext(ShowDialogConfirmIntent(false))
      dialogConfirm.dismiss()
    }
  }

  private fun initialDialogGoodness() {
    val view = layoutInflater.inflate(R.layout.dialog_goodness, null)
    builder.setView(view)
    dialogGoodness.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    dialogGoodness.setCancelable(false)

    val dialogGoodness = view.findViewById<SmileRating>(R.id.smile_rating)
    dialogGoodness.setOnSmileySelectionListener { smiley, reselected ->
      if (reselected) showDialogGoodnessSubject.onNext(ShowDialogGoodnessIntent(false))
    }
  }

  private fun showDialogs(showDialogId: Boolean, showDialogConfirm: Boolean, showDialogGoodness: Boolean) =
    when {
      showDialogId -> dialogId.show()
      showDialogConfirm -> dialogConfirm.show()
      showDialogGoodness ->dialogGoodness.show()
      else -> {
        dialogId.dismiss()
        dialogConfirm.dismiss()
        dialogGoodness.dismiss()
      }
    }

  private fun showKeyboard(show: Boolean) {
    //TODO: fix bag with rotation
    if (show) {
      speechTextField.isClickable = true
      speechTextField.isFocusable = true
      speechTextField.isFocusableInTouchMode = true
      UIUtil.showKeyboard(activity, speechTextField)
    } else {
      UIUtil.hideKeyboard(activity)
      speechTextField.isClickable = false
      speechTextField.isFocusable = false
      speechTextField.isFocusableInTouchMode = false
    }
  }

  private fun showDrawActivity() {
    val intent = Intent(context, DrawActivity::class.java)
    startActivity(intent)
  }

  private fun showToast(error: String) =
    Toast.makeText(activity, error, Toast.LENGTH_LONG).show()

  private fun checkPermissions() =
    rxPermissions.request(permission.RECORD_AUDIO,
                          permission.WRITE_EXTERNAL_STORAGE)
            .subscribe { granted ->
                if (!granted) {
                  Toast.makeText(activity, getString(string.permissions), Toast.LENGTH_LONG).show()
                } else {
                    when (recSpeechStatus) {
                      Constants.REC_STATUS_STOP -> {
                        showDialogIdSubject.onNext(ShowDialogIdIntent(true))
                        speechTextField.text = Editable.Factory.getInstance().newEditable("")
                      }
                      Constants.REC_STATUS_PAUSE ->
                        playPressedSubject.onNext(PlayPressedIntent(
                            idPatient!!, speechTextField.text.toString()
                        ))
                    }
                }
            }
  }

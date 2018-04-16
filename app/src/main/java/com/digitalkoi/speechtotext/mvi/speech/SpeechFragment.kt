package com.digitalkoi.speechtotext.mvi.speech

import android.Manifest.permission
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.AudioManager
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatDelegate
import android.text.Editable
import android.text.TextUtils
import android.text.method.TextKeyListener
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ScrollView
import android.widget.TextView.BufferType.EDITABLE
import android.widget.Toast
import com.digitalkoi.speechtotext.R
import com.digitalkoi.speechtotext.R.layout
import com.digitalkoi.speechtotext.R.string
import com.digitalkoi.speechtotext.mvi.drawing.DrawActivity
import com.digitalkoi.speechtotext.mvi.MviView
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
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.speech_frag.scrollView
import kotlinx.android.synthetic.main.speech_frag.speechGoodnessBt
import kotlinx.android.synthetic.main.speech_frag.speechKeyboardBt
import kotlinx.android.synthetic.main.speech_frag.speechMinusBt
import kotlinx.android.synthetic.main.speech_frag.speechPaintBt
import kotlinx.android.synthetic.main.speech_frag.speechPlayBt
import kotlinx.android.synthetic.main.speech_frag.speechPlusBt
import kotlinx.android.synthetic.main.speech_frag.speechQuestionBt
import kotlinx.android.synthetic.main.speech_frag.speechTextField
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import net.yslibrary.android.keyboardvisibilityevent.Unregistrar
import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil
import kotlin.LazyThreadSafetyMode.NONE

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

  private val builder: AlertDialog.Builder by lazy { AlertDialog.Builder(activity) }
  private val dialogId: AlertDialog by lazy { builder.create() }
  private val dialogConfirm: AlertDialog by lazy { builder.create() }
  private val dialogGoodness: AlertDialog by lazy { builder.create() }
  private lateinit var unregistrar: Unregistrar
  private val audioManager: AudioManager by lazy {
    context.getSystemService(
        Context.AUDIO_SERVICE
    ) as AudioManager
  }

  private var recSpeechStatus: Int? = null
  private var recSpeechStatusLocal = false
  private var keyboardIsOpen = false
  private var idPatient: String? = null
  private var idPatientLocal = ""
  private var textCurrent: String? = null
  private var textPreviously: String? = null
  private var closeFragment = false

  override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?)
      : View? {
    AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
    return inflater?.inflate(layout.speech_frag, container, false)
  }

  override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    bind()
  }

  override fun onPause() {
    super.onPause()
    showKeyboard(false)
    if (recSpeechStatus == Constants.REC_STATUS_PLAY) {
      pausePressedSubject.onNext(PausePressedIntent(Constants.REC_STATUS_PAUSE))
    }
    showDialogs(false, false, false)
    closeFragment = false
  }

  override fun onDestroy() {
    super.onDestroy()
    disposable.dispose()
    unregistrar.unregister()
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

  override fun render(state: SpeechViewState) {
    if (!TextUtils.isEmpty(state.text) && !textPreviously.equals(state.text!!)) {
      textPreviously = state.text
      if (TextUtils.isEmpty(textCurrent)) {
        textCurrent = state.text
        speechTextField.setText(textCurrent, EDITABLE)
      } else {
        textCurrent += "; " + state.text
        speechTextField.setText(textCurrent, EDITABLE)
        scrollView.post { scrollView.fullScroll(ScrollView.FOCUS_DOWN) }
      }
    }
    if (state.error != null) {
      val error = state.error.toString()
      Log.e("error", error)
    }
    speechTextField.textSize = state.fontSize
    if (state.showDialogId || state.showDialogConfirmation || state.showDialogGoodness) {
      showDialogs(state.showDialogId, state.showDialogConfirmation, state.showDialogGoodness)
    } else {
      showDialogs(false, false, false)
    }
    if (keyboardIsOpen != state.showKeyboard) {
      keyboardIsOpen = state.showKeyboard
      showKeyboard(keyboardIsOpen)
    }
    recSpeechStatus = state.recSpeechStatus
    if (!TextUtils.isEmpty(state.idPatient)) idPatient = state.idPatient
  }

  private fun bind() {
    initialDialogId()
    initialDialogConfirm()
    initialDialogGoodness()
    disposable.add(viewModel.states().subscribe(this::render))
    viewModel.processIntents(intents())
    initialClickListeners()

    unregistrar = KeyboardVisibilityEvent
        .registerEventListener(activity, {
          if (!it) { showKeyboardSubject.onNext(ShowKeyboardIntent(false))  } } )
    if (recSpeechStatus == Constants.REC_STATUS_PAUSE) {
        playPressedSubject.onNext(PlayPressedIntent(idPatient!!))
        changeIconPlayButton(Constants.REC_STATUS_PLAY)
      }
//    if (!TextUtils.isEmpty(idPatientLocal))
//      playPressedSubject.onNext(PlayPressedIntent(idPatientLocal))
  }

  private fun initialClickListeners() {
    speechPlusBt.setOnClickListener { zoomInSubject.onNext(ZoomInIntent) }
    speechMinusBt.setOnClickListener { zoomOutSubject.onNext(ZoomOutIntent) }
    speechPaintBt.setOnClickListener { showDrawActivity() }
    speechGoodnessBt.setOnClickListener {
      showDialogGoodnessSubject.onNext(
          ShowDialogGoodnessIntent(true)
      )
    }
    speechQuestionBt.setOnClickListener {
      showDialogConfirmSubject.onNext(
          ShowDialogConfirmIntent(true)
      )
    }
    speechKeyboardBt.setOnClickListener {
      showKeyboardSubject.onNext(
          ShowKeyboardIntent(!keyboardIsOpen)
      )
    }
    speechPlayBt.setOnClickListener {
      when (recSpeechStatus!!) {
        Constants.REC_STATUS_STOP ->
          if (!recSpeechStatusLocal) {
            recSpeechStatusLocal = true
            checkPermissions()
          } else {
            recSpeechStatusLocal = false
            idPatientLocal = ""
            stopPressedSubject.onNext(
                StopPressedIntent(idPatient!!, speechTextField.text.toString())
            )
            idPatient = null
            changeIconPlayButton(Constants.REC_STATUS_STOP)
          }
        Constants.REC_STATUS_PLAY -> {
          recSpeechStatusLocal = false
          stopPressedSubject.onNext(StopPressedIntent(idPatient!!, speechTextField.text.toString()))
          changeIconPlayButton(Constants.REC_STATUS_STOP)
          speechTextField.text = Editable.Factory.getInstance()
              .newEditable("")
          idPatient = null
          idPatientLocal = ""
          textCurrent = ""
          textPreviously = ""
          restoreVolumeLevel()
        }
      }
    }
  }

  private fun changeIconPlayButton(status: Int) {
    when (status) {
      Constants.REC_STATUS_PLAY -> speechPlayBt.setImageDrawable(
          ContextCompat.getDrawable(context, R.drawable.ic_stop)
      )
      else -> speechPlayBt.setImageDrawable(
          ContextCompat.getDrawable(context, R.drawable.shape_play_button)
      )
    }
  }

  private fun initialDialogId() {
    val view = layoutInflater.inflate(R.layout.dialog_patient_id, null)
    builder.setView(view)
    dialogId.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    dialogId.setCancelable(false)

    val dialogPatientOk = view.findViewById<View>(R.id.dialogPatientOk)
    val dialogPatientCancel = view.findViewById<View>(R.id.dialogPatientCancel)
    val dialogPatientIdEdit = view.findViewById<EditText>(R.id.dialogPatientIdEd)

    TextKeyListener.clear(dialogPatientIdEdit.text)
    dialogPatientOk.setOnClickListener {
      if (TextUtils.isEmpty(dialogPatientIdEdit.text.toString())) {
        showToast("Input patient ID please")
      } else {
        muteBeep()
        idPatient = dialogPatientIdEdit.text.toString()
        idPatientLocal = idPatient!!
        playPressedSubject.onNext(PlayPressedIntent(idPatient!!))
        showDialogIdSubject.onNext(ShowDialogIdIntent(false))
        changeIconPlayButton(Constants.REC_STATUS_PLAY)
      }
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
      if (!closeFragment && (recSpeechStatus!! == Constants.REC_STATUS_PAUSE) && !TextUtils.isEmpty(idPatient))
        playPressedSubject.onNext(PlayPressedIntent(idPatient!!))
    }
  }

  private fun initialDialogGoodness() {
    val view = layoutInflater.inflate(R.layout.dialog_goodness, null)
    builder.setView(view)
    dialogGoodness.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    dialogGoodness.setCancelable(false)

    val dialogGoodness = view.findViewById<SmileRating>(R.id.smile_rating)
    dialogGoodness.setOnSmileySelectionListener { smiley, reselected ->
      if (reselected) {
        showDialogGoodnessSubject.onNext(ShowDialogGoodnessIntent(false))
        if (!closeFragment && (recSpeechStatus!! == Constants.REC_STATUS_PAUSE) && !TextUtils.isEmpty(idPatient))
          playPressedSubject.onNext(PlayPressedIntent(idPatient!!))
      }
    }
  }

  private fun showDialogs(
    showDialogId: Boolean,
    showDialogConfirm: Boolean,
    showDialogGoodness: Boolean
  ) {
    return when {
      showDialogId -> dialogId.show()
      showDialogConfirm -> {
        dialogConfirm.show()
        callPauseRecord()
      }
      showDialogGoodness -> {
        dialogGoodness.show()
        callPauseRecord()
      }
      else -> {
        dialogId.dismiss()
        dialogConfirm.dismiss()
        dialogGoodness.dismiss()
      }
    }
  }

  private fun showKeyboard(show: Boolean) {
    if (show) {
      speechTextField.isClickable = true
      speechTextField.isFocusable = true
      speechTextField.isFocusableInTouchMode = true
      speechTextField.requestFocus()
      UIUtil.showKeyboard(activity, speechTextField)
      speechTextField.setSelection(speechTextField.text.length)
      if (recSpeechStatus!! == Constants.REC_STATUS_PLAY) callPauseRecord()

    } else {
      UIUtil.hideKeyboard(activity)
      speechTextField.isClickable = false
      speechTextField.isFocusable = false
      speechTextField.isFocusableInTouchMode = false
      if (!closeFragment && recSpeechStatus!! == Constants.REC_STATUS_PAUSE) {
        playPressedSubject.onNext(PlayPressedIntent(idPatient!!))
      }
    }
  }

  private fun showDrawActivity() {
    closeFragment = true
    callPauseRecord()
    startActivity(Intent(context, DrawActivity::class.java))
  }

  private fun callPauseRecord() {
    if (recSpeechStatus!! == Constants.REC_STATUS_PLAY)
      pausePressedSubject.onNext(PausePressedIntent(Constants.REC_STATUS_PAUSE))
  }

  private fun showToast(error: String) =
    Toast.makeText(activity, error, Toast.LENGTH_LONG).show()

  private fun checkPermissions() =
    rxPermissions.request(permission.RECORD_AUDIO, permission.WRITE_EXTERNAL_STORAGE)
        .subscribe { granted ->
          if (!granted) {
            Toast.makeText(activity, getString(string.permissions), Toast.LENGTH_LONG)
                .show()
          } else {
            muteBeep()
            when (recSpeechStatus) {
              Constants.REC_STATUS_STOP -> {
                showDialogIdSubject.onNext(ShowDialogIdIntent(true))
              }
              Constants.REC_STATUS_PAUSE -> {
                playPressedSubject.onNext(PlayPressedIntent(idPatient!!))
                changeIconPlayButton(Constants.REC_STATUS_PLAY)
              }
            }
          }
        }

  private fun muteBeep() =
    audioManager.setStreamVolume(
        AudioManager.STREAM_MUSIC,
        0,
        AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE)

  private fun restoreVolumeLevel() =
    audioManager.setStreamVolume(
        AudioManager.STREAM_MUSIC,
        7,
        AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE)
}

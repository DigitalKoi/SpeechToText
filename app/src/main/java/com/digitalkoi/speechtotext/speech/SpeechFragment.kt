package com.digitalkoi.speechtotext.speech

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.View
import com.digitalkoi.speechtotext.mvibase.MviView
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import kotlin.LazyThreadSafetyMode.NONE
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.digitalkoi.speechtotext.R
import com.digitalkoi.speechtotext.drawing.DrawActivity
import com.digitalkoi.speechtotext.speech.SpeechIntent.InitialIntent
import com.digitalkoi.speechtotext.speech.SpeechIntent.PlayPressedIntent
import com.digitalkoi.speechtotext.speech.SpeechIntent.ShowDialogConfirmIntent
import com.digitalkoi.speechtotext.speech.SpeechIntent.ShowDialogIdIntent
import com.digitalkoi.speechtotext.speech.SpeechIntent.ShowKeyboardIntent
import com.digitalkoi.speechtotext.speech.SpeechIntent.StopPressedIntent
import com.digitalkoi.speechtotext.speech.SpeechIntent.ZoomInIntent
import com.digitalkoi.speechtotext.speech.SpeechIntent.ZoomOutIntent
import com.digitalkoi.speechtotext.util.Constants
import com.digitalkoi.speechtotext.util.SpeechViewModelFactory
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.speech_frag.*
import kotlin.properties.Delegates

/**
 * @author Taras Zhupnyk (akka DigitalKoi) on 09/03/18.
 */

class SpeechFragment : Fragment(),
    MviView<SpeechIntent, SpeechViewState> {

  private val disposable = CompositeDisposable()
  private val viewModel: SpeechViewModel by lazy(NONE) {
    ViewModelProviders
        .of(this, SpeechViewModelFactory.getInstance(context!!))
        .get(SpeechViewModel::class.java)
  }
  private val playPressedSubject = PublishSubject.create<PlayPressedIntent>()
  private val stopPressedSubject = PublishSubject.create<StopPressedIntent>()
  private val zoomOutSubject = PublishSubject.create<ZoomOutIntent>()
  private val zoomInSubject = PublishSubject.create<ZoomInIntent>()
  private val showDialogIdSubject = PublishSubject.create<ShowDialogIdIntent>()
  private val showDialogConfirmSubject = PublishSubject.create<ShowDialogConfirmIntent>()
  private val showKeyboardSubject = PublishSubject.create<ShowKeyboardIntent>()

  private lateinit var keyboardManager: InputMethodManager
  private val builder: AlertDialog.Builder by lazy { AlertDialog.Builder(activity) }
  private val dialogId: AlertDialog by lazy { builder.create() }
  private val dialogConfirm: AlertDialog by lazy { builder.create() }

  private var recSpeechStatus: Int by Delegates.notNull()
  private var keyboardIsOpen: Boolean by Delegates.notNull()

  override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    val root = inflater?.inflate(R.layout.speech_frag, container, false)
    keyboardManager = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    return root
  }

  override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    bind()
  }

  override fun onDestroy() {
    super.onDestroy()
    disposable.dispose()

  }

  override fun intents(): Observable<SpeechIntent> {
    return Observable.merge(listOf(
            initialIntent(),
            playPressedIntent(),
            stopPressedIntent(),
            zoomInIntent(),
            zoomOutIntent(),
            showDialogIdIntent(),
            showDialogConfirmIntent(),
            showKeyboardIntent()
            )
    )
  }

  @SuppressLint("LogNotTimber")
  override fun render(state: SpeechViewState) {
    recSpeechStatus = state.recSpeechStatus
    if (state.error != null) showToast() //return for fatal error
    speechTextField.textSize = state.fontSize
    if (state.showDialogId || state.showDialogConfirmation) {
      showDialogId(state.showDialogId, state.showDialogConfirmation)
    } else { showDialogId(false, false)
    }
    keyboardIsOpen =
        if (state.showKeyboard) { showKeyboard(true); true }
        else { showKeyboard(false); false }

    Log.d("Fragment: ", state.toString())
  }

  private fun bind() {
    initialDialogId()
    initialDialogConfirm()
    disposable.add(viewModel.states().subscribe(this::render))
    viewModel.processIntents(intents())
    initialClickListeners()
  }

  private fun initialIntent(): Observable<InitialIntent> = Observable.just(InitialIntent)
  private fun playPressedIntent(): Observable<PlayPressedIntent> = playPressedSubject
  private fun stopPressedIntent(): Observable<StopPressedIntent> = stopPressedSubject
  private fun zoomInIntent(): Observable<ZoomInIntent> = zoomInSubject
  private fun zoomOutIntent(): Observable<ZoomOutIntent> = zoomOutSubject
  private fun showDialogIdIntent(): Observable<ShowDialogIdIntent> = showDialogIdSubject
  private fun showDialogConfirmIntent(): Observable<ShowDialogConfirmIntent> = showDialogConfirmSubject
  private fun showKeyboardIntent(): Observable<ShowKeyboardIntent> = showKeyboardSubject


  private fun initialClickListeners() {
    speechPlayBt.setOnClickListener {
      if (recSpeechStatus == Constants.REC_STATUS_STOP) { showDialogIdSubject.onNext(ShowDialogIdIntent(true)) }
      else if (recSpeechStatus == Constants.REC_STATUS_PAUSE) { playPressedSubject.onNext(PlayPressedIntent) }
    }
    speechPauseBt.setOnClickListener { }
    speechStopBt.setOnClickListener { stopPressedSubject.onNext(StopPressedIntent(speechTextField.text)) }
    speechPlusBt.setOnClickListener { zoomInSubject.onNext(ZoomInIntent) }
    speechMinusBt.setOnClickListener { zoomOutSubject.onNext(ZoomOutIntent) }
    speechPaintBt.setOnClickListener { showDrawActivity() }
    speechGoodnessBt.setOnClickListener { }
    speechQuestionBt.setOnClickListener { showDialogConfirmSubject.onNext(ShowDialogConfirmIntent(true)) }
    speechMicPause.setOnClickListener { }
    speechMicPlay.setOnClickListener { }
    speechKeyboardBt.setOnClickListener { showKeyboardSubject.onNext(ShowKeyboardIntent(!keyboardIsOpen)) }
  }

  private fun initialDialogId() {
    val view = layoutInflater.inflate(R.layout.dialog_patient_id, null)
    builder.setView(view)
    dialogId.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    dialogId.setCancelable(false)

    val dialogPatientOk = view.findViewById<View>(R.id.dialogPatientOk)
    val dialogPatientCancel = view.findViewById<View>(R.id.dialogPatientCancel)
    dialogPatientOk.setOnClickListener {
      showDialogIdSubject.onNext(ShowDialogIdIntent(false))
      dialogId.dismiss()
    }
    dialogPatientCancel.setOnClickListener {
      showDialogIdSubject.onNext(ShowDialogIdIntent(false))
      dialogId.dismiss()
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

  private fun showDialogId(showDialogId: Boolean, showDialogConfirm: Boolean) {
    when {
      showDialogId -> dialogId.show()
      showDialogConfirm -> dialogConfirm.show()
      else -> {
        dialogId.dismiss()
        dialogConfirm.dismiss()
      }
    }
  }

  private fun showKeyboard(show: Boolean) {
    //TODO: show/hide keyboard only with button
    if (show) { keyboardManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.SHOW_IMPLICIT) }
    else { keyboardManager.hideSoftInputFromWindow(speechTextField.windowToken, 0) }
  }

  private fun showDrawActivity() {
    val intent = Intent(context, DrawActivity::class.java)
    startActivity(intent)
  }

  private fun showToast() {
    Toast.makeText(activity, "Check network settings please", Toast.LENGTH_LONG).show()
  }
}

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
import com.digitalkoi.speechtotext.R.id.dialogPatientCancel
import com.digitalkoi.speechtotext.R.id.dialogPatientOk
import com.digitalkoi.speechtotext.speech.SpeechIntent.PlayPressedIntent
import com.digitalkoi.speechtotext.speech.SpeechIntent.ShowDialogConfirmIntent
import com.digitalkoi.speechtotext.speech.SpeechIntent.ShowDialogIdIntent
import com.digitalkoi.speechtotext.speech.SpeechIntent.ShowKeyboardIntent
import com.digitalkoi.speechtotext.speech.SpeechIntent.ZoomInIntent
import com.digitalkoi.speechtotext.speech.SpeechIntent.ZoomOutIntent
import com.digitalkoi.speechtotext.util.Constants
import com.digitalkoi.speechtotext.util.SpeechViewModelFactory
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.dialog_confirmation.dialogConfirmYes
import kotlinx.android.synthetic.main.dialog_patient_id.dialogPatientCancel
import kotlinx.android.synthetic.main.dialog_patient_id.dialogPatientOk
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
  private val playPressedSubject = PublishSubject.create<SpeechIntent.PlayPressedIntent>()
  private val zoomOutSubject = PublishSubject.create<SpeechIntent.ZoomOutIntent>()
  private val zoomInSubject = PublishSubject.create<SpeechIntent.ZoomInIntent>()
  private val showDialogIdSubject = PublishSubject.create<SpeechIntent.ShowDialogIdIntent>()
  private val showDialogConfirmSubject = PublishSubject.create<SpeechIntent.ShowDialogConfirmIntent>()
  private val showKeyboardSubject = PublishSubject.create<SpeechIntent.ShowKeyboardIntent>()

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
//    dialogId.dismiss()
//    dialogConfirm.dismiss()
  }

  override fun intents(): Observable<SpeechIntent> {
    return Observable.merge(listOf(
            initialIntent(),
            loadTextIntent(),
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
    if (state.showDialogId) showDialogId()
    if (state.showDialogConfirmation) showDialogConfirm()
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

  private fun initialIntent(): Observable<SpeechIntent.InitialIntent> = Observable.just(SpeechIntent.InitialIntent)

  private fun loadTextIntent(): Observable<SpeechIntent.PlayPressedIntent> = playPressedSubject
  private fun zoomInIntent(): Observable<SpeechIntent.ZoomInIntent> = zoomInSubject
  private fun zoomOutIntent(): Observable<SpeechIntent.ZoomOutIntent> = zoomOutSubject
  private fun showDialogIdIntent(): Observable<SpeechIntent.ShowDialogIdIntent> = showDialogIdSubject
  private fun showDialogConfirmIntent(): Observable<SpeechIntent.ShowDialogConfirmIntent> = showDialogConfirmSubject
  private fun showKeyboardIntent(): Observable<SpeechIntent.ShowKeyboardIntent> = showKeyboardSubject
  private fun initialClickListeners() {
    speechPlayBt.setOnClickListener {
      if (recSpeechStatus == Constants.REC_STATUS_STOP) { showDialogId() }
      else if (recSpeechStatus == Constants.REC_STATUS_PAUSE) { playPressedSubject.onNext(PlayPressedIntent) }
    }
    speechPauseBt.setOnClickListener { }
    speechStopBt.setOnClickListener { }
    speechPlusBt.setOnClickListener { zoomInSubject.onNext(ZoomInIntent) }
    speechMinusBt.setOnClickListener { zoomOutSubject.onNext(ZoomOutIntent) }
    speechPaintBt.setOnClickListener { }
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

  private fun showDialogId() {
    if (!dialogId.isShowing)
      dialogId.show()
  }

  private fun showDialogConfirm() {
    if (!dialogConfirm.isShowing)
      dialogConfirm.show()
  }


  private fun showKeyboard(show: Boolean) {
    //TODO: show/hide keyboard only with button
    if (show) { keyboardManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.SHOW_IMPLICIT) }
    else { keyboardManager.hideSoftInputFromWindow(speechTextField.windowToken, 0) }
  }

  private fun showToast() {
    Toast.makeText(activity, "Check network settings please", Toast.LENGTH_LONG).show()
  }
}

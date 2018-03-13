package com.digitalkoi.speechtotext.speech

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
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.WindowManager.LayoutParams
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import com.digitalkoi.speechtotext.R
import com.digitalkoi.speechtotext.util.SpeechViewModelFactory
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.dialog_confirmation.dialogConfirmNo
import kotlinx.android.synthetic.main.dialog_confirmation.dialogConfirmYes
import kotlinx.android.synthetic.main.speech_frag.*

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

  private lateinit var keyboardManager: InputMethodManager

  private var showDialogPatientId: Boolean = false

  override fun onCreateView(
    inflater: LayoutInflater?,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    val root = inflater?.inflate(R.layout.speech_frag, container, false)

    return root
  }

  override fun onViewCreated(
    view: View?,
    savedInstanceState: Bundle?
  ) {
    super.onViewCreated(view, savedInstanceState)

    bind()
  }

  override fun onDestroy() {
    super.onDestroy()
    disposable.dispose()
  }

  override fun intents(): Observable<SpeechIntent> {
    return Observable.merge(
        initialIntent(),
        loadTextIntent(),
        zoomInIntent(),
        zoomOutIntent()
    )
  }

  override fun render(state: SpeechViewState) {
    state.showIdDialog
    if (state.error != null) {
      showToast()
      return
    }
    if (state.showIdDialog)
    if (state.fontChanged) speechTextField.textSize = state.fontSize
  }

  private fun initialIntent(): Observable<SpeechIntent.InitialIntent> {
    return Observable.just(SpeechIntent.InitialIntent)
  }

  private fun loadTextIntent(): Observable<SpeechIntent.PlayPressedIntent> {
    return playPressedSubject
  }

  private fun zoomInIntent(): Observable<SpeechIntent.ZoomInIntent> {
    return zoomInSubject
  }

  private fun zoomOutIntent(): Observable<SpeechIntent.ZoomOutIntent> {
    return zoomOutSubject
  }

  private fun bind() {

    disposable.add(viewModel.states().subscribe(this::render))
    viewModel.processIntents(intents())

    keyboardManager = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

    initialClickListeners()

  }

  private fun initialClickListeners() {
    speechPlayBt.setOnClickListener { showDialogId() }
    speechPauseBt.setOnClickListener { }
    speechStopBt.setOnClickListener { }
    speechPlusBt.setOnClickListener { zoomInSubject.onNext(SpeechIntent.ZoomInIntent) }
    speechMinusBt.setOnClickListener { zoomOutSubject.onNext(SpeechIntent.ZoomOutIntent) }
    speechPaintBt.setOnClickListener { }
    speechGoodnessBt.setOnClickListener { }
    speechQuestionBt.setOnClickListener { showDialogConfirm() }


    speechMicPause.setOnClickListener { }
    speechMicPlay.setOnClickListener { }
    speechKeyboardBt.setOnClickListener {
      if (keyboardManager.isActive) {
        keyboardManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
      } else {
        keyboardManager.toggleSoftInput(0, InputMethodManager.HIDE_IMPLICIT_ONLY)
      }
    }
  }

  private fun showDialogConfirm() {
    val builder = AlertDialog.Builder(activity)
    val view = layoutInflater.inflate(R.layout.dialog_confirmation, null)
    val dialogConfirmYes = view.findViewById<View>(R.id.dialogConfirmYes)
    builder.setView(view)
    val alertDialog: AlertDialog = builder.create()
    alertDialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    val windowManager = alertDialog.window.attributes

    windowManager.verticalMargin = 0.1f
    windowManager.gravity = Gravity.BOTTOM
    alertDialog.show()

    dialogConfirmYes.isEnabled = true
    dialogConfirmYes.setOnClickListener { alertDialog.dismiss() }
  }

  private fun showDialogId() {
    val builder = AlertDialog.Builder(activity)
    val view = layoutInflater.inflate(R.layout.dialog_patient_id, null)
    val dialogPatientOk = view.findViewById<View>(R.id.dialogPatientOk)
    val dialogPatientCancel = view.findViewById<View>(R.id.dialogPatientCancel)
    builder.setView(view)
    val alertDialog: AlertDialog = builder.create()
    alertDialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    alertDialog.show()

    dialogPatientOk.isEnabled = true
    dialogPatientCancel.isEnabled = true
    dialogPatientOk.setOnClickListener { alertDialog.dismiss() }
    dialogPatientCancel.setOnClickListener { alertDialog.dismiss() }
  }

  private fun showToast() {
    Toast.makeText(activity, "Check network settings please", Toast.LENGTH_LONG)
        .show()
  }
}

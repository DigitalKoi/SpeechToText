package com.digitalkoi.speechtotext.mvi.speech

import android.arch.lifecycle.ViewModel
import com.digitalkoi.speechtotext.mvi.base.MviViewModel
import com.digitalkoi.speechtotext.mvi.speech.SpeechAction.*
import com.digitalkoi.speechtotext.mvi.speech.SpeechIntent.*
import com.digitalkoi.speechtotext.mvi.speech.SpeechResult.*
import com.digitalkoi.speechtotext.mvi.speech.SpeechResult.ShowViewResult.*
import com.digitalkoi.speechtotext.util.Constants.Companion.REC_STATUS_PLAY
import com.digitalkoi.speechtotext.util.Constants.Companion.REC_STATUS_STOP
import com.digitalkoi.speechtotext.util.notOfType
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.PublishSubject

/**
 * @author Taras Zhupnyk (akka DigitalKoi) on 09/03/18.
 */

class SpeechViewModel(
  private val actionProcessorHolder: SpeechActionProcessorHolder
) : ViewModel(), MviViewModel<SpeechIntent, SpeechViewState> {

  private val intentsSubject: PublishSubject<SpeechIntent> = PublishSubject.create()
  private val statesObservable: Observable<SpeechViewState> = compose()

  override fun processIntents(intents: Observable<SpeechIntent>) {
    intents.subscribe(intentsSubject)
  }

  override fun states(): Observable<SpeechViewState> = statesObservable

  private val intentFilter: ObservableTransformer<SpeechIntent, SpeechIntent>
    get() = ObservableTransformer { intents ->
      intents.publish { shared ->
        Observable.merge(
            shared.ofType(InitialIntent::class.java).take(1),
            shared.notOfType(InitialIntent::class.java)
        )
      }
    }

  private fun compose(): Observable<SpeechViewState> {
    return intentsSubject
        .compose(intentFilter)
        .map { this.actionFromIntent(it) }
        .compose(actionProcessorHolder.actionProcessor)
        .scan(SpeechViewState.idle(), reducer)
        .distinctUntilChanged()
        .replay()
        .autoConnect(0)
  }

  private fun actionFromIntent(intent: SpeechIntent): SpeechAction {
    return when (intent) {
      is InitialIntent ->  FontSizeAction
      is PlayPressedIntent -> PlayPressedAction(intent.patientId)
      is StopPressedIntent -> StopPressedAction(intent.id, intent.text)
      is PausePressedIntent -> PausePressedAction(intent.status)
      is ZoomInIntent -> FontSizeInAction
      is ZoomOutIntent -> FontSizeOutAction
      is ShowDialogIdIntent -> ShowDialogIdAction(intent.showView)
      is ShowDialogConfirmIntent -> ShowDialogConfirmAction(intent.showView)
      is ShowDialogGoodnessIntent -> ShowDialogGoodnessAction(intent.showView)
      is ShowKeyboardIntent -> ShowKeyboardAction(intent.showView)
    }
  }

  companion object {

    private val reducer = BiFunction { previousState: SpeechViewState, result: SpeechResult ->
      when (result) {
        is LoadSpeechResult -> when (result) {
          is LoadSpeechResult.InFlight -> previousState.copy(isLoading = true)
          is LoadSpeechResult.Failure -> previousState.copy(
              isLoading = false, error = result.error, recSpeechStatus = REC_STATUS_STOP)
          is LoadSpeechResult.Success -> previousState.copy(
              isLoading = false, idPatient = result.id,
              text = result.text, recSpeechStatus = REC_STATUS_PLAY)

        }
        is SaveSpeechResult ->
          previousState.copy(
              isLoading = false, recSpeechStatus = REC_STATUS_STOP,
              idPatient = null, text = "")

        is PauseSpeechResult -> previousState.copy(isLoading = false, recSpeechStatus = result.state)

        is FontSizeResult -> when (result) {
          is FontSizeResult.Success -> previousState.copy(
              isLoading = false, fontSize = result.fontSize
          )
        }
        is ShowViewResult -> when (result) {
          is ShowDialogIdResult -> previousState.copy(showDialogId = result.state)
          is ShowDialogConfirmResult -> previousState.copy(showDialogConfirmation = result.state)
          is ShowDialogGoodnessResult -> previousState.copy(showDialogGoodness = result.state)
          is ShowKeyboardResult -> previousState.copy(showKeyboard = result.state)
        }
      }
    }
  }
}
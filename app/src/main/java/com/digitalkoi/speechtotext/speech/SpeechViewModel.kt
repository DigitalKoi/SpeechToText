package com.digitalkoi.speechtotext.speech

import android.arch.lifecycle.ViewModel
import com.digitalkoi.speechtotext.mvibase.MviViewModel
import com.digitalkoi.speechtotext.speech.SpeechIntent.InitialIntent
import com.digitalkoi.speechtotext.speech.SpeechIntent.PlayPressedIntent
import com.digitalkoi.speechtotext.speech.SpeechIntent.ShowDialogConfirmIntent
import com.digitalkoi.speechtotext.speech.SpeechIntent.ShowDialogIdIntent
import com.digitalkoi.speechtotext.speech.SpeechIntent.ShowKeyboardIntent
import com.digitalkoi.speechtotext.speech.SpeechIntent.ZoomInIntent
import com.digitalkoi.speechtotext.speech.SpeechIntent.ZoomOutIntent
import com.digitalkoi.speechtotext.speech.SpeechResult.FontSizeResult
import com.digitalkoi.speechtotext.speech.SpeechResult.LoadSpeechResult
import com.digitalkoi.speechtotext.speech.SpeechResult.ShowViewResult
import com.digitalkoi.speechtotext.speech.SpeechResult.ShowViewResult.ShowDialogConfirmResult
import com.digitalkoi.speechtotext.speech.SpeechResult.ShowViewResult.ShowDialogIdResult
import com.digitalkoi.speechtotext.speech.SpeechResult.ShowViewResult.ShowKeyboardResult
import com.digitalkoi.speechtotext.util.Constants
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
            shared.ofType(SpeechIntent.InitialIntent::class.java).take(1),
            shared.notOfType(SpeechIntent.InitialIntent::class.java)
        )
      }
    }

  private fun compose(): Observable<SpeechViewState> {
    return intentsSubject
        .compose(intentFilter)
        .map(this::actionFromIntent)
        .compose(actionProcessorHolder.actionProcessor)
        .scan(SpeechViewState.idle(), reducer)
        .distinctUntilChanged()
        .replay()
        .autoConnect(0)
  }

  private fun actionFromIntent(intent: SpeechIntent): SpeechAction {
    return when (intent) {
      is InitialIntent -> SpeechAction.FontSizeAction
      is PlayPressedIntent -> SpeechAction.PlayPressedAction(Constants.REC_STATUS_PLAY)
      is ZoomInIntent -> SpeechAction.FontSizeInAction
      is ZoomOutIntent -> SpeechAction.FontSizeOutAction
      is ShowDialogIdIntent -> SpeechAction.ShowDialogIdAction(intent.showView)
      is ShowDialogConfirmIntent -> SpeechAction.ShowDialogConfirmAction(intent.showView)
      is ShowKeyboardIntent -> SpeechAction.ShowKeyboardAction(intent.showView)
    }
  }

  companion object {

    private val reducer = BiFunction { previousState: SpeechViewState, result: SpeechResult ->
      when (result) {
        is LoadSpeechResult -> when (result) {
          is LoadSpeechResult.InFlight -> previousState.copy(isLoading = true)
          is LoadSpeechResult.Failure -> previousState.copy(isLoading = false, error = result.error)
          is LoadSpeechResult.Success -> previousState.copy(isLoading = false, text = result.text)

        }
        is FontSizeResult -> when (result) {
          is FontSizeResult.Success -> previousState.copy(
              isLoading = false, fontSize = result.fontSize
          )
        }
        is ShowViewResult -> when (result) {
          is ShowDialogIdResult -> previousState.copy(showDialogId = result.state)
          is ShowDialogConfirmResult -> previousState.copy(showDialogConfirmation = result.state)
          is ShowKeyboardResult -> previousState.copy(showKeyboard = result.state)
        }
      }
    }
  }
}
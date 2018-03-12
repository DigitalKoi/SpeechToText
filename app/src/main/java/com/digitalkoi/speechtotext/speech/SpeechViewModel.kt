package com.digitalkoi.speechtotext.speech

import android.arch.lifecycle.ViewModel
import com.digitalkoi.speechtotext.mvibase.MviViewModel
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import com.digitalkoi.speechtotext.speech.SpeechResult.*
import com.digitalkoi.speechtotext.util.notOfType
import io.reactivex.ObservableTransformer
import io.reactivex.functions.BiFunction

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
            is SpeechIntent.InitialIntent -> SpeechAction.GetFontSizeAction
            is SpeechIntent.LoadTextIntent -> SpeechAction.LoadSpeechAction
            is SpeechIntent.ZoomInIntent -> SpeechAction.FontSizeInAction
            is SpeechIntent.ZoomOutIntent -> SpeechAction.FontSizeOutAction
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
                    is FontSizeResult.Success -> previousState.copy(isLoading = false, fontChanged = true, fontSize = result.fontSize)
                }
            }
        }
    }
}
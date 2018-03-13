package com.digitalkoi.speechtotext.speech

import com.digitalkoi.speechtotext.data.SpeechRepository
import com.digitalkoi.speechtotext.speech.SpeechAction.*
import com.digitalkoi.speechtotext.speech.SpeechResult.*
import com.digitalkoi.speechtotext.util.schedulers.BaseSchedulerProvider
import io.reactivex.Observable
import io.reactivex.ObservableTransformer

/**
 * @author Taras Zhupnyk (akka DigitalKoi) on 09/03/18.
 */

class SpeechActionProcessorHolder(
        private val speechRepository: SpeechRepository,
        private val schedulerProvider: BaseSchedulerProvider
) {

    private val playPressedProcessor =
            ObservableTransformer<PlayPressedAction, LoadSpeechResult> { action ->
                action.flatMap {
                    speechRepository.getSpeech()
                            .toObservable()
                            .map { text -> LoadSpeechResult.Success(text) }
                            .cast(LoadSpeechResult::class.java)
                            .onErrorReturn(LoadSpeechResult::Failure)
                            .subscribeOn(schedulerProvider.io())
                            .observeOn(schedulerProvider.ui())
                            .startWith(LoadSpeechResult.InFlight)
                }
            }

    private val fontSizeProcessor =
            ObservableTransformer<FontSizeAction, FontSizeResult> { action ->
                action.flatMap {
                    speechRepository.getTextSize()
                            .toObservable()
                            .map { fontSize -> FontSizeResult.Success(fontSize) }
                            .cast(FontSizeResult::class.java)
                            .subscribeOn(schedulerProvider.io())
                            .observeOn(schedulerProvider.ui())
                }
            }

    private val fontSizeInProcessor =
            ObservableTransformer<FontSizeInAction, FontSizeResult> { action ->
                action.flatMap {
                    speechRepository.zoomIn()
                            .toObservable()
                            .map { zoomIn -> FontSizeResult.Success(zoomIn) }
                            .cast(FontSizeResult::class.java)
                            .subscribeOn(schedulerProvider.io())
                            .observeOn(schedulerProvider.ui())
                }
            }

    private val fontSizeOutProcessor =
            ObservableTransformer<FontSizeOutAction, FontSizeResult> { acttion ->
                acttion.flatMap {
                    speechRepository.zoomOut()
                            .toObservable()
                            .map { zoomOut -> FontSizeResult.Success(zoomOut) }
                            .cast(FontSizeResult::class.java)
                            .subscribeOn(schedulerProvider.io())
                            .observeOn(schedulerProvider.ui())
                }
            }

    internal var actionProcessor =
            ObservableTransformer<SpeechAction, SpeechResult> { action ->
                action.publish { shared ->
                    Observable.merge(
                            shared.ofType(PlayPressedAction::class.java).compose(playPressedProcessor),
                            shared.ofType(FontSizeAction::class.java).compose(fontSizeProcessor),
                            shared.ofType(FontSizeInAction::class.java).compose(fontSizeInProcessor),
                            shared.ofType(FontSizeOutAction::class.java).compose(fontSizeOutProcessor)
                    )
                }
            }
}
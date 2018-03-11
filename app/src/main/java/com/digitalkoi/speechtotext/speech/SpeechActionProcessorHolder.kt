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

    private val loadTextProcessor =
            ObservableTransformer<LoadSpeech, LoadSpeechResult> { action ->
                action.flatMap { action ->
                    null
                }
            }

    private val fontSizeProcessor =
            ObservableTransformer<FontSizeAction, SpeechResult.FontSizeResult> { action ->
                action.flatMap {
                    null
                }
            }

    internal var actionProcessor =
            ObservableTransformer<SpeechAction, SpeechResult> { action ->
                action.publish { shared ->
                    Observable.merge(
                            shared.ofType(LoadSpeech::class.java).compose(loadTextProcessor),
                            shared.ofType(FontSizeAction::class.java).compose(fontSizeProcessor)
                    )
                }
            }
}
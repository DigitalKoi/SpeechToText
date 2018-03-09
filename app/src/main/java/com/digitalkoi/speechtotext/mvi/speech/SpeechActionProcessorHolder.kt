package com.digitalkoi.speechtotext.mvi.speech

import com.digitalkoi.speechtotext.mvi.data.SpeechRepository
import com.digitalkoi.speechtotext.mvi.speech.SpeechAction.LoadSpeech
import com.digitalkoi.speechtotext.mvi.speech.SpeechResult.LoadSpeechResult
import com.digitalkoi.speechtotext.mvi.util.schedulers.BaseSchedulerProvider
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
}
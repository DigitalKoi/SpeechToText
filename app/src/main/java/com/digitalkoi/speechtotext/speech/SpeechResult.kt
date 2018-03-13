package com.digitalkoi.speechtotext.speech

import com.digitalkoi.speechtotext.mvibase.MviResult

/**
 * @author Taras Zhupnyk (akka DigitalKoi) on 09/03/18.
 */

sealed class SpeechResult : MviResult {

    sealed class LoadSpeechResult : SpeechResult() {
        data class Success(val text: String) : LoadSpeechResult()
        data class Failure(val error: Throwable) : LoadSpeechResult()
        object InFlight : LoadSpeechResult()
        object ShowDialogId : LoadSpeechResult()
    }

    sealed class FontSizeResult : SpeechResult() {
        data class Success(val fontSize: Float) : FontSizeResult()
    }
}
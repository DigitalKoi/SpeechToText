package com.digitalkoi.speechtotext.mvi.speech

import com.digitalkoi.speechtotext.mvi.MviResult

/**
 * @author Taras Zhupnyk (akka DigitalKoi) on 09/03/18.
 */

sealed class SpeechResult : MviResult {

  sealed class LoadSpeechResult : SpeechResult() {
    data class Success(val id: String, val text: String) : LoadSpeechResult()
    data class Failure(val error: Throwable) : LoadSpeechResult()
    object InFlight : LoadSpeechResult()
  }

  object SaveSpeechResult : SpeechResult()

  object PauseSpeechResult : SpeechResult()

  sealed class FontSizeResult : SpeechResult() {
    data class Success(val fontSize: Float) : FontSizeResult()
  }

  sealed class ShowViewResult : SpeechResult() {
    data class ShowDialogIdResult(val state: Boolean) : ShowViewResult()
    data class ShowDialogConfirmResult(val state: Boolean) : ShowViewResult()
    data class ShowDialogGoodnessResult(val state: Boolean) :ShowViewResult()
    data class ShowKeyboardResult(val state: Boolean) : ShowViewResult()
  }
}
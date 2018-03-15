package com.digitalkoi.speechtotext.speech

import com.digitalkoi.speechtotext.mvi.mvibase.MviIntent

/**
 * @author Taras Zhupnyk (akka DigitalKoi) on 09/03/18.
 */

sealed class SpeechIntent : MviIntent {

  object InitialIntent : SpeechIntent()

  object PlayPressedIntent : SpeechIntent()

  data class StopPressedIntent(val text: String) : SpeechIntent()

  object ZoomInIntent : SpeechIntent()

  object ZoomOutIntent : SpeechIntent()

  data class ShowDialogIdIntent(val showView: Boolean) : SpeechIntent()

  data class ShowDialogConfirmIntent(val showView: Boolean) : SpeechIntent()

  data class ShowKeyboardIntent(val showView: Boolean) : SpeechIntent()

}
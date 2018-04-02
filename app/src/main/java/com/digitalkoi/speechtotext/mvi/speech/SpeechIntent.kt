package com.digitalkoi.speechtotext.mvi.speech

import com.digitalkoi.speechtotext.mvi.mvibase.MviIntent

/**
 * @author Taras Zhupnyk (akka DigitalKoi) on 09/03/18.
 */

sealed class SpeechIntent : MviIntent {

  object InitialIntent : SpeechIntent()

  data class PlayPressedIntent(val patientId: String) : SpeechIntent()

  data class StopPressedIntent(val id: String, val text: String) : SpeechIntent()

  object PausePressedIntent : SpeechIntent()

  object ZoomInIntent : SpeechIntent()

  object ZoomOutIntent : SpeechIntent()

  data class ShowDialogIdIntent(val showView: Boolean) : SpeechIntent()

  data class ShowDialogConfirmIntent(val showView: Boolean) : SpeechIntent()

  data class ShowDialogGoodnessIntent(val showView: Boolean) : SpeechIntent()

  data class ShowKeyboardIntent(val showView: Boolean) : SpeechIntent()

}
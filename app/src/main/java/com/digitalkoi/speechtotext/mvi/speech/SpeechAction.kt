package com.digitalkoi.speechtotext.mvi.speech

import com.digitalkoi.speechtotext.mvi.MviAction

/**
 * @author Taras Zhupnyk (akka DigitalKoi) on 09/03/18.
 */

sealed class SpeechAction : MviAction {

  data class PlayPressedAction(val id: String) : SpeechAction()

  data class StopPressedAction(val id: String, val text: String) : SpeechAction()

  object PausePressedAction : SpeechAction()

  object FontSizeAction : SpeechAction()

  object FontSizeInAction : SpeechAction()

  object FontSizeOutAction : SpeechAction()

  data class ShowDialogIdAction(val showView: Boolean) : SpeechAction()

  data class ShowDialogConfirmAction(val showView: Boolean) : SpeechAction()

  data class ShowDialogGoodnessAction(val showView: Boolean) : SpeechAction()

  data class ShowKeyboardAction(val showView: Boolean) : SpeechAction()

}
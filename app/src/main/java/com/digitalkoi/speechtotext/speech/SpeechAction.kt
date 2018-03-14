package com.digitalkoi.speechtotext.speech

import com.digitalkoi.speechtotext.mvibase.MviAction

/**
 * @author Taras Zhupnyk (akka DigitalKoi) on 09/03/18.
 */

sealed class SpeechAction : MviAction {

  data class PlayPressedAction(val statusRecord: Int) : SpeechAction()

  object FontSizeAction : SpeechAction()

  object FontSizeInAction : SpeechAction()

  object FontSizeOutAction : SpeechAction()

  data class ShowDialogIdAction(val showView: Boolean) : SpeechAction()

  data class ShowDialogConfirmAction(val showView: Boolean) : SpeechAction()

  data class ShowKeyboardAction(val showView: Boolean) : SpeechAction()

}
package com.digitalkoi.speechtotext.speech

import com.digitalkoi.speechtotext.mvibase.MviAction

/**
 * @author Taras Zhupnyk (akka DigitalKoi) on 09/03/18.
 */

sealed class SpeechAction : MviAction {

    object PlayPressedAction: SpeechAction()

    object ShowDialogID: SpeechAction()

    object FontSizeAction : SpeechAction()

    object FontSizeInAction : SpeechAction()

    object FontSizeOutAction : SpeechAction()
}
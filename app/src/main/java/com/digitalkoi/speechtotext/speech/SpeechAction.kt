package com.digitalkoi.speechtotext.speech

import com.digitalkoi.speechtotext.mvibase.MviAction

/**
 * @author Taras Zhupnyk (akka DigitalKoi) on 09/03/18.
 */

sealed class SpeechAction : MviAction {

    object LoadSpeechAction: SpeechAction()

    object GetFontSizeAction : SpeechAction()

    object FontSizeInAction : SpeechAction()

    object FontSizeOutAction : SpeechAction()
}
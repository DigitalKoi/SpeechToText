package com.digitalkoi.speechtotext.speech

import com.digitalkoi.speechtotext.mvibase.MviAction

/**
 * @author Taras Zhupnyk (akka DigitalKoi) on 09/03/18.
 */

sealed class SpeechAction : MviAction {

    data class SetupViewAction(
            val fontSize: Float
    ) : SpeechAction()

    data class LoadSpeech(
            val forceUpdate: Boolean
    ) : SpeechAction()

    data class FontSizeAction(val fontSize: Float) : SpeechAction()
}
package com.digitalkoi.speechtotext.speech

import com.digitalkoi.speechtotext.mvi.mvibase.MviIntent

/**
 * @author Taras Zhupnyk (akka DigitalKoi) on 09/03/18.
 */

sealed class SpeechIntent : MviIntent {

    object InitialIntent : SpeechIntent()

    object LoadTextIntent : SpeechIntent()

    object ZoomInIntent : SpeechIntent()

    object ZoomOutIntent : SpeechIntent()

}
package com.digitalkoi.speechtotext.mvi.speech

import com.digitalkoi.speechtotext.mvi.mvibase.MviViewState

/**
 * @author Taras Zhupnyk (akka DigitalKoi) on 09/03/18.
 */

data class SpeechViewState (
        val isLoading: Boolean
) : MviViewState {
}
package com.digitalkoi.speechtotext.speech

import com.digitalkoi.speechtotext.mvibase.MviViewState

/**
 * @author Taras Zhupnyk (akka DigitalKoi) on 09/03/18.
 */

data class SpeechViewState (
        val isLoading: Boolean,
        val text: String,
        val error: Throwable?,
        val fontChanged: Boolean,
        val fontSize: Float
) : MviViewState {
    companion object {
        fun idle(): SpeechViewState {
            return SpeechViewState(
                    isLoading = false,
                    text = "",
                    error = null,
                    fontChanged = false,
                    fontSize = 18f

            )
        }
    }
}
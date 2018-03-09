package com.digitalkoi.speechtotext.mvi.speech

import com.digitalkoi.speechtotext.mvi.mvibase.MviViewState

/**
 * @author Taras Zhupnyk (akka DigitalKoi) on 09/03/18.
 */

sealed class SpeechUiModel(val inProgress: Boolean = false,
                           val text: String? = null)
    : MviViewState {

    object InProgress : SpeechUiModel(true, null)

    object Failed : SpeechUiModel()

    data class Success(private val  result: String?) : SpeechUiModel(false, result)

    class Idle : SpeechUiModel(false, null)
}
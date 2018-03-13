package com.digitalkoi.speechtotext.speech

import com.digitalkoi.speechtotext.mvibase.MviViewState
import com.digitalkoi.speechtotext.util.Constants

/**
 * @author Taras Zhupnyk (akka DigitalKoi) on 09/03/18.
 */

data class SpeechViewState(
        val isLoading: Boolean,
        //All about text
        val text: String?,
        val fontSize: Float,
        val fontChanged: Boolean,
        //Id patient for saving in file
        val idPatient: String?,
        val showIdDialog: Boolean,
        //status for recording speech:
        //0 - stop, 1 - play, 2 - pause
        val recSpeechStatus: Byte,
        val error: Throwable?

) : MviViewState {
    companion object {
        fun idle(): SpeechViewState {
            return SpeechViewState(
                    isLoading = false,
                    text = null,
                    fontSize = Constants.DEFAULT_FONT_SIZE,
                    fontChanged = false,
                    idPatient = null,
                    showIdDialog = true,
                    recSpeechStatus = 0,
                    error = null
            )
        }
    }
}
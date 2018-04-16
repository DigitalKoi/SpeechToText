package com.digitalkoi.speechtotext.mvi.speech

import com.digitalkoi.speechtotext.mvi.MviViewState
import com.digitalkoi.speechtotext.util.Constants

/**
 * @author Taras Zhupnyk (akka DigitalKoi) on 09/03/18.
 */

data class SpeechViewState(
  val isLoading: Boolean,
    //All about text
  val text: String,
  val fontSize: Float,
  val idPatient: String?,       //Id patient for saving in file
  val showDialogId: Boolean,
  val showDialogConfirmation: Boolean,  //status dialog box YesNo
  val showDialogGoodness: Boolean,
  val showKeyboard: Boolean,
  val recSpeechStatus: Int,    //status for recording speech: 0 - stop, 1 - play, 2 - pause
  val error: Throwable?

) : MviViewState {
  companion object {
    fun idle(): SpeechViewState {
      return SpeechViewState(
          isLoading = false,
          text = "",
          fontSize = Constants.DEFAULT_FONT_SIZE,
          idPatient = null,
          showDialogId = false,
          showDialogConfirmation = false,
          showDialogGoodness = false,
          showKeyboard = false,
          recSpeechStatus = 0,
          error = null
      )
    }
  }
}
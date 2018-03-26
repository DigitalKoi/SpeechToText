package com.digitalkoi.speechtotext.history

import com.digitalkoi.speechtotext.data.file.CSVConversation
import com.digitalkoi.speechtotext.mvibase.MviViewState
import com.digitalkoi.speechtotext.util.Constants

/**
 * @author Taras Zhupnyk (akka DigitalKoi) on 24/03/18.
 */

data class HistoryViewState(
  val fontSize: Float,
  val dataList: List<CSVConversation>?,
  val showDateDialog: Boolean,
  val date: String?,
    val error: Throwable?
) : MviViewState {
  companion object {
    fun idle(): HistoryViewState {
      return HistoryViewState(
          fontSize = Constants.DEFAULT_FONT_SIZE,
          dataList = null,
          showDateDialog = false,
          date = null,
          error = null
      )
    }
  }
}
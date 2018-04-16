package com.digitalkoi.speechtotext.mvi.history

import com.digitalkoi.speechtotext.data.file.CSVConversation

/**
 * @author Taras Zhupnyk (akka DigitalKoi) on 24/03/18.
 */

sealed class HistoryResult {

  sealed class InitialResult : HistoryResult() {
    data class Success(val fontSize: Float) : InitialResult()
    data class Failure(val error: Throwable) : InitialResult()
  }

  data class UpdateListResult(val dataList: List<CSVConversation>, val date: String) : HistoryResult()

  data class ShowDataPickerResult(val showDataPickerDialog: Boolean) : HistoryResult()
}
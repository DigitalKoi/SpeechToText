package com.digitalkoi.speechtotext.history

import com.digitalkoi.speechtotext.data.file.CSVConversation

/**
 * @author Taras Zhupnyk (akka DigitalKoi) on 24/03/18.
 */

sealed class HistoryResult {

  sealed class InitialResult : HistoryResult() {
    data class Success(val fontSize: Float, val dataList: List<CSVConversation>) : InitialResult()
    data class Failure(val error: Throwable) : InitialResult()
  }

  sealed class ShowDateResult : HistoryResult() {
    data class Success(val dataList: List<CSVConversation>) : ShowDateResult()
    data class Failure(val error: Throwable) : ShowDateResult()
  }
}
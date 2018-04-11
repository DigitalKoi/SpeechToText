package com.digitalkoi.speechtotext.mvi.detail

import com.digitalkoi.speechtotext.data.file.CSVConversation
import com.digitalkoi.speechtotext.mvi.MviResult

/**
 * @author Taras Zhupnyk (akka DigitalKoi) on 11/04/18.
 */

sealed class DetailResult : MviResult {

  sealed class PopulateResult : DetailResult() {
    data class Success(val item: CSVConversation) : PopulateResult()
    data class Failure(val error: Throwable) : PopulateResult()
    object InFlight : PopulateResult()
  }

  sealed class EditResult : DetailResult() {
    object Success : EditResult()
    data class Failure(val error: Throwable) : EditResult()
    object InFlight : EditResult()
  }

  sealed class DeleteResult : DetailResult() {
    object Success : DeleteResult()
    data class Failure(val error: Throwable) : DeleteResult()
    object InFlight : DeleteResult()
  }
}
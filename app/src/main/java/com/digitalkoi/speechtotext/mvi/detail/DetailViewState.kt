package com.digitalkoi.speechtotext.mvi.detail

import com.digitalkoi.speechtotext.data.file.CSVConversation
import com.digitalkoi.speechtotext.mvi.MviViewState

/**
 * @author Taras Zhupnyk (akka DigitalKoi) on 10/04/18.
 */

data class DetailViewState(
  val isLoading: Boolean,
  val item: CSVConversation?,
  val error: Throwable?
) : MviViewState {
  companion object {
    fun idle(): DetailViewState {
      return DetailViewState(
          isLoading = false,
          item = null,
          error = null)
    }
  }
}
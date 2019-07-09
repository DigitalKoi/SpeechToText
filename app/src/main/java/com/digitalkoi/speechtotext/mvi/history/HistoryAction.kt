package com.digitalkoi.speechtotext.mvi.history

import com.digitalkoi.speechtotext.mvi.base.MviAction

/**
 * @author Taras Zhupnyk (akka DigitalKoi) on 24/03/18.
 */

sealed class HistoryAction : MviAction {
  object InitialAction : HistoryAction()
  data class UpdateListAction(val date: String) : HistoryAction()
  data class ShowDataPickerAction(val showDataPickerAction: Boolean) : HistoryAction()
}
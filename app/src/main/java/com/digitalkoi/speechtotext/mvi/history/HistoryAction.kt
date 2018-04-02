package com.digitalkoi.speechtotext.mvi.history

import com.digitalkoi.speechtotext.mvi.MviAction

/**
 * @author Taras Zhupnyk (akka DigitalKoi) on 24/03/18.
 */

sealed class HistoryAction : MviAction {

  object InitialAction : HistoryAction()

  data class ShowDialogDateAction(val date: String) : HistoryAction()
}
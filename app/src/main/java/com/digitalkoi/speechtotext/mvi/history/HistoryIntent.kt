package com.digitalkoi.speechtotext.mvi.history

import com.digitalkoi.speechtotext.mvi.mvibase.MviIntent

/**
 * @author Taras Zhupnyk (akka DigitalKoi) on 24/03/18.
 */

sealed class HistoryIntent : MviIntent {

  object InitialIntent : HistoryIntent()
  data class UpdateListIntent(val date: String) : HistoryIntent()
  data class ShowDataPickerIntent(val showDataPickerDialog: Boolean) : HistoryIntent()
}
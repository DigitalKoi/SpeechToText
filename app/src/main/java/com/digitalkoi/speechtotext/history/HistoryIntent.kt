package com.digitalkoi.speechtotext.history

import com.digitalkoi.speechtotext.mvi.mvibase.MviIntent

/**
 * @author Taras Zhupnyk (akka DigitalKoi) on 24/03/18.
 */

sealed class HistoryIntent : MviIntent {

  object InitialIntent : HistoryIntent()

  data class ShowDateIntent(val date: String) : HistoryIntent()
}
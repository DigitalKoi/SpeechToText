package com.digitalkoi.speechtotext.mvi.detail

import com.digitalkoi.speechtotext.data.file.CSVConversation
import com.digitalkoi.speechtotext.mvi.mvibase.MviIntent

/**
 * @author Taras Zhupnyk (akka DigitalKoi) on 10/04/18.
 */

sealed class DetailIntent : MviIntent {
  data class InitialIntent(val id: String) : DetailIntent()
  data class SaveIntent(val item: CSVConversation) : DetailIntent()
  data class DeleteIntent(val item: CSVConversation) : DetailIntent()
}
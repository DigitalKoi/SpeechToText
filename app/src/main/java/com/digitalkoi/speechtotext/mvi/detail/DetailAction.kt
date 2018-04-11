package com.digitalkoi.speechtotext.mvi.detail

import com.digitalkoi.speechtotext.data.file.CSVConversation
import com.digitalkoi.speechtotext.mvi.MviAction

/**
 * @author Taras Zhupnyk (akka DigitalKoi) on 11/04/18.
 */

sealed class DetailAction : MviAction {
  data class PopulatelAction(val id: String) : DetailAction()
  data class EditAction(val item: CSVConversation) : DetailAction()
  data class DeleteAction(val item: CSVConversation) : DetailAction()
}
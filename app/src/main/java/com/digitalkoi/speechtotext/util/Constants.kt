package com.digitalkoi.speechtotext.util

/**
 * @author Taras Zhupnyk (akka DigitalKoi) on 10/03/18.
 */

class Constants {

  companion object {
    const val APP_SETTINGS = "appsettings"
    const val FONT_SIZE_SHARED_PREFERENCES = "font_size"

    const val DEFAULT_FONT_SIZE = 20f
    const val MINIMUM_FONT_SIZE = 8f
    const val MAXIMUM_FONT_SIZE = 72f

    const val REC_STATUS_STOP = 0
    const val REC_STATUS_PLAY = 1
    const val REC_STATUS_PAUSE = 2
    const val REC_STATUS_ROTATION = 3

    const val ARGUMENT_ITEM_ID = "patient_id"
    const val PATIENT_TEXT = "patient_text"
  }
}
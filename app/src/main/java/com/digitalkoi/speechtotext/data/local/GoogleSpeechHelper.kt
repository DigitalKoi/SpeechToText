package com.digitalkoi.speechtotext.data.local

import android.os.Bundle
import android.speech.RecognitionListener

/**
 * @author Taras Zhupnyk (akka DigitalKoi) on 17/03/18.
 */

class GoogleSpeechHelper : RecognitionListener {

  init {

  }

  override fun onReadyForSpeech(params: Bundle?) {
    TODO(
        "not implemented"
    ) //To change body of created functions use File | Settings | File Templates.
  }

  override fun onRmsChanged(rmsdB: Float) {
    TODO(
        "not implemented"
    ) //To change body of created functions use File | Settings | File Templates.
  }

  override fun onBufferReceived(buffer: ByteArray?) {
    TODO(
        "not implemented"
    ) //To change body of created functions use File | Settings | File Templates.
  }

  override fun onPartialResults(partialResults: Bundle?) {
    TODO(
        "not implemented"
    ) //To change body of created functions use File | Settings | File Templates.
  }

  override fun onEvent(
    eventType: Int,
    params: Bundle?
  ) {
    TODO(
        "not implemented"
    ) //To change body of created functions use File | Settings | File Templates.
  }

  override fun onBeginningOfSpeech() {
    TODO(
        "not implemented"
    ) //To change body of created functions use File | Settings | File Templates.
  }

  override fun onEndOfSpeech() {
    TODO(
        "not implemented"
    ) //To change body of created functions use File | Settings | File Templates.
  }

  override fun onError(error: Int) {
    TODO(
        "not implemented"
    ) //To change body of created functions use File | Settings | File Templates.
  }

  override fun onResults(results: Bundle?) {
    TODO(
        "not implemented"
    ) //To change body of created functions use File | Settings | File Templates.
  }

}
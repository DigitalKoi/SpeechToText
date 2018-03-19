package com.digitalkoi.speechtotext.data.remote

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import com.digitalkoi.speechtotext.util.SingletonHolderDoubleArg
import com.digitalkoi.speechtotext.util.schedulers.BaseSchedulerProvider
import io.reactivex.Observable

/**
 * @author Taras Zhupnyk (akka DigitalKoi) on 17/03/18.
 */

class SpeechRemoteDataSource(
  context: Context,
  schedulerProvider: BaseSchedulerProvider
) : SpeechInput, RecognitionListener {

  private val speech: SpeechRecognizer by lazy { SpeechRecognizer.createSpeechRecognizer(context) }
  private var listener: ((String) -> Unit)? = null

  override fun startListener(): Observable<((String) -> Unit)?>? {
    val recognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
    recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "en")
    speech.setRecognitionListener(this)
    speech.startListening(recognizerIntent)
    return Observable.create { listener }
  }

  override fun stopListener() {
    speech.stopListening()
  }

  override fun onResults(results: Bundle?) {
    listener?.invoke(results?.getStringArray(SpeechRecognizer.RESULTS_RECOGNITION)?.joinToString(" ") ?: "")
    startListener()
  }

  override fun onReadyForSpeech(params: Bundle?) {
  }

  override fun onRmsChanged(rmsdB: Float) {
  }

  override fun onBufferReceived(buffer: ByteArray?) {
  }

  override fun onPartialResults(partialResults: Bundle?) {
  }

  override fun onEvent(eventType: Int, params: Bundle?) {
  }

  override fun onBeginningOfSpeech() {
  }

  override fun onEndOfSpeech() {
  }

  override fun onError(error: Int) {
  }

    companion object : SingletonHolderDoubleArg<SpeechRemoteDataSource, Context, BaseSchedulerProvider>(
        ::SpeechRemoteDataSource
  )
}
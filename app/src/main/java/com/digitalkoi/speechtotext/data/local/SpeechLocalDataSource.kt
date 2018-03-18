package com.digitalkoi.speechtotext.data.local

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import com.digitalkoi.speechtotext.util.SingletonHolderDoubleArg
import com.digitalkoi.speechtotext.data.SpeechDataSource
import com.digitalkoi.speechtotext.util.schedulers.BaseSchedulerProvider
import com.digitalkoi.speechtotext.util.Constants
import io.reactivex.Completable
import io.reactivex.Single

/**
 * @author Taras Zhupnyk (akka DigitalKoi) on 09/03/18.
 */

class SpeechLocalDataSource private constructor(
  context: Context,
  schedulerProvider: BaseSchedulerProvider
) : SpeechDataSource, RecognitionListener {
  private val speechToText: SpeechRecognizer by lazy { SpeechRecognizer.createSpeechRecognizer(context) }
  private val recognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
  private var sharedPreferences: SharedPreferences? = null
  private var fontSize: Float
  private val editor: SharedPreferences.Editor

  init {
    sharedPreferences = context.getSharedPreferences(Constants.APP_SETTINGS, Context.MODE_PRIVATE)
    fontSize = sharedPreferences!!.getFloat(
        Constants.FONT_SIZE_SHARED_PREFERENCES, Constants.DEFAULT_FONT_SIZE
    )
    editor = sharedPreferences!!.edit()
  }

  override fun getSpeechToText(): Single<String> {
      recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "en")
//      recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
      speechToText.startListening(recognizerIntent)
      return Single.just("hello")
  }

  override fun saveSpeech(id: String, text: String): Completable {
   return Completable.complete()
  }

  override fun changeSpeechResource(): Completable {
    return Completable.complete()
  }


  override fun zoomIn(): Single<Float> {
    if (fontSize <= Constants.MAXIMUM_FONT_SIZE) {
      fontSize += 2f
      editor.putFloat(Constants.FONT_SIZE_SHARED_PREFERENCES, fontSize)
      editor.apply()
    }
    return Single.just(fontSize)
  }

  override fun zoomOut(): Single<Float> {
    if (fontSize >= Constants.MINIMUM_FONT_SIZE) {
      fontSize -= 2f
      editor.putFloat(Constants.FONT_SIZE_SHARED_PREFERENCES, fontSize)
      editor.apply()
    }
    return Single.just(fontSize)
  }

  override fun getTextSize(): Single<Float> {
    return Single.just(fontSize)
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

  override fun onBeginningOfSpeech() {}

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

  companion object : SingletonHolderDoubleArg<SpeechLocalDataSource, Context, BaseSchedulerProvider>(
      { context, schedulerProvider -> SpeechLocalDataSource(context, schedulerProvider) }
  )
}
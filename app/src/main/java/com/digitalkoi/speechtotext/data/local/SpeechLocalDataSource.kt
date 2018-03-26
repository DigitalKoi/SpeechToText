package com.digitalkoi.speechtotext.data.local

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import com.digitalkoi.speechtotext.util.SingletonHolderDoubleArg
import com.digitalkoi.speechtotext.data.SpeechDataSource
import com.digitalkoi.speechtotext.data.file.CSVConversation
import com.digitalkoi.speechtotext.data.file.FileCSVHelper
import com.digitalkoi.speechtotext.data.remote.SpeechInput
import com.digitalkoi.speechtotext.util.schedulers.BaseSchedulerProvider
import com.digitalkoi.speechtotext.util.Constants
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

/**
 * @author Taras Zhupnyk (akka DigitalKoi) on 09/03/18.
 */

class SpeechLocalDataSource private constructor(
  context: Context,
  schedulerProvider: BaseSchedulerProvider
) : SpeechDataSource {

  private val speechToText: SpeechRecognizer by lazy { SpeechRecognizer.createSpeechRecognizer(context) }

  private val recognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
  private var sharedPreferences: SharedPreferences? = null
  private var fontSize: Float
  private val editor: SharedPreferences.Editor
  private lateinit var speechInput: SpeechInput
  init {
    sharedPreferences = context.getSharedPreferences(Constants.APP_SETTINGS, Context.MODE_PRIVATE)
    fontSize = sharedPreferences!!.getFloat(
        Constants.FONT_SIZE_SHARED_PREFERENCES, Constants.DEFAULT_FONT_SIZE
    )
    editor = sharedPreferences!!.edit()
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

  override fun getListFromFile(date: String): Single<List<CSVConversation>> {
    return Single
        .just(FileCSVHelper.readFile())
  }

  companion object : SingletonHolderDoubleArg<SpeechLocalDataSource, Context, BaseSchedulerProvider>(
      { context, schedulerProvider -> SpeechLocalDataSource(context, schedulerProvider) }
  )
}
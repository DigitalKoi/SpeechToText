package com.digitalkoi.speechtotext.data.local

import android.content.Context
import android.content.SharedPreferences
import com.digitalkoi.speechtotext.data.SpeechDataSource
import com.digitalkoi.speechtotext.data.file.CSVConversation
import com.digitalkoi.speechtotext.data.file.FileCSVHelper
import com.digitalkoi.speechtotext.util.Constants
import com.digitalkoi.speechtotext.util.SingletonHolderSingleArg
import io.reactivex.Completable
import io.reactivex.Single

/**
 * @author Taras Zhupnyk (akka DigitalKoi) on 09/03/18.
 */

class SpeechLocalDataSource private constructor(context: Context) : SpeechDataSource {

  private var sharedPreferences: SharedPreferences? = null
  private var fontSize: Float
  private val editor: SharedPreferences.Editor
  private val fileCSVHelper : FileCSVHelper by lazy { FileCSVHelper() }

  init {
    sharedPreferences = context.getSharedPreferences(Constants.APP_SETTINGS, Context.MODE_PRIVATE)
    fontSize = sharedPreferences!!.getFloat(
        Constants.FONT_SIZE_SHARED_PREFERENCES, Constants.DEFAULT_FONT_SIZE
    )
    editor = sharedPreferences!!.edit()
  }

  override fun saveSpeech(patientId: String, conversation: String): Completable {
    fileCSVHelper.writeItemInFile(patientId, conversation)
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
    return Single.just(fileCSVHelper.readAllFile(date))
  }

  override fun getItemFromFile(id: String): Single<CSVConversation> {
    return Single.just(fileCSVHelper.gettingItem(id))
  }

  override fun deleteItemFromFile(item: CSVConversation): Completable {
    fileCSVHelper.deletingItem(item)
    return Completable.complete()
  }

  override fun saveItemToFile(item: CSVConversation): Completable {
    fileCSVHelper.savingItem(item)
    return Completable.complete()
  }

  companion object : SingletonHolderSingleArg<SpeechLocalDataSource, Context>(
      ::SpeechLocalDataSource
  )
}
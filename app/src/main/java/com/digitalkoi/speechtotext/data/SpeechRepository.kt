package com.digitalkoi.speechtotext.data

import com.digitalkoi.speechtotext.data.file.CSVConversation
import com.digitalkoi.speechtotext.data.file.FileCSVHelper
import com.digitalkoi.speechtotext.data.remote.SpeechInput
import com.digitalkoi.speechtotext.util.SingletonHolderDoubleArg
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit.SECONDS

/**
 * @author Taras Zhupnyk (akka DigitalKoi) on 09/03/18.
 */

open class SpeechRepository private constructor(
  private val speechRemoteDataSource: SpeechInput,
  private val speechLocalDataSource: SpeechDataSource
) : SpeechDataSource, SpeechInput {

  private var stopListener = true

  override fun startListener(): Flowable<String> {
    stopListener = false
    return Flowable.defer {
      Flowable.defer { speechRemoteDataSource.startListener().debounce(1, SECONDS) }.retry()
    }
        .debounce(1, SECONDS)
        .repeatUntil { stopListener }
        .subscribeOn(AndroidSchedulers.mainThread())
  }

  override fun stopListener(): Completable {
    stopListener = true
    return speechRemoteDataSource.stopListener()
  }

  override fun saveSpeech(
    patientId: String,
    conversation: String
  ): Completable {
    FileCSVHelper.writeFile(patientId, conversation)
    return Completable.complete()
  }

  override fun changeSpeechResource(): Completable {
    return Completable.complete()
  }

  override fun getTextSize(): Single<Float> {
    return speechLocalDataSource.getTextSize()
  }

  override fun zoomIn(): Single<Float> {
    return speechLocalDataSource.zoomIn()
  }

  override fun zoomOut(): Single<Float> {
    return speechLocalDataSource.zoomOut()
  }

  override fun getListFromFile(date: String): Single<List<CSVConversation>> {
    return speechLocalDataSource.getListFromFile(date)
  }

  companion object : SingletonHolderDoubleArg<SpeechRepository, SpeechInput, SpeechDataSource>(
      ::SpeechRepository
  )
}

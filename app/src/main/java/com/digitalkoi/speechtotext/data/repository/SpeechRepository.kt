package com.digitalkoi.speechtotext.data.repository

import com.digitalkoi.speechtotext.data.SpeechDataSource
import com.digitalkoi.speechtotext.data.file.CSVConversation
import com.digitalkoi.speechtotext.data.file.FileCSVHelper
import com.digitalkoi.speechtotext.data.remote.SpeechInput
import com.digitalkoi.speechtotext.util.SingletonHolderDoubleArg
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import java.util.concurrent.TimeUnit.SECONDS

/**
 * @author Taras Zhupnyk (akka DigitalKoi) on 09/03/18.
 */

open class SpeechRepository private constructor(
  private val speechRemoteDataSource: SpeechInput,
  private val speechLocalDataSource: SpeechDataSource
) : SpeechDataSource, SpeechInput {

  override fun startListener(): Flowable<String> {
    return speechRemoteDataSource.startListener().repeat()
  }

  override fun stopListener(): Completable {
    return speechRemoteDataSource.stopListener()
  }

  override fun saveSpeech(patientId: String, conversation: String): Completable {
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

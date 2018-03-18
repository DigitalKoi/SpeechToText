package com.digitalkoi.speechtotext.data

import com.digitalkoi.speechtotext.util.SingletonHolderSingleArg
import io.reactivex.Completable
import io.reactivex.Single

/**
 * @author Taras Zhupnyk (akka DigitalKoi) on 09/03/18.
 */

open class SpeechRepository private constructor(
//        private val speechRemoteDataSource: SpeechDataSource,
  private val speechLocalDataSource: SpeechDataSource
) : SpeechDataSource {

  override fun getSpeechToText(): Single<String> {

    return Single.just("hello")
  }

  override fun saveSpeech(id: String, text: String): Completable {
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

  companion object : SingletonHolderSingleArg<SpeechRepository, SpeechDataSource>(
      ::SpeechRepository
  )
}
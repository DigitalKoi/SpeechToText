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
  override fun getSpeech(): Single<String> {
    TODO(
        "not implemented"
    ) //To change body of created functions use File | Settings | File Templates.
  }

  override fun saveSpeech(
    id: String,
    text: String
  ): Completable {
    TODO(
        "not implemented"
    ) //To change body of created functions use File | Settings | File Templates.
  }

  override fun changeSpeechResource(): Completable {
    TODO(
        "not implemented"
    ) //To change body of created functions use File | Settings | File Templates.
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
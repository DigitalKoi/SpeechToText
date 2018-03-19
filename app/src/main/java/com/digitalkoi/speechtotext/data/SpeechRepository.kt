package com.digitalkoi.speechtotext.data

import com.digitalkoi.speechtotext.data.remote.SpeechInput
import com.digitalkoi.speechtotext.util.SingletonHolderDoubleArg
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

/**
 * @author Taras Zhupnyk (akka DigitalKoi) on 09/03/18.
 */

open class SpeechRepository private constructor(
  private val speechRemoteDataSource: SpeechInput,
  private val speechLocalDataSource: SpeechDataSource
) : SpeechDataSource, SpeechInput {


  override fun startListener(): Observable<((String) -> Unit)?>? {
    return  speechRemoteDataSource.startListener()
  }

  override fun stopListener() {
    speechRemoteDataSource.stopListener()
  }

  override fun saveSpeech(id: String, text: String): Completable {
    stopListener()
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

  companion object : SingletonHolderDoubleArg<SpeechRepository, SpeechInput, SpeechDataSource>(
      ::SpeechRepository
  )
}
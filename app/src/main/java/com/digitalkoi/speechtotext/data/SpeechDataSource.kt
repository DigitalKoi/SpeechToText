package com.digitalkoi.speechtotext.data

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

/**
 * @author Taras Zhupnyk (akka DigitalKoi) on 09/03/18.
 */

interface SpeechDataSource {

  fun saveSpeech(id: String, text: String): Completable

  fun changeSpeechResource(): Completable

  fun zoomIn(): Single<Float>

  fun zoomOut(): Single<Float>

  fun getTextSize(): Single<Float>
}
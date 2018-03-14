package com.digitalkoi.speechtotext.data

import io.reactivex.Single

/**
 * @author Taras Zhupnyk (akka DigitalKoi) on 09/03/18.
 */

interface SpeechDataSource {

  fun getSpeech(): Single<String>

  fun changeSpeechResource()

  fun zoomIn(): Single<Float>

  fun zoomOut(): Single<Float>

  fun getTextSize(): Single<Float>
}
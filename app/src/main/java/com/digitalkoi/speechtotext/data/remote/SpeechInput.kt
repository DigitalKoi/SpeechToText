package com.digitalkoi.speechtotext.data.remote

import io.reactivex.Completable
import io.reactivex.Flowable

/**
 * @author Taras Zhupnyk (akka DigitalKoi) on 18/03/18.
 */

interface SpeechInput {

  fun startListener(): Flowable<String>

  fun stopListener() : Completable

}
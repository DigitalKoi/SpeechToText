package com.digitalkoi.speechtotext.data.remote

import io.reactivex.Observable

/**
 * @author Taras Zhupnyk (akka DigitalKoi) on 18/03/18.
 */

interface SpeechInput {

  fun startListener(): Observable<((String) -> Unit)?>?

  fun stopListener()
}
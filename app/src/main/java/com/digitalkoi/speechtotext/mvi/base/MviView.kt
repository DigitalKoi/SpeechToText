package com.digitalkoi.speechtotext.mvi.base

import io.reactivex.Observable

/**
 * @author Taras Zhupnyk (akka DigitalKoi) on 09/03/18.
 */

interface MviView<I : MviIntent, in S : MviViewState> {

  fun intents(): Observable<I>

  fun render(state: S)
}
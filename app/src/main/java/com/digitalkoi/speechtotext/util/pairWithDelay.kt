package com.digitalkoi.speechtotext.util

import io.reactivex.Observable
import java.util.concurrent.TimeUnit.SECONDS

/**
 * @author Taras Zhupnyk (akka DigitalKoi) on 11/04/18.
 */

fun <T> pairWithDelay(immediate: T, delayed: T): Observable<T> {
 return Observable.timer(2, SECONDS)
     .map { delayed }
     .startWith(immediate)
}
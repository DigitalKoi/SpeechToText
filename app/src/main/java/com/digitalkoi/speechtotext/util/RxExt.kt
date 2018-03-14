package com.digitalkoi.speechtotext.util

import io.reactivex.Observable
import io.reactivex.annotations.CheckReturnValue
import io.reactivex.annotations.SchedulerSupport

/**
 * @author Taras Zhupnyk (akka DigitalKoi) on 11/03/18.
 */

@CheckReturnValue
@SchedulerSupport(SchedulerSupport.NONE)
fun <T : Any, U : Any> Observable<T>.notOfType(clazz: Class<U>): Observable<T> {
  checkNotNull(clazz) { "clazz is null" }
  return filter { !clazz.isInstance(it) }
}
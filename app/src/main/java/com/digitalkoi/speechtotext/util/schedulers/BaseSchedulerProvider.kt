package com.digitalkoi.speechtotext.util.schedulers

import io.reactivex.Scheduler

/**
 * @author Taras Zhupnyk (akka DigitalKoi) on 09/03/18.
 */

interface BaseSchedulerProvider {

  fun computation(): Scheduler

  fun io(): Scheduler

  fun ui(): Scheduler
}
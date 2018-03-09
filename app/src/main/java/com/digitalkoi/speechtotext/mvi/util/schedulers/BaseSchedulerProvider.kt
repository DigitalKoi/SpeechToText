package com.digitalkoi.speechtotext.mvi.util.schedulers

import io.reactivex.Scheduler

/**
 * @author Taras Zhupnyk (akka DigitalKoi) on 09/03/18.
 */

interface BaseSchedulerProvider {

    fun computatuin(): Scheduler

    fun io(): Scheduler

    fun ui(): Scheduler
}
package com.digitalkoi.speechtotext.mvi.util.schedulers

import com.digitalkoi.speechtotext.mvi.util.schedulers.BaseSchedulerProvider
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * @author Taras Zhupnyk (akka DigitalKoi) on 09/03/18.
 */

object SchedulerProvider : BaseSchedulerProvider {

    override fun computatuin(): Scheduler = Schedulers.computation()

    override fun io(): Scheduler = Schedulers.io()

    override fun ui(): Scheduler = AndroidSchedulers.mainThread()
}
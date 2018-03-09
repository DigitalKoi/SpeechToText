package com.digitalkoi.speechtotext.mvi.data.local

import android.content.Context
import com.digitalkoi.speechtotext.mvi.data.SingletonHolderDoubleArg
import com.digitalkoi.speechtotext.mvi.data.SpeechDataSource
import com.digitalkoi.speechtotext.mvi.util.schedulers.BaseSchedulerProvider
import io.reactivex.Single

/**
 * @author Taras Zhupnyk (akka DigitalKoi) on 09/03/18.
 */

class SpeechLocalDataSource private constructor(
        context: Context,
        schedulerProvider: BaseSchedulerProvider
) : SpeechDataSource {

    override fun getSpeech(): Single<String> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun refreshSpeech() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    companion object : SingletonHolderDoubleArg<SpeechLocalDataSource, Context, BaseSchedulerProvider>(
            ::SpeechLocalDataSource
    )
}
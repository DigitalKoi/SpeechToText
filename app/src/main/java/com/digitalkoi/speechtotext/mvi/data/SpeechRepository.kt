package com.digitalkoi.speechtotext.mvi.data

import com.digitalkoi.speechtotext.mvi.util.SingletonHolderSingleArg
import io.reactivex.Single

/**
 * @author Taras Zhupnyk (akka DigitalKoi) on 09/03/18.
 */

open class SpeechRepository private constructor(
        private val speechRemoteDataSource: SpeechDataSource,
        private val speechLocalDataSource: SpeechDataSource
) : SpeechDataSource {


    override fun getSpeech(): Single<String> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun refreshSpeech() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    companion object : SingletonHolderDoubleArg<SpeechRepository, SpeechDataSource, SpeechDataSource>(
            ::SpeechRepository
    )
}
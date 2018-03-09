package com.digitalkoi.speechtotext.mvi.speech

import android.arch.lifecycle.ViewModel
import com.digitalkoi.speechtotext.mvi.mvibase.MviViewModel
import io.reactivex.Observable

/**
 * @author Taras Zhupnyk (akka DigitalKoi) on 09/03/18.
 */

class SpeechViewModel(
        private val actionProcessorHolder: SpeechActionProcessorHolder
) : ViewModel(), MviViewModel<SpeechIntent, SpeechViewState>  {

    override fun processIntents(intents: Observable<SpeechIntent>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun states(): Observable<SpeechViewState> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
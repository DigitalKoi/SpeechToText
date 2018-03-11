package com.digitalkoi.speechtotext.data

import io.reactivex.Single

/**
 * @author Taras Zhupnyk (akka DigitalKoi) on 09/03/18.
 */

interface SpeechDataSource {

    fun getSpeech(forceUpdate: Boolean): Single<String> {
        if (forceUpdate) refreshSpeech()
        return getSpeech()
    }

    fun getSpeech(): Single<String>

    fun refreshSpeech()

    fun zoomIn(): Single<Float>

    fun zoomOut(): Single<Float>
}
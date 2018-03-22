package com.digitalkoi.speechtotext.data.remote

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.EventLog.Event
import com.digitalkoi.speechtotext.data.local.SpeechLocalDataSource
import com.digitalkoi.speechtotext.util.SingletonHolderDoubleArg
import com.digitalkoi.speechtotext.util.schedulers.BaseSchedulerProvider
import io.reactivex.BackpressureStrategy
import io.reactivex.BackpressureStrategy.BUFFER
import io.reactivex.Flowable
import io.reactivex.Flowable.create
import io.reactivex.FlowableEmitter
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Cancellable

/**
 * @author Taras Zhupnyk (akka DigitalKoi) on 17/03/18.
 */

class SpeechRemoteDataSource(
  context: Context,
  schedulerProvider: BaseSchedulerProvider
) : SpeechInput {

  private val speech: SpeechRecognizer by lazy { SpeechRecognizer.createSpeechRecognizer(context) }
  private var listener: RecognitionListener? = null

  override fun startListener(): Flowable<String> {
    val recognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
    recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "en")
    speech.startListening(recognizerIntent)

    return create({ emitter: FlowableEmitter<String> ->

            listener = object : RecognitionListener {
              override fun onReadyForSpeech(params: Bundle?) {}

              override fun onRmsChanged(rmsdB: Float) {}

              override fun onBufferReceived(buffer: ByteArray?) {}

              override fun onPartialResults(partialResults: Bundle?) {}

              override fun onEvent(eventType: Int, params: Bundle?) {}

              override fun onBeginningOfSpeech() {}

              override fun onEndOfSpeech() {}

              override fun onError(error: Int) {}

              override fun onResults(results: Bundle?) {
                val matches = results!!.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)[0] ?: ""
                emitter.onNext(matches)
                startListener()
              }
            }
            speech.setRecognitionListener(listener)
//            emitter.setCancellable { speech.cancel() }

    }, BUFFER)
        .subscribeOn(AndroidSchedulers.mainThread())
  }

  override fun stopListener() {
    speech.stopListening()
  }

//    listener?.invoke(
//        results?.getStringArray(SpeechRecognizer.RESULTS_RECOGNITION)?.joinToString(" ") ?: ""
//    )

  companion object : SingletonHolderDoubleArg<SpeechRemoteDataSource, Context, BaseSchedulerProvider>(
      ::SpeechRemoteDataSource
  )
}

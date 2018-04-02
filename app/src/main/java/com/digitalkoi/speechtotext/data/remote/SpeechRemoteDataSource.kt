package com.digitalkoi.speechtotext.data.remote

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import com.digitalkoi.speechtotext.util.SingletonHolderDoubleArg
import com.digitalkoi.speechtotext.util.schedulers.BaseSchedulerProvider
import io.reactivex.BackpressureStrategy.BUFFER
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Flowable.defer
import io.reactivex.FlowableEmitter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import java.util.concurrent.TimeUnit.MILLISECONDS
import java.util.concurrent.TimeUnit.SECONDS

/**
 * @author Taras Zhupnyk (akka DigitalKoi) on 17/03/18.
 */

class SpeechRemoteDataSource(
  context: Context,
  schedulerProvider: BaseSchedulerProvider
) : SpeechInput {

  private val speech: SpeechRecognizer by lazy { SpeechRecognizer.createSpeechRecognizer(context) }
  private var listener: RecognitionListener? = null
  private val disposable = CompositeDisposable()

  val flowable = Flowable.create({ emitter: FlowableEmitter<String> ->

    listener = object : RecognitionListener {
      override fun onReadyForSpeech(params: Bundle?) {
        Log.i("RemoteDate", "onReadyForSpeech")
      }

      override fun onRmsChanged(rmsdB: Float) {
        Log.i("RemoteDate", "onRmsChanged")
      }

      override fun onBufferReceived(buffer: ByteArray?) {
        Log.i("RemoteDate", "onBufferReceived")
      }

      override fun onPartialResults(partialResults: Bundle?) {
        Log.i("RemoteDate", "onPartialResults")
      }

      override fun onEvent(eventType: Int, params: Bundle?) {
        Log.i("RemoteDate", "onEvent")

      }

      override fun onBeginningOfSpeech() {
        Log.i("RemoteDate", "onBeginningOfSpeech")
      }

      override fun onEndOfSpeech() {
        Log.i("RemoteDate", "onEndOfSpeech")
      }

      override fun onError(error: Int) {
        Log.e("RemoteDate", "onError: $error")
        emitter.onError(Throwable(error.toString()))
      }

      override fun onResults(results: Bundle?) {
        val matches = results!!.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)[0] ?: ""
        emitter.onNext(matches)
      }
    }
    speech.setRecognitionListener(listener)
    emitter.setCancellable { speech.cancel() }

  }, BUFFER).subscribeOn(AndroidSchedulers.mainThread())

  override fun startListener(): Flowable<String> {
    val recognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
    recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "en")
    speech.startListening(recognizerIntent)

    return defer { flowable }.debounce(500, MILLISECONDS).retry()
  }

  override fun stopListener(): Completable {
    speech.stopListening()
    return Completable.complete()
  }

  private fun restartListener() {
    speech.stopListening()
    flowable.blockingLast()
    startListener()
  }

  companion object : SingletonHolderDoubleArg<SpeechRemoteDataSource, Context, BaseSchedulerProvider>(
      ::SpeechRemoteDataSource
  )
}

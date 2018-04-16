package com.digitalkoi.speechtotext.data.remote

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import com.digitalkoi.speechtotext.util.SingletonHolderSingleArg
import io.reactivex.BackpressureStrategy.BUFFER
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Flowable.defer
import io.reactivex.FlowableEmitter

/**
 * @author Taras Zhupnyk (akka DigitalKoi) on 17/03/18.
 */

class SpeechRemoteDataSource(context: Context) : SpeechInput {

  private var speech: SpeechRecognizer? = null
  private var listener: RecognitionListener? = null
  val recognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)


  private var flowable = Flowable.create({ emitter: FlowableEmitter<String> ->
    run {
      listener = object : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle?) {
          Log.i("RemoteDate", "onReadyForSpeech")
        }

        override fun onRmsChanged(rmsdB: Float) {
//          Log.d("onRmsChanged", rmsdB.toString())
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
          if (error == 8) stopSpeech()
          emitter.onError(Throwable(error.toString()))
        }

        override fun onResults(results: Bundle?) {
          val result = results!!.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)[0] ?: ""
          Log.i("RemoteDate", "onResults: $result")
          emitter.onNext(result)
        }
      }
      speech = SpeechRecognizer.createSpeechRecognizer(context)
      speech?.setRecognitionListener(listener)
      speech?.startListening(recognizerIntent)
      emitter.setCancellable { speech?.cancel() }
    }
    }, BUFFER)

  override fun startListener(): Flowable<String> {
    recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "en")
    return defer { flowable.doOnEach { speech?.startListening(recognizerIntent) } }
        .retry()
  }

  override fun stopListener(): Completable {
    stopSpeech()
    return Completable.complete()
  }

  private fun stopSpeech() {
    speech?.stopListening()
    speech?.cancel()
    speech?.destroy()
    speech = null
  }

  companion object : SingletonHolderSingleArg<SpeechRemoteDataSource, Context>(
      ::SpeechRemoteDataSource
  )
}

package com.digitalkoi.speechtotext.util

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.content.Context
import com.digitalkoi.speechtotext.speech.SpeechActionProcessorHolder
import com.digitalkoi.speechtotext.speech.SpeechViewModel
import com.digitalkoi.speechtotext.di.Injection

/**
 * @author Taras Zhupnyk (akka DigitalKoi) on 09/03/18.
 */

class SpeechViewModelFactory private constructor(
  private val applicationContext: Context
) : ViewModelProvider.Factory {

  override fun <T : ViewModel?> create(modelClass: Class<T>): T {

    if (modelClass == SpeechViewModel::class.java) {
      return SpeechViewModel(
          SpeechActionProcessorHolder(
              Injection.provideTasksRepository(applicationContext),
              Injection.provideSchedulerProvider()
          )
      ) as T
    }
    throw IllegalAccessException("unknown model class $modelClass")
  }

  companion object : SingletonHolderSingleArg<SpeechViewModelFactory, Context>(
      ::SpeechViewModelFactory
  )
}
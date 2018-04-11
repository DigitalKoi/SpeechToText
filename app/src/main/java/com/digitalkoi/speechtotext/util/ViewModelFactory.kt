package com.digitalkoi.speechtotext.util

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.content.Context
import com.digitalkoi.speechtotext.mvi.speech.SpeechActionProcessorHolder
import com.digitalkoi.speechtotext.mvi.speech.SpeechViewModel
import com.digitalkoi.speechtotext.di.Injection
import com.digitalkoi.speechtotext.mvi.detail.DetailActionProcessorHolder
import com.digitalkoi.speechtotext.mvi.detail.DetailViewModel
import com.digitalkoi.speechtotext.mvi.history.HistoryActionProcessorHolder
import com.digitalkoi.speechtotext.mvi.history.HistoryViewModel

/**
 * @author Taras Zhupnyk (akka DigitalKoi) on 09/03/18.
 */

class ViewModelFactory private constructor(
  private val applicationContext: Context
) : ViewModelProvider.Factory {

  override fun <T : ViewModel?> create(modelClass: Class<T>): T {

    if (modelClass == SpeechViewModel::class.java) {
      return SpeechViewModel(
          SpeechActionProcessorHolder(
              Injection.provideRepository(applicationContext),
              Injection.provideSchedulerProvider()
          )
      ) as T
    }

    if (modelClass == HistoryViewModel::class.java) {
      return HistoryViewModel(
          HistoryActionProcessorHolder(
              Injection.provideRepository(applicationContext),
              Injection.provideSchedulerProvider()
          )
      ) as T
    }

    if (modelClass == DetailViewModel::class.java) {
      return DetailViewModel(
          DetailActionProcessorHolder(
              Injection.provideRepository(applicationContext),
              Injection.provideSchedulerProvider()
          )
      ) as T
    }

    throw IllegalAccessException("unknown model class $modelClass")
  }

  companion object : SingletonHolderSingleArg<ViewModelFactory, Context>(
      ::ViewModelFactory
  )
}
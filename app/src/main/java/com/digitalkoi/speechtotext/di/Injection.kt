package com.digitalkoi.speechtotext.di

import android.content.Context
import com.digitalkoi.speechtotext.data.local.SpeechLocalDataSource
import com.digitalkoi.speechtotext.data.repository.SpeechRepository
import com.digitalkoi.speechtotext.data.remote.SpeechRemoteDataSource
import com.digitalkoi.speechtotext.util.schedulers.BaseSchedulerProvider
import com.digitalkoi.speechtotext.util.schedulers.SchedulerProvider

/**
 * Enables injection of mock implementations for
 * [SpeechLocalDataSource] at compile time. This is useful for testing, since it allows us to use
 * a fake instance of the class to isolate the dependencies and run a test hermetically.
 */

object Injection {

  fun provideRepository(context: Context): SpeechRepository =
    SpeechRepository.getInstance(
        SpeechRemoteDataSource.getInstance(context, provideSchedulerProvider()),
        SpeechLocalDataSource.getInstance(context, provideSchedulerProvider()))

  fun provideSchedulerProvider(): BaseSchedulerProvider = SchedulerProvider
  }


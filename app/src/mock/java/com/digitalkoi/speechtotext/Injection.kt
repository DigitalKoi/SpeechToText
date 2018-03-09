/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.digitalkoi.speechtotext

import android.content.Context
import com.digitalkoi.speechtotext.mvi.data.local.SpeechLocalDataSource
import com.digitalkoi.speechtotext.mvi.data.SpeechRepository
import com.digitalkoi.speechtotext.mvi.util.schedulers.BaseSchedulerProvider
import com.digitalkoi.speechtotext.mvi.util.schedulers.SchedulerProvider
import com.digitalkoi.speechtotext.data.FakeTasksRemoteDataSource

/**
 * Enables injection of mock implementations for
 * [TasksDataSource] at compile time. This is useful for testing, since it allows us to use
 * a fake instance of the class to isolate the dependencies and run a test hermetically.
 */
object Injection {
  fun provideTasksRepository(context: Context): SpeechRepository {
    return SpeechRepository.getInstance(
        FakeTasksRemoteDataSource,
        SpeechLocalDataSource.getInstance(context, provideSchedulerProvider())

    )
  }

  fun provideSchedulerProvider(): BaseSchedulerProvider = SchedulerProvider
}

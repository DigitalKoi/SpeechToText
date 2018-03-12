/*
 * Copyright 2016, The Android Open Source Project
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

package com.digitalkoi.speechtotext.data

import io.reactivex.Single

/**
 * Implementation of a remote data source with static access to the data for easy testing.
 */

object FakeTasksRemoteDataSource : SpeechDataSource {
  override fun getSpeech(): Single<String> {
   return Single.just("hello")
  }

  override fun changeSpeechResource() {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun getTextSize(): Single<Float> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun zoomIn(): Single<Float> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun zoomOut(): Single<Float> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }


}

package com.digitalkoi.speechtotext.data

import com.digitalkoi.speechtotext.data.file.CSVConversation
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

/**
 * @author Taras Zhupnyk (akka DigitalKoi) on 09/03/18.
 */

interface SpeechDataSource {

  fun saveSpeech(id: String, text: String): Completable

  fun changeSpeechResource(): Completable

  fun zoomIn(): Single<Float>

  fun zoomOut(): Single<Float>

  fun getTextSize(): Single<Float>

  fun getListFromFile(date: String): Single<List<CSVConversation>>

  fun getItemFromFile(id: String): Single<CSVConversation>

  fun deleteItemFromFile(item: CSVConversation): Completable

  fun saveItemToFile(item: CSVConversation): Completable
}
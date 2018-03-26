package com.digitalkoi.speechtotext.history

import android.arch.lifecycle.ViewModel
import com.digitalkoi.speechtotext.history.HistoryIntent.*
import com.digitalkoi.speechtotext.history.HistoryAction.*
import com.digitalkoi.speechtotext.history.HistoryResult.*
import com.digitalkoi.speechtotext.history.HistoryIntent.ShowDateIntent
import com.digitalkoi.speechtotext.history.HistoryResult.InitialResult.Failure
import com.digitalkoi.speechtotext.history.HistoryResult.InitialResult.Success
import com.digitalkoi.speechtotext.mvibase.MviViewModel
import com.digitalkoi.speechtotext.util.notOfType
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.PublishSubject

/**
 * @author Taras Zhupnyk (akka DigitalKoi) on 24/03/18.
 */

class HistoryViewModel(
  private val actionProcessorHolder: HistoryActionProcessorHolder
): ViewModel(), MviViewModel<HistoryIntent, HistoryViewState> {

  private val intentSubject: PublishSubject<HistoryIntent> = PublishSubject.create()
  private val statesObservable: Observable<HistoryViewState> = compose()

  override fun processIntents(intents: Observable<HistoryIntent>) {
    intents.subscribe(intentSubject)
  }

  override fun states(): Observable<HistoryViewState> = statesObservable

  private val intentFilter: ObservableTransformer<HistoryIntent, HistoryIntent>
  get() = ObservableTransformer { intents ->
    intents.publish { shared ->
      Observable.merge(
          shared.ofType(HistoryIntent.InitialIntent::class.java).take(1),
          shared.notOfType(HistoryIntent.InitialIntent::class.java)
      )
    }
  }

  private fun compose(): Observable<HistoryViewState> {
    return intentSubject
        .compose(intentFilter)
        .map { this.actionFromIntent(it) }
        .compose(actionProcessorHolder.actionProcessor)
        .scan(HistoryViewState.idle(), reducer)
        .distinctUntilChanged()
        .replay()
        .autoConnect(0)
  }

  private fun actionFromIntent(intent: HistoryIntent): HistoryAction {
    return when(intent) {
      is InitialIntent -> InitialAction
      is ShowDateIntent -> ShowDialogDateAction(intent.date)
    }
  }

  companion object {

    private val reducer = BiFunction { previousState: HistoryViewState, result: HistoryResult ->
      when (result) {
        is InitialResult -> when (result) {
          is Failure -> previousState.copy(error = result.error)
          is Success -> previousState.copy(fontSize = result.fontSize, dataList = result.dataList)
        }
        is ShowDateResult -> when (result) {
          is ShowDateResult.Failure -> previousState.copy(error = result.error)
          is ShowDateResult.Success -> previousState.copy(dataList = result.dataList)
        }
      }
    }
  }
}
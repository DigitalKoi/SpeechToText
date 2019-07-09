package com.digitalkoi.speechtotext.mvi.history

import android.arch.lifecycle.ViewModel
import com.digitalkoi.speechtotext.mvi.base.MviViewModel
import com.digitalkoi.speechtotext.mvi.history.HistoryAction.InitialAction
import com.digitalkoi.speechtotext.mvi.history.HistoryAction.ShowDataPickerAction
import com.digitalkoi.speechtotext.mvi.history.HistoryAction.UpdateListAction
import com.digitalkoi.speechtotext.mvi.history.HistoryIntent.InitialIntent
import com.digitalkoi.speechtotext.mvi.history.HistoryIntent.ShowDataPickerIntent
import com.digitalkoi.speechtotext.mvi.history.HistoryIntent.UpdateListIntent
import com.digitalkoi.speechtotext.mvi.history.HistoryResult.InitialResult
import com.digitalkoi.speechtotext.mvi.history.HistoryResult.InitialResult.Failure
import com.digitalkoi.speechtotext.mvi.history.HistoryResult.InitialResult.Success
import com.digitalkoi.speechtotext.mvi.history.HistoryResult.ShowDataPickerResult
import com.digitalkoi.speechtotext.mvi.history.HistoryResult.UpdateListResult
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
          shared.ofType(InitialIntent::class.java).take(1),
          shared.ofType(UpdateListIntent::class.java),
          shared.ofType(ShowDataPickerIntent::class.java)
      )
    }
  }

  private fun compose(): Observable<HistoryViewState> {
    return intentSubject
        .compose(intentFilter)
        .map(this::actionFromIntent)
        .compose(actionProcessorHolder.actionProcessor)
        .scan(HistoryViewState.idle(), reducer)
        .distinctUntilChanged()
        .replay()
        .autoConnect(0)
  }

  private fun actionFromIntent(intent: HistoryIntent): HistoryAction {
    return when(intent) {
      is InitialIntent -> InitialAction
      is UpdateListIntent -> UpdateListAction(intent.date)
      is ShowDataPickerIntent -> ShowDataPickerAction(intent.showDataPickerDialog)
    }
  }

  companion object {

    private val reducer = BiFunction { previousState: HistoryViewState, result: HistoryResult ->
      when (result) {
        is InitialResult -> when (result) {
          is Failure -> previousState.copy(error = result.error)
          is Success -> previousState.copy(fontSize = result.fontSize)
        }
        is UpdateListResult -> previousState.copy(dataList = result.dataList, date = result.date)
        is ShowDataPickerResult -> previousState.copy(showDateDialog = result.showDataPickerDialog)
      }
    }
  }
}
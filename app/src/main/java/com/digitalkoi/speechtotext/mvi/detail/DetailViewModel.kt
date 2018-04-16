package com.digitalkoi.speechtotext.mvi.detail

import android.arch.lifecycle.ViewModel
import com.digitalkoi.speechtotext.mvi.MviViewModel
import com.digitalkoi.speechtotext.mvi.detail.DetailAction.DeleteAction
import com.digitalkoi.speechtotext.mvi.detail.DetailAction.SaveAction
import com.digitalkoi.speechtotext.mvi.detail.DetailAction.PopulatelAction
import com.digitalkoi.speechtotext.mvi.detail.DetailIntent.DeleteIntent
import com.digitalkoi.speechtotext.mvi.detail.DetailIntent.SaveIntent
import com.digitalkoi.speechtotext.mvi.detail.DetailIntent.InitialIntent
import com.digitalkoi.speechtotext.mvi.detail.DetailResult.DeleteResult
import com.digitalkoi.speechtotext.mvi.detail.DetailResult.EditResult
import com.digitalkoi.speechtotext.mvi.detail.DetailResult.PopulateResult
import com.digitalkoi.speechtotext.util.notOfType
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.PublishSubject

/**
 * @author Taras Zhupnyk (akka DigitalKoi) on 11/04/18.
 */

class DetailViewModel(
  private val actionProcessorHolder: DetailActionProcessorHolder
) : ViewModel(), MviViewModel<DetailIntent, DetailViewState> {

  private val intentsSubject: PublishSubject<DetailIntent> = PublishSubject.create()
  private val statesObservable: Observable<DetailViewState> = compose()

  override fun processIntents(intents: Observable<DetailIntent>) {
    intents.subscribe(intentsSubject)
  }

  private val intentFilter: ObservableTransformer<DetailIntent, DetailIntent>
  get() = ObservableTransformer { intents ->
    intents.publish { shared ->
      Observable.merge(
          shared.ofType(DetailIntent.InitialIntent::class.java).take(1),
          shared.notOfType(DetailIntent.InitialIntent::class.java)
      )
    }
  }

  private fun compose(): Observable<DetailViewState> {
    return intentsSubject
        .compose(intentFilter)
        .map { this.actionFromIntent(it) }
        .compose(actionProcessorHolder.actionProcessor)
        .scan(DetailViewState.idle(), reducer)
        .distinctUntilChanged()
        .replay()
        .autoConnect(0)
  }

  private fun actionFromIntent(intent: DetailIntent): DetailAction {
    return when (intent) {
      is InitialIntent -> PopulatelAction(intent.id)
      is SaveIntent -> SaveAction(intent.item)
      is DeleteIntent -> DeleteAction(intent.item)
    }
  }

  override fun states(): Observable<DetailViewState> = statesObservable

  companion object {
    private var reducer = BiFunction { previousState: DetailViewState, result: DetailResult ->
      when (result) {
        is PopulateResult -> when (result) {
          is PopulateResult.InFlight -> previousState.copy(isLoading = true)
          is PopulateResult.Failure -> previousState.copy(isLoading = false, error = result.error)
          is PopulateResult.Success -> previousState.copy(
              isLoading = false, item = result.item)
        }
        is EditResult -> when (result) {
          is EditResult.InFlight -> previousState.copy(isLoading = true)
          is EditResult.Failure -> previousState.copy(isLoading = false, error = result.error)
          is EditResult.Success -> previousState.copy(isLoading = false)
        }
        is DeleteResult -> when (result) {
          is DeleteResult.InFlight -> previousState.copy(isLoading = true)
          is DeleteResult.Failure -> previousState.copy(isLoading = false, error = result.error)
          is DeleteResult.Success -> previousState.copy(isLoading = false)
        }
      }
    }
  }
}
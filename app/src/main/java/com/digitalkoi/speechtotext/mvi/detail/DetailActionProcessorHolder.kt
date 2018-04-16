package com.digitalkoi.speechtotext.mvi.detail

import com.digitalkoi.speechtotext.data.SpeechRepository
import com.digitalkoi.speechtotext.mvi.detail.DetailAction.DeleteAction
import com.digitalkoi.speechtotext.mvi.detail.DetailAction.SaveAction
import com.digitalkoi.speechtotext.mvi.detail.DetailAction.PopulatelAction
import com.digitalkoi.speechtotext.mvi.detail.DetailResult.DeleteResult
import com.digitalkoi.speechtotext.mvi.detail.DetailResult.EditResult
import com.digitalkoi.speechtotext.mvi.detail.DetailResult.PopulateResult
import com.digitalkoi.speechtotext.util.schedulers.BaseSchedulerProvider
import io.reactivex.Observable
import io.reactivex.ObservableTransformer

/**
 * @author Taras Zhupnyk (akka DigitalKoi) on 11/04/18.
 */

class DetailActionProcessorHolder(
  private val repository: SpeechRepository,
  private val schedulerProvider: BaseSchedulerProvider
) {

  private val populateProcessor =
    ObservableTransformer<PopulatelAction, PopulateResult> { actions ->
      actions.flatMap { action ->
        repository.getItemFromFile(action.id)
            .toObservable()
            .map(PopulateResult::Success)
            .cast(PopulateResult::class.java)
            .onErrorReturn(PopulateResult::Failure)
            .subscribeOn(schedulerProvider.ui())
            .observeOn(schedulerProvider.ui())
            .startWith(PopulateResult.InFlight)
      }
    }

  private val editProcessor =
    ObservableTransformer<SaveAction, EditResult> { actions ->
      actions.flatMap { action ->
        repository.saveItemToFile(action.item)
            .andThen(Observable.just(EditResult.Success))
            .cast(EditResult::class.java)
            .onErrorReturn(EditResult::Failure)
            .subscribeOn(schedulerProvider.io())
            .observeOn(schedulerProvider.ui())
            .startWith(EditResult.InFlight)
      }
    }

  private val deleteProcessor =
    ObservableTransformer<DeleteAction, DeleteResult> { actions ->
      actions.flatMap { action ->
        repository.deleteItemFromFile(action.item)
            .andThen(Observable.just(DeleteResult.Success))
            .cast(DeleteResult::class.java)
            .onErrorReturn(DeleteResult::Failure)
            .subscribeOn(schedulerProvider.io())
            .observeOn(schedulerProvider.ui())
            .startWith(DeleteResult.InFlight)
      }
    }

  internal var actionProcessor =
    ObservableTransformer<DetailAction, DetailResult> { action ->
      action.publish { shared ->
        Observable.merge(
            shared.ofType(PopulatelAction::class.java).compose(populateProcessor),
            shared.ofType(SaveAction::class.java).compose(editProcessor),
            shared.ofType(DeleteAction::class.java).compose(deleteProcessor)
        )
      }
    }

}


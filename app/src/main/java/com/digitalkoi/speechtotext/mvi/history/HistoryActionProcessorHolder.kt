package com.digitalkoi.speechtotext.mvi.history

import com.digitalkoi.speechtotext.data.file.CSVConversation
import com.digitalkoi.speechtotext.data.SpeechRepository
import com.digitalkoi.speechtotext.util.schedulers.BaseSchedulerProvider
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import com.digitalkoi.speechtotext.mvi.history.HistoryAction.*
import com.digitalkoi.speechtotext.mvi.history.HistoryResult.*
import io.reactivex.functions.BiFunction

/**
 * @author Taras Zhupnyk (akka DigitalKoi) on 24/03/18.
 */

class HistoryActionProcessorHolder(
  private val speechRepository: SpeechRepository,
  private val schedulerProvider: BaseSchedulerProvider
) {

  private val initialProcessor =
    ObservableTransformer<InitialAction, InitialResult> { actions ->
      actions.flatMap {
            speechRepository.getTextSize().toObservable()
                .map { font: Float -> InitialResult.Success(font) }
            .cast(InitialResult::class.java)
            .onErrorReturn(InitialResult::Failure)
            .subscribeOn(schedulerProvider.io())
            .observeOn(schedulerProvider.ui())
      }
    }

  private val updateListProcessor =
    ObservableTransformer<UpdateListAction, UpdateListResult> { actions ->
      actions.flatMap { state ->
        speechRepository.getListFromFile(state.date)
            .toObservable()
            .map { list -> UpdateListResult(list, state.date) }
            .subscribeOn(schedulerProvider.io())
            .observeOn(schedulerProvider.ui())
      }
    }

  private val showDataDialog =
    ObservableTransformer<ShowDataPickerAction, ShowDataPickerResult> { actions ->
      actions.map { state ->
        ShowDataPickerResult(state.showDataPickerAction)}
          .subscribeOn(schedulerProvider.io())
          .observeOn(schedulerProvider.ui())
    }

  internal var actionProcessor =
    ObservableTransformer<HistoryAction, HistoryResult> { actions ->
      actions.publish { shared ->
        Observable.merge(
            shared.ofType(InitialAction::class.java).compose(initialProcessor),
            shared.ofType(UpdateListAction::class.java).compose(updateListProcessor),
            shared.ofType(ShowDataPickerAction::class.java).compose(showDataDialog)
        )
      }
    }

}

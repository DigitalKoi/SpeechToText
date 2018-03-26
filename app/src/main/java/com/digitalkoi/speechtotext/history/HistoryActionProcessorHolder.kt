package com.digitalkoi.speechtotext.history

import com.digitalkoi.speechtotext.data.repository.SpeechRepository
import com.digitalkoi.speechtotext.util.schedulers.BaseSchedulerProvider
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import com.digitalkoi.speechtotext.history.HistoryAction.*
import com.digitalkoi.speechtotext.history.HistoryResult.*

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
        speechRepository.getTextSize()
            .toObservable()
            .map { fontSize -> InitialResult.Success(fontSize, listOf()) }
      }
    }

  private val showDialogDateProcessor =
    ObservableTransformer<ShowDialogDateAction, ShowDateResult> { actions ->
      actions
          .flatMap { action ->
            speechRepository.getListFromFile("22/03/2018")
                .toObservable()
                .map { conversationList -> ShowDateResult.Success(conversationList) }
          }
    }

  internal var actionProcessor =
    ObservableTransformer<HistoryAction, HistoryResult> { actions ->
      actions.publish { shared ->
        Observable.merge(
            shared.ofType(InitialAction::class.java).compose(initialProcessor),
            shared.ofType(ShowDialogDateAction::class.java).compose(showDialogDateProcessor)
        )
      }
    }

}

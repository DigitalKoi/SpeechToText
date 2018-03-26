package com.digitalkoi.speechtotext.speech

import com.digitalkoi.speechtotext.data.repository.SpeechRepository
import com.digitalkoi.speechtotext.speech.SpeechAction.*
import com.digitalkoi.speechtotext.speech.SpeechResult.*
import com.digitalkoi.speechtotext.speech.SpeechResult.ShowViewResult.ShowDialogConfirmResult
import com.digitalkoi.speechtotext.speech.SpeechResult.ShowViewResult.ShowDialogIdResult
import com.digitalkoi.speechtotext.speech.SpeechResult.ShowViewResult.ShowKeyboardResult
import com.digitalkoi.speechtotext.util.schedulers.BaseSchedulerProvider
import io.reactivex.ObservableTransformer
import io.reactivex.Observable
import io.reactivex.ObservableSource

/**
 * @author Taras Zhupnyk (akka DigitalKoi) on 09/03/18.
 */

class SpeechActionProcessorHolder(
  private val speechRepository: SpeechRepository,
  private val schedulerProvider: BaseSchedulerProvider
) {

  private val playPressedProcessor =
    ObservableTransformer<PlayPressedAction, LoadSpeechResult> { actions ->
      actions.flatMap { action ->
        speechRepository.startListener()
            .toObservable()
            .map { text ->
              LoadSpeechResult.Success(action.id, text) }
            .cast(LoadSpeechResult::class.java)
            .onErrorReturn(LoadSpeechResult::Failure)
            .subscribeOn(schedulerProvider.io())
            .observeOn(schedulerProvider.ui())
            .startWith(LoadSpeechResult.InFlight)
      }
    }

  private val stopPressedProcessor =
    ObservableTransformer<StopPressedAction, SaveSpeechResult> { actions ->
        actions.flatMap { action ->
              speechRepository.saveSpeech(action.id, action.text
              ).andThen(Observable.just(SaveSpeechResult))
            }
      }


  private val pausePressedProcessor =
    ObservableTransformer<PausePressedAction, PauseSpeechResult> { actions ->
      actions.flatMap {
            speechRepository.stopListener()
                .andThen(Observable.just(PauseSpeechResult))
          }
    }

  private val fontSizeProcessor =
    ObservableTransformer<FontSizeAction, FontSizeResult> { actions ->
      actions.flatMap {
        speechRepository.getTextSize()
            .toObservable()
            .map { fontSize -> FontSizeResult.Success(fontSize) }
            .cast(FontSizeResult::class.java)
            .subscribeOn(schedulerProvider.io())
            .observeOn(schedulerProvider.ui())
      }
    }

  private val fontSizeInProcessor =
    ObservableTransformer<FontSizeInAction, FontSizeResult> { actions ->
      actions.flatMap {
        speechRepository.zoomIn()
            .toObservable()
            .map { zoomIn -> FontSizeResult.Success(zoomIn) }
            .cast(FontSizeResult::class.java)
            .subscribeOn(schedulerProvider.io())
            .observeOn(schedulerProvider.ui())
      }
    }

  private val fontSizeOutProcessor =
    ObservableTransformer<FontSizeOutAction, FontSizeResult> { actions ->
      actions.flatMap {
        speechRepository.zoomOut()
            .toObservable()
            .map { zoomOut -> FontSizeResult.Success(zoomOut) }
            .cast(FontSizeResult::class.java)
            .subscribeOn(schedulerProvider.io())
            .observeOn(schedulerProvider.ui())
      }
    }

  private val showDialogIdProcessor =
    ObservableTransformer<ShowDialogIdAction, ShowDialogIdResult> { actions ->
      actions.map { state -> ShowViewResult.ShowDialogIdResult(state.showView) }
          .subscribeOn(schedulerProvider.io())
          .observeOn(schedulerProvider.ui())
    }

  private val showDialogConfirmProcessor =
    ObservableTransformer<ShowDialogConfirmAction, ShowDialogConfirmResult> { actions ->
      actions.map { state -> ShowViewResult.ShowDialogConfirmResult(state.showView) }
          .subscribeOn(schedulerProvider.io())
          .observeOn(schedulerProvider.ui())
    }

  private val showKeyboardProcessor =
    ObservableTransformer<ShowKeyboardAction, ShowKeyboardResult> { actions ->
      actions.map { state ->
        ShowViewResult.ShowKeyboardResult(state.showView) }
          .subscribeOn(schedulerProvider.io())
          .observeOn(schedulerProvider.ui())
    }

    internal var actionProcessor =
  ObservableTransformer<SpeechAction, SpeechResult> { action ->
      action.publish { shared ->
        Observable.merge(
            listOf(
                shared.ofType(PlayPressedAction::class.java).compose(playPressedProcessor),
                shared.ofType(StopPressedAction::class.java).compose(stopPressedProcessor),
                shared.ofType(PausePressedAction::class.java).compose(pausePressedProcessor),
                shared.ofType(FontSizeAction::class.java).compose(fontSizeProcessor),
                shared.ofType(FontSizeInAction::class.java).compose(fontSizeInProcessor),
                shared.ofType(FontSizeOutAction::class.java).compose(fontSizeOutProcessor),
                shared.ofType(ShowDialogIdAction::class.java).compose(showDialogIdProcessor),
                shared.ofType(ShowDialogConfirmAction::class.java).compose(showDialogConfirmProcessor),
                shared.ofType(ShowKeyboardAction::class.java).compose(showKeyboardProcessor)
            )
        )
      }
    }
}
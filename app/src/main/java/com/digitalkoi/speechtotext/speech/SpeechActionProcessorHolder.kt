package com.digitalkoi.speechtotext.speech

import com.digitalkoi.speechtotext.data.SpeechRepository
import com.digitalkoi.speechtotext.speech.SpeechAction.*
import com.digitalkoi.speechtotext.speech.SpeechResult.*
import com.digitalkoi.speechtotext.speech.SpeechResult.ShowViewResult.ShowDialogConfirmResult
import com.digitalkoi.speechtotext.speech.SpeechResult.ShowViewResult.ShowDialogIdResult
import com.digitalkoi.speechtotext.speech.SpeechResult.ShowViewResult.ShowKeyboardResult
import com.digitalkoi.speechtotext.util.schedulers.BaseSchedulerProvider
import io.reactivex.Observable
import io.reactivex.ObservableTransformer

/**
 * @author Taras Zhupnyk (akka DigitalKoi) on 09/03/18.
 */

class SpeechActionProcessorHolder(
  private val speechRepository: SpeechRepository,
  private val schedulerProvider: BaseSchedulerProvider
) {

  private val playPressedProcessor =
    ObservableTransformer<PlayPressedAction, LoadSpeechResult> { action ->
      action.flatMap {
        speechRepository.getSpeech()
            .toObservable()
            .map { text -> LoadSpeechResult.Success(text) }
            .cast(LoadSpeechResult::class.java)
            .onErrorReturn(LoadSpeechResult::Failure)
            .subscribeOn(schedulerProvider.io())
            .observeOn(schedulerProvider.ui())
            .startWith(LoadSpeechResult.InFlight)
      }
    }

  private val stopPressedProcessor =
    ObservableTransformer<StopPressedAction, SaveSpeechResult> { action ->
      action.flatMap {
        speechRepository.saveSpeech()
            .
            .toObservable()
            .map { SaveSpeechResult.Success }
            .cast(SaveSpeechResult::class.java)
            .onErrorReturn(SaveSpeechResult::Failure)
            .subscribeOn(schedulerProvider.io())
            .observeOn(schedulerProvider.ui())
            .startWith(SaveSpeechResult.InFlight)
      }
    }

  private val fontSizeProcessor =
    ObservableTransformer<FontSizeAction, FontSizeResult> { action ->
      action.flatMap {
        speechRepository.getTextSize()
            .toObservable()
            .map { fontSize -> FontSizeResult.Success(fontSize) }
            .cast(FontSizeResult::class.java)
            .subscribeOn(schedulerProvider.io())
            .observeOn(schedulerProvider.ui())
      }
    }

  private val fontSizeInProcessor =
    ObservableTransformer<FontSizeInAction, FontSizeResult> { action ->
      action.flatMap {
        speechRepository.zoomIn()
            .toObservable()
            .map { zoomIn -> FontSizeResult.Success(zoomIn) }
            .cast(FontSizeResult::class.java)
            .subscribeOn(schedulerProvider.io())
            .observeOn(schedulerProvider.ui())
      }
    }

  private val fontSizeOutProcessor =
    ObservableTransformer<FontSizeOutAction, FontSizeResult> { action ->
      action.flatMap {
        speechRepository.zoomOut()
            .toObservable()
            .map { zoomOut -> FontSizeResult.Success(zoomOut) }
            .cast(FontSizeResult::class.java)
            .subscribeOn(schedulerProvider.io())
            .observeOn(schedulerProvider.ui())
      }
    }

  private val showDialogIdProcessor =
    ObservableTransformer<ShowDialogIdAction, ShowDialogIdResult> { action ->
      action.map { state -> ShowViewResult.ShowDialogIdResult(state.showView) }
          .subscribeOn(schedulerProvider.io())
          .observeOn(schedulerProvider.ui())
    }

  private val showDialogConfirmProcessor =
    ObservableTransformer<ShowDialogConfirmAction, ShowDialogConfirmResult> { action ->
      action.map { state -> ShowViewResult.ShowDialogConfirmResult(state.showView) }
          .subscribeOn(schedulerProvider.io())
          .observeOn(schedulerProvider.ui())
    }

  private val showKeyboardProcessor =
    ObservableTransformer<ShowKeyboardAction, ShowKeyboardResult> { action ->
      action.map { state ->
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
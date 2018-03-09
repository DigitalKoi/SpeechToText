package com.digitalkoi.speechtotext.mvi.speech

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.View
import com.digitalkoi.speechtotext.mvi.mvibase.MviView
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject
import kotlin.LazyThreadSafetyMode.NONE
import android.arch.lifecycle.ViewModelProviders
import android.view.LayoutInflater
import android.view.ViewGroup
import com.digitalkoi.speechtotext.R
import com.digitalkoi.speechtotext.mvi.util.SpeechViewModelFactory

/**
 * @author Taras Zhupnyk (akka DigitalKoi) on 09/03/18.
 */

class SpeechFragment : Fragment(),
        MviView<SpeechIntent, SpeechUiModel> {

    private val disposable = CompositeDisposable()
    private val viewModel: SpeechViewModel by lazy(NONE) {
        ViewModelProviders
                .of(this, SpeechViewModelFactory.getInstance(context!!))
                .get(SpeechViewModel::class.java)
    }
    private val loadTextSubject = BehaviorSubject.create<SpeechIntent.LoadTextIntent>()

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater?.inflate(R.layout.speech_frag, container, false)

        return root
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bind()
    }

    override fun intents(): Observable<SpeechIntent> {
        return Observable.merge(initialIntent(), loadTextSubject)
    }

    override fun render(state: SpeechUiModel) {
        when {
            state.inProgress -> {}
            state is SpeechUiModel.Failed -> {}
            state is SpeechUiModel.Success -> {}
        }
    }

    private fun initialIntent(): Observable<SpeechIntent.InitialIntent> {
        return Observable.just(SpeechIntent.InitialIntent)
    }

    private fun bind() {

    }
}
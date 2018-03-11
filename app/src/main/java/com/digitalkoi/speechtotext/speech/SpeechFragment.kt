package com.digitalkoi.speechtotext.speech

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.View
import com.digitalkoi.speechtotext.mvibase.MviView
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject
import kotlin.LazyThreadSafetyMode.NONE
import android.arch.lifecycle.ViewModelProviders
import android.text.Editable
import android.view.LayoutInflater
import android.view.ViewGroup
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.Unbinder
import com.digitalkoi.speechtotext.R
import com.digitalkoi.speechtotext.util.SpeechViewModelFactory
import kotlinx.android.synthetic.main.speech_frag.*

/**
 * @author Taras Zhupnyk (akka DigitalKoi) on 09/03/18.
 */

class SpeechFragment : Fragment(),
        MviView<SpeechIntent, SpeechViewState> {

    private val disposable = CompositeDisposable()
    private val viewModel: SpeechViewModel by lazy(NONE) {
        ViewModelProviders
                .of(this, SpeechViewModelFactory.getInstance(context!!))
                .get(SpeechViewModel::class.java)
    }
    private val loadTextSubject = BehaviorSubject.create<SpeechIntent.LoadTextIntent>()
    private val zoomInSubject = BehaviorSubject.create<SpeechIntent.ZoomInIntent>()
    private val zoomOutSubject = BehaviorSubject.create<SpeechIntent.ZoomOutIntent>()


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater?.inflate(R.layout.speech_frag, container, false)

        return root
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        bind()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        bind()
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.dispose()
    }

    override fun intents(): Observable<SpeechIntent> {
        return Observable.merge(
                initialIntent(),
                loadTextIntent(),
                zoomInIntent(),
                zoomOutIntent())
    }

    override fun render(state: SpeechViewState) {
        if (state.error != null) {
            //TODO: show error
            return
        }
        if (state.fontChanged) speechTextField.textSize = state.fontSize
    }


    private fun initialIntent(): Observable<SpeechIntent.InitialIntent> {
        return Observable.just(SpeechIntent.InitialIntent)
    }

    private fun loadTextIntent(): Observable<SpeechIntent.LoadTextIntent> {
        return loadTextSubject
    }

    private fun zoomInIntent(): Observable<SpeechIntent.ZoomInIntent> {
        return zoomInSubject
    }

    private fun zoomOutIntent(): Observable<SpeechIntent.ZoomOutIntent> {
        return zoomOutSubject
    }

    private fun bind() {

        disposable.add(viewModel.states().subscribe(this::render))
        viewModel.processIntents(intents())

        speechPlusBt.setOnClickListener { zoomInSubject.onNext(SpeechIntent.ZoomInIntent) }
        speechMinusBt.setOnClickListener { zoomOutSubject.onNext(SpeechIntent.ZoomOutIntent) }
    }
}
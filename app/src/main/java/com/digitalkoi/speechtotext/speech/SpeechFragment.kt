package com.digitalkoi.speechtotext.speech

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.View
import com.digitalkoi.speechtotext.mvibase.MviView
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import kotlin.LazyThreadSafetyMode.NONE
import android.arch.lifecycle.ViewModelProviders
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import com.digitalkoi.speechtotext.R
import com.digitalkoi.speechtotext.util.SpeechViewModelFactory
import io.reactivex.subjects.PublishSubject
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
    private val speechPlaySubject = PublishSubject.create<SpeechIntent>()
    private val loadTextSubject = PublishSubject.create<SpeechIntent.LoadTextIntent>()
    private val zoomOutSubject = PublishSubject.create<SpeechIntent.ZoomOutIntent>()
    private val zoomInSubject = PublishSubject.create<SpeechIntent.ZoomInIntent>()


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater?.inflate(R.layout.speech_frag, container, false)

        return root
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
            showToast()
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

        speechPlayBt.setOnClickListener { speechPlaySubject.onNext(SpeechIntent.LoadTextIntent) }
        speechStopBt.setOnClickListener {  }
        speechMicPause.setOnClickListener{  }
        speechMicPlay.setOnClickListener{  }
        speechPlusBt.setOnClickListener { zoomInSubject.onNext(SpeechIntent.ZoomInIntent) }
        speechMinusBt.setOnClickListener { zoomOutSubject.onNext(SpeechIntent.ZoomOutIntent) }
    }

    private fun showToast() {
        Toast.makeText(activity, "Check network settings please", Toast.LENGTH_LONG).show()
    }
}

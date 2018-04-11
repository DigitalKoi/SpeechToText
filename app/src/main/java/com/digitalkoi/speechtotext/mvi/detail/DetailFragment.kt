package com.digitalkoi.speechtotext.mvi.detail

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.digitalkoi.speechtotext.data.file.CSVConversation
import com.digitalkoi.speechtotext.mvi.MviView
import com.digitalkoi.speechtotext.mvi.detail.DetailIntent.DeleteIntent
import com.digitalkoi.speechtotext.util.Constants
import com.digitalkoi.speechtotext.util.ViewModelFactory
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import kotlin.LazyThreadSafetyMode.NONE

/**
 * @author Taras Zhupnyk (akka DigitalKoi) on 10/04/18.
 */

class DetailFragment : Fragment(), MviView<DetailIntent, DetailViewState> {
  private val disposable = CompositeDisposable()
  private val argumentId: String
    get() = arguments!!.getString(Constants.ARGUMENT_ITEM_ID)
  private lateinit var item: CSVConversation

  override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    var root = super.onCreateView(inflater, container, savedInstanceState)
    setHasOptionsMenu(true)
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

  private fun bind() {
    disposable.add(viewModel.states().subscribe(this::render))
    viewModel.processIntents(intents())
  }

  private val viewModel: DetailViewModel by lazy(NONE) {
    ViewModelProviders
        .of(this, ViewModelFactory.getInstance(activity!!))
        .get(DetailViewModel::class.java)
  }

  override fun intents(): Observable<DetailIntent> = Observable.merge(initialIntent(), editIntent(), deleteIntent())

  override fun render(state: DetailViewState) {

  }

  private fun initialIntent(): Observable<DetailIntent.InitialIntent> =
    Observable.just(DetailIntent.InitialIntent(argumentId))

  private fun editIntent(): Observable<DetailIntent.EditIntent> =
    Observable.just(DetailIntent.EditIntent(item))

  private fun deleteIntent(): Observable<DetailIntent.DeleteIntent> =
    Observable.just(DeleteIntent(item))

  companion object {
    operator fun invoke(id: String): DetailFragment {
      return DetailFragment().apply {
        arguments = Bundle().apply {
          putString(Constants.ARGUMENT_ITEM_ID, id)
        }
      }
    }
  }
}
package com.digitalkoi.speechtotext.mvi.history

import android.app.Activity
import android.app.DatePickerDialog
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatDelegate
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import com.digitalkoi.speechtotext.R
import com.digitalkoi.speechtotext.data.file.CSVConversation
import com.digitalkoi.speechtotext.mvi.base.MviView
import com.digitalkoi.speechtotext.mvi.detail.DetailActivity
import com.digitalkoi.speechtotext.mvi.history.HistoryIntent.InitialIntent
import com.digitalkoi.speechtotext.mvi.history.HistoryIntent.ShowDataPickerIntent
import com.digitalkoi.speechtotext.mvi.history.HistoryIntent.UpdateListIntent
import com.digitalkoi.speechtotext.util.Constants
import com.digitalkoi.speechtotext.util.ViewModelFactory
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.history_frag.historyDatePicker
import kotlinx.android.synthetic.main.history_frag.historyRecycler
import kotlinx.android.synthetic.main.history_list_item.view.historyConversationItem
import kotlinx.android.synthetic.main.history_list_item.view.historyDateItem
import kotlinx.android.synthetic.main.history_list_item.view.historyIdItem
import kotlinx.android.synthetic.main.history_list_item.view.historyPatientIdItem
import java.text.SimpleDateFormat
import java.util.Calendar

/**
 * @author Taras Zhupnyk (akka DigitalKoi) on 24/03/18.
 */

class HistoryFragment : Fragment(), MviView<HistoryIntent, HistoryViewState> {

  private val viewModel: HistoryViewModel by lazy {
    ViewModelProviders
        .of(this, ViewModelFactory.getInstance(activity!!))
        .get(HistoryViewModel::class.java)
  }

  private lateinit var list: List<CSVConversation>
  private val disposable = CompositeDisposable()
  private val updatedListSubject = PublishSubject.create<UpdateListIntent>()
  private val showDataPickerDialogSubject = PublishSubject.create<ShowDataPickerIntent>()
  private fun initialIntent(): Observable<InitialIntent> = Observable.just(InitialIntent)
  private fun updateListIntent(): Observable<UpdateListIntent> = updatedListSubject
  private fun showDataDialogIntent(): Observable<ShowDataPickerIntent> = showDataPickerDialogSubject

  private var showTime: Boolean = false
  private var datePicker: String = ""

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
    setHasOptionsMenu(true)
    return inflater.inflate(R.layout.history_frag, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    bind()
  }

  override fun onResume() {
    super.onResume()
    updatedListSubject.onNext(UpdateListIntent(datePicker))
  }

  override fun onDestroyView() {
    super.onDestroyView()
    disposable.dispose()
  }

  private fun bind() {
    initDatePicker()
    disposable.add(viewModel.states().subscribe(this::render))
    viewModel.processIntents(intents())
  }

  override fun intents(): Observable<HistoryIntent> {
    return Observable.merge(
        initialIntent(),
        updateListIntent(),
        showDataDialogIntent()
    )
  }

  override fun render(state: HistoryViewState) {
    if (!TextUtils.equals(datePicker, state.date)) {
      datePicker = state.date
      updatedListSubject.onNext(UpdateListIntent(datePicker))
    }
    if (state.dataList != null) {
      list = state.dataList
      showList(list)
    }
  }

  private fun showList(dataList: List<CSVConversation>) {
    historyRecycler.setUp(
        dataList,
        R.layout.history_list_item, {
      historyIdItem.text = it.serialNumber
      historyPatientIdItem.text = it.patientId
      if (showTime) historyDateItem.text = it.time.substring(11, 19)
      else historyDateItem.text = it.time.substring(0, 10)
      historyConversationItem.text = it.conversation
    },
        { showDetailActivity(serialNumber) }
    )
  }

  private fun initDatePicker() {
    historyDatePicker.setOnClickListener {
      val calendar = Calendar.getInstance()
      val year = calendar.get(Calendar.YEAR)
      val month = calendar.get(Calendar.MONTH)
      val day = calendar.get(Calendar.DAY_OF_MONTH)
      val dataPickerDialog =
        DatePickerDialog(
            activity as Activity, DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
          run {
            val newDate = Calendar.getInstance()
            newDate.set(year, monthOfYear, dayOfMonth)
            val dateFormatter = SimpleDateFormat("yyyy-MM-dd")
            datePicker = dateFormatter.format(newDate.time)
            showTime = true
            updatedListSubject.onNext(UpdateListIntent(datePicker))
//            showDataPickerDialogSubject.onNext(ShowDataPickerIntent(false))
          }
        }, year, month, day
        )
//      showDataPickerDialogSubject.onNext(ShowDataPickerIntent(true))
      dataPickerDialog.show()
    }
  }

  private fun showDetailActivity(itemId: String) {
    val intent = Intent(activity, DetailActivity::class.java)
    intent.putExtra(Constants.ARGUMENT_ITEM_ID, itemId)
    startActivity(intent)
  }

  override fun onCreateOptionsMenu(
    menu: Menu?,
    inflater: MenuInflater?
  ) {
    inflater?.inflate(R.menu.menu_history, menu)
    super.onCreateOptionsMenu(menu, inflater)
  }

  override fun onOptionsItemSelected(item: MenuItem?): Boolean {
    when (item!!.itemId) {
      R.id.menu_clear_filter -> {
        showTime = false
        updatedListSubject.onNext(UpdateListIntent(""))
      }
    }
    return super.onOptionsItemSelected(item)
  }
}
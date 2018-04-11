package com.digitalkoi.speechtotext.mvi.history

import android.app.DatePickerDialog
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.digitalkoi.speechtotext.R
import com.digitalkoi.speechtotext.data.file.CSVConversation
import com.digitalkoi.speechtotext.mvi.history.HistoryIntent.InitialIntent
import com.digitalkoi.speechtotext.mvi.history.HistoryIntent.ShowDateIntent
import com.digitalkoi.speechtotext.mvi.MviView
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
import java.util.Calendar
import java.text.SimpleDateFormat

/**
 * @author Taras Zhupnyk (akka DigitalKoi) on 24/03/18.
 */

class HistoryFragment : Fragment(),
    MviView<HistoryIntent, HistoryViewState> {

  private val viewModel: HistoryViewModel by lazy {
    ViewModelProviders
        .of(this, ViewModelFactory.getInstance(activity!!))
        .get(HistoryViewModel::class.java)
  }

  private val showDateSubject = PublishSubject.create<ShowDateIntent>()
  private lateinit var datePicker: String
  private lateinit var list: List<CSVConversation>
  private val disposable = CompositeDisposable()
  private fun initialIntent(): Observable<InitialIntent> = Observable.just(InitialIntent)
  private fun showDateIntent(): Observable<ShowDateIntent> = showDateSubject

  override fun onCreateView(
    inflater: LayoutInflater?,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return inflater?.inflate(R.layout.history_frag, container, false)
  }

  override fun onViewCreated(
    view: View?,
    savedInstanceState: Bundle?
  ) {
    super.onViewCreated(view, savedInstanceState)
    bind()
  }

  override fun onDestroy() {
    super.onDestroy()
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
        showDateIntent()
    )
  }

  override fun render(state: HistoryViewState) {
    if (state.dataList != null) {
      list = state.dataList!!
      showList(list)
    }
  }

  private fun initDatePicker() {
    historyDatePicker.setOnClickListener {
      val calendar = Calendar.getInstance()
      val year = calendar.get(Calendar.YEAR)
      val month = calendar.get(Calendar.MONTH)
      val day = calendar.get(Calendar.DAY_OF_MONTH)
      val dpd =
        DatePickerDialog(
            activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
          run {
            val newDate = Calendar.getInstance()
            newDate.set(year, monthOfYear, dayOfMonth)
            val dateFormatter = SimpleDateFormat("yyyy-MM-dd")
            datePicker = dateFormatter.format(newDate.time)
            filterAndShowList(datePicker)
          }

        }, year, month, day
        )
      dpd.show()
    }
  }

  private fun filterAndShowList(data: String) {
    var listTemp = ArrayList<CSVConversation>()
    list.forEach { item ->
      if (data == item.time.substring(0, 10))
        listTemp.add(item)
    }
    showList(listTemp)
  }

  private fun showList(dataList: List<CSVConversation>) {
    historyRecycler.setUp(dataList, R.layout.history_list_item, {
      historyIdItem.text = it.serialNumber
      historyPatientIdItem.text = it.patientId
      historyDateItem.text = it.time
      historyConversationItem.text = it.conversation
    })
  }
}

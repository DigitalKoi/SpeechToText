package com.digitalkoi.speechtotext.mvi.detail

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatDelegate
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView.BufferType.EDITABLE
import android.widget.Toast
import com.digitalkoi.speechtotext.R
import com.digitalkoi.speechtotext.data.file.CSVConversation
import com.digitalkoi.speechtotext.mvi.MviView
import com.digitalkoi.speechtotext.mvi.detail.DetailIntent.DeleteIntent
import com.digitalkoi.speechtotext.mvi.detail.DetailIntent.InitialIntent
import com.digitalkoi.speechtotext.mvi.detail.DetailIntent.SaveIntent
import com.digitalkoi.speechtotext.util.Constants
import com.digitalkoi.speechtotext.util.ViewModelFactory
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.detail_frag.detail_patientId
import kotlinx.android.synthetic.main.detail_frag.detail_text
import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil
import kotlin.LazyThreadSafetyMode.NONE

/**
 * @author Taras Zhupnyk (akka DigitalKoi) on 10/04/18.
 */

class DetailFragment : Fragment(), MviView<DetailIntent, DetailViewState> {
  private val disposable = CompositeDisposable()
  private val argumentId: String
    get() = arguments!!.getString(Constants.ARGUMENT_ITEM_ID)
  private var item: CSVConversation? = null
  private var menu: Menu? = null

  private val savePressedSubject = PublishSubject.create<SaveIntent>()
  private val deletePressedSubject = PublishSubject.create<DeleteIntent>()

  private fun initialIntent(): Observable<InitialIntent> = Observable.just(InitialIntent(argumentId)
  )
  private fun saveIntent(): Observable<SaveIntent> = savePressedSubject
  private fun deleteIntent(): Observable<DeleteIntent> = deletePressedSubject

  override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?)
      : View? {
    AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
    setHasOptionsMenu(true)
    return inflater?.inflate(R.layout.detail_frag, container, false)
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
    allowEditing(false)
  }

  private val viewModel: DetailViewModel by lazy(NONE) {
    ViewModelProviders
        .of(this, ViewModelFactory.getInstance(activity!!))
        .get(DetailViewModel::class.java)
  }

  override fun intents(): Observable<DetailIntent> =
    Observable.merge(initialIntent(), saveIntent(), deleteIntent())

  override fun render(state: DetailViewState) {
    if (state.item != null) {
      item = state.item!!
      setTextToFields()
    }
  }

  private fun setTextToFields() {
    detail_patientId.setText(item!!.patientId, EDITABLE)
    detail_text.setText(item!!.conversation, EDITABLE)
  }

  private fun allowEditing(allow: Boolean) {
    detail_patientId.isClickable = allow
    detail_patientId.isFocusable = allow
    detail_patientId.isFocusableInTouchMode = allow
    detail_text.isClickable = allow
    detail_text.isFocusable = allow
    detail_text.isFocusableInTouchMode = allow
    showItemsMenu(true)
    if (allow) {
      detail_patientId.setSelection(detail_patientId.text.length)
      detail_text.setSelection(detail_text.text.length)
      detail_text.requestFocus()
      UIUtil.showKeyboard(activity, detail_text)
      showItemsMenu(false)
    }
  }

  private fun showItemsMenu(show: Boolean) {
    menu?.findItem(R.id.menu_delete)
        ?.isVisible = show
    menu?.findItem(R.id.menu_edit)
        ?.isVisible = show
    menu?.findItem(R.id.menu_save)
        ?.isVisible = !show

  }

  override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
    inflater?.inflate(R.menu.menu_detail, menu)
    this.menu = menu
    super.onCreateOptionsMenu(menu, inflater)
  }

  override fun onOptionsItemSelected(item: MenuItem?): Boolean {
    when (item!!.itemId) {
      R.id.menu_edit -> allowEditing(true)
      R.id.menu_delete -> {
        activity.finish()
        deletePressedSubject.onNext(DeleteIntent(this.item!!))
        showToast("Item was deleted")
      }
      R.id.menu_save -> {
        activity.finish()
        saveItemToFile()
        showToast("Item was edited")
      }
    }
    return super.onOptionsItemSelected(item)
  }

  private fun showToast(text: String) =
    Toast.makeText(activity, text, Toast.LENGTH_SHORT).show()

  private fun saveItemToFile() {
    val editedItem = CSVConversation(
        item!!.serialNumber,
        item!!.time,
        detail_patientId.text.toString(),
        detail_text.text.toString()
    )

    savePressedSubject.onNext(SaveIntent(editedItem))
  }

  companion object {
    operator fun invoke(itemId: String): DetailFragment {
      return DetailFragment().apply {
        arguments = Bundle().apply {
          putString(Constants.ARGUMENT_ITEM_ID, itemId)
        }
      }
    }
  }
}
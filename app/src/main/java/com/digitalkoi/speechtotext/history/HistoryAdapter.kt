package com.digitalkoi.speechtotext.history

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.digitalkoi.speechtotext.util.recycler.AbstractAdapter

/**
 * @author Taras Zhupnyk (akka DigitalKoi) on 25/03/18.
 */

class HistoryAdapter<ITEM>(
  items: List<ITEM>,
  layoutResId: Int,
  private val bindViewHolder: View.(ITEM) -> Unit
) : AbstractAdapter<ITEM>(items, layoutResId) {

  private var itemClick: ITEM.() -> Unit = {}

  constructor(
    items: List<ITEM>,
    layoutResId: Int,
    bindHolder: View.(ITEM) -> Unit,
    itemClick: ITEM.() -> Unit = {}
  ) : this(items, layoutResId, bindHolder) {
    this.itemClick = itemClick
  }

  override fun onBindViewHolder(
    holder: Holder,
    position: Int
  ): Unit =
    holder.itemView.bindViewHolder(itemList[position])

  override fun onItemClick(
    itemView: View,
    position: Int
  ) =
    itemList[position].itemClick()

}

fun <ITEM> RecyclerView.setUp(
  items: List<ITEM>,
  layoutResId: Int,
  bindViewHolder: View.(ITEM) -> Unit,
  itemClick: ITEM.() -> Unit = {},
  manager: RecyclerView.LayoutManager = LinearLayoutManager(this.context)
): HistoryAdapter<ITEM> {

  val historyAdapter by lazy {
    HistoryAdapter(
        items, layoutResId, { bindViewHolder(it) }, { itemClick() })
  }

  layoutManager = manager
  adapter = historyAdapter
  return historyAdapter
}

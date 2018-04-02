package com.digitalkoi.speechtotext.util.recycler

import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.digitalkoi.speechtotext.util.recycler.AbstractAdapter.Holder

/**
 * @author Taras Zhupnyk (akka DigitalKoi) on 25/03/18.
 */

abstract class AbstractAdapter<ITEM> constructor(protected var itemList: List<ITEM>,
  private val layoutResId: Int)
  : RecyclerView.Adapter<Holder>() {

  override fun getItemCount() = itemList.size

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
    val view = parent inflate layoutResId
    val viewHolder = Holder(view)
    val itemView = viewHolder.itemView
    itemView.setOnClickListener {
      val adapterPosition = viewHolder.adapterPosition
      if (adapterPosition != RecyclerView.NO_POSITION) {
        onItemClick(itemView, adapterPosition)
      }
    }
    return viewHolder
  }

  override fun onBindViewHolder(holder: Holder, position: Int) {
    val item = itemList[position]
    holder.itemView.bind(item)
  }

  fun update(items: List<ITEM>) {
    updateAdapterWithDiffResult(calculateDiff(items))
  }

  private fun updateAdapterWithDiffResult(result: DiffUtil.DiffResult) {
    result.dispatchUpdatesTo(this)
  }

  private fun calculateDiff(newItems: List<ITEM>) =
    DiffUtil.calculateDiff(
        DiffUtilCallback(itemList, newItems)
    )

  fun add(item: ITEM) {
    itemList.toMutableList().add(item)
    notifyItemInserted(itemList.size)
  }

  fun remove(position: Int) {
    itemList.toMutableList().removeAt(position)
    notifyItemRemoved(position)
  }

  final override fun onViewRecycled(holder: Holder) {
    super.onViewRecycled(holder)
    onViewRecycled(holder.itemView)
  }

  protected open fun onViewRecycled(itemView: View) {
  }

  protected open fun onItemClick(itemView: View, position: Int) {
  }

  protected open fun View.bind(item: ITEM) {
  }

  class Holder(itemView: View) : RecyclerView.ViewHolder(itemView)
}
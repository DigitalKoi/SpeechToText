package com.digitalkoi.speechtotext.util.recycler

import android.support.v7.util.DiffUtil

/**
 * @author Taras Zhupnyk (akka DigitalKoi) on 25/03/18.
 */
 
internal class DiffUtilCallback<ITEM>(
  private val oldItems: List<ITEM>,
    private val newItems: List<ITEM>
) : DiffUtil.Callback() {

  override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
    oldItems[oldItemPosition] == newItems[newItemPosition]

  override fun getOldListSize(): Int = oldItems.size

  override fun getNewListSize(): Int = newItems.size

  override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
      oldItems[oldItemPosition] == newItems[newItemPosition]

}
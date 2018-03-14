package com.digitalkoi.speechtotext.util

import android.annotation.SuppressLint
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager

/**
 * @author Taras Zhupnyk (akka DigitalKoi) on 09/03/18.
 */

@SuppressLint("CommitTransaction")
fun addFragmentToActivity(
  fragmentManager: FragmentManager,
  fragment: Fragment,
  frameId: Int
) {

  fragmentManager.beginTransaction()
      .run {
        add(frameId, fragment)
        commit()
      }
}
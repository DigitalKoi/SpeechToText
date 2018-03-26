package com.digitalkoi.speechtotext.history

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.digitalkoi.speechtotext.R
import com.digitalkoi.speechtotext.util.addFragmentToActivity

/**
 * @author Taras Zhupnyk (akka DigitalKoi) on 24/03/18.
 */

class HistoryActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.history_act)

    if (supportFragmentManager.findFragmentById(R.id.historyContainer) == null)
      addFragmentToActivity(supportFragmentManager, HistoryFragment(), R.id.historyContainer)
  }
}
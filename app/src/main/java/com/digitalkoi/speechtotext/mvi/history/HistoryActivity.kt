package com.digitalkoi.speechtotext.mvi.history

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.digitalkoi.speechtotext.R
import com.digitalkoi.speechtotext.util.addFragmentToActivity
import kotlinx.android.synthetic.main.history_act.historyToolbar

/**
 * @author Taras Zhupnyk (akka DigitalKoi) on 24/03/18.
 */

class HistoryActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.history_act)

    setSupportActionBar(historyToolbar)
    supportActionBar?.run {
      setDisplayHomeAsUpEnabled(true)
      setDisplayShowHomeEnabled(true)
    }


    if (supportFragmentManager.findFragmentById(R.id.historyContainer) == null)
      addFragmentToActivity(supportFragmentManager, HistoryFragment(), R.id.historyContainer)
  }

  override fun onSupportNavigateUp(): Boolean {
    onBackPressed()
    return super.onSupportNavigateUp()
  }
}
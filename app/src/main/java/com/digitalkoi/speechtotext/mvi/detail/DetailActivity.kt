package com.digitalkoi.speechtotext.mvi.detail

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.digitalkoi.speechtotext.R
import com.digitalkoi.speechtotext.util.Constants
import com.digitalkoi.speechtotext.util.addFragmentToActivity
import kotlinx.android.synthetic.main.detail_act.detailToolbar

/**
 * @author Taras Zhupnyk (akka DigitalKoi) on 10/04/18.
 */

class DetailActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.detail_act)

    setSupportActionBar(detailToolbar)
    supportActionBar?.run {
      setDisplayHomeAsUpEnabled(true)
      setDisplayShowHomeEnabled(true)
    }

    val itemId = intent.getStringExtra(Constants.ARGUMENT_ITEM_ID)

    if (supportFragmentManager.findFragmentById(R.id.detailContentFrame) == null)
      addFragmentToActivity(supportFragmentManager, DetailFragment(itemId), R.id.detailContentFrame)
  }

  override fun onSupportNavigateUp(): Boolean {
    onBackPressed()
    return super.onSupportNavigateUp()
  }
}
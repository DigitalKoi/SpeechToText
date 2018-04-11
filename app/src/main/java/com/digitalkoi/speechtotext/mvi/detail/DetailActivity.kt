package com.digitalkoi.speechtotext.mvi.detail

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.digitalkoi.speechtotext.R
import com.digitalkoi.speechtotext.util.Constants
import com.digitalkoi.speechtotext.util.addFragmentToActivity

/**
 * @author Taras Zhupnyk (akka DigitalKoi) on 10/04/18.
 */

class DetailActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    setContentView(R.layout.detail_act)

    setSupportActionBar(findViewById(R.id.detailToolbar))
    supportActionBar?.run {
      setDisplayHomeAsUpEnabled(true)
      setDisplayShowHomeEnabled(true)

      val patientId = intent.getStringExtra(Constants.ARGUMENT_ITEM_ID)

      if (supportFragmentManager.findFragmentById(R.id.detailContentFrame) == null) {
        addFragmentToActivity(supportFragmentManager, DetailFragment(patientId), R.id.detailContentFrame)
      }
    }
  }
}
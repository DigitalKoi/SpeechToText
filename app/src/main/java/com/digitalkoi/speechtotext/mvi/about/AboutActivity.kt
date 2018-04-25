package com.digitalkoi.speechtotext.mvi.about

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.app.AppCompatDelegate
import com.digitalkoi.speechtotext.R.layout
import kotlinx.android.synthetic.main.about_act.aboutToolbar

/**
 * @author Taras Zhupnyk (akka DigitalKoi) on 14/04/18.
 */

class AboutActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(layout.about_act)

    AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
    setSupportActionBar(aboutToolbar)
    supportActionBar!!.setDisplayHomeAsUpEnabled(true)
  }

  override fun onSupportNavigateUp(): Boolean {
    onBackPressed()
    return super.onSupportNavigateUp()
  }
}
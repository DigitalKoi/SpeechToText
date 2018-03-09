package com.digitalkoi.speechtotext.mvi.speech

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.digitalkoi.speechtotext.R
import com.digitalkoi.speechtotext.mvi.util.addFragmentToActivity

class SpeechActivity : AppCompatActivity(){


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.speech_act)

        if (supportFragmentManager.findFragmentById(R.id.speechContainer) == null) {
            addFragmentToActivity(supportFragmentManager, SpeechFragment(), R.id.speechContainer)
        }
    }

}

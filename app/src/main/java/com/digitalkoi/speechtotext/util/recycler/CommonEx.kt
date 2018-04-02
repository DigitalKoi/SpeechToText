package com.digitalkoi.speechtotext.util.recycler

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * @author Taras Zhupnyk (akka DigitalKoi) on 25/03/18.
 */
 
infix fun ViewGroup.inflate(layoutResId: Int) : View =
    LayoutInflater.from(context).inflate(layoutResId, this, false)
package com.digitalkoi.speechtotext.mvi.drawing;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import com.digitalkoi.speechtotext.R;

/**
 * @author Taras Zhupnyk (akka DigitalKoi) on 14/03/18.
 */

class ColorHelper {
  @ColorInt
  static int getRandomMaterialColor(@NonNull Context context) {
    TypedArray colors = context.getResources().obtainTypedArray(R.array.material_colors);
    int index = (int) (Math.random() * colors.length());
    int color = colors.getColor(index, Color.BLACK);
    colors.recycle();
    return color;
  }
}

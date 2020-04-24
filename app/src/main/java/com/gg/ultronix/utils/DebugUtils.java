package com.gg.ultronix.utils;

import android.util.Log;


public class DebugUtils {

  public static void log(String log) {
    if (ConfigUtils.DEBUG_ON) {
      Log.v("Ultronix ", log);
    }
  }

}

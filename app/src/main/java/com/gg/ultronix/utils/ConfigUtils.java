package com.gg.ultronix.utils;

import android.media.AudioFormat;

public class ConfigUtils {
  public static final short FREQ_RANGE_START = 3000;
  public static final short FREQ_RANGE_END = 20000;
  public static final short FREQ_STEP = 10;
  public static final int SAMPLE_RATE = 44100;
  public static final int TIME_BAND = 8192;
  public static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;

  public static final int MAX_SIGNAL_STRENGTH = 65535;

  public static final short NONSENSE_DATA = 256;

  public static final short START_COMMAND = 130;
  public static final short STOP_COMMAND = 128;

  public static final boolean DEBUG_ON = true;
}

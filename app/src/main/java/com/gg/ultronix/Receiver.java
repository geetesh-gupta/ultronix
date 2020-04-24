package com.gg.ultronix;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.util.Log;

import com.gg.ultronix.exception.UltronixException;
import com.gg.ultronix.fft.FFT;
import com.gg.ultronix.utils.ConfigUtils;
import com.gg.ultronix.utils.DebugUtils;
import com.gg.ultronix.utils.ListUtils;

import java.util.ArrayList;
import java.util.List;

public class Receiver {
  private AudioRecord audioRecord;

  private Thread thread;

  private boolean threadRunning = true;

  private static Receiver sReceiver;

  private static List<Short> list = new ArrayList<>();
  private short[] recordedData = new short[ConfigUtils.TIME_BAND];

  public static Receiver getReceiver() {
    if (sReceiver == null) {
      sReceiver = new Receiver();
    }
    return sReceiver;
  }

  public void initializeReceiver() throws UltronixException {
    if (audioRecord == null || audioRecord.getState() == AudioRecord.STATE_UNINITIALIZED) {
      audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, ConfigUtils.SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, ConfigUtils.AUDIO_FORMAT, AudioTrack.getMinBufferSize(ConfigUtils.SAMPLE_RATE, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT) * 4);
    }
    if (audioRecord.getRecordingState() != AudioRecord.RECORDSTATE_RECORDING) {
      audioRecord.startRecording();
    }
    if (!threadRunning) {
      threadRunning = true;
    }
    if (thread == null) {
      initThread();
    }
    if (thread.getState() == Thread.State.NEW) {
      thread.start();
    }
    if (thread.getState() == Thread.State.TERMINATED) {
      initThread();
      thread.start();
    }
  }

  public static List<Short> getList() {
    return list;
  }

  public void stopReceiver() throws UltronixException {
    if (audioRecord != null && audioRecord.getState() == AudioRecord.STATE_INITIALIZED) {
      audioRecord.stop();
      audioRecord.release();
    }
    if (threadRunning) {
      threadRunning = false;
    }
    if (thread.isAlive()) {
      thread.interrupt();
    }
  }

  private void initThread() {
    thread = new Thread() {
      @Override
      public void run() {
        while (threadRunning) {
          audioRecord.read(recordedData, 0, ConfigUtils.TIME_BAND);
          short parsedData = parseRecData(recordedData);
          list.add(parsedData);
        }
      }
    };
  }

  private short parseRecData(short[] recordedData) {
    float[] floatData = ListUtils.convertArrayShortToArrayFloat(recordedData);
    short freq = calcFreq(floatData);
    DebugUtils.log("Freq: " + freq);
    return freq;
  }

  private static short calcFreq(float[] floatData) {
//    StringBuilder s = new StringBuilder();
//    for (float floatDatum : floatData) s.append(floatDatum);
//    Log.v("GG", s.toString());
    int size = floatData.length;
    int fftSize = calcFftSize(size);
    FFT fft = new FFT(fftSize, ConfigUtils.SAMPLE_RATE);
    fft.forward(floatData);
    float maxAmp = 0;
    short index = 0;
    for (short i = ConfigUtils.FREQ_RANGE_START; i < ConfigUtils.FREQ_RANGE_END; i++) {
      float curAmp = fft.getFreq(i);
//      Log.v("GG", "Freq " + i + " Amp " + curAmp);
      if (curAmp > maxAmp) {
        maxAmp = curAmp;
        index = i;
      }
    }
    return index;
  }

  private static int calcFftSize(int size) {
    int count = 0;
    int i;
    for (i = 0; i < 32 && size != 0; i++) {
      if ((size & 1) == 1) {
        count++;
      }
      size >>= 1;
    }
    int r = count == 1 ? i - 1 : i;
    return 1 << r;
  }

}

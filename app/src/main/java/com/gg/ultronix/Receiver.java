package com.gg.ultronix;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Looper;

import com.gg.ultronix.fft.FFT;
import com.gg.ultronix.utils.ConfigUtils;
import com.gg.ultronix.utils.ListUtils;

class Receiver {
  private AudioRecord audioRecord;

  private Thread thread;

  private boolean threadRunning = true;

  private static Receiver sReceiver;

  private short[] recordedData = new short[ConfigUtils.TIME_BAND];

  static Receiver getReceiver() {
    if (sReceiver == null) {
      sReceiver = new Receiver();
    }
    return sReceiver;
  }

  void initializeReceiver() {
    if (audioRecord == null || audioRecord.getState() == AudioRecord.STATE_UNINITIALIZED) {
      audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, ConfigUtils.SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, ConfigUtils.RECEIVER_AUDIO_FORMAT, AudioTrack.getMinBufferSize(ConfigUtils.SAMPLE_RATE, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT) * 4);
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

  void stopReceiver() {
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
          parseRecData(recordedData);
        }
      }
    };
  }

  private void parseRecData(short[] recordedData) {
    float[] floatData = ListUtils.convertArrayShortToArrayFloat(recordedData);
    short freq = calcFreq(floatData);
    new Handler(Looper.getMainLooper()).post(() -> Ultronix.ultronixListener.OnReceiveData(freq));
  }

  private static short calcFreq(float[] floatData) {
    int size = floatData.length;
    int fftSize = calcFftSize(size);
    FFT fft = new FFT(fftSize, ConfigUtils.SAMPLE_RATE);
    fft.forward(floatData);
    float maxAmp = 0;
    short index = 0;
    for (short i = ConfigUtils.FREQ_RANGE_START; i < ConfigUtils.FREQ_RANGE_END; i++) {
      float curAmp = fft.getFreq(i);
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

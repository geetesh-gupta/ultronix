package com.gg.ultronix;

import android.media.AudioTrack;
import android.util.Log;

import com.gg.ultronix.utils.AudioUtils;

class Sender {
  private static Sender sSender;

  private AudioTrack mAudioTrack;

  private Thread thread;

  static Sender getSender() {
    if (sSender == null) {
      sSender = new Sender();
    }
    return sSender;
  }

  void send(int freq) {
    int duration = 10;
    float[] freqArray = AudioUtils.generateFreqArray(freq, duration);
    if (mAudioTrack == null) {
      mAudioTrack = AudioUtils.generateAudioTrack();
      mAudioTrack.write(freqArray, 0, freqArray.length, AudioTrack.WRITE_BLOCKING);
      play();
    }
  }

  private synchronized void play() {
    thread = new Thread() {
      public void run() {
        while (true) {
          if (mAudioTrack == null) break;
          mAudioTrack.play();
          try {
            Thread.sleep(100);
          } catch (Exception e) {
            System.out.println("Unable to sleep thread");
          }
          if (mAudioTrack == null) break;
          mAudioTrack.setPlaybackHeadPosition(0);
          mAudioTrack.stop();
        }
      }
    };
    thread.start();
  }

  synchronized void stop() {
    if (mAudioTrack != null) {
      mAudioTrack.stop();
      mAudioTrack.release();
      mAudioTrack = null;
    }
    if (thread != null) {
      try {
        thread.interrupt();
        thread = null;
      } catch (Exception e) {
        Log.e("Err", e.toString());
      }
    }
  }

}

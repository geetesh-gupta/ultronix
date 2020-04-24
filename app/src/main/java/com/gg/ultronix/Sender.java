package com.gg.ultronix;

import android.app.Activity;
import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Build;
import android.util.Log;

import com.gg.ultronix.utils.ConfigUtils;
import com.gg.ultronix.utils.DebugUtils;
import com.gg.ultronix.utils.ListUtils;

import java.util.ArrayList;
import java.util.List;

public class Sender {

  private static Sender sSender;

  private AudioTrack mAudioTrack;

  private Thread thread;
  private boolean threadRunning = true;
  private boolean started = false;

  public static Sender getSender() {
    if (sSender == null) {
      sSender = new Sender();
    }
    return sSender;
  }

  public void send(Activity activity, int freq) {
    List<Short> list = new ArrayList<>();
    for (int i = 0; i < ConfigUtils.TIME_BAND; i++) {
      double angle = 2.0 * i * freq * Math.PI / ConfigUtils.SAMPLE_RATE;
      list.add((short) (Math.sin(angle) * ConfigUtils.MAX_SIGNAL_STRENGTH));
    }
    play(list);
  }

  private synchronized void play(final List<Short> list) {
    int mode = AudioTrack.MODE_STATIC;
    generateAudioTrack(mode);
    thread = new Thread() {
      public void run() {
        if (!started) {
          if (mAudioTrack == null) generateAudioTrack(mode);
          mAudioTrack.write(ListUtils.convertListShortToArrayShort(list), 0, list.size());
          if (mAudioTrack.getState() != AudioTrack.STATE_INITIALIZED) {
            generateAudioTrack(mode);
          }
          mAudioTrack.play();
          started = true;
        }
        if (mAudioTrack.getPlayState() != AudioTrack.PLAYSTATE_PLAYING) {
          if (mAudioTrack.getState() != AudioTrack.STATE_INITIALIZED) {
            generateAudioTrack(mode);
          }
          mAudioTrack.play();
        }
      }
    };
    thread.start();
  }

  synchronized void stop() {
    started = false;
    if (thread != null) {
      try {
        thread.interrupt();
        thread.join();
        thread = null;
      } catch (Exception e) {
        Log.e("Err", e.toString());
      }
    }
    if (mAudioTrack != null) {
      mAudioTrack.stop();
      mAudioTrack.release();
      mAudioTrack = null;
    }
  }

  private void generateAudioTrack(int mode) {
    if (mAudioTrack == null || mAudioTrack.getState() != AudioTrack.STATE_INITIALIZED) {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        mAudioTrack = new AudioTrack.Builder()
            .setAudioAttributes(new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build())
            .setAudioFormat(new AudioFormat.Builder()
                .setEncoding(ConfigUtils.AUDIO_FORMAT)
                .setSampleRate(ConfigUtils.SAMPLE_RATE)
                .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                .build())
            .setBufferSizeInBytes(AudioTrack.getMinBufferSize(ConfigUtils.SAMPLE_RATE, AudioFormat.CHANNEL_OUT_MONO, ConfigUtils.AUDIO_FORMAT) * 4)
            .setTransferMode(mode)
            .build();
      } else {
        mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, ConfigUtils.SAMPLE_RATE, AudioFormat.CHANNEL_OUT_MONO, ConfigUtils.AUDIO_FORMAT, AudioTrack.getMinBufferSize(ConfigUtils.SAMPLE_RATE, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT) * 4, mode);
      }
    }
  }

}

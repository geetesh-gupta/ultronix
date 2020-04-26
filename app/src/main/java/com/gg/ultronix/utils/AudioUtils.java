package com.gg.ultronix.utils;

import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Build;

public class AudioUtils {

  public static float[] generateFreqArray(int freq, int duration) {
    int numSamples = ConfigUtils.SAMPLE_RATE * duration;
    float[] freqArray = new float[numSamples + 1];
    for (int i = 0; i <= numSamples; i++) {
      freqArray[i] = (float)Math.sin(2 * Math.PI * i * freq / ConfigUtils.SAMPLE_RATE);
    }
    return freqArray;
  }

  public static AudioTrack generateAudioTrack() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      return new AudioTrack.Builder()
          .setAudioAttributes(new AudioAttributes.Builder()
              .setUsage(AudioAttributes.USAGE_MEDIA)
              .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
              .build())
          .setAudioFormat(new AudioFormat.Builder()
              .setEncoding(ConfigUtils.SENDER_AUDIO_FORMAT)
              .setSampleRate(ConfigUtils.SAMPLE_RATE)
              .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
              .build())
          .setBufferSizeInBytes(AudioTrack.getMinBufferSize(ConfigUtils.SAMPLE_RATE, AudioFormat.CHANNEL_OUT_MONO, ConfigUtils.SENDER_AUDIO_FORMAT) * 4)
          .setTransferMode(AudioTrack.MODE_STATIC)
          .build();
    } else {
      return new AudioTrack(AudioManager.STREAM_MUSIC, ConfigUtils.SAMPLE_RATE, AudioFormat.CHANNEL_OUT_MONO, ConfigUtils.SENDER_AUDIO_FORMAT, AudioTrack.getMinBufferSize(ConfigUtils.SAMPLE_RATE, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT) * 4, AudioTrack.MODE_STATIC);
    }
  }
}


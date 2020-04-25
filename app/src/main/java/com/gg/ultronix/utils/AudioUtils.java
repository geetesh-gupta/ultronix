package com.gg.ultronix.utils;

import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Build;

public class AudioUtils {

  public static byte[] generateFreqBytes(int freq) {
    double dnumSamples = (double) 10 * ConfigUtils.SAMPLE_RATE;
    dnumSamples = Math.ceil(dnumSamples);
    int numSamples = (int) dnumSamples;
    double[] sample = new double[numSamples];
    byte[] generatedSnd = new byte[2 * numSamples];

    for (int i = 0; i < numSamples; ++i) {      // Fill the sample array
      sample[i] = Math.sin(freq * 2 * Math.PI * i / (ConfigUtils.SAMPLE_RATE));
    }

    // convert to 16 bit pcm sound array
    // assumes the sample buffer is normalized.
    int idx = 0;
    int i;

    int ramp = numSamples / 20;  // Amplitude ramp as a percent of sample count

    for (i = 0; i < ramp; ++i) {  // Ramp amplitude up (to avoid clicks)
      // Ramp up to maximum
      final short val = (short) (sample[i] * 32767 * i / ramp);
      // in 16 bit wav PCM, first byte is the low order byte
      generatedSnd[idx++] = (byte) (val & 0x00ff);
      generatedSnd[idx++] = (byte) ((val & 0xff00) >>> 8);
    }

    for (i = ramp; i < numSamples - ramp;
         ++i) {                        // Max amplitude for most of the samples
      // scale to maximum amplitude
      final short val = (short) (sample[i] * 32767);
      // in 16 bit wav PCM, first byte is the low order byte
      generatedSnd[idx++] = (byte) (val & 0x00ff);
      generatedSnd[idx++] = (byte) ((val & 0xff00) >>> 8);
    }

    for (i = numSamples - ramp; i < numSamples; ++i) { // Ramp amplitude down
      // Ramp down to zero
      final short val = (short) (sample[i] * 32767 * (numSamples - i) / ramp);
      // in 16 bit wav PCM, first byte is the low order byte
      generatedSnd[idx++] = (byte) (val & 0x00ff);
      generatedSnd[idx++] = (byte) ((val & 0xff00) >>> 8);
    }
    return generatedSnd;
  }

  public static AudioTrack generateAudioTrack() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      return new AudioTrack.Builder()
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
          .setTransferMode(AudioTrack.MODE_STATIC)
          .build();
    } else {
      return new AudioTrack(AudioManager.STREAM_MUSIC, ConfigUtils.SAMPLE_RATE, AudioFormat.CHANNEL_OUT_MONO, ConfigUtils.AUDIO_FORMAT, AudioTrack.getMinBufferSize(ConfigUtils.SAMPLE_RATE, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT) * 4, AudioTrack.MODE_STATIC);
    }
  }
}


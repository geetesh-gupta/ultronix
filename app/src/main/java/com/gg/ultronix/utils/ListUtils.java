package com.gg.ultronix.utils;

import java.util.List;

public class ListUtils {

  public static short[] convertListShortToArrayShort(List<Short> list) {
    int size = list.size();
    short[] data = new short[size];
    for (int i = 0; i < size; i++) {
      data[i] = list.get(i);
    }
    return data;
  }

  public static byte[] convertListBytesToArrayBytes(List<Byte> list) {
    int size = list.size();
    byte[] data = new byte[size];
    for (int i = 0; i < size; i++) {
      data[i] = list.get(i);
    }
    return data;
  }

  public static float[] convertArrayShortToArrayFloat(short[] recordedData) {
    int size = recordedData.length;
    float[] floatData = new float[size];
    for (int i = 0; i < size; i++) {
      floatData[i] = recordedData[i];
    }
    return floatData;
  }

}

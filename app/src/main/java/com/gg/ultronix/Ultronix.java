package com.gg.ultronix;

import android.app.Activity;

import com.gg.ultronix.exception.UltronixException;

import java.util.List;

/**
 * Enable you to use the Ultronix, <br>
 * to transmit and detect ultrasonic sound<br>
 *
 * @author Geetesh Gupta - geeetshgupta98@gmail.com
 * @version 0.0.1
 */
public class Ultronix {

  private Activity activity;

//  private static SoundifyListener soundifyListener;

  public Ultronix(Activity activity) {
    this.activity = activity;
  }

  public void startListening() throws UltronixException {
    Receiver.getReceiver().initializeReceiver();
  }

  public void stopListening() throws UltronixException {
    Receiver.getReceiver().stopReceiver();
  }

  public void send(short freq) {
    Sender.getSender().send(activity, freq);
  }

  public List<Short> receive() {
    return Receiver.getList();
  }

//  public interface SoundifyListener {
//    void OnReceiveData(byte[] data);
//
//    void OnReceiveError(int code, String msg);
//  }
//
//  public void setSoundifyListener(SoundifyListener listener) {
//    soundifyListener = listener;
//  }

}

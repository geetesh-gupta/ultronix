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

  private static UltronixListener ultronixListener;

  public Ultronix(Activity activity) {
    this.activity = activity;
  }

  public void startListening() throws UltronixException {
    Receiver.getReceiver().initializeReceiver();
  }

  public void stopListening() throws UltronixException {
    Receiver.getReceiver().stopReceiver();
  }

  public void send(int freq) {
    Sender.getSender().send(freq);
  }

  public void stopSending() {
    Sender.getSender().stop();
  }

  public List<Short> receive() {
    return Receiver.getList();
  }

  public interface UltronixListener {
    void OnReceiveData(short freq);

    void OnReceiveError(int code, String msg);
  }

  public void setUltronixListener(UltronixListener listener) {
    ultronixListener = listener;
  }

}

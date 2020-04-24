package com.gg.ultronix;

import com.gg.ultronix.exception.UltronixException;

/**
 * Enable you to use the Ultronix, <br>
 * to transmit and detect ultrasonic sound<br>
 *
 * @author Geetesh Gupta - geeetshgupta98@gmail.com
 * @version 0.0.1
 */
public class Ultronix {
  static UltronixListener ultronixListener;

  void startListening() throws UltronixException {
    Receiver.getReceiver().initializeReceiver();
  }

  public void stopListening() throws UltronixException {
    Receiver.getReceiver().stopReceiver();
  }

  void send(int freq) {
    Sender.getSender().send(freq);
  }

  void stopSending() {
    Sender.getSender().stop();
  }

  public interface UltronixListener {
    void OnReceiveData(short freq);
  }

  void setUltronixListener(UltronixListener listener) {
    ultronixListener = listener;
  }

}

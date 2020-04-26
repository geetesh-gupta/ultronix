package com.gg.ultronix;

/**
 * Enable you to use the Ultronix, <br>
 * to transmit and detect ultrasonic sound<br>
 *
 * @author Geetesh Gupta - geeetshgupta98@gmail.com
 * @version 0.0.1
 */
class Ultronix {
  static UltronixListener ultronixListener;

  void startListening() {
    Receiver.getReceiver().initializeReceiver();
  }

  public void stopListening() {
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

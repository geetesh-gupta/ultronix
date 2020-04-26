package com.gg.ultronix;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.gg.ultronix.utils.ConfigUtils;
import com.gg.ultronix.utils.DebugUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {
  private Ultronix ultronix;

  private EditText freqText;
  private TextView showFreqTextView;
  private FloatingActionButton fab;
  private boolean isPlaying = false;
  private long breakTimeLeft = 0;
  private final ArrayList<Short> lastTenResults = new ArrayList<>();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    requestPermission();
  }

  private void requestPermission() {
    if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {

      if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)) {
        Toast.makeText(this, "Please grant permissions to record audio", Toast.LENGTH_LONG).show();
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 1);
      } else {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 1);
      }
    } else {
      work();
    }
  }

  private void work() {
    ultronix = new Ultronix();
    ultronix.startListening();
    ultronix.setUltronixListener(this::OnReceiveData);

    fab = findViewById(R.id.fab);
    showFreqTextView = findViewById(R.id.curFreq);
    freqText = findViewById(R.id.inputFreq);

    fab.setOnClickListener(v -> {
      if (isPlaying) {
        handleStop();
      } else {
        String freqString = freqText.getText().toString();
        if (!"".equals(freqString)) {
          int freq = Integer.parseInt(freqString);
          handlePlay(freq);
        } else if (!isPlaying) {
          Toast.makeText(MainActivity.this, "Please enter a frequency!", Toast.LENGTH_SHORT).show();
        }
      }
    });
  }

  private void handleStop() {
    if (isPlaying) {
      ultronix.stopSending();
      isPlaying = false;
      fab.setImageResource(R.drawable.ic_play_arrow_white_24dp);
    }
  }

  private void handlePlay(int freq) {
    if (!isPlaying) {
      ultronix.send(freq);
      isPlaying = true;
      fab.setImageResource(R.drawable.ic_stop_white_24dp);
    }
  }

  // Handling callback
  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    if (requestCode == 1) {
      if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        work();
      } else {
        Toast.makeText(this, "Permissions Denied to record audio", Toast.LENGTH_LONG).show();
        requestPermission();
      }
    }
  }

  private void OnReceiveData(short data) {
    DebugUtils.log("Received MaxAmpFreq " + data);
    showFreqTextView.setText(String.valueOf(data));
    if (lastTenResults.size() < 10)
      lastTenResults.add(data);
    else {
      lastTenResults.remove(0);
      lastTenResults.add(data);
      startAlarm();
    }
  }

  private void startAlarm() {
    if (!isPlaying && breakTimeLeft <= 0) {
      if (Collections.min(lastTenResults) > ConfigUtils.MIN_FREQ_THRESHOLD) {
        handlePlay(ConfigUtils.ALARM_FREQ);
        Toast.makeText(MainActivity.this, "Press the stop button to disable alarm!", Toast.LENGTH_LONG).show();
        new CountDownTimer(ConfigUtils.BREAK_TIME, 1000) {

          public void onTick(long millisUntilFinished) {
            breakTimeLeft = millisUntilFinished;
            DebugUtils.log("BreakTimeLeft" + breakTimeLeft);
          }

          public void onFinish() {
            breakTimeLeft = 0;
          }
        }.start();
      }
    }
  }
}

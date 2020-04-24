package com.gg.ultronix;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.gg.ultronix.exception.UltronixException;
import com.gg.ultronix.utils.DebugUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {
  Ultronix ultronix;

  EditText freqText;
  TextView showFreqTextView;
  FloatingActionButton fab;
  private boolean isPlaying = false;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    requestPermission();
  }

  public void requestPermission() {
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

  public void work() {
    ultronix = new Ultronix();
    try {
      ultronix.startListening();
      ultronix.setUltronixListener(this::OnReceiveData);
    } catch (UltronixException e) {
      e.printStackTrace();
    }

    fab = findViewById(R.id.fab);
    showFreqTextView = findViewById(R.id.curFreq);
    freqText = findViewById(R.id.inputFreq);

    fab.setOnClickListener(v -> handlePlay());
  }

  private void handlePlay() {
    String freqString = freqText.getText().toString();
    if (!"".equals(freqString)) {
      if (!isPlaying) {
        // Play Tone
        ultronix.send(Integer.parseInt(freqString));
        isPlaying = true;
        fab.setImageResource(R.drawable.ic_stop_white_24dp);
      } else {
        // Stop Tone
        ultronix.stopSending();
        isPlaying = false;
        fab.setImageResource(R.drawable.ic_play_arrow_white_24dp);
      }
    } else if ("".equals(freqString)) {
      Toast.makeText(MainActivity.this, "Please enter a frequency!", Toast.LENGTH_SHORT).show();
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
  }
}

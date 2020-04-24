package com.gg.ultronix;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.gg.ultronix.exception.UltronixException;
import com.gg.ultronix.utils.DebugUtils;

public class MainActivity extends AppCompatActivity {
  Ultronix ultronix;

  EditText freqText;
  TextView text;
  Button send;
  Button stop;

  Ultronix.UltronixListener listener;

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

        //Give user option to still opt-in the permissions
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 1);
      } else {
        // No explanation needed; request the permission
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 1);

        // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
        // app-defined int constant. The callback method gets the
        // result of the request.
      }
    } else {
      work();
    }
  }

  public void work() {
    ultronix = new Ultronix(this);
    try {
      ultronix.startListening();
    } catch (UltronixException e) {
      e.printStackTrace();
    }

    send = findViewById(R.id.button);
    stop = findViewById(R.id.button2);
    text = findViewById(R.id.textView);
    freqText = findViewById(R.id.editText);

    send.setOnClickListener(v -> {
      String editTextValue = freqText.getText().toString();
      if (!editTextValue.equals("")) ultronix.send(Integer.parseInt(editTextValue));
      else ultronix.send((short) 15000);
    });
    stop.setOnClickListener(v -> ultronix.stopSending());
    ultronix.setUltronixListener(this::OnReceiveData);
  }

  // Handling callback
  @Override
  public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
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
    text.setText(String.valueOf(data));
  }
}

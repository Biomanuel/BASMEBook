package com.biomanuel97.basmebook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.TextView;
import android.widget.Toast;

public class StartupActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_PERMISSION_READ_CONTACTS = 100;
    private static long delayTime = 3000;
    private static int delayTimeInterval = 500;
    private UtilManager mManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);

        final TextView tvBootingDots = findViewById(R.id.booting_dots);
        setUpApp();

        new CountDownTimer(delayTime, delayTimeInterval) {
            public void onFinish() {

                Intent MainActivity = new Intent(getBaseContext(), MainActivity.class);
                startActivity(MainActivity);

                finish();
            }

            public void onTick(long millisUntilFinished) {
                tvBootingDots.setText(tvBootingDots.getText() + ".");
            }

        }.start();


    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_PERMISSION_READ_CONTACTS) {
            // TODO: Write Code load messages here too
            mManager.loadSMS(this.getApplicationContext());
        } else {
            Toast.makeText(this, "Please grant permission", Toast.LENGTH_SHORT).show();
        }
    }

    private void setUpApp() {
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        boolean firstStart = prefs.getBoolean("firstStart", true);

        if(firstStart) {
            delayTime = 6000;
            delayTimeInterval = 1000;
            int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS);

            mManager = UtilManager.getSetupInstance(this.getApplicationContext());
            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {

            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_SMS},
                        REQUEST_CODE_PERMISSION_READ_CONTACTS);
            }

            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("firstStart", false);
            editor.apply();
        }else
            mManager = UtilManager.getInitInstance(this.getApplicationContext());
    }


}

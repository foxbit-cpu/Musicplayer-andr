package com.example.musicplayerexam;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.musicplayerexam.data.scanner.MediaScanner;

public class SplashActivity extends AppCompatActivity {

    private static final int PERMISSION_AUDIO_REQUEST_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        checkAndRequestPermissions();
    }

    private void checkAndRequestPermissions() {
        // Проверка разрешения на чтение аудио
        boolean hasAudioPermission;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            hasAudioPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_AUDIO)
                    == PackageManager.PERMISSION_GRANTED;
        } else {
            hasAudioPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED;
        }

        // Проверка разрешения на уведомления (Android 13+)
        boolean hasNotificationPermission = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            hasNotificationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    == PackageManager.PERMISSION_GRANTED;
        }

        if (hasAudioPermission && hasNotificationPermission) {
            startApp();
        } else {
            String[] permissions;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                permissions = new String[]{Manifest.permission.READ_MEDIA_AUDIO, Manifest.permission.POST_NOTIFICATIONS};
            } else {
                permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};
            }
            ActivityCompat.requestPermissions(this, permissions, PERMISSION_AUDIO_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_AUDIO_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startApp();
            } else {
                Toast.makeText(this, "Без разрешения на чтение музыки приложение не может работать", Toast.LENGTH_LONG).show();
                finish();
            }
        } else {
            startApp(); // fallback
        }
    }

    private void startApp() {
        // Запускаем фоновое сканирование музыки (оно не блокирует UI)
        MediaScanner.scanAllAudio(getApplicationContext());

        // Через 1500 мс переходим в MainActivity
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish();
        }, 1500);
    }
}
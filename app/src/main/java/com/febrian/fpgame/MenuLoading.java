package com.febrian.fpgame;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import static com.febrian.fpgame.Helper.KEYLOGIN;
import static com.febrian.fpgame.Helper.keylogin;
import static java.lang.Thread.sleep;

public class MenuLoading extends Activity {

    private ProgressBar progressBar;

    private int Value = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_loading);
//
//        SharedPreferences settings = getSharedPreferences(KEYLOGIN, Context.MODE_PRIVATE);
//        settings.edit().clear().commit();

        progressBar = findViewById(R.id.progressBar);
        progressBar.setProgress(0); //Set Progress Dimulai Dari O

        Button register, login;
        register = findViewById(R.id.register);
        login = findViewById(R.id.login);

        register.setVisibility(View.INVISIBLE);
        login.setVisibility(View.INVISIBLE);

        // Handler untuk Updating data pada latar belakang
        @SuppressLint("HandlerLeak") final Handler handler = new Handler() {
            @SuppressLint("HandlerLeak")
            @Override
            public void handleMessage(Message msg) {
                Value++;
            }
        };

        // Thread untuk updating progress pada ProgressBar di Latar Belakang
        Thread thread = new Thread(() -> {
            try {
                for (int w = 0; w <= progressBar.getMax(); w++) {
                    progressBar.setProgress(w);
                    sleep(10);
                }

            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        });
        thread.start();
        Handler handler1 = new Handler();
        handler1.postDelayed(() -> {
            if (progressBar.getMax() == 100) {

                SharedPreferences sharedPreferences = getSharedPreferences(KEYLOGIN, MODE_PRIVATE);
                String log = sharedPreferences.getString(keylogin, "");

                if (!log.equals("")) {
                    Intent intent = new Intent(getApplicationContext(), MainMenu.class);
                    startActivity(intent);
                    finish();
                } else {
                    register.setVisibility(View.VISIBLE);
                    login.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }
        }, 5100);

        login.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        });

        register.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
        });
    }
}
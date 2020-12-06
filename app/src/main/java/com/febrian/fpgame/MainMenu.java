package com.febrian.fpgame;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.IOException;

public class MainMenu extends Activity {

    private Button musicOnOff;
    boolean musicOn = true;



    int i = 0;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        context = this;
        musicOnOff = findViewById(R.id.music_on_off);
        final MediaPlayer[] bgSound = {null};
        bgSound[0] = MediaPlayer.create(context, R.raw.music1);
        bgSound[0].setLooping(true);
        bgSound[0].start();
        musicOnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i++;
                musicOn = !musicOn;
                if (musicOn) {
                    if(bgSound[0] == null){
                        bgSound[0] = MediaPlayer.create(context, R.raw.music1);
                        bgSound[0].setLooping(true);
                    }
                    bgSound[0].start();
                    musicOnOff.setBackground(getDrawable(R.drawable.btn_music_on));
                }
                else{
                    if(bgSound[0] != null){
                        bgSound[0].pause();
                    }
                    musicOnOff.setBackground(getDrawable(R.drawable.btn_music_off));
                }
            }
        });
    }
}
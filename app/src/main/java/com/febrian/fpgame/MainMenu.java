package com.febrian.fpgame;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

import java.io.IOException;

public class MainMenu extends Activity implements  View.OnClickListener {

    private Button musicOnOff, btnPlay, btnCredit, btnHighscore, btnExit;
    boolean musicOn = true;
    Context context;
    Animation anim, title_anim;
    View title_view;
    final MediaPlayer[] bgSound = {null};

    ImageView comet1,comet2,comet3, bintang_biasa,bintang_bulat;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        context = this;

        comet1 = findViewById(R.id.comet1);
        comet2 = findViewById(R.id.comet2);
        comet3 = findViewById(R.id.comet3);
        bintang_biasa = findViewById(R.id.bintang_biasa);
        bintang_bulat = findViewById(R.id.bintang_bulat);

        comet1.setAnimation(AnimationUtils.loadAnimation(this, R.anim.comet1_anim));
        comet2.setAnimation(AnimationUtils.loadAnimation(this, R.anim.comet2_anim));
        comet3.setAnimation(AnimationUtils.loadAnimation(this, R.anim.comet3_anim));

        bintang_biasa.setAnimation(AnimationUtils.loadAnimation(this, R.anim.bintang1_anim));
        bintang_bulat.setAnimation(AnimationUtils.loadAnimation(this, R.anim.bintang2_anim));

        //get View Id
        musicOnOff = findViewById(R.id.music_on_off);
        btnPlay = findViewById(R.id.btn_play);
        btnCredit = findViewById(R.id.btn_credit);
        btnHighscore = findViewById(R.id.btn_highscore);
        btnExit = findViewById(R.id.btn_exit);
        title_view = findViewById(R.id.view);

        title_anim = AnimationUtils.loadAnimation(context, R.anim.scale_title_anim);
       // title_view.setAnimation(title_anim);

        //implement setOnclick
        btnPlay.setOnClickListener(this);
        btnHighscore.setOnClickListener(this);
        btnCredit.setOnClickListener(this);
        btnExit.setOnClickListener(this);
        musicOnOff.setOnClickListener(this);

        bgSound[0] = MediaPlayer.create(context, R.raw.music1);
        bgSound[0].setLooping(true);
        bgSound[0].start();
    }

    private void PlayMusic(){
        if(bgSound[0] == null){
            bgSound[0] = MediaPlayer.create(context, R.raw.music1);
            bgSound[0].setLooping(true);
        }
        bgSound[0].start();
    }

    private void PauseMusic(){
        if(bgSound[0] != null){
            bgSound[0].pause();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        PauseMusic();
    }

    @Override
    protected void onResume() {
        super.onResume();
        PlayMusic();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_play :
                Play();
                break;
            case R.id.btn_highscore:
                HighScore();
                break;
            case R.id.btn_credit:
                Credit();
                break;
            case R.id.btn_exit:
                Exit();
                break;
            case R.id.music_on_off:
                MusicOnOff();
                break;
        }
    }

    private void Play(){
        Intent play = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(play);
        finish();
    }

    private void HighScore(){
        Intent highscore = new Intent(getApplicationContext(), Highscore.class);
        startActivity(highscore);
        finish();
    }

    private void Credit(){
        Intent credit = new Intent(getApplicationContext(), Credits.class);
        startActivity(credit);
        finish();
    }

    private void Exit(){
        finish();
    }

    private void MusicOnOff(){
            musicOn = !musicOn;
            anim = AnimationUtils.loadAnimation(context, R.anim.bounce_anim);
            if (musicOn) {
                PlayMusic();
                musicOnOff.setAnimation(anim);
                musicOnOff.setBackground(getDrawable(R.drawable.btn_music_on));
            }
            else{
                PauseMusic();
                musicOnOff.setAnimation(anim);
                musicOnOff.setBackground(getDrawable(R.drawable.btn_music_off));
            }
    }
}
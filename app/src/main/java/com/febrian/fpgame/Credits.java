package com.febrian.fpgame;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import androidx.core.content.ContextCompat;

public class Credits extends Activity{

    final MediaPlayer[] bgSound = {null};
    boolean musicOn = true;
    private Button musicOnOff;
    Context context;
    Animation anim;
    View comet1,comet2,comet3, bintang_biasa,bintang_bulat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credits);

        context = this;

        Button back = findViewById(R.id.back);

        comet1 = findViewById(R.id.comet1);
        comet2 = findViewById(R.id.comet2);
        comet3 = findViewById(R.id.comet3);
        bintang_biasa = findViewById(R.id.bintang_biasa);
        bintang_bulat = findViewById(R.id.bintang_bulat);

        musicOnOff = findViewById(R.id.music_on_off);

        back.setOnClickListener(v -> {
            back.setAnimation(AnimationUtils.loadAnimation(this, R.anim.button_bounce_anim));
            startActivity(new Intent(getApplicationContext(), MainMenu.class));
            finish();
        });

        comet1.setAnimation(AnimationUtils.loadAnimation(this, R.anim.comet1_anim));
        comet2.setAnimation(AnimationUtils.loadAnimation(this, R.anim.comet2_anim));
        comet3.setAnimation(AnimationUtils.loadAnimation(this, R.anim.comet3_anim));

        bintang_biasa.setAnimation(AnimationUtils.loadAnimation(this, R.anim.bintang1_anim));
        bintang_bulat.setAnimation(AnimationUtils.loadAnimation(this, R.anim.bintang2_anim));

        bgSound[0] = MediaPlayer.create(context, R.raw.music1);
        bgSound[0].setLooping(true);
        bgSound[0].start();

        musicOnOff.setOnClickListener(v -> {
            musicOn = !musicOn;
            anim = AnimationUtils.loadAnimation(context, R.anim.bounce_anim);
            if (musicOn) {
                PlayMusic();
                musicOnOff.setAnimation(anim);
                musicOnOff.setBackground(ContextCompat.getDrawable(this,R.drawable.btn_music_on_res_com));
            }
            else{
                PauseMusic();
                musicOnOff.setAnimation(anim);
                musicOnOff.setBackground(ContextCompat.getDrawable(this, R.drawable.btn_music_off_res_com));
            }
        });
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
}
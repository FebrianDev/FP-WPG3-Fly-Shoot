package com.febrian.fpgame;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import static com.febrian.fpgame.Helper.KEYLOGIN;
import static com.febrian.fpgame.Helper.keylogin;

public class MainMenu extends Activity implements View.OnClickListener{

    private Button btnPlay, btnCredit, btnHighscore, btnExit, musicOnOff;
    boolean musicOn = true;
    Context context;
    Animation anim;
    final MediaPlayer[] bgSound = {null};

    TextView name, score;
    boolean dropdown = false;
    View comet1,comet2,comet3, bintang_biasa,bintang_bulat;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        context = this;

        name = findViewById(R.id.name);
        score = findViewById(R.id.score);

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
        de.hdodenhof.circleimageview.CircleImageView profile = findViewById(R.id.profile);
        de.hdodenhof.circleimageview.CircleImageView btn_profile = findViewById(R.id.btn_profil);
        de.hdodenhof.circleimageview.CircleImageView btn_logout = findViewById(R.id.btn_logout);

        //implement setOnclick
        btnPlay.setOnClickListener(this);
        btnHighscore.setOnClickListener(this);
        btnCredit.setOnClickListener(this);
        btnExit.setOnClickListener(this);
        musicOnOff.setOnClickListener(this);

        bgSound[0] = MediaPlayer.create(context, R.raw.music1);
        bgSound[0].setLooping(true);
        bgSound[0].start();


        SharedPreferences sharedPreferences = getSharedPreferences(KEYLOGIN, MODE_PRIVATE);
        String getUsername = sharedPreferences.getString(keylogin, "");
        Log.d("Key", getUsername);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(getUsername);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String urlImg = snapshot.child("url_image").getValue().toString();
                Log.d("Key", urlImg);
                Picasso.get().load(urlImg).into(profile);
                name.setText(snapshot.child("username").getValue().toString());
                score.setText(snapshot.child("score").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        btn_logout.setTranslationY(-240);
        btn_logout.setVisibility(View.INVISIBLE);

        btn_profile.setTranslationY(-120);
        btn_profile.setVisibility(View.INVISIBLE);

        profile.setOnClickListener(v -> {
            dropdown = !dropdown;

            if(dropdown){
                btn_profile.setAnimation(AnimationUtils.loadAnimation(this, R.anim.dropdown_anim));
                btn_logout.setAnimation(AnimationUtils.loadAnimation(this, R.anim.dropdown_anim));
                btn_logout.setTranslationY(0);
                btn_logout.setVisibility(View.VISIBLE);
                btn_profile.setTranslationY(0);
                btn_profile.setVisibility(View.VISIBLE);
            }else{
                btn_profile.setAnimation(AnimationUtils.loadAnimation(this, R.anim.dropdown_back_anim));
                btn_logout.setAnimation(AnimationUtils.loadAnimation(this, R.anim.dropdown_back_anim));
                btn_logout.setTranslationY(-120);
                btn_logout.setVisibility(View.INVISIBLE);
                btn_profile.setTranslationY(-120);
                btn_profile.setVisibility(View.INVISIBLE);
            }
        });



        btn_profile.setOnClickListener(v->{
            Intent intent = new Intent(getApplicationContext(), EditProfile.class);
            startActivity(intent);
        });

        btn_logout.setOnClickListener(v->{
            sharedPreferences.edit().clear().apply();
            startActivity(new Intent(getApplicationContext(), MenuLoading.class));
            finish();
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
        btnPlay.setAnimation(AnimationUtils.loadAnimation(this, R.anim.button_bounce_anim));
        Intent play = new Intent(getApplicationContext(), GameActivity.class);
        startActivity(play);
        finish();
    }

    private void HighScore(){
        btnHighscore.setAnimation(AnimationUtils.loadAnimation(this, R.anim.button_bounce_anim));
        Intent highscore = new Intent(getApplicationContext(), Highscore.class);
        startActivity(highscore);
        finish();
    }

    private void Credit(){
        btnCredit.setAnimation(AnimationUtils.loadAnimation(this, R.anim.button_bounce_anim));
        Intent credit = new Intent(getApplicationContext(), Credits.class);
        startActivity(credit);
        finish();
    }

    private void Exit(){
        btnExit.setAnimation(AnimationUtils.loadAnimation(this, R.anim.button_bounce_anim));
        finish();
    }

    private void MusicOnOff(){
            musicOn = !musicOn;
            anim = AnimationUtils.loadAnimation(context, R.anim.bounce_anim);
            if (musicOn) {
                PlayMusic();
                musicOnOff.setAnimation(anim);
                musicOnOff.setBackground(ContextCompat.getDrawable(this,R.drawable.btn_music_on));
            }
            else{
                PauseMusic();
                musicOnOff.setAnimation(anim);
                musicOnOff.setBackground(ContextCompat.getDrawable(this, R.drawable.btn_music_off));
            }
    }

}
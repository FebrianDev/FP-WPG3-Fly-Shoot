package com.febrian.fpgame;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
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

public class DetailActivity extends Activity {

    TextView tv_username, tv_bio, tv_score;
    String username;
    DatabaseReference reference;
    Button back;
    de.hdodenhof.circleimageview.CircleImageView img;

    final MediaPlayer[] bgSound = {null};
    Button musicOnOff;

    boolean musicOn = true;
    Animation anim;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        tv_username = findViewById(R.id.tv_username);
        tv_bio = findViewById(R.id.tv_bio);
        tv_score = findViewById(R.id.tv_score);
        img = findViewById(R.id.img);
        username = getIntent().getStringExtra("name");
        back = findViewById(R.id.back);
        reference = FirebaseDatabase.getInstance().getReference().child("Users").child(username);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                tv_username.setText(snapshot.child("username").getValue().toString());
                tv_bio.setText(snapshot.child("bio").getValue().toString());
                tv_score.setText(snapshot.child("score").getValue().toString());
                Picasso.get().load(snapshot.child("url_image").getValue().toString()).into(img);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        back.setOnClickListener(v->{
            startActivity(new Intent(getApplicationContext(), Highscore.class));
            finish();
        });

        musicOnOff = findViewById(R.id.music_on_off);
        musicOnOff.setOnClickListener(v->{
            musicOn = !musicOn;
            anim = AnimationUtils.loadAnimation(this, R.anim.bounce_anim);
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
        });
        bgSound[0] = MediaPlayer.create(this, R.raw.music1);
        bgSound[0].setLooping(true);
        bgSound[0].start();
    }

    private void PlayMusic(){
        if(bgSound[0] == null){
            bgSound[0] = MediaPlayer.create(this, R.raw.music1);
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
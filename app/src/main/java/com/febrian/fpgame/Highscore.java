package com.febrian.fpgame;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

public class Highscore extends Activity {

    final MediaPlayer[] bgSound = {null};
    boolean musicOn = true;
    private Button musicOnOff;
    Context context;
    Animation anim;

    View comet1,comet2,comet3, bintang_biasa,bintang_bulat;

    RecyclerView rv;
    ArrayList<Data> data = new ArrayList<>();
    DataAdapter dataAdapter;

    DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_highscore);

        Button back = findViewById(R.id.back);
        back.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), MainMenu.class)));

        rv = findViewById(R.id.rv);

        reference = FirebaseDatabase.getInstance().getReference();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Data d = dataSnapshot.getValue(Data.class);
                    data.add(d);
                }
                System.out.println("Data "+data.size());

                Collections.sort(data, new Comparator<Data>() {
                    @Override
                    public int compare(Data o1, Data o2) {
                        return Integer.valueOf(o2.score).compareTo(o1.score);
                    }
                });



                rv.setLayoutManager(new LinearLayoutManager(Highscore.this));
                rv.setHasFixedSize(true);
                dataAdapter = new DataAdapter(Highscore.this, data);
                rv.setAdapter(dataAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("Data : "+ error);
            }
        });

        comet1 = findViewById(R.id.comet1);
        comet2 = findViewById(R.id.comet2);
        comet3 = findViewById(R.id.comet3);
        bintang_biasa = findViewById(R.id.bintang_biasa);
        bintang_bulat = findViewById(R.id.bintang_bulat);

        back.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), MainMenu.class));
            finish();
        });

        comet1.setAnimation(AnimationUtils.loadAnimation(this, R.anim.comet1_anim));
        comet2.setAnimation(AnimationUtils.loadAnimation(this, R.anim.comet2_anim));
        comet3.setAnimation(AnimationUtils.loadAnimation(this, R.anim.comet3_anim));

        bintang_biasa.setAnimation(AnimationUtils.loadAnimation(this, R.anim.bintang1_anim));
        bintang_bulat.setAnimation(AnimationUtils.loadAnimation(this, R.anim.bintang2_anim));

        context = this;
        musicOnOff = findViewById(R.id.music_on_off);

        bgSound[0] = MediaPlayer.create(context, R.raw.music1);
        bgSound[0].setLooping(true);
        bgSound[0].start();

        musicOnOff.setOnClickListener(v -> {
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
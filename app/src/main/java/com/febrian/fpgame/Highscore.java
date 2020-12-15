package com.febrian.fpgame;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

public class Highscore extends Activity {

    private Button back;
    ImageView comet1,comet2,comet3, bintang_biasa,bintang_bulat;

    RecyclerView rv;
    ArrayList<Data> data = new ArrayList<>();
    DataAdapter dataAdapter;

    DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_highscore);

        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainMenu.class));
            }
        });

        rv = findViewById(R.id.rv);

        reference = FirebaseDatabase.getInstance().getReference();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Data d = dataSnapshot.getValue(Data.class);
                    data.add(d);
                }

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

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainMenu.class));
                finish();
            }
        });

        comet1.setAnimation(AnimationUtils.loadAnimation(this, R.anim.comet1_anim));
        comet2.setAnimation(AnimationUtils.loadAnimation(this, R.anim.comet2_anim));
        comet3.setAnimation(AnimationUtils.loadAnimation(this, R.anim.comet3_anim));

        bintang_biasa.setAnimation(AnimationUtils.loadAnimation(this, R.anim.bintang1_anim));
        bintang_bulat.setAnimation(AnimationUtils.loadAnimation(this, R.anim.bintang2_anim));
    }
}
package com.febrian.fpgame;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import static com.febrian.fpgame.Helper.KEYLOGIN;
import static com.febrian.fpgame.Helper.keylogin;

public class EditProfile extends Activity {

    private EditText edtUsername, edtPassword, edtBio;
    private TextView tv_score;
    private Button update;
    private String username, password, bio, imageOld;
    long score;
    private DatabaseReference reference;
    private StorageReference storage;

    private de.hdodenhof.circleimageview.CircleImageView image;
    private Button addImg;

    private Uri uri;
    private int max_photo = 1;

    final MediaPlayer[] bgSound = {null};
    Button musicOnOff, back;

    boolean musicOn = true;
    Animation anim;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        //GetID
        edtUsername = findViewById(R.id.edt_username_update);
        edtPassword = findViewById(R.id.edt_password_update);
        edtBio = findViewById(R.id.edt_bio);
        tv_score = findViewById(R.id.tv_score);
        update = findViewById(R.id.update);
        image = findViewById(R.id.img);
        addImg = findViewById(R.id.add_image);
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

        back = findViewById(R.id.back);
        back.setOnClickListener(v->{
            startActivity(new Intent(getApplicationContext(), MainMenu.class));
            finish();
        });

        SharedPreferences sharedPreferences = getSharedPreferences(KEYLOGIN, MODE_PRIVATE);
        String getUsername = sharedPreferences.getString(keylogin, "");

        reference = FirebaseDatabase.getInstance().getReference().child("Users");
        storage = FirebaseStorage.getInstance().getReference().child("ImageUsers");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                edtUsername.setText(snapshot.child(getUsername).child("username").getValue().toString());
                edtPassword.setText(snapshot.child(getUsername).child("password").getValue().toString());
                edtBio.setText(snapshot.child(getUsername).child("bio").getValue().toString());
                score = (long)snapshot.child(getUsername).child("score").getValue();
                tv_score.setText("Your Score : " + score);
                imageOld = snapshot.child(getUsername).child("url_image").getValue().toString();
                Picasso.get().load(imageOld).into(image);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        addImg.setOnClickListener(v -> {
            findImage();

        });

        update.setOnClickListener(v -> {

            update.setEnabled(false);
            update.setText("Loading...");
            username = edtUsername.getText().toString().trim();
            password = edtPassword.getText().toString().trim();
            bio = edtBio.getText().toString().trim();
            final String[] newImage = new String[1];

            if (uri != null) {
                StorageReference storageReference = storage.child(System.currentTimeMillis() + "." + getFileExtension(uri));
                storageReference.putFile(uri).addOnSuccessListener(taskSnapshot -> {
                    storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                        newImage[0] = uri.toString();
                        if (!username.equals(getUsername)) {
                            DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference().child("Users").child(username);
                            reference2.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                    if(snapshot.exists()){
                                        System.out.println("Yes");
                                        Toast.makeText(getApplicationContext(), "Username tidak bisa digunakan!", Toast.LENGTH_LONG).show();
                                        update.setEnabled(true);
                                        update.setText("Update");
                                    }else {
                                        snapshot.getRef().child("username").setValue(username);
                                        snapshot.getRef().child("password").setValue(password);
                                        snapshot.getRef().child("score").setValue(Long.valueOf((long)score));
                                        snapshot.getRef().child("bio").setValue(bio);
                                        if (newImage[0].equals(""))
                                            snapshot.getRef().child("url_image").setValue(imageOld);
                                        else
                                            snapshot.getRef().child("url_image").setValue(newImage[0]);
                                        sharedPreferences.edit().clear().commit();
                                        reference.getRef().child(getUsername).removeValue();
                                        update.setEnabled(true);
                                        update.setText("Update");
                                        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                        } else {
                            reference.getRef().child(getUsername).child("username").setValue(username);
                            reference.getRef().child(getUsername).child("password").setValue(password);
                            reference.getRef().child(getUsername).child("score").setValue(Long.valueOf((long)score));
                            reference.getRef().child(getUsername).child("bio").setValue(bio);
                            if (newImage[0].equals(""))
                                reference.getRef().child(username).child("url_image").setValue(imageOld);
                            else
                                reference.getRef().child(username).child("url_image").setValue(newImage[0]);
                            update.setEnabled(true);
                            update.setText("Update");
                        }
                    });
                });
            } else {
                newImage[0] = imageOld;
                if (!username.equals(getUsername)) {
                    DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference().child("Users").child(username);
                    reference2.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                System.out.println("Yes");
                                Toast.makeText(getApplicationContext(), "Username tidak bisa digunakan!", Toast.LENGTH_LONG).show();
                                update.setEnabled(true);
                                update.setText("Update");
                            }else {
                                snapshot.getRef().child("username").setValue(username);
                                snapshot.getRef().child("password").setValue(password);
                                snapshot.getRef().child("score").setValue(Long.valueOf((long)score));
                                snapshot.getRef().child("bio").setValue(bio);
                                if (newImage[0].equals(""))
                                    snapshot.getRef().child("url_image").setValue(imageOld);
                                else
                                    snapshot.getRef().child("url_image").setValue(newImage[0]);
                                sharedPreferences.edit().clear().commit();
                                reference.getRef().child(getUsername).removeValue();
                                update.setEnabled(true);
                                update.setText("Update");
                                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                } else {
                    reference.getRef().child(getUsername).child("username").setValue(username);
                    reference.getRef().child(getUsername).child("password").setValue(password);
                    reference.getRef().child(getUsername).child("score").setValue(Long.valueOf((long)score));
                    reference.getRef().child(getUsername).child("bio").setValue(bio);
                    if (newImage[0].equals(""))
                        reference.getRef().child(username).child("url_image").setValue(imageOld);
                    else
                        reference.getRef().child(username).child("url_image").setValue(newImage[0]);
                    update.setEnabled(true);
                    update.setText("Update");
                }
            }
        });
    }

    String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void findImage() {
        Intent img = new Intent();
        img.setType("image/*");
        img.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(img, max_photo);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == max_photo && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            uri = data.getData();
            Picasso.get().load(uri).centerCrop().fit().into(image);
        }
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
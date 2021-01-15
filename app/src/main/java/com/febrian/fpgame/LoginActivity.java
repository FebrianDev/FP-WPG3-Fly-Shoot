package com.febrian.fpgame;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.febrian.fpgame.Helper.KEYLOGIN;
import static com.febrian.fpgame.Helper.keylogin;

public class LoginActivity extends Activity {

    private EditText edtUsername, edtPassword;
    private Button login;
    private String username, password;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtUsername = findViewById(R.id.edt_username_login);
        edtPassword = findViewById(R.id.edt_password_login);


        login = findViewById(R.id.login);
        login.setOnClickListener(v -> {
            login.setEnabled(false);
            login.setText("Loading...");
            username = edtUsername.getText().toString().trim();
            password = edtPassword.getText().toString().trim();

            reference = FirebaseDatabase.getInstance().getReference().child("Users").child(username);
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        String pwd  = snapshot.child("password").getValue().toString();
                        if(pwd.equals(password)){

                            SharedPreferences sharedPreferences = getSharedPreferences(KEYLOGIN, MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(keylogin, username);
                            editor.apply();

                            login.setEnabled(true);
                            login.setText("Login");
                            Intent intent = new Intent(getApplicationContext(), MainMenu.class);
                            startActivity(intent);
                            finish();
                        }else{
                            login.setEnabled(true);
                            login.setText("Login");
                            Toast.makeText(getApplicationContext(), "Password Salah!", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        login.setEnabled(true);
                        login.setText("Login");
                        Toast.makeText(getApplicationContext(), "Username Tidak Ada!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getApplicationContext(), String.valueOf(error), Toast.LENGTH_SHORT).show();
                }
            });

        });
    }
}
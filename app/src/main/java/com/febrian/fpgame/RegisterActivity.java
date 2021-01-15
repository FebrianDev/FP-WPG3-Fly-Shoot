package com.febrian.fpgame;

import androidx.annotation.NonNull;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.febrian.fpgame.Helper.KEYLOGIN;
import static com.febrian.fpgame.Helper.keylogin;

public class RegisterActivity extends Activity {

    private EditText edtUsername, edtPassword;
    private Button register;
    private String username, password;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        edtUsername = findViewById(R.id.edt_username);
        edtPassword = findViewById(R.id.edt_password);

        register = findViewById(R.id.register);
        register.setOnClickListener(v -> {

            register.setEnabled(false);
            register.setText("Loading...");

            username = edtUsername.getText().toString().trim();
            password = edtPassword.getText().toString().trim();

            boolean success = true;
            reference = FirebaseDatabase.getInstance().getReference().child("Users").child(username);

            if (username.equals("")) {
                edtUsername.setError("Username harus diisi!");
                success = false;
            } else {
                success = true;
            }

            if (password.equals("")) {
                edtPassword.setError("Password harus diisi!");
                success = false;
            } else if (password.length() < 4) {
                edtPassword.setError("Password minimal 4 karakter!");
                success = false;
            } else {
                success = true;
            }
            if (success) {

                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.exists()) {
                            edtUsername.setError("Username Sudah Ada!");
                        } else {

                            SharedPreferences sharedPreferences = getSharedPreferences(KEYLOGIN, MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(keylogin, username);
                            editor.apply();

                            snapshot.getRef().child("username").setValue(username);
                            snapshot.getRef().child("password").setValue(password);
                            snapshot.getRef().child("score").setValue(Long.valueOf((long)0));
                            snapshot.getRef().child("bio").setValue("");
                            snapshot.getRef().child("url_image").setValue("https://firebasestorage.googleapis.com/v0/b/fp-game.appspot.com/o/ImageUsers%2Ficon_nopic.png?alt=media&token=2d35a2ef-6d8c-4515-a917-646327f77d17");

                            register.setEnabled(true);
                            register.setText("Register");
                            Intent intent = new Intent(getApplicationContext(), MainMenu.class);
                            startActivity(intent);
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getApplicationContext(), String.valueOf(error), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
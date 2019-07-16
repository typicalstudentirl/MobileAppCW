package com.example.mobileappcw;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    TextView editText_Login_email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialise firebase auth object
        firebaseAuth = FirebaseAuth.getInstance();

        // Find Login Text view to set focus instead of focusing on password first
        editText_Login_email = (TextView) findViewById(R.id.editText_Login_email);
        editText_Login_email.requestFocus();
    }

    public void loginUser(View view) {
        String email = ((EditText) findViewById(R.id.editText_Login_email)).getText().toString();
        String password = ((EditText) findViewById(R.id.editText_Login_password)).getText().toString();

        // Are fields empty/valid
        if(TextUtils.isEmpty(email)){
            Toast.makeText(this, "Enter email", Toast.LENGTH_LONG).show();
            return;
        }
        if(TextUtils.isEmpty(password)){
            Toast.makeText(this, "Enter password", Toast.LENGTH_LONG).show();
            return;
        }

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this,
                                    "Login successful", Toast.LENGTH_LONG).show();
                            finish();
                            startActivity(new Intent(getApplicationContext(), WeatherActivity.class));
                        }else{
                            Toast.makeText(LoginActivity.this,
                                    "Error logging in",Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    public void goRegister(View view){
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
    }
}

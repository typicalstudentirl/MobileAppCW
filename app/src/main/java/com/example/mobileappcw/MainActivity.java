package com.example.mobileappcw;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;

public class MainActivity extends AppCompatActivity {
    //defining FireBaseauth object
    private FirebaseAuth firebaseAuth;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(!isNetworkAvailable()){
            Toast.makeText(MainActivity.this, "Network connection unavailable.",
                    Toast.LENGTH_LONG).show();
        }

        firebaseAuth = firebaseAuth.getInstance();
        progressBar = (ProgressBar) findViewById(R.id.progressBar_UsrReg);
        progressBar.setVisibility(View.INVISIBLE);
    }

    public void registerUser(View view) {
        // getting email and password from edit texts
        String email = ((EditText) findViewById(R.id.editText_UserEmail)).getText().toString();
        String password = ((EditText) findViewById(R.id.editText_Password)).getText().toString();
        String conPassword = ((EditText) findViewById(R.id.editText_conPassword)).getText().toString();

        //Use validate registration method to confirm passwords match and fields != NULL
        if (!validReg(email, password, conPassword)){
            return;
        }

        //if the email and password are not empty, display progress bar
        progressBar.setVisibility(View.VISIBLE);
        //creating a new user
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //if success
                        if (task.isSuccessful()) {
                            progressBar.setVisibility(View.GONE);

                            Toast.makeText(MainActivity.this, "Registration successful",
                                    Toast.LENGTH_LONG).show();
                            startActivity(new Intent(MainActivity.this, LoginActivity.class));
                            finish();
                        } else {
                            FirebaseAuthException e = (FirebaseAuthException) task.getException();
                            Toast.makeText(MainActivity.this, "Registration failed : "
                                    + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    public void goLogin(View view){
        startActivity(new Intent (MainActivity.this, LoginActivity.class));
    }

    public boolean validReg(String uName, String pWord, String conPWord) {
        if (TextUtils.isEmpty(uName)) {
            Toast.makeText(this, "Please enter email", Toast.LENGTH_LONG).show();
            return false;
        }
        if (TextUtils.isEmpty(pWord)) {
            Toast.makeText(this, "Please enter password", Toast.LENGTH_LONG).show();
            return false;
        }
        //Confirm passwords match
        if (TextUtils.equals(pWord, conPWord) == false) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_LONG).show();
            return false;
        }
        else return true;
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}


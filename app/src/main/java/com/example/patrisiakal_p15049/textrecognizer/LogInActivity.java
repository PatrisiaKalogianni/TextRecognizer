package com.example.patrisiakal_p15049.textrecognizer;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class LogInActivity extends AppCompatActivity {

    EditText email, pass;
    Button btn_logIn;
    TextView tv_signUp;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        // Initialize Firebase Auth.
        mAuth = FirebaseAuth.getInstance();
        email = findViewById(R.id.editText5);
        pass = findViewById(R.id.editText7);
        btn_logIn = findViewById(R.id.button2);
        tv_signUp = findViewById(R.id.textView);


        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser mFbUser = mAuth.getCurrentUser();
                if(mFbUser !=null){
                    Intent i = new Intent(LogInActivity.this, MainActivity.class);
                    startActivity(i);
                }
                else{
                }
            }
        };
    }

    public void logInBtn(View v){
        String eml = email.getText().toString();
        String pw = pass.getText().toString();
        if(eml.isEmpty()){
            email.setError("Please enter email");
            email.requestFocus();
        }
        else if(pw.isEmpty()){
            pass.setError("Please enter password");
            pass.requestFocus();
        }
        else if(eml.isEmpty() && pw.isEmpty()){
            Toast.makeText(LogInActivity.this, "Fields are empty", Toast.LENGTH_SHORT).show();
        }
        else if(!(eml.isEmpty() && pw.isEmpty())){
            mAuth.signInWithEmailAndPassword(eml, pw).addOnCompleteListener(LogInActivity.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(!task.isSuccessful()){
                        Toast.makeText(LogInActivity.this, "Login Error, Please try again!", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Intent itoMain = new Intent(LogInActivity.this, MainActivity.class);
                        startActivity(itoMain);
                    }
                }
            });
        }
        else{
            Toast.makeText(LogInActivity.this, "Error occured! Please try again!", Toast.LENGTH_SHORT).show();

        }
    }

    public void signUpBtn(View v){
        Intent isign = new Intent(LogInActivity.this, SignUpActivity.class);
        startActivity(isign);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);
    }

}

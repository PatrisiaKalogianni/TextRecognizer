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

public class SignUpActivity extends AppCompatActivity {

    EditText email, pass;
    Button btn_signUp;
    TextView tv_signIn;
    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();
        email = findViewById(R.id.editText6);
        pass = findViewById(R.id.editText);
        btn_signUp = findViewById(R.id.button);
        tv_signIn = findViewById(R.id.textView2);



    }

    public void SignUpButton(View v){

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
            Toast.makeText(SignUpActivity.this, "Fields are empty", Toast.LENGTH_SHORT).show();
        }
        else if(!(eml.isEmpty() && pw.isEmpty()) && pw.length()>= 6){
            mAuth.createUserWithEmailAndPassword(eml, pw).addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(!task.isSuccessful()){

                        Toast.makeText(SignUpActivity.this, "Sign in was unsuccessful. Please try again", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                    }
                }
            });
        }
        else if(!(eml.isEmpty() && pw.isEmpty()) && pw.length()< 6){
            Toast.makeText(SignUpActivity.this, "Error! Your password must contain at least 6 characters!", Toast.LENGTH_SHORT).show();

        }
        else{
            Toast.makeText(SignUpActivity.this, "Error occured! Please try again!", Toast.LENGTH_SHORT).show();

        }
    }

    public void SignInTextView(View v){
        Intent intent = new Intent(SignUpActivity.this, LogInActivity.class);
        startActivity(intent);
    }

}

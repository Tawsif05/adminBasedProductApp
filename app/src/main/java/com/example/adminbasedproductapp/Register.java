package com.example.adminbasedproductapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

public class Register extends AppCompatActivity  implements View.OnClickListener{

    private EditText signUpEmailEditText, signUpPasswordEditText;
    private TextView signInTextView;
    private Button signUpButton;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        this.setTitle("Sign Up Activity");

        mAuth = FirebaseAuth.getInstance();

        signUpEmailEditText = findViewById(R.id.signUpEmailEditTextId);
        signUpPasswordEditText = findViewById(R.id.signUpPasswordEditTextId);
        signInTextView = findViewById(R.id.signInTextViewId);
        signUpButton = findViewById(R.id.signUpButtonId);
        progressBar = findViewById(R.id.progressbarId);

        signInTextView.setOnClickListener(this);
        signUpButton.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.signUpButtonId:
                userRegister();

                break;

            case R.id.signInTextViewId:
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                break;

        }
    }

    private void userRegister() {
        String email = signUpEmailEditText.getText().toString().trim();
        String password = signUpPasswordEditText.getText().toString().trim();
        //checking the validity of the email
        if(email.isEmpty())
        {
            signUpEmailEditText.setError("Enter an email address");
            signUpEmailEditText.requestFocus();
            return;
        }

        if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            signUpEmailEditText.setError("Enter a valid email address");
            signUpEmailEditText.requestFocus();
            return;
        }

        //checking the validity of the password
        if(password.isEmpty())
        {
            signUpPasswordEditText.setError("Enter a password");
            signUpPasswordEditText.requestFocus();
            return;
        }
        if(password.length()<6){
            signUpPasswordEditText.setError("Minimum length of a password should be 6");
            signUpPasswordEditText.requestFocus();
        }

        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressBar.setVisibility(View.GONE);
                if (task.isSuccessful()) {

                    Toast.makeText(getApplicationContext(),"Registration is Successful",Toast.LENGTH_SHORT).show();
                    finish();
                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                } else {
                    if(task.getException() instanceof FirebaseAuthUserCollisionException){
                        Toast.makeText(getApplicationContext(),"User is already Registered",Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(getApplicationContext(),"Error: "+task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                    }


                }
            }
        });

    }
}
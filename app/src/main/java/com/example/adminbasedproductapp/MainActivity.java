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

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText signInEmailEditText, signInPasswordEditText;
    private TextView signUpTextView;
    private Button signInButton;
    private ProgressBar progressbar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setTitle("Sign In Activity");

        mAuth = FirebaseAuth.getInstance();

        signInEmailEditText = findViewById(R.id.signInEmailEditTextId);
        signInPasswordEditText = findViewById(R.id.signInPasswordEditTextId);
        signUpTextView = findViewById(R.id.signUpTextViewId);
        signInButton = findViewById(R.id.signInButtonId);
        progressbar = findViewById(R.id.progressbarId);


        signUpTextView.setOnClickListener(this);
        signInButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.signInButtonId:
                userLogin();
                break;

            case R.id.signUpTextViewId:
                Intent intent = new Intent(getApplicationContext(), Register.class);
                startActivity(intent);
                break;

        }
    }

    private void userLogin() {

        String email = signInEmailEditText.getText().toString().trim();
        String password = signInPasswordEditText.getText().toString().trim();
        //checking the validity of the email
        if(email.isEmpty())
        {
            signInEmailEditText.setError("Enter an email address");
            signInEmailEditText.requestFocus();
            return;
        }

        if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            signInEmailEditText.setError("Enter a valid email address");
            signInEmailEditText.requestFocus();
            return;
        }

        //checking the validity of the password
        if(password.isEmpty())
        {
            signInPasswordEditText.setError("Enter a password");
            signInPasswordEditText.requestFocus();
            return;
        }
        if(password.length()<6){
            signInPasswordEditText.setError("Minimum length of a password should be 6");
            signInPasswordEditText.requestFocus();
        }
        progressbar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressbar.setVisibility(View.GONE);
                if(task.isSuccessful()  && email.equalsIgnoreCase("tawsif@gmail.com") && password.equalsIgnoreCase("tawsif1234")){
                    finish();
                    Intent intent = new Intent(getApplicationContext(),adminHomePage.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
                else if(task.isSuccessful() && !email.equalsIgnoreCase("tawsif@gmail.com") && !password.equalsIgnoreCase("tawsif1234")){
                    finish();
                    Intent intent = new Intent(getApplicationContext(),adminHomePage.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(getApplicationContext(),"Login Unsuccessful",Toast.LENGTH_SHORT).show();

                }
            }
        });
    }
}
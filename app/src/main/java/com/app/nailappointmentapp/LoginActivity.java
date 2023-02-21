package com.app.nailappointmentapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private TextView forgotPasswordTextView;
    private FirebaseAuth firebaseAuth;
    private ProgressBar progressBar;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEditText = findViewById(R.id.emailSignIn);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.Login);
        forgotPasswordTextView = findViewById(R.id.forgotpassword);
        progressBar = findViewById(R.id.progressbars);
        progressBar.setVisibility(View.GONE);
        progressDialog = new ProgressDialog(this);

        firebaseAuth = FirebaseAuth.getInstance();

        loginButton.setOnClickListener(v -> {
            final String email = emailEditText.getText().toString();
            if (email.isEmpty()) {
                emailEditText.setError("Email is empty");
                emailEditText.requestFocus();
                return;
            }
            String password = passwordEditText.getText().toString().trim();
            if (password.isEmpty()) {
                passwordEditText.setError("Enter Password");
                passwordEditText.requestFocus();
                return;
            }
            if (password.length() < 6) {
                passwordEditText.setError("at least 6 digits");
                passwordEditText.requestFocus();
                return;
            }
            validateAndSignIn(emailEditText.getText().toString(), passwordEditText.getText().toString());
        });

        forgotPasswordTextView.setOnClickListener(v -> {
            resetPassword();
        });

        findViewById(R.id.login).setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
        });
    }

    private void resetPassword() {
        String resetEmail = emailEditText.getText().toString().trim();

        if (resetEmail.isEmpty()) {
            emailEditText.setError("Email is empty");
            emailEditText.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        firebaseAuth.sendPasswordResetEmail(resetEmail).addOnCompleteListener(task -> {
            progressBar.setVisibility(View.GONE);

            if (task.isSuccessful()) {
                Toast.makeText(LoginActivity.this, "A password reset email has been sent", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(LoginActivity.this, "Failed to send reset email", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void validateAndSignIn(String userEmail, String userPassword) {
        progressDialog.setMessage("Please Wait...");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener(task -> {
            progressDialog.dismiss();

            if (task.isSuccessful()) {
                Toast.makeText(LoginActivity.this, "Login succeeded :)", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(LoginActivity.this, MainMenuActivity.class));
            } else {
                Toast.makeText(LoginActivity.this, "Login failed :(", Toast.LENGTH_SHORT).show();
            }
        });
    }
}


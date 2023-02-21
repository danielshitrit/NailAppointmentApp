package com.app.nailappointmentapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.auth.User;

public class RegisterActivity extends AppCompatActivity {

    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText nameRegister;
    private EditText phoneRegister;
    private Button registerBtn;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private LinearLayout login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initViews();
        initFirebaseAuth();

        registerBtn.setOnClickListener(v -> registerUser());
        login.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), LoginActivity.class)));
    }

    private void initViews() {
        nameRegister = findViewById(R.id.nameRegister);
        phoneRegister = findViewById(R.id.phoneRegister);
        emailEditText = findViewById(R.id.emailRegister);
        passwordEditText = findViewById(R.id.passwordRegister);
        registerBtn = findViewById(R.id.button_register);
        progressBar = findViewById(R.id.progressbar);
        progressBar.setVisibility(View.GONE);
        login = findViewById(R.id.login);
    }

    private void initFirebaseAuth() {
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // User is already signed in, do something
        }
    }

    private void registerUser() {
        final String email = emailEditText.getText().toString();
        final String name = nameRegister.getText().toString();
        final String phone = phoneRegister.getText().toString();
        String password = passwordEditText.getText().toString().trim();

        if (email.isEmpty()) {
            emailEditText.setError("Email is empty");
            emailEditText.requestFocus();
            return;
        }
        if (name.isEmpty()) {
            nameRegister.setError("Name is empty");
            nameRegister.requestFocus();
            return;
        }
        if (phone.isEmpty()) {
            phoneRegister.setError("Phone is empty");
            phoneRegister.requestFocus();
            return;
        }
        if (phone.length() < 10) {
            phoneRegister.setError("not valid");
            phoneRegister.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("not valid Email");
            emailEditText.requestFocus();
            return;
        }
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

        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        saveUserDetailsToFirebase(name, phone, email);
                    } else {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(RegisterActivity.this, "Registration Failed :(", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void saveUserDetailsToFirebase(String name, String phone, String email) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            return;
        }

        String resultEmail = email.replace(".", "");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference userRef = database.getReference("Users").child(resultEmail).child("UserDetails");
        userRef.child("email").setValue(email);
        userRef.child("phone").setValue(phone);
        userRef.child("name").setValue(name).addOnCompleteListener(task -> {
            progressBar.setVisibility(View.GONE);
            if (task.isSuccessful()) {
                Toast.makeText(RegisterActivity.this, "Registration succeeded :)", Toast.LENGTH_LONG).show();
                startActivity(new Intent(RegisterActivity.this, MainMenuActivity.class));
            }
        });
    }
}

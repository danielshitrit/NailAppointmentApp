package com.app.nailappointmentapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainMenuActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private TextView tvName;
    private CardView cardView;
    private CardView cardCreateAppointment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        mAuth = FirebaseAuth.getInstance();

        tvName = findViewById(R.id.tvName);
        cardView = findViewById(R.id.cardView);
        cardCreateAppointment = findViewById(R.id.cardCreateSummons);

        cardView.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), ViewAppointmentsActivity.class));
        });

        cardCreateAppointment.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), SelectServiceActivity.class));
        });

        findViewById(R.id.logout).setOnClickListener(v -> {
            mAuth.signOut();
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        });

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
            return;
        }

        String email = currentUser.getEmail().replace(".", "");
        FirebaseDatabase.getInstance().getReference("Users")
                .child(email)
                .child("UserDetails")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            String name = snapshot.child("name").getValue(String.class);
                            if (name != null) {
                                tvName.setText("Hi, " + name);
                            }
                        } else {
                            Toast.makeText(MainMenuActivity.this, "User details not found", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(MainMenuActivity.this, "Failed to load user details", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}

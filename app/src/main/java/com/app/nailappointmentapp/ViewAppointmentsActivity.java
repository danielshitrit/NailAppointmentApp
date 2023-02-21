package com.app.nailappointmentapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ViewAppointmentsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FirebaseRecyclerAdapter recyclerAdapter;
    private String date;
    private ProgressBar progress_bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_appointment);

        Intent intent = getIntent();
        if (getIntent().hasExtra("date")) {
            date = intent.getStringExtra("date");
        }

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        progress_bar = findViewById(R.id.progress_bar);

        setupRecyclerView();
    }

    public void setupRecyclerView(){
        //query
        String UserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("appointments").child(UserId);
        com.google.firebase.database.Query firebaseSearchQuery = databaseReference;
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()){
                    progress_bar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        //firebase recyclerview
        FirebaseRecyclerOptions<Appointment> options =
                new FirebaseRecyclerOptions.Builder<Appointment>().setQuery(firebaseSearchQuery, Appointment.class).build();

        recyclerAdapter = new FirebaseRecyclerAdapter<Appointment, TodoViewHolder>(options) {
            @NonNull
            @Override
            public TodoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                // attach layout to RecyclerView
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false);
                return new TodoViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(TodoViewHolder holder, int position, Appointment model) {
                progress_bar.setVisibility(View.GONE);

                // set data in views
                holder.serviceTextView.setText(model.getService());
                holder.timestampTextView.setText("Date: "+model.getDate());
                holder.tvTime.setText("Time: "+model.getTime());
                holder.tvPrice.setText("Price: "+model.getPrice());

                holder.btnEdit.setOnClickListener((v)->{
                    Intent intent = new Intent(getApplicationContext(), CalendarActivity.class);
                    intent.putExtra("service",model.getService());
                    intent.putExtra("date",model.getDate());
                    intent.putExtra("time",model.getTime());
                    intent.putExtra("price",model.getPrice());
                    intent.putExtra("docId",model.getId());
                    startActivity(intent);

                });
                holder.btnCancel.setOnClickListener((v)->{

                    databaseReference.child(getRef(position).getKey()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(ViewAppointmentsActivity.this, "Appointment Deleted", Toast.LENGTH_SHORT).show();
                        }
                    });
                });
            }
        };


        recyclerAdapter.startListening();
        // add adapter to recyclerview

        recyclerView.setAdapter(recyclerAdapter);
    }
    @Override
    protected void onStart() {
        super.onStart();
        recyclerAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //recyclerAdapter.stopListening();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //recyclerAdapter.notifyDataSetChanged();
    }

    //View Holder class
    public static class TodoViewHolder extends RecyclerView.ViewHolder{
        TextView serviceTextView,timestampTextView,tvTime,tvPrice;
        Button btnCancel,btnEdit;
        public TodoViewHolder(@NonNull View itemView) {
            super(itemView);
            serviceTextView = itemView.findViewById(R.id.tvService);
            timestampTextView = itemView.findViewById(R.id.tvDate);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            btnCancel = itemView.findViewById(R.id.btnCancel);
            btnEdit = itemView.findViewById(R.id.btnEdit);
        }
    }
}

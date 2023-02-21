package com.app.nailappointmentapp;

import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CalendarActivity extends AppCompatActivity {

    private TextView tvDate;
    private TextView tvTime;
    private TextView selectTime;
    private TextView page_title;
    private TextView selectService;
    private String date;
    private String newDate;
    private String day;
    private String time;
    private String dateStr;
    private String docID;
    private String service;
    private String price;
    boolean isEditMode = false;
    private ProgressDialog progressDialog;
    private int hour24hrs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait..");
        Intent intent = getIntent();
        if (getIntent().hasExtra("date")) {
            date = intent.getStringExtra("date");
        } else {
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
            date = formatter.format(new Date());
        }

        selectService = findViewById(R.id.selectService);
        if (getIntent().hasExtra("service")) {
            service = intent.getStringExtra("service");
            selectService.setText(service);
        }
        if (getIntent().hasExtra("price")) {
            price = intent.getStringExtra("price");
        }
        if (getIntent().hasExtra("time")) {
            time = intent.getStringExtra("time");
        }
        tvDate = findViewById(R.id.tvDate);
        tvTime = findViewById(R.id.tvTime);
        selectTime = findViewById(R.id.selectTime);
        page_title = findViewById(R.id.page_title);

        newDate = date;

        //receive data
        dateStr = getIntent().getStringExtra("date");
        docID = getIntent().getStringExtra("docId");

        if (docID!=null && !docID.isEmpty()){
            isEditMode = true;
            tvTime.setText(time);
            selectTime.setText(time);
            tvDate.setText(dateStr);
        }

        if(isEditMode){
            page_title.setText("Edit Appointment");
        }

        String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        tvDate.setText(date);
        SimpleDateFormat inFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date date2 = null;
        try {
            date2 = inFormat.parse(newDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat outFormat = new SimpleDateFormat("EEEE");
        day = outFormat.format(date2);

        Calendar calendar = Calendar.getInstance();
        hour24hrs = calendar.get(Calendar.HOUR_OF_DAY);

        CalendarView calendarView = findViewById(R.id.calendarView);

        calendarView.setMinDate(System.currentTimeMillis() - 1000);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int day) {
                String dayString = String.valueOf(day);
                if (day < 10) {
                    dayString = "0" + dayString;
                }

                String monthString = String.valueOf(month + 1);
                if ((month + 1) < 10) {
                    monthString = "0" + monthString;
                }

                newDate = dayString + "-" + monthString + "-" + String.valueOf(year);
                tvDate.setText(newDate);


                SimpleDateFormat inFormat = new SimpleDateFormat("dd-MM-yyyy");
                Date date2 = null;
                try {
                    date2 = inFormat.parse(newDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                SimpleDateFormat outFormat = new SimpleDateFormat("EEEE");
                CalendarActivity.this.day = outFormat.format(date2);
            }
        });

        selectTime.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(CalendarActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        selectTime.setText( selectedHour + ":" + selectedMinute);
                        tvTime.setText(selectedHour + ":" + selectedMinute);
                        hour24hrs = selectedHour;
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();

            }
        });

        Button bookAppointment = findViewById(R.id.bookAppointment);
        bookAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!tvDate.getText().toString().isEmpty()){

                    //Calendar calendar = Calendar.getInstance();
                    //int hour24hrsCurrentTime = calendar.get(Calendar.HOUR_OF_DAY);

                    String customerDate = tvDate.getText().toString()+" "+tvTime.getText().toString()+":00";
                    SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                    try {
                        Date date = format.parse(customerDate);

                        long cusLong = date.getTime();
                        long currLong  = new Date().getTime();

                        System.out.println(date);

                        if(cusLong>currLong){
                            if(day.equals("Sunday") || day.equals("Monday")|| day.equals("Tuesday")|| day.equals("Wednesday")|| day.equals("Thursday")){

                                if(hour24hrs>=10 && hour24hrs<=18){
                                    saveNoteToFirebase(tvTime.getText().toString(),tvDate.getText().toString(),selectService.getText().toString());
                                }else{
                                    Toast.makeText(CalendarActivity.this, "Operating hours are 10:00-18:00", Toast.LENGTH_SHORT).show();
                                }
                            }else{
                                Toast.makeText(CalendarActivity.this, "Operating days are Sunday to Thursday", Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            Toast.makeText(CalendarActivity.this, "Operating days are Sunday to Thursday at 10:00-18:00", Toast.LENGTH_SHORT).show();
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    //if data is today and then hour must be greater than or equal to
                }
            }
        });
    }

    void saveNoteToFirebase(String time,String date,String service){
        DocumentReference documentReference;
        if (isEditMode){
            progressDialog.show();
            FirebaseUser currentUser= FirebaseAuth.getInstance().getCurrentUser();
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("appointments").child(currentUser.getUid()).child(docID);
            databaseReference.child("service").setValue(service);
            databaseReference.child("price").setValue(price);
            databaseReference.child("date").setValue(date);
            databaseReference.child("time").setValue(time).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {

                    progressDialog.dismiss();

                    Utility.showToast(CalendarActivity.this,"Appointment created successfully");
                    Intent intent = new Intent(getApplicationContext(),ViewAppointmentsActivity.class);
                    startActivity(intent);
                    finish();
                }
            });

        }else{
            progressDialog.show();

            FirebaseUser currentUser= FirebaseAuth.getInstance().getCurrentUser();
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("appointments").child(currentUser.getUid()).child(time+date);
            //DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("appointments");
            //Query query = databaseReference.orderByChild("date_time").equalTo(date + "_" + time);
            //query.addListenerForSingleValueEvent(new ValueEventListener() {
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if(snapshot.exists()){
                        Toast.makeText(CalendarActivity.this, "Appointment already exist at that time", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }else{
                        databaseReference.child("service").setValue(service);
                        databaseReference.child("price").setValue(price);
                        databaseReference.child("date").setValue(date);
                        databaseReference.child("id").setValue(databaseReference.getKey());

                        databaseReference.child("time").setValue(time).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                progressDialog.dismiss();

                                Utility.showToast(CalendarActivity.this,"Appointment created successfully");
                                Intent intent = new Intent(getApplicationContext(), MainMenuActivity.class);
                                startActivity(intent);
                                finish();

                            }
                        });
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }
}
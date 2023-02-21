package com.app.nailappointmentapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class SelectServiceActivity extends AppCompatActivity {

    CardView cd1,cd2,cd3,cd4;
    Button btn_next;
    String service,price;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_service);

        btn_next = findViewById(R.id.btn_next);
        cd1 = findViewById(R.id.cd1);
        cd2 = findViewById(R.id.cd2);
        cd3 = findViewById(R.id.cd3);
        cd4 = findViewById(R.id.cd4);

        cd1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cd1.setCardBackgroundColor(Color.parseColor("#DFDFDF"));
                cd2.setCardBackgroundColor(Color.parseColor("#F5F5F5"));
                cd3.setCardBackgroundColor(Color.parseColor("#F5F5F5"));
                cd4.setCardBackgroundColor(Color.parseColor("#F5F5F5"));

                service = "Nail Manicure";
                price = "$20";
            }
        });

        cd2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cd2.setCardBackgroundColor(Color.parseColor("#DFDFDF"));
                cd1.setCardBackgroundColor(Color.parseColor("#F5F5F5"));
                cd3.setCardBackgroundColor(Color.parseColor("#F5F5F5"));
                cd4.setCardBackgroundColor(Color.parseColor("#F5F5F5"));

                service = "Soft Gel Manicure";
                price = "$15";
            }
        });

        cd3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cd3.setCardBackgroundColor(Color.parseColor("#DFDFDF"));
                cd1.setCardBackgroundColor(Color.parseColor("#F5F5F5"));
                cd2.setCardBackgroundColor(Color.parseColor("#F5F5F5"));
                cd4.setCardBackgroundColor(Color.parseColor("#F5F5F5"));

                service = "Hard Gel Manicure";
                price = "$25";
            }
        });

        cd4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cd4.setCardBackgroundColor(Color.parseColor("#DFDFDF"));
                cd1.setCardBackgroundColor(Color.parseColor("#F5F5F5"));
                cd2.setCardBackgroundColor(Color.parseColor("#F5F5F5"));
                cd3.setCardBackgroundColor(Color.parseColor("#F5F5F5"));

                service = "Acrylic Manicure";
                price = "$15";
            }
        });

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(price!=null && service !=null){
                    Intent intent = new Intent(getApplicationContext(),CalendarActivity.class);
                    intent.putExtra("service",service);
                    intent.putExtra("price",price);
                    startActivity(intent);
                }else{
                    Toast.makeText(SelectServiceActivity.this, "Please select service", Toast.LENGTH_SHORT).show();
                }


            }
        });
    }
}
package com.example.manipulatecars;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SecondActivity extends AppCompatActivity implements View.OnClickListener {

    ArrayAdapter<String> adapter;
    ArrayList<String> list;
    Cars cars;
    ListView lvCars;

    Button btnReturn;
    DatabaseReference carsDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        initialize();
    }

    private void initialize() {
        carsDatabase = FirebaseDatabase.getInstance().getReference("Cars");
        list = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, R.layout.car_info, list);


        btnReturn = findViewById(R.id.btnReturn);
        btnReturn.setOnClickListener(this);
        lvCars = findViewById(R.id.lvCars);

        lvCars.setAdapter(adapter);

        carsDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Cars value = snapshot.getValue(Cars.class);
                list.add(value.toString());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        finish();
    }

}
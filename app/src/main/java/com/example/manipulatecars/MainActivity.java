package com.example.manipulatecars;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, ValueEventListener, OnSuccessListener, OnFailureListener {

    EditText edId, edPrice;
    RadioGroup rgBrand;
    CheckBox cbStatus;

    RadioButton rbTo, rbMa, rbHy;
    Button btnAdd, btnUpdate, btnRemove, btnFind, btnList;
    DatabaseReference carsDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialize();
    }

    private void initialize() {
        edId = findViewById(R.id.edId);
        edPrice = findViewById(R.id.edPrice);
        rgBrand = findViewById(R.id.rgBrand);
        cbStatus = findViewById(R.id.cbStatus);
        btnAdd = findViewById(R.id.btnAdd);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnRemove = findViewById(R.id.btnRemove);
        btnFind = findViewById(R.id.btnFind);
        btnList = findViewById(R.id.btnList);
        rbTo = findViewById(R.id.rbToyota);
        rbMa = findViewById(R.id.rbMazda);
        rbHy = findViewById(R.id.rbHyundai);

        btnAdd.setOnClickListener(this);
        btnUpdate.setOnClickListener(this);
        btnRemove.setOnClickListener(this);
        btnFind.setOnClickListener(this);
        btnList.setOnClickListener(this);

        carsDatabase = FirebaseDatabase.getInstance().getReference("Cars");
    }


    @Override
    public void onClick(View view) {
        int viewId = view.getId();

        switch (viewId){
            case R.id.btnAdd:
                addCar(view);
                break;
            case R.id.btnUpdate:
                updateCar(view);
                break;
            case R.id.btnRemove:
                removeCar(view);
                break;
            case R.id.btnFind:
                findCar(view);
                break;
            case R.id.btnList:
                listCar();
                break;

        }
    }



    private void listCar() {
        Intent i = new Intent(this, SecondActivity.class);
        startActivity(i);
    }
    private void clearFields(){
        edId.getText().clear();
        edPrice.getText().clear();
        rgBrand.clearCheck();
        cbStatus.setChecked(false);
        edId.requestFocus();
    }
    private String getRBValue(){
        int brand = rgBrand.getCheckedRadioButtonId();

        String result = null;

        switch (brand){

            case R.id.rbToyota:
                result = "Toyota";
                break;
            case R.id.rbMazda:
                result = "Mazda";
                break;
            case R.id.rbHyundai:
                result = "Hyundai";
                break;
        }
        return result;
    }
    private String getChBValue(){
        String result;
        if (cbStatus.isChecked()){
            result = "Used";
        }else {
            result = "New";
        }

        return result;
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot snapshot) {
        if (snapshot.exists()){

            String id = snapshot.child("id").getValue().toString();
            String brand = snapshot.child("brand").getValue(String.class);
            String status = snapshot.child("status").getValue(String.class);
            String price = snapshot.child("price").getValue().toString();


            if (brand.equals("Toyota")){
                rbTo.setChecked(true);
            }
            if (brand.equals("Mazda")){
                rbMa.setChecked(true);
            }
            if (brand.equals("Hyundai")){
                rbHy.setChecked(true);
            }

            if (status.equals("New")){
               cbStatus.setChecked(false);
            }
            if (status.equals("Used")){
                cbStatus.setChecked(true);
            }
            edId.setText(id);
            edPrice.setText(price);


        }else {
            Toast.makeText(this, "No Car with id: " + edId.getText().toString(), Toast.LENGTH_SHORT).show();
            clearFields();
        }
    }
    private void findCar(View view) {

        String id = edId.getText().toString();
        DatabaseReference carsChild;
        if (id.isEmpty()){
            Snackbar.make(view, "Please, provide Car ID...!", Snackbar.LENGTH_LONG).show();
        }else {
            carsChild = carsDatabase.child(id);
            carsChild.addValueEventListener(this);
            Snackbar.make(view, "The Car id: " + id +"\n has been Fetched successfully", Snackbar.LENGTH_LONG).show();
        }

    }



    @Override
    public void onCancelled(@NonNull DatabaseError error) {
        Toast.makeText(this, error.getMessage(), Toast.LENGTH_SHORT).show();
    }

    private void removeCar(View view) {
        String id = edId.getText().toString();

        if (id.isEmpty()){
            Snackbar.make(view, "Please, provide Car ID...!", Snackbar.LENGTH_LONG).show();
        }else {
            carsDatabase.child(id).removeValue();
            Snackbar.make(view, "The Car id: " + id +"\n has been removed successfully", Snackbar.LENGTH_LONG).show();
            clearFields();
        }


    }
    private void updateCar(View view) {
        try {
            String id = edId.getText().toString();
            String brand = getRBValue();
            String status = getChBValue();
            String price = edPrice.getText().toString();

            Cars cars = new Cars(Integer.parseInt(id), brand, status, Double.parseDouble(price));

            carsDatabase.child(id).setValue(cars);
            Snackbar.make(view, "Updated successfully", Snackbar.LENGTH_LONG).show();
        }catch (Exception ex){
            Snackbar.make(view, ex.getMessage(),Snackbar.LENGTH_LONG).show();
        }

    }
    private void addCar(View view) {
        try {
            String id = edId.getText().toString();
            String brand = getRBValue();
            String status = getChBValue();
            String price = edPrice.getText().toString();

            Cars cars = new Cars(Integer.parseInt(id), brand, status, Double.parseDouble(price));

            carsDatabase.child(id).setValue(cars).addOnSuccessListener(this);
            carsDatabase.child(id).setValue(cars).addOnFailureListener(this);

        }catch (Exception ex){
            Snackbar.make(view, ex.getMessage(), Snackbar.LENGTH_LONG).show();
        }
    }
    @Override
    public void onFailure(@NonNull Exception e) {
        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        clearFields();
    }
    @Override
    public void onSuccess(Object o) {
        Toast.makeText(this, "Car id: " + edId.getText().toString()+" has been added successfully", Toast.LENGTH_SHORT).show();
        clearFields();
    }
}
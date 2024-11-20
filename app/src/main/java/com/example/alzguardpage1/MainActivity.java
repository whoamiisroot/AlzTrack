package com.example.alzguardpage1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.UUID;

import javax.security.auth.login.LoginException;

public class MainActivity extends AppCompatActivity {
    //FirebaseAuth auth;
    Button buttonTraq,buttonPat;
    DatabaseReference databaseRef;
    DatabaseReference patientsRef;
    DatabaseReference traqueursRef;

    //public static String check;

    //FirebaseUser user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //auth = FirebaseAuth.getInstance();
        //user = auth.getCurrentUser();

        buttonTraq = findViewById(R.id.button3);
        buttonPat = findViewById(R.id.button);

        databaseRef = FirebaseDatabase.getInstance("https://alzguardpage1-default-rtdb.europe-west1.firebasedatabase.app/").getReference();
        //initialisation des noeuds
        patientsRef = databaseRef.child("patient");
        traqueursRef = databaseRef.child("traqueur");


        //if (user == null){
        //    Intent intent = new Intent(getApplicationContext(), login.class);
         //   startActivity(intent);
         //   finish();
        //}
       // else{
        //    textview.setText(user.getEmail());
        //}
        buttonTraq.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //check="traqueur";
                String nom = getIntent().getStringExtra("nom");
                String adress = getIntent().getStringExtra("adress");
                String email = getIntent().getStringExtra("email");

                String userId = UUID.randomUUID().toString();

                SharedPreferences sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("userId", userId);
                editor.apply();

                HashMap<String, Object> userData = new HashMap<>();
                userData.put("userid", userId);
                userData.put("nom", nom);
                userData.put("adresse", adress);
                userData.put("email", email);
                traqueursRef.child(userId).setValue(userData);

                Intent intent = new Intent(getApplicationContext(),HomePageT.class);
                intent.putExtra("nom", nom);
                intent.putExtra("userId", userId);
                startActivity(intent);
                finish();
            }
        });
        buttonPat.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //check="patient";
                String nom = getIntent().getStringExtra("nom");
                String adress = getIntent().getStringExtra("adress");
                String email = getIntent().getStringExtra("email");
                String userId = UUID.randomUUID().toString();

                SharedPreferences sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("userId", userId);
                editor.apply();

                HashMap<String, Object> location = new HashMap<>();
                HashMap<String, Object> userData = new HashMap<>();
                userData.put("userid", userId);
                userData.put("nom", nom);
                userData.put("adresse", adress);
                userData.put("email", email);
                location.put("latitude", 0.0);
                location.put("longitude", 0.0);
                userData.put("location",location);



                patientsRef.child(userId).setValue(userData);


                Intent intent = new Intent(getApplicationContext(),HomePageP.class);
                intent.putExtra("nom", nom);
                intent.putExtra("userId", userId);
                startActivity(intent);
                finish();
            }
        });

    }
}
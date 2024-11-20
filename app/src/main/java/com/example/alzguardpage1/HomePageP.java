package com.example.alzguardpage1;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.alzguardpage1.LocationService;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomePageP extends AppCompatActivity {
    ImageView bgapp, clover,qr ,closeButton ,startButton,stopButton ,maison;
    TextView qrText ,helloText;
    LinearLayout textsplash, texthome, menus;
    Animation frombottom;



    DatabaseReference databaseRef;
    DatabaseReference patientsRef;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page_p);
        helloText=findViewById(R.id.helloText);
        closeButton = findViewById(R.id.closeButton);

        startButton = findViewById(R.id.start_button);
        stopButton = findViewById(R.id.stop_button);
        maison =(ImageView)findViewById(R.id.maison);
        qr =(ImageView)findViewById(R.id.qr);
        qrText=findViewById(R.id.qrText);
        frombottom = AnimationUtils.loadAnimation(this,R.anim.frombottom);
        bgapp = (ImageView) findViewById(R.id.bgap);
        clover = (ImageView) findViewById(R.id.clover);
        textsplash = (LinearLayout) findViewById(R.id.textsplash);
        texthome = (LinearLayout) findViewById(R.id.texthome);
        menus = (LinearLayout) findViewById(R.id.menus);
        String userId = getIntent().getStringExtra("userId");
        bgapp.animate().translationY(-1550).setDuration(800).setStartDelay(300);
        clover.animate().alpha(0).setDuration(1000).setStartDelay(1000);
        textsplash.animate().translationY(140).alpha(0).setDuration(1000).setStartDelay(1000);
        texthome.startAnimation(frombottom);
        menus.startAnimation(frombottom);

        databaseRef = FirebaseDatabase.getInstance("https://alzguardpage1-default-rtdb.europe-west1.firebasedatabase.app/").getReference();
        //initialisation des noeuds
        patientsRef = databaseRef.child("patient");
        DatabaseReference nomRef = databaseRef.child("patient").child(userId).child("nom");

// Add a listener to retrieve the value of the "nom" field
        nomRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get the value of the "nom" field
                String nom = dataSnapshot.getValue(String.class);
                helloText.setText(nom);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle any errors that occur
                Log.e("TAG", "Error retrieving data", databaseError.toException());
            }
        });







        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        maison.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),MaisonActivity.class);
                intent.putExtra("userId", userId);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();

            }
        });



        qr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userId = getIntent().getStringExtra("userId");
                Intent intent = new Intent(getApplicationContext(),AppP.class);
                intent.putExtra("userId", userId);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();

            }
        });
        qrText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userId = getIntent().getStringExtra("userId");
                Intent intent = new Intent(getApplicationContext(),AppP.class);
                intent.putExtra("userId", userId);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
            }
        });




        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePageP.this, LocationService.class);
                intent.putExtra("userId", userId);
                intent.setAction(LocationService.ACTION_START);
                startService(intent);
                Toast.makeText(HomePageP.this, "GPS est activé", Toast.LENGTH_SHORT).show();
            }
        });


        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePageP.this, LocationService.class);
                intent.setAction(LocationService.ACTION_STOP);
                startService(intent);
                Toast.makeText(HomePageP.this, "GPS est désactivé", Toast.LENGTH_SHORT).show();
            }
        });
    }
}









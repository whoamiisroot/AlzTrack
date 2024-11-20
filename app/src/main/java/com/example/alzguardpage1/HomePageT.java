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

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomePageT extends AppCompatActivity {
    ImageView background, logo,qrscan ,check_out ;
    TextView scanText ,hello;
    LinearLayout bienvenuText, textup, menu;
    Animation frombottom;



    DatabaseReference databaseRef;
    DatabaseReference traqueursRef;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page_t);
        hello=findViewById(R.id.hello);
        check_out = findViewById(R.id.check_out);


        qrscan =(ImageView)findViewById(R.id.qrscan);
        scanText=findViewById(R.id.scanText);
        frombottom = AnimationUtils.loadAnimation(this,R.anim.frombottom);
        background = (ImageView) findViewById(R.id.background);
        logo = (ImageView) findViewById(R.id.logo);
        bienvenuText = (LinearLayout) findViewById(R.id.bienvenuText);
        textup = (LinearLayout) findViewById(R.id.textup);
        menu = (LinearLayout) findViewById(R.id.menu);
        String userId = getIntent().getStringExtra("userId");
        background.animate().translationY(-1550).setDuration(800).setStartDelay(300);
        logo.animate().alpha(0).setDuration(1000).setStartDelay(1000);
        bienvenuText.animate().translationY(140).alpha(0).setDuration(1000).setStartDelay(1000);
        textup.startAnimation(frombottom);
        menu.startAnimation(frombottom);

        databaseRef = FirebaseDatabase.getInstance("https://alzguardpage1-default-rtdb.europe-west1.firebasedatabase.app/").getReference();
        //initialisation des noeuds
        traqueursRef = databaseRef.child("traqueur");
        DatabaseReference nomRef = databaseRef.child("traqueur").child(userId).child("nom");

// Add a listener to retrieve the value of the "nom" field
        nomRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get the value of the "nom" field
                String nom = dataSnapshot.getValue(String.class);
                hello.setText(nom);

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle any errors that occur
                Log.e("TAG", "Error retrieving data", databaseError.toException());
            }

        });

        check_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        qrscan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),AppT.class);
                intent.putExtra("userId", userId);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();

            }
        });
    }
}

package com.example.alzguardpage1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class login extends AppCompatActivity {
    TextInputEditText editTextEmail , editTextPassword ;
    Button buttonLogin;
    FirebaseAuth auth;
    ProgressBar progressBar;
    TextView textView ,forgetPassword;
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = auth.getCurrentUser();
        if(currentUser != null){

        }
    }

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        editTextEmail = findViewById(R.id.email);
        TextView emailError = findViewById(R.id.emailError);
        editTextPassword = findViewById(R.id.password);
        TextView passwordError = findViewById(R.id.passwordError);
        buttonLogin = findViewById(R.id.btn_login);
        String nom = getIntent().getStringExtra("nom");

        forgetPassword=findViewById(R.id.forgetPassword);

        auth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressBar);
        textView = findViewById(R.id.registerNow);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),Register.class);
                startActivity(intent);
                finish();
            }
        });

        forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ResetPasswordDialogFragment dialogFragment = new ResetPasswordDialogFragment();
                dialogFragment.show(getSupportFragmentManager(), "reset_password");
            }
        });




        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                String email = editTextEmail.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email) && TextUtils.isEmpty(password)){
                    passwordError.setText("Tous les champs sont obligatoires");
                    passwordError.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    return;
                } else {
                    passwordError.setVisibility(View.GONE);
                }

                if (TextUtils.isEmpty(email)){
                    emailError.setText("Champ obligatoire");
                    emailError.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    return;
                } else {
                    emailError.setVisibility(View.GONE);
                }
                if (TextUtils.isEmpty(password)){
                    passwordError.setText("Champ obligatoire");
                    passwordError.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    return;
                } else {
                    passwordError.setVisibility(View.GONE);
                }

                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {


                                    SharedPreferences sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
                                    String userId = sharedPreferences.getString("userId", null);



                                    FirebaseAuth auth=FirebaseAuth.getInstance();
                                    FirebaseUser currentUser = auth.getCurrentUser();
                                    if(currentUser != null && userId != null){
                                        DatabaseReference rootRef = FirebaseDatabase.getInstance("https://alzguardpage1-default-rtdb.europe-west1.firebasedatabase.app/").getReference();
                                        DatabaseReference patientsRef = rootRef.child("patient");
                                        DatabaseReference traqueursRef = rootRef.child("traqueur");

                                        Query query = traqueursRef.orderByChild("email").equalTo(email);
                                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.exists()) {
                                                    // User belongs to "traqueur" node
                                                    Toast.makeText(login.this, "Connexion réussie.",
                                                            Toast.LENGTH_SHORT).show();
                                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                        String userId = snapshot.getKey();
                                                        Intent intent = new Intent(login.this, HomePageT.class);
                                                        intent.putExtra("userId", userId);
                                                        startActivity(intent);
                                                        finish();
                                                    }
                                                } else {
                                                    // User does not belong to "traqueur" node, check "patient" node
                                                    Query query = patientsRef.orderByChild("email").equalTo(email);
                                                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            if (dataSnapshot.exists()) {
                                                                // User belongs to "patient" node
                                                                Toast.makeText(login.this, "Connexion réussie.",
                                                                        Toast.LENGTH_SHORT).show();
                                                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                                    String userId = snapshot.getKey();
                                                                    Intent intent = new Intent(login.this, HomePageP.class);
                                                                    intent.putExtra("userId", userId);
                                                                    intent.putExtra("nom", nom);
                                                                    startActivity(intent);
                                                                    finish();
                                                                }
                                                            } else {
                                                                // User does not exist in either node
                                                                System.out.println("error 1");
                                                                Toast.makeText(login.this, "Authentification échouée.",
                                                                        Toast.LENGTH_SHORT).show();
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(DatabaseError databaseError) {
                                                            // Handle the error
                                                            System.out.println("error 2");
                                                        }
                                                    });
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {
                                                // Handle the error
                                                System.out.println("error 3");
                                            }
                                        });


                                    }

                                    /*if (MainActivity.check=="traqueur"){
                                        Intent intent = new Intent(login.this,AppT.class);
                                        startActivity(intent);
                                        finish();

                                    }else if (MainActivity.check=="patient"){
                                        Intent intent = new Intent(login.this,AppP.class);
                                        startActivity(intent);
                                        finish();
                                    }

                                     */


                                } else {

                                    Toast.makeText(login.this, "Authentification échouée.",
                                            Toast.LENGTH_SHORT).show();
                                    editTextEmail.requestFocus();
                                    editTextEmail.setText("");
                                    editTextPassword.setText("");

                                }

                            }
                        }) ;

                }


        });


    }
}
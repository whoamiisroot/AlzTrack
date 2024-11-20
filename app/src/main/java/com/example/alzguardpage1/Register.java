package com.example.alzguardpage1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Register extends AppCompatActivity {
    TextInputEditText editTextEmail , editTextPassword ,editTextRePassword,editTextNom,editTextPhone,editTextAdress;
    Button buttonReg;
    FirebaseAuth auth;
    ProgressBar progressBar;
    TextView textView;
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = auth.getCurrentUser();
        if(currentUser != null){

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        editTextEmail = findViewById(R.id.email);
        TextView emailError = findViewById(R.id.emailError);
        editTextPassword = findViewById(R.id.password);
        TextView passwordError = findViewById(R.id.passwordError);
        editTextRePassword=findViewById(R.id.repassword);
        TextView repasswordError= findViewById(R.id.repasswordError);
        editTextNom=findViewById(R.id.nom);
        Pattern phonePattern = Pattern.compile("^((0|\\+212)[675]\\d{8})$");
        editTextPhone=findViewById(R.id.phone);
        TextView phoneError = findViewById(R.id.phoneError);
        editTextAdress=findViewById(R.id.adresse);
        TextView adressError = findViewById(R.id.adressError);
        buttonReg = findViewById(R.id.btn_register);
        auth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressBar);
        textView = findViewById(R.id.loginNow);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),login.class);
                startActivity(intent);
                finish();
            }
        });


        buttonReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                String email,password,nom,adress;

                nom=editTextNom.getText().toString();
                adress=editTextAdress.getText().toString().trim();
                String repassword = editTextRePassword.getText().toString().trim();
                email = editTextEmail.getText().toString().trim();
                password = editTextPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email)||TextUtils.isEmpty(password)||TextUtils.isEmpty(nom)||TextUtils.isEmpty(adress)||TextUtils.isEmpty(repassword) || TextUtils.isEmpty(editTextPhone.getText())) {
                    adressError.setText("Tous les champs sont obligatoires");
                    adressError.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    return;
                } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    adressError.setVisibility(View.GONE);
                    emailError.setText("Entrez un e-mail valide");
                    emailError.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    return;
                } else if(password.length() < 6){
                    adressError.setVisibility(View.GONE);
                    emailError.setVisibility(View.GONE);
                    passwordError.setText("Le mot de passe doit avoir au moins 6 caractères");
                    passwordError.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    return;
                }else if (!password.equals(repassword) && password.length() >= 6){
                    adressError.setVisibility(View.GONE);
                    emailError.setVisibility(View.GONE);
                    passwordError.setVisibility(View.GONE);
                    repasswordError.setText("Répéter le même mot de passe");
                    repasswordError.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    return;
                }else {
                    adressError.setVisibility(View.GONE);
                    emailError.setVisibility(View.GONE);
                    passwordError.setVisibility(View.GONE);
                    repasswordError.setVisibility(View.GONE);
                    phoneError.setVisibility(View.GONE);

                    String phone = editTextPhone.getText().toString();
                    Matcher phoneMatcher = phonePattern.matcher(phone);
                    boolean isValidPhone = phoneMatcher.matches();

                    if (!isValidPhone) {
                        phoneError.setText("Entrez un numéro de téléphone valide");
                        phoneError.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                        return;
                    }
                }

                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    Toast.makeText(Register.this, "Compte créé.",
                                            Toast.LENGTH_SHORT).show();



                                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                                    intent.putExtra("nom", nom);
                                    intent.putExtra("adress", adress);
                                    intent.putExtra("email",email);
                                    startActivity(intent);
                                    finish();
                                }
                                else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(Register.this, "Authentication échouée.",
                                            Toast.LENGTH_SHORT).show();
                                    editTextNom.requestFocus();
                                    editTextNom.setText("");
                                    editTextEmail.setText("");
                                    editTextPassword.setText("");
                                    editTextRePassword.setText("");
                                    editTextPhone.setText("");
                                    editTextAdress.setText("");
                                }

                            }
                        });



            }
        });

    }
}
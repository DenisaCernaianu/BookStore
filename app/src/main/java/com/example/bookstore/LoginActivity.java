package com.example.bookstore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.bookstore.Model.Users;
import com.example.bookstore.databinding.ActivityLoginBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    Button btnSignUp;

    Button btnLogIn;
    EditText  email, password, phone;
    private ActivityLoginBinding binding ;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://bookstore-7c44c-default-rtdb.firebaseio.com/");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setContentView(R.layout.activity_login);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        firebaseAuth = firebaseAuth.getInstance();

        progressDialog =  new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);


        btnSignUp = findViewById(R.id.btnSignUp);
        btnLogIn = findViewById(R.id.btnLogin);
        email = findViewById(R.id.ETEmailLogin);
        password = findViewById(R.id.ETPasswordLogin);
        phone = findViewById(R.id.ETphoneLogin);


        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });


       btnLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                validateData();
            }

        
       });
    }

   private  String emaildb = " ", passworddb = " ", phonedb ="" ;
    private void validateData() {
        passworddb = password.getText().toString().trim();
        emaildb = email.getText().toString().trim();
        phonedb = phone.getText().toString().trim();
        if(TextUtils.isEmpty(emaildb)){
            Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show();
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(emaildb).matches()){
            Toast.makeText(this, "Invalid email address", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(passworddb)){
            Toast.makeText(this, "Please enter a password", Toast.LENGTH_SHORT).show();
        }else {
            verifyUser();
        }

    }

    private void verifyUser() {


        databaseReference.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild(phonedb))
                {
                   Users userData = snapshot.child(phonedb).getValue(Users.class);

                    if(userData.getEmail().equals(emaildb)){
                        if((userData.getPassword() )== passworddb.hashCode()){

                            Toast.makeText(LoginActivity.this, "Log in Successfully!!!!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                            finish();
                        }else {
                            Toast.makeText(LoginActivity.this, "Password incorrect !", Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        Toast.makeText(LoginActivity.this, "Email is incorrect !", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(LoginActivity.this, "An account with this phone number doesn't exists!", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


}
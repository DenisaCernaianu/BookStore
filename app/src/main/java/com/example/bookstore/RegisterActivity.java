package com.example.bookstore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaCodec;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.bookstore.databinding.ActivityLoginBinding;
import com.example.bookstore.databinding.ActivityRegisterBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ktx.Firebase;

import java.util.HashMap;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {
    Button btnSignUp;
    EditText username, email, password, phone, confirmPassword;
    Button btnLogIn;

    private ActivityRegisterBinding binding;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://bookstore-7c44c-default-rtdb.firebaseio.com/");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setContentView(R.layout.activity_register);
      binding = ActivityRegisterBinding.inflate(getLayoutInflater());
       setContentView(binding.getRoot());

        firebaseAuth = firebaseAuth.getInstance();

        progressDialog =  new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        btnSignUp = findViewById(R.id.btnSignUp);
        btnLogIn = findViewById(R.id.btnLogin);
        username = findViewById(R.id.ETusername);
        email = findViewById(R.id.ETemail);
        password = findViewById(R.id.ETpassword);
        phone = findViewById(R.id.ETphone);
        confirmPassword = findViewById(R.id.ETconfirmpassword);

        btnLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               startActivity(new Intent(RegisterActivity.this, LoginActivity.class));


            }
        });

      btnSignUp.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              validateData();
          }
      });
    }

    private String usernamedb = " ", emaildb = " ", passworddb=" ", confirmPassworddb="", phonedb = " ";
    private void validateData() {

        usernamedb = username.getText().toString().trim();
        emaildb = email.getText().toString().trim();
        passworddb = password.getText().toString().trim();
        confirmPassworddb = confirmPassword.getText().toString().trim();
        phonedb = phone.getText().toString().trim();

        if(TextUtils.isEmpty(usernamedb)){
            Toast.makeText(this, "Please enter your username", Toast.LENGTH_SHORT).show();
        }
      else if(TextUtils.isEmpty(emaildb)){
            Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show();
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(emaildb).matches()){
            Toast.makeText(this, "Invalid email address", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(phonedb)){
            Toast.makeText(this, "Please enter phone number", Toast.LENGTH_SHORT).show();}
        else if(TextUtils.isEmpty(passworddb)){
            Toast.makeText(this, "Please enter a password", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(confirmPassworddb)){
            Toast.makeText(this, "Please confirm your password", Toast.LENGTH_SHORT).show();
        }else if(!passworddb.equals(confirmPassworddb)){
            Toast.makeText(this, "Password doesn't match", Toast.LENGTH_SHORT).show();
        }else
        {
            progressDialog.setMessage("Creating Account");
            progressDialog.show();
            createUserAccount();
        }

    }

    private void createUserAccount() {
        databaseReference.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild(phonedb)){
                    Toast.makeText(RegisterActivity.this, "An account with this phone number already exists!", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                } else {

                    //trimit datele catre realtime database si folosesc nr de tel ca id unic
                    databaseReference.child("Users").child(phonedb).child("username").setValue(usernamedb);
                    databaseReference.child("Users").child(phonedb).child("email").setValue(emaildb);
                    databaseReference.child("Users").child(phonedb).child("password").setValue(passworddb.hashCode());
                    progressDialog.dismiss();
                    Toast.makeText(RegisterActivity.this, "Your account was created", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}
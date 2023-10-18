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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.ktx.Firebase;

import java.util.HashMap;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {
    Button btnsignUp;
   // EditText username, email, password;
    Button btnlogIn;

    private ActivityRegisterBinding binding;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

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

        btnsignUp = findViewById(R.id.btnSignUp);
        btnlogIn = findViewById(R.id.btnLogin);
      //  username = findViewById(R.id.ETusername);
      //  email = findViewById(R.id.ETemail);
      //  password = findViewById(R.id.ETpassword);

        btnlogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               startActivity(new Intent(RegisterActivity.this, LoginActivity.class));


            }
        });

      binding.btnSignUp.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              validateData();
          }
      });
    }

    private String username = " ", email = " ", password=" ";
    private void validateData() {

        username = binding.ETusername.getText().toString().trim();
        email = binding.ETemail.getText().toString().trim();
        password = binding.ETpassword.getText().toString().trim();
        String confirmpassword = binding.ETconfirmpassword.getText().toString().trim();

        if(TextUtils.isEmpty(username)){
            Toast.makeText(this, "Please enter your username", Toast.LENGTH_SHORT).show();
        }
      else if(TextUtils.isEmpty(email)){
            Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show();
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(this, "Invalid email address", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(password)){
            Toast.makeText(this, "Please enter a password", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(confirmpassword)){
            Toast.makeText(this, "Please confirm your password", Toast.LENGTH_SHORT).show();
        }else if(!password.equals(confirmpassword)){
            Toast.makeText(this, "Password doesn't match", Toast.LENGTH_SHORT).show();
        }else
        {
            createUserAccount();
        }

    }

    private void createUserAccount() {
        progressDialog.setMessage("Creating Account");
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                           updateUserInfo();

                    }
                })

                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                               progressDialog.dismiss();
                               Toast.makeText(RegisterActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateUserInfo() {

        progressDialog.setMessage("Saving user information");

        long timestamp = System.currentTimeMillis();

        String uid = firebaseAuth.getUid();
      //adaug intrare in baza de date, tabela users
       HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("uid", uid);
        hashMap.put("email", email);
        hashMap.put("username", username);
        hashMap.put("profileImage","");
        hashMap.put("userType", "user");
        hashMap.put("timestamp", timestamp);

        //creez tabela Users in baza de date cu atributele din dictionarul de mai sus
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(uid)
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();
                        Toast.makeText(RegisterActivity.this, "Your accound was created", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                        finish();
                    }
                })

                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(RegisterActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
    }
}
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
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.bookstore.Model.Users;
import com.example.bookstore.databinding.ActivityLoginBinding;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {
    Button btnSignUp;

    Button btnLogIn;
    EditText   password, phone;

    String verificationCode;
    String phoneNumber;
    com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken resendingToken;
   // private ActivityLoginBinding binding ;
   private FirebaseAuth firebaseAuth;

   ProgressBar progressBar;
    private ProgressDialog progressDialog;



    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://bookstore-7c44c-default-rtdb.firebaseio.com/");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_login);
       // binding = ActivityLoginBinding.inflate(getLayoutInflater());
        //setContentView(binding.getRoot());
        firebaseAuth = firebaseAuth.getInstance();

        progressDialog =  new ProgressDialog(this);
        progressDialog.setTitle("Vă rugăm așteptați");
        progressDialog.setCanceledOnTouchOutside(false);


        btnSignUp = findViewById(R.id.btnSignUp);
        btnLogIn = findViewById(R.id.btnLogin);

        password = findViewById(R.id.ETPasswordLogin);
        phone = findViewById(R.id.ETphoneLogin);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);






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

   private  String passworddb = " ", phonedb ="" ;
    private void validateData() {
        passworddb = password.getText().toString().trim();
        phonedb = phone.getText().toString().trim();
        if(TextUtils.isEmpty(phonedb)){
            Toast.makeText(this, "Introduceți numărul de telefon", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(passworddb)){
            Toast.makeText(this, "Introduceți parola", Toast.LENGTH_SHORT).show();
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

                        if((userData.getPassword() )== passworddb.hashCode()){

                          //  Toast.makeText(LoginActivity.this, "Succes!!", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.VISIBLE);
                            phoneNumber = "+4" + phone.getText().toString().trim();
                           sendOTP(phoneNumber,false );

                        }else {
                            Toast.makeText(LoginActivity.this, "Parolă incorectă !", Toast.LENGTH_LONG).show();
                        }
                }else {
                    Toast.makeText(LoginActivity.this, "Un cont cu acest număr de telefon nu există ! ", Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void sendOTP(String phoneNumber, boolean isResend){
        PhoneAuthOptions.Builder builder =
                PhoneAuthOptions.newBuilder(firebaseAuth).setPhoneNumber(phoneNumber)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                startActivity(new Intent(LoginActivity.this, HomeActivity.class));

                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {

                            }

                            @Override
                            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                super.onCodeSent(s, forceResendingToken);
                                verificationCode = s;
                                resendingToken = forceResendingToken;
                                Toast.makeText(LoginActivity.this, "Codul a fost trimis cu succes!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(LoginActivity.this, VerifyOTPActivity.class);
                                intent.putExtra("verificationCode", verificationCode);
                                intent.putExtra("phoneNumber",phoneNumber);
                                startActivity(intent);}


                        });

        if(isResend){PhoneAuthProvider.verifyPhoneNumber(builder.setForceResendingToken(resendingToken).build());}
        else{PhoneAuthProvider.verifyPhoneNumber(builder.build());}


    }


}
package com.example.bookstore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;

import android.widget.Toast;

import com.example.bookstore.Model.AdminActivity;
import com.example.bookstore.Model.Users;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    Button btnSignUp;

    Button btnLogIn;
    EditText   password, email;

    Button forgotPassword;

    CheckBox checkBoxViewPassword;

    String nrTel;


    private  String passworddb = " ", emaildb ="" ;
   private FirebaseAuth firebaseAuth;

   ProgressBar progressBar;
    private int resetare=0;



    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://bookstore-7c44c-default-rtdb.firebaseio.com/");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_login);
        firebaseAuth = FirebaseAuth.getInstance();

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Vă rugăm așteptați");
        progressDialog.setCanceledOnTouchOutside(false);


        btnSignUp = findViewById(R.id.btnSignUp);
        btnLogIn = findViewById(R.id.btnLogin);

        password = findViewById(R.id.ETPasswordLogin);
        email = findViewById(R.id.ETemailLogin);
        forgotPassword = findViewById(R.id.TVforget);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        checkBoxViewPassword=findViewById(R.id.checkBoxViewPassword);






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

       checkBoxViewPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
           @Override
           public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
               if(b){
                   password.setTransformationMethod(null);
               }else
               {
                   password.setTransformationMethod(new PasswordTransformationMethod());
               }
           }
       });

       forgotPassword.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               resetPassword();
           }
       });
    }

    private void resetPassword() {
        emaildb = email.getText().toString().trim();
        resetare=1;

        if(!emaildb.equals("")) {
            progressBar.setVisibility(View.VISIBLE);
            firebaseAuth.sendPasswordResetEmail(emaildb).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(LoginActivity.this, "Un link pentru resetarea parolei a fost trimis pe email", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(LoginActivity.this, "Link ul nu a fost trimis", Toast.LENGTH_SHORT).show();

                }
            });
        }
        else
        {
            Toast.makeText(this, "Introduceti emailul!", Toast.LENGTH_SHORT).show();
        }
    }


    private void validateData() {
        passworddb = password.getText().toString().trim();
        emaildb = email.getText().toString().trim();
        if(TextUtils.isEmpty(emaildb) || !Patterns.EMAIL_ADDRESS.matcher(emaildb).matches()){
            Toast.makeText(this, "Introduceți o adresa de email valida", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(passworddb)){
            Toast.makeText(this, "Introduceți parola", Toast.LENGTH_SHORT).show();
        }else {


            LoginUser();
        }

    }

    private void LoginUser() {
        progressBar.setVisibility(View.VISIBLE);

        firebaseAuth.signInWithEmailAndPassword(emaildb,passworddb)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        verifyUser();
                        if(resetare==1 )

                        { databaseReference.child("Users").child(firebaseAuth.getCurrentUser().getUid()).child("password").setValue(passworddb.hashCode());}

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(LoginActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.INVISIBLE);
                    }
                });
    }

    private void verifyUser() {
        String uid = firebaseAuth.getCurrentUser().getUid();

       // progressBar.setVisibility(View.VISIBLE);

        databaseReference.child("Users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(firebaseAuth.getCurrentUser().isEmailVerified())

                {
                   Users userData = snapshot.getValue(Users.class);
                    nrTel = userData.getPhone();

                        if((userData.getPassword() )== passworddb.hashCode()){

                            if(emaildb.equals("bookverse2024@yahoo.com")){
                                startActivity(new Intent(LoginActivity.this, AdminActivity.class));
                                finish();
                            }
                            else

                            {
                             startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                            }

                        }else {
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(LoginActivity.this, "Parolă incorectă !", Toast.LENGTH_LONG).show();
                        }
                }
                else { progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(LoginActivity.this, "Va rugam sa va verificati email-ul ! ", Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(LoginActivity.this, "Un cont cu acest email nu există ! ", Toast.LENGTH_LONG).show();
            }
        });

    }

   /* public void sendOTP(String phoneNumber, boolean isResend){
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
                                intent.putExtra("phoneNumber1",phonedb);
                                startActivity(intent);
                            finish();}



                        });

        if(isResend){PhoneAuthProvider.verifyPhoneNumber(builder.setForceResendingToken(resendingToken).build());}
        else{PhoneAuthProvider.verifyPhoneNumber(builder.build());}


    }*/


}
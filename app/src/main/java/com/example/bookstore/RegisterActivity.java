package com.example.bookstore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
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
import android.widget.Toast;

import com.example.bookstore.Model.Books;
import com.example.bookstore.Model.MyAdapter;
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

public class RegisterActivity extends AppCompatActivity {
    Button btnSignUp;
    EditText username, email, password, phone, confirmPassword;
    Button btnLogIn;

    CheckBox checkBoxViewPasswordRegister;
   // private ActivityRegisterBinding binding;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private int verify = 1;

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://bookstore-7c44c-default-rtdb.firebaseio.com/");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseAuth = FirebaseAuth.getInstance();

        progressDialog =  new ProgressDialog(this);
        progressDialog.setTitle("Vă rugăm așteptați");
        progressDialog.setCanceledOnTouchOutside(false);

        btnSignUp = findViewById(R.id.btnSignUp);
        btnLogIn = findViewById(R.id.btnLogin);
        username = findViewById(R.id.ETusername);
        email = findViewById(R.id.ETemail);
        password = findViewById(R.id.ETpassword);
        phone = findViewById(R.id.ETphone);
        confirmPassword = findViewById(R.id.ETconfirmpassword);
        checkBoxViewPasswordRegister=findViewById(R.id.checkBoxViewPasswordRegister);

        btnLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
               finish();

            }
        });

      btnSignUp.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              validateData();
          }
      });

        checkBoxViewPasswordRegister.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    password.setTransformationMethod(null);
                    confirmPassword.setTransformationMethod(null);
                }else
                {
                    password.setTransformationMethod(new PasswordTransformationMethod());
                    confirmPassword.setTransformationMethod(new PasswordTransformationMethod());
                }
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

        if(TextUtils.isEmpty(usernamedb))
        {
            Toast.makeText(this, "Introduceți numele !", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(emaildb))
        {
            Toast.makeText(this, "Introduceți adresa de email !", Toast.LENGTH_SHORT).show();
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(emaildb).matches())
        {
            Toast.makeText(this, "Adresă de email invalidă !", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(phonedb))
        {
            Toast.makeText(this, "Introduceți numărul de telefon !", Toast.LENGTH_SHORT).show();
        }
        else if((phonedb.length() != 10) || !(phonedb.substring(0,2).equals("07")))
        {
            Toast.makeText(this, "Numărul de telefon este invalid !", Toast.LENGTH_LONG).show();
        }
        else if(TextUtils.isEmpty(passworddb))
        {
            Toast.makeText(this, "Introduceți parola !", Toast.LENGTH_SHORT).show();
        }
        else if(passworddb.length()<6)
        {
            Toast.makeText(this, "Introduceți o parolă de minim 6 caractere !", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(confirmPassworddb))
        {
            Toast.makeText(this, "Confirmați parola !", Toast.LENGTH_SHORT).show();
        }else if(!passworddb.equals(confirmPassworddb))
        {
            Toast.makeText(this, "Parolele nu se potrivesc !", Toast.LENGTH_SHORT).show();
        }else
        {
            createUserAccountFirebase();
            //verifyPhone();
        }

    }

    private void verifyPhone() {
        String uid = firebaseAuth.getUid();
        verify=1;
       // phonedb = phone.getText().toString().trim();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.child("Users").getChildren()) {

                    final String idUser = dataSnapshot.child("id").getValue(String.class);
                    final String getNumber = dataSnapshot.child("phone").getValue(String.class);

                    if (getNumber.equals(phonedb) && !uid.equals(idUser)) {
                        verify = 0;

                    }
                }
                if (verify == 0) {
                    Toast.makeText(RegisterActivity.this, "Numarul de telefon e asociat cu un alt cont!", Toast.LENGTH_SHORT).show();
                 /*   runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if (!isFinishing()){
                                new AlertDialog.Builder(RegisterActivity.this)
                                        .setTitle("Numar incorect!")
                                        .setMessage("Numarul introdus e deja utilzat de alt cont! Introdoceti nr personal!!!")
                                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                               // finish();
                                            }
                                        }).show();
                            }

                        }
                    });*/
                } else {
                    createUserAccountFirebase();
                }


            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(RegisterActivity.this, ""+ error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

    }



    private void createUserAccountFirebase() {


        firebaseAuth.createUserWithEmailAndPassword(emaildb,passworddb)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        progressDialog.setMessage("Se creează contul ");
                        progressDialog.show();
                        saveUserAccount();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                        Toast.makeText(RegisterActivity.this, ""+ e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveUserAccount() {
        progressDialog.setMessage("Se salvează informațiile în baza de date");
        progressDialog.show();

        String uid = firebaseAuth.getUid();

       databaseReference.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                {
                    //trimit datele catre realtime database

                    databaseReference.child("Users").child(uid).child("username").setValue(usernamedb);
                    databaseReference.child("Users").child(uid).child("email").setValue(emaildb);
                    databaseReference.child("Users").child(uid).child("phone").setValue(phonedb);
                    databaseReference.child("Users").child(uid).child("id").setValue(uid);
                    databaseReference.child("Users").child(uid).child("password").setValue(passworddb.hashCode());
                    progressDialog.dismiss();

                    sendEmail();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }

    private void sendEmail() {
        firebaseAuth.getCurrentUser().sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

                Toast.makeText(RegisterActivity.this, "Un link pentru verificarea identitatii a fost trimis pe email!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(RegisterActivity.this, "Email-ul nu a putut fi trimis!", Toast.LENGTH_SHORT).show();

            }
        });



    }
}
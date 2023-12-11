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


import com.example.bookstore.databinding.ActivityRegisterBinding;
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

   // private ActivityRegisterBinding binding;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://bookstore-7c44c-default-rtdb.firebaseio.com/");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
     // binding = ActivityRegisterBinding.inflate(getLayoutInflater());
       //setContentView(binding.getRoot());

        //firebaseAuth = firebaseAuth.getInstance();

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
            Toast.makeText(this, "Introduceți numele !", Toast.LENGTH_SHORT).show();
        }
      else if(TextUtils.isEmpty(emaildb)){
            Toast.makeText(this, "Introduceți adresa de email !", Toast.LENGTH_SHORT).show();
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(emaildb).matches()){
            Toast.makeText(this, "Adresă de email invalidă !", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(phonedb)){
            Toast.makeText(this, "Introduceți numărul de telefon !", Toast.LENGTH_SHORT).show();}
        else if((phonedb.length() != 10) || !(phonedb.substring(0,2).equals("07"))){
            Toast.makeText(this, "Numărul de telefon este invalid !", Toast.LENGTH_LONG).show();}
        else if(TextUtils.isEmpty(passworddb)){
            Toast.makeText(this, "Introduceți parola !", Toast.LENGTH_SHORT).show();
        }   else if(passworddb.length()<6){
            Toast.makeText(this, "Introduceți o parolă de minim 6 caractere !", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(confirmPassworddb)){
            Toast.makeText(this, "Confirmați parola !", Toast.LENGTH_SHORT).show();
        }else if(!passworddb.equals(confirmPassworddb)){
            Toast.makeText(this, "Parolele nu se potrivesc !", Toast.LENGTH_SHORT).show();
        }else
        {
            //progressDialog.setMessage("Creating Account");
            //progressDialog.show();
            createUserAccount();
        }

    }

    private void createUserAccount() {
        databaseReference.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild(phonedb)){
                    Toast.makeText(RegisterActivity.this, "Un cont cu acest număr de telefon deja există !", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                } else {
                    progressDialog.setMessage("Creating Account");
                    progressDialog.show();

                    //trimit datele catre realtime database si folosesc nr de tel ca id unic
                    databaseReference.child("Users").child(phonedb).child("username").setValue(usernamedb);
                    databaseReference.child("Users").child(phonedb).child("email").setValue(emaildb);
                    databaseReference.child("Users").child(phonedb).child("phone").setValue(phonedb);
                    databaseReference.child("Users").child(phonedb).child("password").setValue(passworddb.hashCode());
                    progressDialog.dismiss();
                    Toast.makeText(RegisterActivity.this, "Contul a fost creat !", Toast.LENGTH_SHORT).show();
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
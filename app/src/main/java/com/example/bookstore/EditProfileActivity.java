package com.example.bookstore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.bookstore.Model.Books;
import com.example.bookstore.Model.MyAdapterProfile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class EditProfileActivity extends AppCompatActivity {

    private EditText usernameEdit, phoneEdit;
    private Button backBtn, saveBtn;

    private String uid, oldNumber;

    private int ok=1;

     FirebaseAuth firebaseAuth;

    private ProgressDialog progressDialog;

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://bookstore-7c44c-default-rtdb.firebaseio.com/");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        usernameEdit = findViewById(R.id.userameProfileEdit);
        phoneEdit = findViewById(R.id.phoneNameEdit);
        backBtn = findViewById(R.id.backButtonEditProfile);
        saveBtn = findViewById(R.id.btnSaveProfile);

        firebaseAuth= FirebaseAuth.getInstance();

        uid = firebaseAuth.getCurrentUser().getUid();


        progressDialog =  new ProgressDialog(this);
        progressDialog.setTitle("Vă rugăm așteptați");



        loadUserInfo();



        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateUserInfo();

            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(EditProfileActivity.this, MyProfileActivity.class));
                //onBackPressed();
                finish();

            }
        });


    }

    private void validateUserInfo() {
        ok=1;
        if((phoneEdit.getText().toString().length() != 10) || !(phoneEdit.getText().toString().substring(0,2).equals("07"))){
            Toast.makeText(this, "Numărul de telefon este invalid !", Toast.LENGTH_LONG).show();}
        else if(usernameEdit.getText().toString().equals("")) {
            Toast.makeText(this, "Completeaza numele de utilizator!", Toast.LENGTH_LONG).show();}
        else {

            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    for (DataSnapshot dataSnapshot : snapshot.child("Users").getChildren()) {

                        final String idUser = dataSnapshot.child("id").getValue(String.class);
                        final String getNumber = dataSnapshot.child("phone").getValue(String.class);
                        String nr = phoneEdit.getText().toString();
                        if (nr.equals(getNumber) && !uid.equals(idUser)) {
                            ok = 0;
                            //break;

                        }
                    }

                   if (ok == 0) {
                       progressDialog.dismiss();
                       runOnUiThread(new Runnable() {
                           @Override
                           public void run() {

                               if (!isFinishing()){
                                   new AlertDialog.Builder(EditProfileActivity.this)
                                           .setTitle("Numarul de telefon e deja asociat cu alt cont!")
                                           .setMessage("Va rugam introduceti numarul personal de telefon!")
                                           .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                               @Override
                                               public void onClick(DialogInterface dialogInterface, int i) {

                                                   startActivity(new Intent(EditProfileActivity.this, EditProfileActivity.class));
                                                   finish();
                                                  // onDestroy();

                                               }
                                           }).show();
                               }

                           }
                       });
                    } else { progressDialog.setMessage("Se actualizeaza datele...");
                        progressDialog.show();
                        saveUserInfo();

                    }

                }


                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
    }


    private void saveUserInfo() {



        databaseReference.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                //trimit datele catre realtime database

                databaseReference.child("Users").child(uid).child("username").setValue(usernameEdit.getText().toString());
                databaseReference.child("Users").child(uid).child("phone").setValue(phoneEdit.getText().toString());




            }
            // }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //  Toast.makeText(AddBookActivity.this, "Cartea nu a putut fi adaugata!", Toast.LENGTH_LONG).show();

            }



        });
     //   Toast.makeText(EditProfileActivity.this, "Modificarile au fost salvate!", Toast.LENGTH_SHORT).show();

      // if(!oldNumber.equals(phoneEdit.getText().toString()))
        {
            updateNumberForBooks();
        }
     /*   else  {
            progressDialog.dismiss();
           Toast.makeText(EditProfileActivity.this, "Modificarile au fost salvate!", Toast.LENGTH_SHORT).show();
            //startActivity(new Intent(EditProfileActivity.this,MyProfileActivity.class ));
           //Toast.makeText(EditProfileActivity.this, "Modificarile au fost salvate!", Toast.LENGTH_SHORT).show();
            // onDestroy();

        }*/

    }

    private void updateNumberForBooks() {

        new Thread(new Runnable() {
            public void run() {

                // A potentially time consuming task.
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        for(DataSnapshot dataSnapshot: snapshot.child("Books").getChildren()){

                            final String idBook  = dataSnapshot.getKey().toString();
                            final String getOwnerNumber = dataSnapshot.child("ownerNumber").getValue(String.class);
                            if(getOwnerNumber.equals(oldNumber)){
                                databaseReference.child("Books").child(idBook).child("ownerNumber").setValue(phoneEdit.getText().toString());


                            }


                        }

                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        progressDialog.dismiss();
                    }
                });

            }
        }).start();


        //Toast.makeText(EditProfileActivity.this, "Modificarile au fost salvate!", Toast.LENGTH_SHORT).show();

        progressDialog.dismiss();
        startActivity(new Intent(EditProfileActivity.this,MyProfileActivity.class ));
        finish();
    }

    private void loadUserInfo() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://bookstore-7c44c-default-rtdb.firebaseio.com/");
        databaseReference.child("Users").child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        final String getUsername = snapshot.child("username").getValue(String.class);
                        final String getPhone = snapshot.child("phone").getValue(String.class);

                        oldNumber=getPhone;
                        usernameEdit.setText(getUsername);
                        phoneEdit.setText(getPhone);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


    }



}
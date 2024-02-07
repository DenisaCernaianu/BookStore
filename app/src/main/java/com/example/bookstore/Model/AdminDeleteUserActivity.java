package com.example.bookstore.Model;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bookstore.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class AdminDeleteUserActivity extends AppCompatActivity {

    String userId, phoneUser;

    private TextView username, email, phone ;

    private FirebaseAuth firebaseAuth;

    private Button btnDeleteAcc, backButton;

    private ProgressDialog progressDialog;

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://bookstore-7c44c-default-rtdb.firebaseio.com/");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_delete_user);

        Intent intent = getIntent();
        userId = intent.getStringExtra("userId");


        username= findViewById(R.id.userName);
        email=findViewById(R.id.emailName);
        phone= findViewById(R.id.phoneName);
        btnDeleteAcc=findViewById(R.id.btnDeleteAcc);
        backButton=findViewById(R.id.backButton);

        progressDialog =  new ProgressDialog(this);
        progressDialog.setTitle("Vă rugăm așteptați");


        loadUserInfo();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onBackPressed();
                finish();
            }
        });

        btnDeleteAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteUser();
            }
        });
    }



    private void loadUserInfo() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://bookstore-7c44c-default-rtdb.firebaseio.com/");
        databaseReference.child("Users").child(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        final String getUsername = snapshot.child("username").getValue(String.class);
                        final String getEmail = snapshot.child("email").getValue(String.class);
                        final String getPhone = snapshot.child("phone").getValue(String.class);
                        final String getId = snapshot.child("id").getValue(String.class);


                        username.setText(getUsername);
                        email.setText(getEmail);
                        phone.setText(getPhone);

                       phoneUser=getPhone;


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }


    private void deleteUser() {


        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://bookstore-7c44c-default-rtdb.firebaseio.com/");
       // phoneUser =  databaseReference.child("Users").child(userId).child("phone").toString();


        progressDialog.setMessage("Se sterge utilizatorul...");
        progressDialog.show();

        deleteBooksbyUser();


    }

    private void deleteBooksbyUser() {

        new Thread(new Runnable() {
            public void run() {
                // A potentially time consuming task.

                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.child("Books").getChildren()) {
                            final String getOwnerNumber = dataSnapshot.child("ownerNumber").getValue(String.class);
                            final String getId = dataSnapshot.child("id").getValue(String.class);

                            if (getOwnerNumber.equals(phoneUser)) {
                                databaseReference.child("Books").child(getId).removeValue();

                            }
                        }
                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                databaseReference.child("Users").child(userId).removeValue();

            }
        }).start();

        Toast.makeText(this, "Utilizatorul si toate cartile ce apartin contului au fost sterse!", Toast.LENGTH_SHORT).show();
        onBackPressed();
        finish();
    }

}
package com.example.bookstore.Model;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bookstore.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class AdminDeleteUserActivity extends AppCompatActivity {

    String userId;

    private TextView username, email, phone ;

    private Button btnDeleteAcc, backButton;

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


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }


    private void deleteUser() {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://bookstore-7c44c-default-rtdb.firebaseio.com/");
        databaseReference.child("Users").child(userId).removeValue();

        Toast.makeText(this, "Utilizatorul a fost sters", Toast.LENGTH_SHORT).show();
             onBackPressed();
             finish();


    }

}
package com.example.bookstore.Model;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.bookstore.AddBookActivity;
import com.example.bookstore.ExchangeActivity;
import com.example.bookstore.HomeActivity;
import com.example.bookstore.LoginActivity;
import com.example.bookstore.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AdminActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    List<Users> list;
    DatabaseReference databaseReference;
    MyAdapterUser adapter;

    Button logOut, goBooks;

    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);



       logOut = findViewById(R.id.logOut);
        recyclerView = findViewById(R.id.recycleview1);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        // DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://bookstore-7c44c-default-rtdb.firebaseio.com/");
        list = new ArrayList<>();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(AdminActivity.this));
        adapter = new MyAdapterUser(this, list);
        recyclerView.setAdapter(adapter);
        goBooks = findViewById(R.id.btnGoBooks);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for(DataSnapshot dataSnapshot: snapshot.child("Users").getChildren()){
                    final String getUsername = dataSnapshot.child("username").getValue(String.class);
                    final String getEmail = dataSnapshot.child("email").getValue(String.class);
                    final String getPhone = dataSnapshot.child("phone").getValue(String.class);
                    final String getId = dataSnapshot.child("id").getValue(String.class);
                    final String getPassword = dataSnapshot.child("password").getValue(String.class);


                    Users users = new Users(getUsername, getEmail, getPhone,getId,getPassword);
                        if(!getEmail.equals("bookverse2024@yahoo.com")){
                        list.add(users);}


                }
                adapter.notifyDataSetChanged();
                recyclerView.setAdapter(new MyAdapterUser(AdminActivity.this, list));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


       logOut.setOnClickListener(new View.OnClickListener() {
                @Override
           public void onClick(View view) {
                    startActivity(new Intent(AdminActivity.this, LoginActivity.class));
                     finish();

      }
});

       goBooks.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               startActivity(new Intent(AdminActivity.this, AdminViewBooksActivity.class));
               finish();
           }
       });

    }
}
package com.example.bookstore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.bookstore.Model.Books;
import com.example.bookstore.Model.MyAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FavoriteBooksActivity extends AppCompatActivity {

    Button btnGoFav, btnGoAcc, btnGoExchange, btnGoHome;
    RecyclerView recyclerView;
    DatabaseReference databaseReference;
    MyAdapter adapter;
    private FirebaseAuth firebaseAuth;

    List<Books> list, filteredList;

    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_books);

        btnGoFav = findViewById(R.id.btnGoFav);
        btnGoAcc=findViewById(R.id.btnGoAcc);
        btnGoExchange=findViewById(R.id.btnGoExchange);
        btnGoHome= findViewById(R.id.btnGoHome);

        firebaseAuth = FirebaseAuth.getInstance();

        uid = firebaseAuth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        btnGoHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(FavoriteBooksActivity.this, HomeActivity.class));
            }
        });

        btnGoExchange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(FavoriteBooksActivity.this, ExchangeActivity.class));

            }
        });

        recyclerView = findViewById(R.id.recycleview1);

        // DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://bookstore-7c44c-default-rtdb.firebaseio.com/");
        list = new ArrayList<>();
        filteredList = new ArrayList<>();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(FavoriteBooksActivity.this));
        adapter = new MyAdapter(this, list);
        recyclerView.setAdapter(adapter);





        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for(DataSnapshot dataSnapshot: snapshot.child("Users").child(uid).child("Wishlist").getChildren()){

                     Books books = dataSnapshot.getValue(Books.class);
                    
                     list.add(books);


                }
                adapter.notifyDataSetChanged();
                recyclerView.setAdapter(new MyAdapter(FavoriteBooksActivity.this, list));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
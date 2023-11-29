package com.example.bookstore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.bookstore.Model.Books;
import com.example.bookstore.Model.MyAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {


RecyclerView recyclerView;
List<Books> list;
DatabaseReference databaseReference;
MyAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Intent intent = getIntent();
        String getNumber = intent.getStringExtra("phoneNumber");

       // intent.putExtra("getNumber",getNumber);

        recyclerView = findViewById(R.id.recycleview);
        databaseReference = FirebaseDatabase.getInstance().getReference();
       // DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://bookstore-7c44c-default-rtdb.firebaseio.com/");
        list = new ArrayList<>();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(HomeActivity.this));
        adapter = new MyAdapter(this, list);
        recyclerView.setAdapter(adapter);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
               list.clear();
                for(DataSnapshot dataSnapshot: snapshot.child("Books").getChildren()){
                    final String getTitle = dataSnapshot.child("title").getValue(String.class);
                    final String getAuthor = dataSnapshot.child("author").getValue(String.class);
                    final String getPrice = dataSnapshot.child("price").getValue(String.class);
                    final String getImage = dataSnapshot.child("image").getValue(String.class);
                    final String getType = dataSnapshot.child("type").getValue(String.class);
                    final String getOwnerNumber = dataSnapshot.child("ownerNumber").getValue(String.class);
                    final String getDescription = dataSnapshot.child("description").getValue(String.class);

                   // Books books = dataSnapshot.getValue(Books.class);
                    //list.add(books);
                    if(!getPrice.equals("0")){
                    Books books = new Books(getTitle, getAuthor, getType, getDescription,getImage, getPrice, getOwnerNumber  );
                    list.add(books);}


                }
                adapter.notifyDataSetChanged();
                recyclerView.setAdapter(new MyAdapter(HomeActivity.this, list));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        FloatingActionButton fab = findViewById(R.id.fab_addBook);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //startActivity(new Intent(HomeActivity.this, AddBookActivity.class));
                Intent intent = new Intent(HomeActivity.this, AddBookActivity.class);
                intent.putExtra("phoneNumber",getNumber);
                startActivity(intent);
               finish();
              // Toast.makeText(HomeActivity.this, getNumber, Toast.LENGTH_SHORT).show();
            }
        });

    }
}
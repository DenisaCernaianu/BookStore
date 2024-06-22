package com.example.bookstore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.bookstore.Model.Books;
import com.example.bookstore.Model.MyAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FavoriteBooksActivity extends AppCompatActivity {

    Button btnGoFav, btnGoAcc, btnGoExchange, btnGoHome, btnSeeRecommendations;
    RecyclerView recyclerView;
    DatabaseReference databaseReference;
    MyAdapter adapter;
    private FirebaseAuth firebaseAuth;

    List<Books> list, filteredList;

    List<String> listIds;

    String uid, bookId;

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

        btnGoAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(FavoriteBooksActivity.this, MyProfileActivity.class));
            }
        });

        FloatingActionButton fabR = findViewById(R.id.seeRecommendations);
        fabR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (!isFinishing()){
                           new AlertDialog.Builder(FavoriteBooksActivity.this)
                                    .setTitle("Vă rugăm așteptați")
                                    .setMessage("Se generează recomandările pentru dumneavoastră...")
                                   .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            //finish();
                                        }
                                    })
                                    .show();
                        }

                    }
                });

                startActivity(new Intent(FavoriteBooksActivity.this, RecommendationsActivity.class));
               // finish();
            }
        });

        recyclerView = findViewById(R.id.recycleview1);

        // DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://bookstore-7c44c-default-rtdb.firebaseio.com/");
        list = new ArrayList<>();
        listIds=new ArrayList<>();
        filteredList = new ArrayList<>();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(FavoriteBooksActivity.this));
        adapter = new MyAdapter(this, list);
        recyclerView.setAdapter(adapter);

        getBooksId();







    }

    private void getBooksId() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listIds.clear();
                // list.clear();
                for(DataSnapshot dataSnapshot: snapshot.child("Users").child(uid).child("Wishlist").getChildren()){

                    Books books = dataSnapshot.getValue(Books.class);
                    final String getId = dataSnapshot.child("id").getValue(String.class);
                    //String getId = books.getId();
                    //  list.add(books);
                    listIds.add(getId);
                }

                getBooks();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getBooks() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for(int i=0; i<listIds.size();i++){
                    for(DataSnapshot dataSnapshot: snapshot.child("Books").getChildren()){
                        bookId = dataSnapshot.child("id").getValue(String.class);


                        Books books = dataSnapshot.getValue(Books.class);
                        if(bookId.equals(listIds.get(i))){
                        //final String getId = dataSnapshot.child("id").getValue(String.class);
                        //String getId = books.getId();
                        list.add(books);
                        break;}
                       // Toast.makeText(FavoriteBooksActivity.this, "id ul e " +listIds.get(0), Toast.LENGTH_SHORT).show();
                    }}

                Collections.sort(list, new Comparator<Books>() {
                    @Override
                    public int compare(Books book1, Books book2) {
                        return book1.getTitle().compareTo(book2.getTitle());
                    }
                });
                    adapter.notifyDataSetChanged();
                    recyclerView.setAdapter(new MyAdapter(FavoriteBooksActivity.this, list));
                }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}
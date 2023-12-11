package com.example.bookstore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

public class HomeActivity extends AppCompatActivity  {


RecyclerView recyclerView;
List<Books> list, filteredList;
DatabaseReference databaseReference;
MyAdapter adapter;
EditText ETSearch;
TextView pageTitle;

Button btnGoFav, btnGoAcc, btnGoExchange;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Intent intent = getIntent();
        String getNumber = intent.getStringExtra("phoneNumber");

       intent.putExtra("getNumber",getNumber);
       pageTitle=findViewById(R.id.pageTitle);
       ETSearch = findViewById(R.id.ETSearch);
       btnGoFav = findViewById(R.id.btnGoFav);
       btnGoAcc=findViewById(R.id.btnGoAcc);
       btnGoExchange=findViewById(R.id.btnGoExchange);

        recyclerView = findViewById(R.id.recycleview);
        databaseReference = FirebaseDatabase.getInstance().getReference();
       // DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://bookstore-7c44c-default-rtdb.firebaseio.com/");
        list = new ArrayList<>();
        filteredList = new ArrayList<>();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(HomeActivity.this));
        adapter = new MyAdapter(this, list);
        recyclerView.setAdapter(adapter);

        pageTitle.setText("Cărți pe care le poți cumpăra :");



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
                    final String getId = dataSnapshot.child("id").getValue(String.class);

                   // Books books = dataSnapshot.getValue(Books.class);
                    //list.add(books);
                    if(!getPrice.equals("0")){
                    Books books = new Books(getTitle, getAuthor, getType, getDescription,getImage, getPrice, getOwnerNumber, getId );
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

        btnGoExchange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, ExchangeActivity.class);
                startActivity(intent);

            }
        });
     ETSearch.addTextChangedListener(new TextWatcher() {
         @Override
         public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

         }

         @Override
         public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            // if(charSequence.length()>0){

            // searchByTitle(charSequence.toString());}
         }

         @Override
         public void afterTextChanged(Editable editable) {

             searchByTitle(editable.toString());

         }

     });

    }

    private void searchByTitle( String bookName) {
        filteredList.clear();


        for(int i=0; i < list.size();i++ ) {
            if ((list.get(i).getTitle().toUpperCase().contains(bookName.toUpperCase())) || (list.get(i).getAuthor().toUpperCase().contains(bookName.toUpperCase())) )  {
                filteredList.add(list.get(i));
            }
        }
        if(filteredList.size()==0){
            //Toast.makeText(this, "Nu s-a găsit cartea !", Toast.LENGTH_LONG).show();
           // adapter.notifyDataSetChanged();
            pageTitle.setText("Cartea nu a fost gasita!");
            recyclerView.setAdapter(new MyAdapter(HomeActivity.this, filteredList));
        }else {
            pageTitle.setText("Cărți pe care le poți cumpăra :");
            adapter.notifyDataSetChanged();
            recyclerView.setAdapter(new MyAdapter(HomeActivity.this, filteredList));
        }

       // databaseReference.child("Books").addListenerForSingleValueEvent(new ValueEventListener() {
      /*  databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                filteredList.clear();
                for(DataSnapshot dataSnapshot: snapshot.child("Books").getChildren()){

                if(dataSnapshot.child("title").getValue(String.class).toUpperCase().contains(bookName.toUpperCase()))
                {

                    final String getTitle = dataSnapshot.child("title").getValue(String.class);
                    final String getAuthor = dataSnapshot.child("author").getValue(String.class);
                    final String getPrice = dataSnapshot.child("price").getValue(String.class);
                    final String getImage = dataSnapshot.child("image").getValue(String.class);
                    final String getType =dataSnapshot.child("type").getValue(String.class);
                    final String getOwnerNumber = dataSnapshot.child("ownerNumber").getValue(String.class);
                    final String getDescription = dataSnapshot.child("description").getValue(String.class);
                    final String getId = dataSnapshot.child("id").getValue(String.class);

                    // Books books = dataSnapshot.getValue(Books.class);
                    //list.add(books);
                    if(!getPrice.equals("0")){
                        Books books = new Books(getTitle, getAuthor, getType, getDescription,getImage, getPrice, getOwnerNumber, getId );
                        filteredList.add(books);

                    }

                    adapter.notifyDataSetChanged();
                    recyclerView.setAdapter(new MyAdapter(HomeActivity.this, filteredList));


                }else if(filteredList.size()==0) {

                    recyclerView.setAdapter(new MyAdapter(HomeActivity.this,list));
                }

            }}

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        */
    }


}
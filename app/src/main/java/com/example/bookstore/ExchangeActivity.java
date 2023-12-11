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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ExchangeActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    List<Books> list, filteredList;
    DatabaseReference databaseReference;
    MyAdapter adapter;
    EditText ETSearch1;

    TextView pageTitle;

    Button btnGoFav, btnGoAcc, btnGoExchange, btnGoHome;

    TextView tvprice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exchange);

        ETSearch1 = findViewById(R.id.ETSearch1);

        btnGoFav = findViewById(R.id.btnGoFav);
        btnGoAcc=findViewById(R.id.btnGoAcc);
        btnGoExchange=findViewById(R.id.btnGoExchange);
        btnGoHome = findViewById(R.id.btnGoHome);
        pageTitle=findViewById(R.id.pageTitle);
        pageTitle.setText("Cărți valabile pentru schimb :");

        recyclerView = findViewById(R.id.recycleview1);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        // DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://bookstore-7c44c-default-rtdb.firebaseio.com/");
        list = new ArrayList<>();
        filteredList = new ArrayList<>();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(ExchangeActivity.this));
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
                    final String getId = dataSnapshot.child("id").getValue(String.class);

                    // Books books = dataSnapshot.getValue(Books.class);
                    //list.add(books);
                    if(getPrice.equals("0")){
                        Books books = new Books(getTitle, getAuthor, getType, getDescription,getImage, getPrice, getOwnerNumber, getId );
                        list.add(books);}


                }
                adapter.notifyDataSetChanged();
                recyclerView.setAdapter(new MyAdapter(ExchangeActivity.this, list));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        btnGoHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ExchangeActivity.this, HomeActivity.class));
                finish();
            }
        });


        ETSearch1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
               /*     try {
                        if(list.size()!=0){
                        adapter.getFilter().filter(charSequence);
                        //recyclerView.setAdapter(new MyAdapter(HomeActivity.this, list));
                            //
                            }
                    }
                    catch (Exception e){
                        Toast.makeText(HomeActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

                    }*/
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
            if ((list.get(i).getTitle().toUpperCase().contains(bookName.toUpperCase())) || (list.get(i).getAuthor().toUpperCase().contains(bookName.toUpperCase())) ) {
                filteredList.add(list.get(i));
            }
        }
        if(filteredList.size()==0){
            pageTitle.setText("Cartea nu a fost gasita!");
           adapter.notifyDataSetChanged();
           // recyclerView.setAdapter(new MyAdapter(ExchangeActivity.this, list));
            //Toast.makeText(this, "Nu s-a găsit cartea !", Toast.LENGTH_SHORT).show();
            recyclerView.setAdapter(new MyAdapter(ExchangeActivity.this, filteredList));
        }else {
            pageTitle.setText("Cărți valabile pentru schimb :");
            adapter.notifyDataSetChanged();
            recyclerView.setAdapter(new MyAdapter(ExchangeActivity.this, filteredList));
        }}
}
package com.example.bookstore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.example.bookstore.Model.Books;
import com.example.bookstore.Model.MyAdapter;
import com.example.bookstore.Model.MyAdapterRecommendations;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class RecommendationsActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    String phoneUser, titleFav;
    DatabaseReference databaseReference ;
     TextView textRecomandari;

     Button backButton;

     String[] titles;

    RecyclerView recyclerView;

    MyAdapterRecommendations adapter;

    List<Books> listRecommendations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommendations);

        textRecomandari = findViewById(R.id.TextRecomandari);
        databaseReference= FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();

        backButton=findViewById(R.id.backButton);

        listRecommendations = new ArrayList<>();
        recyclerView = findViewById(R.id.recycleviewRecommand);

        GridLayoutManager gridLayout = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(gridLayout);

        recyclerView.setHasFixedSize(true);
      //  recyclerView.setLayoutManager(new LinearLayoutManager(RecommendationsActivity.this));
        adapter = new MyAdapterRecommendations(this, listRecommendations);
        recyclerView.setAdapter(adapter);


       SharedPreferences settingsTitle = getSharedPreferences("MyPreferencesTitle", 0);
       titleFav = settingsTitle.getString("title", "Partners in Crime");



        if (! Python.isStarted()) {
            Python.start(new AndroidPlatform(this));
        }

        makeRecommendations();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onBackPressed();
                finish();

            }
        });

    }


    private void makeRecommendations(){
        DatabaseReference userRef = databaseReference.child("Users");
        userRef.child(firebaseAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                phoneUser = snapshot.child("phone").getValue(String.class);

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(RecommendationsActivity.this, ""+ error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });


        Python py = Python.getInstance();
        PyObject myModule = py.getModule("myRecommender");
        PyObject myFnCallVale = myModule.get("recommand");

        new Thread(new Runnable() {
            public void run() {
                // A potentially time consuming task.
                String list = myFnCallVale.call(titleFav,phoneUser).toString();

                if(list.equals("nicioRecomandare")) {System.out.println("Nu am gasit recomandari!"); textRecomandari.setVisibility(View.VISIBLE); }
                else {
                    textRecomandari.setText(list);
                    list = list.substring(1, list.length() - 1);
                    list = list.replace("'", "");
                    titles = list.split(",");
                    System.out.println("Titlurile  " + titles.toString());


                    /////
                    databaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            listRecommendations.clear();
                            for(DataSnapshot dataSnapshot: snapshot.child("Books").getChildren()){
                                final String getTitle = dataSnapshot.child("title").getValue(String.class);
                                final String getAuthor = dataSnapshot.child("author").getValue(String.class);
                                final String getPrice = dataSnapshot.child("price").getValue(String.class);
                                final String getImage = dataSnapshot.child("image").getValue(String.class);
                                final String getType = dataSnapshot.child("type").getValue(String.class);
                                final String getOwnerNumber = dataSnapshot.child("ownerNumber").getValue(String.class);
                                final String getDescription = dataSnapshot.child("description").getValue(String.class);
                                final String getId = dataSnapshot.child("id").getValue(String.class);

                                for (String title : titles) {
                                    if (title.charAt(0) == ' ') title = title.substring(1);
                                    if (title.charAt(title.length() - 1) == ' ')
                                        title = title.substring(0, title.length() - 1);


                                    if (getTitle.equals(title)) {
                                        Books books = new Books(getTitle, getAuthor, getType, getDescription, getImage, getPrice, getOwnerNumber, getId);
                                        listRecommendations.add(books);
                                    }
                                }

                            }

                            adapter.notifyDataSetChanged();
                           recyclerView.setAdapter(new MyAdapterRecommendations(RecommendationsActivity.this, listRecommendations));
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    /////
                }
            }
        }).start();



    }


}
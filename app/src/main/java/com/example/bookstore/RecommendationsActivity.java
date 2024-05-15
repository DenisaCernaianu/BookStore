package com.example.bookstore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RecommendationsActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    String phoneUser, titleFav;
    DatabaseReference databaseReference ;
     TextView textRecomandari;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommendations);

        textRecomandari = findViewById(R.id.TextRecomandari);
        databaseReference= FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();


       SharedPreferences settingsTitle = getSharedPreferences("MyPreferencesTitle", 0);
       titleFav = settingsTitle.getString("title", "Partners in Crime");


        if (! Python.isStarted()) {
            Python.start(new AndroidPlatform(this));
        }

        makeRecommendations();

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

                if(list.equals("nicioRecomandare")) {System.out.println("Nu am gasit recomandari!");   textRecomandari.setText(list);}
                else {
                    textRecomandari.setText(list);
                    list = list.substring(1, list.length() - 1);
                    list = list.replace("'", "");
                    String[] titles = list.split(",");

                    for (String title : titles) {
                        if (title.charAt(0) == ' ') title = title.substring(1);
                        if (title.charAt(title.length() - 1) == ' ')
                            title = title.substring(0, title.length() - 1);
                        System.out.println("Titlul final e " + title);
                    }
                }
            }
        }).start();



    }


}
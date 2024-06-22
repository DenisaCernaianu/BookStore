package com.example.bookstore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bookstore.Model.Books;
import com.example.bookstore.Model.MyAdapterProfile;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MyProfileActivity extends AppCompatActivity {

    DatabaseReference databaseReference;

    FirebaseAuth firebaseAuth;
    private TextView usernameProfile, emailProfile, phoneProfile;

    private ImageButton btnEditProfile;
    List<Books> list;

    private RecyclerView recyclerView;

    private MyAdapterProfile adapter;

    private String uid, telefon, email, username;

    Button btnGoFav, btnGoHome, btnGoExchange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        recyclerView=findViewById(R.id.recycleviewProfile);

        list = new ArrayList<>();

        firebaseAuth = FirebaseAuth.getInstance();

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(MyProfileActivity.this));
        adapter = new MyAdapterProfile(this, list);
        recyclerView.setAdapter(adapter);


        btnGoExchange=findViewById(R.id.btnGoExchange);
        btnGoHome=findViewById(R.id.btnGoHome);
        btnGoFav=findViewById(R.id.btnGoFav);
        btnEditProfile = findViewById(R.id.btnEditProfile);

        usernameProfile=findViewById(R.id.usernameProfile);
        emailProfile=findViewById(R.id.emailProfile);
        phoneProfile=findViewById(R.id.phoneProfile);
        databaseReference = FirebaseDatabase.getInstance().getReference();


        btnGoFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MyProfileActivity.this, FavoriteBooksActivity.class));
            }
        });

        btnGoHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MyProfileActivity.this, HomeActivity.class));
            }
        });

        btnGoExchange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MyProfileActivity.this, ExchangeActivity.class));
            }
        });

        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MyProfileActivity.this,EditProfileActivity.class));
                finish();
            }

        });

        DatabaseReference userRef = databaseReference.child("Users");


        userRef.child(firebaseAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                telefon = snapshot.child("phone").getValue(String.class);
                email = snapshot.child("email").getValue(String.class);
                username = snapshot.child("username").getValue(String.class);

                phoneProfile.setText(telefon);
                emailProfile.setText(email);
                usernameProfile.setText(username);

            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MyProfileActivity.this, ""+ error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });



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


                    if(getOwnerNumber!=null) {
                        if (getOwnerNumber.equals(telefon)) {
                            Books books = new Books(getTitle, getAuthor, getType, getDescription, getImage, getPrice, getOwnerNumber, getId);
                            list.add(books);
                        }
                    }

                }
                adapter.notifyDataSetChanged();
                recyclerView.setAdapter(new MyAdapterProfile(MyProfileActivity.this, list));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

}
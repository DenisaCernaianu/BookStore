package com.example.bookstore;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class HomeActivity extends AppCompatActivity {





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Intent intent = getIntent();
        String getNumber = intent.getStringExtra("phoneNumber");

        FloatingActionButton fab = findViewById(R.id.fab_addBook);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //startActivity(new Intent(HomeActivity.this, AddBookActivity.class));
                Intent intent = new Intent(new Intent(HomeActivity.this, AddBookActivity.class));
                intent.putExtra("phoneNumber",getNumber);
                startActivity(intent);
            }
        });

    }
}
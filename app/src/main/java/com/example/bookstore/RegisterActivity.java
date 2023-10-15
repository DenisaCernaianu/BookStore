package com.example.bookstore;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class RegisterActivity extends AppCompatActivity {
    Button btnsignUp;
    EditText username, email, password;
    Button btnlogIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        btnsignUp = findViewById(R.id.btnSignUp);
        btnlogIn = findViewById(R.id.btnLogin);
        username = findViewById(R.id.ETusername);
        email = findViewById(R.id.ETemail);
        password = findViewById(R.id.ETpassword);

        btnlogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               startActivity(new Intent(RegisterActivity.this, LoginActivity.class));


            }
        });
    }
}
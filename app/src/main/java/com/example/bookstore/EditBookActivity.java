package com.example.bookstore;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bookstore.Model.Books;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class EditBookActivity extends AppCompatActivity {

    String bookId, priceBook;

    Books bookData;
    private Uri ImageUri = null, ImageFinalUri=null;

    private EditText title, author,  details,price,type;

    private final static int  GALLERY_PERM_CODE = 1;


    private TextView textViewPrice;
    private ImageView image;

    private Button saveBook;
    private ImageButton deleteBook;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://bookstore-7c44c-default-rtdb.firebaseio.com/");

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_book);

        Intent intent = getIntent();
        bookId = intent.getStringExtra("bookId");

        title = findViewById(R.id.titleNameProfileEdit);
        author = findViewById(R.id.authorNameEdit);
        price = findViewById(R.id.priceNameProfileEdit);
        details = findViewById(R.id.DetailsNameProfileEdit);
        type = findViewById(R.id.typeNameProfileEdit);
        image = findViewById(R.id.imageNameProfileEdit);
        textViewPrice = findViewById(R.id.TextViewPrice);

        saveBook = findViewById(R.id.btnSaveEditedBook);
        deleteBook=findViewById(R.id.btnDeleteBook);

        loadBookInfo();

        saveBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveBookInfo();
            }
        });

        deleteBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (!isFinishing()){
                            new AlertDialog.Builder(EditBookActivity.this)
                                    .setTitle("Cartea va fi stearsa!")
                                    .setMessage("Sunteti sigur ca doriti sa stergeti cartea?")
                                    .setNegativeButton("NU", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            finish();
                                        }
                                    })
                                    .setPositiveButton("DA", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            databaseReference.child("Books").child(bookId).removeValue();
                                            Toast.makeText(EditBookActivity.this, "Cartea a fost stearsa", Toast.LENGTH_SHORT).show();
                                            finish();
                                        }
                                    }).show();
                        }

                   }
                });
            }
        });


    }

    private void saveBookInfo() {
        databaseReference.child("Books").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                //trimit datele catre realtime database

                databaseReference.child("Books").child(bookId).child("title").setValue(title.getText().toString());
                databaseReference.child("Books").child(bookId).child("author").setValue(author.getText().toString());
                databaseReference.child("Books").child(bookId).child("type").setValue(type.getText().toString());
                databaseReference.child("Books").child(bookId).child("description").setValue(details.getText().toString());
                //databaseReference.child("Books").child(bookId).child("image").setValue(ImageUri);

                if(!priceBook.equals("0")){
                    databaseReference.child("Books").child(bookId).child("price").setValue(price.getText().toString());

                        }

                Toast.makeText(EditBookActivity.this, "Modificarile au fost salvate!", Toast.LENGTH_SHORT).show();

            }
            // }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //  Toast.makeText(AddBookActivity.this, "Cartea nu a putut fi adaugata!", Toast.LENGTH_LONG).show();

            }

        });



    }

    private void loadBookInfo() {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://bookstore-7c44c-default-rtdb.firebaseio.com/");
        databaseReference.child("Books").child(bookId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        final String getTitle = snapshot.child("title").getValue(String.class);
                        final String getAuthor = snapshot.child("author").getValue(String.class);
                        final String getPrice = snapshot.child("price").getValue(String.class);
                        final String getImage = snapshot.child("image").getValue(String.class);
                        final String getType = snapshot.child("type").getValue(String.class);
                        final String getOwnerNumber = snapshot.child("ownerNumber").getValue(String.class);
                        final String getDescription = snapshot.child("description").getValue(String.class);
                        final String getId = snapshot.child("id").getValue(String.class);

                        priceBook = getPrice;
                        bookData = snapshot.getValue(Books.class);

                        if(getPrice.equals("0")){
                            price.setVisibility(View.GONE);
                            textViewPrice.setVisibility(View.GONE);
                            // tvprice.setVisibility(View.GONE);



                        } else {
                            price.setVisibility(View.VISIBLE);
                            textViewPrice.setVisibility(View.VISIBLE);



                        }
                        title.setText(getTitle);
                        author.setText(getAuthor);
                        price.setText(getPrice);
                        type.setText(getType);
                        details.setText(getDescription);
                        //image.setImageURI(getImage);
                        Picasso.get().load(getImage).into(image);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }



}
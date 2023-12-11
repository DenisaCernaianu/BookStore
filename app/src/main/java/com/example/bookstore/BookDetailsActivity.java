package com.example.bookstore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bookstore.Model.Users;
import com.example.bookstore.databinding.ActivityBookDetailsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


public class BookDetailsActivity extends AppCompatActivity {

    ActivityBookDetailsBinding binding;

    String bookId, phoneNumber, priceBook;

    private TextView  title, author,  details,price,type,tvprice, phone, tvphone, lei;
    private ImageView image;
    private Button addBookToFav, addBookToCart, contactOwner, backButton;

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://bookstore-7c44c-default-rtdb.firebaseio.com/");

FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();




    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_details);

        Intent intent = getIntent();
        bookId = intent.getStringExtra("bookId");
        //Intent intent1 = getIntent();
        //phoneNumber = intent1.getStringExtra("phoneNumber");




        title = findViewById(R.id.titleName);
        author =findViewById(R.id.authorName);
        price = findViewById(R.id.priceName);
        details=findViewById(R.id.DetailsName);
        type = findViewById(R.id.typeName);
        image = findViewById(R.id.imageName);
        addBookToFav=findViewById(R.id.btnAddFav);


        tvprice=findViewById(R.id.tvPrice);
        phone = findViewById(R.id.phoneName);
        tvphone=findViewById(R.id.tvPhone);
        contactOwner=findViewById(R.id.btnContactOwner);
        backButton=findViewById(R.id.backButton);
        lei=findViewById(R.id.lei);



        loadBookDetails();

          addBookToFav.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View view) {
                  addToFavourite();
              }
          });

          backButton.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View view) {

                      onBackPressed();
                      finish();

                /*  if(priceBook.equals("0")){
                      startActivity(new Intent(BookDetailsActivity.this,ExchangeActivity.class));
                  } else {
                      startActivity(new Intent(BookDetailsActivity.this,HomeActivity.class));
                  }*/
              }
          });
    }

    private void addToFavourite() {
        databaseReference.child("Users").child("0700000000").child("Wishlist").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild(bookId))
                {
                    Toast.makeText(BookDetailsActivity.this, "Cartea e deja adaugata la favotite !" + bookId + phoneNumber, Toast.LENGTH_LONG).show();

                    }else {
                   Toast.makeText(BookDetailsActivity.this, "urmeaza!"+ bookId + phoneNumber, Toast.LENGTH_LONG).show();

                    }


            }

            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadBookDetails() {

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

                     if(getPrice.equals("0")){
                         price.setVisibility(View.GONE);
                         tvprice.setVisibility(View.GONE);
                         lei.setVisibility(View.GONE);



                     } else {
                         price.setVisibility(View.VISIBLE);
                         tvprice.setVisibility(View.VISIBLE);
                         lei.setVisibility(View.VISIBLE);


                     }
                     title.setText(getTitle);
                     author.setText(getAuthor);
                     price.setText(getPrice);
                     type.setText(getType);
                     details.setText(getDescription);
                     phone.setText(getOwnerNumber);
                     //image.setImageURI(getImage);
                     Picasso.get().load(getImage).into(image);

                 }

                 @Override
                 public void onCancelled(@NonNull DatabaseError error) {

                 }
             });
    }


}
package com.example.bookstore;

import static androidx.constraintlayout.widget.ConstraintLayoutStates.TAG;

import android.Manifest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bookstore.Model.Books;
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
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 1;

    String bookId, phoneNumber, priceBook, uid, titleFav;

    private TextView  title, author,  details,price,type,tvprice, phone, tvphone, lei;
    private ImageView image;
    private Button  addBookToCart, contactOwner, backButton;
    private ImageButton addBookToFav, addBookToFavDelete;

    private Books bookData;

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
        addBookToFavDelete=findViewById(R.id.btnAddFav1);


        tvprice=findViewById(R.id.tvPrice);
        phone = findViewById(R.id.phoneName);
        tvphone=findViewById(R.id.tvPhone);
        contactOwner=findViewById(R.id.btnContactOwner);
        backButton=findViewById(R.id.backButton);
        lei=findViewById(R.id.lei);

        uid=firebaseAuth.getCurrentUser().getUid();

        loadBookDetails();

          addBookToFav.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View view) {
                  addBookToFav.setVisibility(View.GONE);
                  addBookToFavDelete.setVisibility(View.VISIBLE);
                  addToFavourite();
              }
          });
        addBookToFavDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addBookToFavDelete.setVisibility(View.GONE);
                addBookToFav.setVisibility(View.VISIBLE);
                deleteFromFavourite();
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
          contactOwner.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View view) {


                 // sendSMS();
                  /*Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                  sendIntent.setData(Uri.parse("sms:"));
                  sendIntent.putExtra("sms_body", bookData.getOwnerNumber());
                  startActivity(sendIntent);*/
                  checkForSmsPermission();

              }
          });
    }


    private void checkForSmsPermission() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS) !=
                PackageManager.PERMISSION_GRANTED) {
            // Permission not yet granted. Use requestPermissions().
            // MY_PERMISSIONS_REQUEST_SEND_SMS is an
            // app-defined int constant. The callback method gets the
            // result of the request.
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS},
                    MY_PERMISSIONS_REQUEST_SEND_SMS);
        } else {
            // Permission already granted. Enable the message button.
            sendSMS();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == MY_PERMISSIONS_REQUEST_SEND_SMS){
                if(grantResults.length>0 && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED){
                    sendSMS();
                }

                else {
                    Toast.makeText(this, "Vă rugăm să permiteți accesul aplicației la mesaje din setări!", Toast.LENGTH_SHORT).show();
                }

        }}


    private void sendSMS() {
        /*    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) // At least KitKat
            {
                String defaultSmsPackageName = Telephony.Sms.getDefaultSmsPackage(this); // Need to change the build to API 19

                Intent sendIntent = new Intent(Intent.ACTION_SEND);
                sendIntent.setType("text/plain");
                sendIntent.putExtra(Intent.EXTRA_TEXT, "text");
                sendIntent.putExtra(Intent.EXTRA_PHONE_NUMBER,bookData.getOwnerNumber().toString() );

                if (defaultSmsPackageName != null)// Can be null in case that there is no default, then the user would be able to choose
                // any app that support this intent.
                {
                    sendIntent.setPackage(defaultSmsPackageName);
                }
                startActivity(sendIntent);

            }
            else // For early versions, do what worked for you before.
            {
                Intent smsIntent = new Intent(android.content.Intent.ACTION_VIEW);
                smsIntent.setType("vnd.android-dir/mms-sms");
                smsIntent.putExtra("address",bookData.getOwnerNumber());
                smsIntent.putExtra("sms_body","message");
                startActivity(smsIntent);
            }*/

        Intent smsIntent = new Intent(Intent.ACTION_VIEW);
        smsIntent.setType("vnd.android-dir/mms-sms");
        smsIntent.putExtra("address", bookData.getOwnerNumber());
        smsIntent.putExtra("sms_body","Hei! Am vazut cartea publicata de tine in aplicatia Bookverse (" + bookData.getTitle() + " de " + bookData.getAuthor() + ") si m-ar interesa .." );
        startActivity(smsIntent);
        }


    private void deleteFromFavourite() {

        databaseReference.child("Users").child(uid).child("Wishlist").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(bookId)) {
                    databaseReference.child("Users").child(uid).child("Wishlist").child(bookId).removeValue();
                    Toast.makeText(BookDetailsActivity.this, "Cartea a fost stearsa din lista de favorite !", Toast.LENGTH_SHORT).show();


                } else {
                    //Toast.makeText(BookDetailsActivity.this, "urmeaza!"+ bookId + phoneNumber, Toast.LENGTH_LONG).show();
                }


            }

            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }




    private void addToFavourite() {
        databaseReference.child("Users").child(uid).child("Wishlist").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild(bookId))
                {
                    Toast.makeText(BookDetailsActivity.this, "Cartea e deja adaugata la favorite !" , Toast.LENGTH_LONG).show();

                    }else {
                   //Toast.makeText(BookDetailsActivity.this, "urmeaza!"+ bookId + phoneNumber, Toast.LENGTH_LONG).show();
                 ///   databaseReference.child("Users").child(uid).child("Wishlist").child(bookId).child("ownerNumber").setValue(bookData.getOwnerNumber());
                   // //databaseReference.child("Users").child(uid).child("Wishlist").child(bookId).child("title").setValue(bookData.getTitle());
                    //databaseReference.child("Users").child(uid).child("Wishlist").child(bookId).child("author").setValue(bookData.getAuthor());
                    //databaseReference.child("Users").child(uid).child("Wishlist").child(bookId).child("type").setValue(bookData.getType());
                   // databaseReference.child("Users").child(uid).child("Wishlist").child(bookId).child("price").setValue(bookData.getPrice());
                    //databaseReference.child("Users").child(uid).child("Wishlist").child(bookId).child("description").setValue(bookData.getDescription());
                    //databaseReference.child("Users").child(uid).child("Wishlist").child(bookId).child("image").setValue(bookData.getImage());
                    databaseReference.child("Users").child(uid).child("Wishlist").child(bookId).child("id").setValue(bookData.getId());
                    Toast.makeText(BookDetailsActivity.this, "Cartea a fost adaugata la favorite !" , Toast.LENGTH_SHORT).show();
                    //
                    titleFav = bookData.getTitle();
                    SharedPreferences settingsTitle = getSharedPreferences("MyPreferencesTitle", 0);
                    SharedPreferences.Editor editor = settingsTitle.edit();
                    editor.putString("title", titleFav);
                    editor.apply();
                    //
                }


            }

            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadBookDetails() {
        databaseReference.child("Users").child(uid).child("Wishlist").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(bookId)) {
                    addBookToFavDelete.setVisibility(View.VISIBLE);
                    addBookToFav.setVisibility(View.GONE);

                    //Toast.makeText(BookDetailsActivity.this, "Cartea e deja adaugata la favotite !" + bookId + phoneNumber, Toast.LENGTH_LONG).show();
                } else {

                    addBookToFavDelete.setVisibility(View.GONE);
                    addBookToFav.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

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
                        // tvprice.setVisibility(View.GONE);
                         lei.setVisibility(View.GONE);



                     } else {
                         price.setVisibility(View.VISIBLE);
                        // tvprice.setVisibility(View.VISIBLE);
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
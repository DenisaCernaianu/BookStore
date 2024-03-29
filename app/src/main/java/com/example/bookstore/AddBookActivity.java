package com.example.bookstore;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bookstore.Model.Books;
import com.example.bookstore.Model.Users;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;
import android.Manifest.permission.*;


public class AddBookActivity extends AppCompatActivity {
   private Button addBook, backButton, addBookGallery;

   private String bTitle, bAuthor, bType, bDescription, bPrice, saveCurrentData, saveCurrentTime;

    private EditText bookTitle, bookAuthor, bookPrice, bookDetails, bookType;
private boolean state;
    private CheckBox checkBoxSale;
    private TextView textViewPrice;

    private ImageView bookImage;
    private static final int GalleryPick = 1, GALLERY_PERM_CODE=2;

    private static final int CAMERA_PERM_CODE =101, CAPTURE_CODE=100;
    private Uri ImageUri = null, ImageFinalUri=null;
    private Uri ImageUriCam = null;


    private String bookRandomKey, downloadUrl;
    private StorageReference bookImageReference;

    private DatabaseReference dbRef;
    private String  phoneNumber, nrtel, uid;

    private int check=0;
    ProgressBar progressBarAdd;

    private FirebaseAuth firebaseAuth;


   // DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://bookstore-7c44c-default-rtdb.firebaseio.com/");
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);
        String currentPhotoPath;
       // phoneNumber="0";

        Intent intent = getIntent();
         phoneNumber = intent.getStringExtra("phoneNumber");
        databaseReference= FirebaseDatabase.getInstance().getReference();
        bookImageReference = FirebaseStorage.getInstance().getReference().child("Book Images");

        backButton=findViewById(R.id.backButton);
        addBookGallery=findViewById(R.id.addBookGallery);
        addBook = findViewById(R.id.addBook);
        bookImage = findViewById(R.id.bookImage);
        bookTitle= findViewById(R.id.bookTitle);
        bookAuthor=findViewById(R.id.bookAuthor);
        bookType=findViewById(R.id.bookType);
        bookPrice=findViewById(R.id.bookPrice);
        bookDetails=findViewById(R.id.bookDetails);
        textViewPrice=findViewById(R.id.textViewPrice);
        checkBoxSale=findViewById(R.id.checkBoxSale);
        progressBarAdd=findViewById(R.id.progressBarAdd);
        progressBarAdd.setVisibility(View.INVISIBLE);
        dbRef = FirebaseDatabase.getInstance().getReference().child("Books");

        firebaseAuth = FirebaseAuth.getInstance();
        uid = firebaseAuth.getCurrentUser().getUid();






        checkBoxSale.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {


          @Override
          public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

              if(b==true){
              textViewPrice.setVisibility(View.VISIBLE);
              bookPrice.setVisibility(View.VISIBLE);
              check=1;
              }
              else{
                  textViewPrice.setVisibility(View.INVISIBLE);
                  bookPrice.setVisibility(View.INVISIBLE);
                  check=0;
              }
                 }




      });
        bookImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){
                    if (checkSelfPermission(Manifest.permission.CAMERA)==
                            PackageManager.PERMISSION_DENIED// ||
                            /*checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)==
                                    PackageManager.PERMISSION_DENIED*/ ){

                        String[] permission = {Manifest.permission.CAMERA/*, Manifest.permission.WRITE_EXTERNAL_STORAGE*/};
                        requestPermissions(permission, CAMERA_PERM_CODE);

                    }
                    else{
                      openCamera();
                    }
                }else{
                    //openCamera();
                }
               // openCamera();
               //openGallery();


                }

        });

addBookGallery.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){
            if (
                    checkSelfPermission(Manifest.permission.READ_MEDIA_IMAGES)==
                            PackageManager.PERMISSION_DENIED ){

                String[] permission1 = { Manifest.permission.READ_MEDIA_IMAGES};
                requestPermissions(permission1, GALLERY_PERM_CODE);

            }
            else{
                openGallery();
            }
        }else{
            //openCamera();
        }

      //  openGallery();




    }
});
        addBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ValidateProductData();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               startActivity(new Intent(AddBookActivity.this, HomeActivity.class));
                finish();


            }
        });
    }
    private void openGallery(){
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,GALLERY_PERM_CODE);
    }

    private void openCamera(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA_PERM_CODE);

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case CAMERA_PERM_CODE:
                if(grantResults.length>0 && grantResults[0] ==
                PackageManager.PERMISSION_GRANTED){
                   openCamera();
                   break;
                }

          case GALLERY_PERM_CODE:
                if(grantResults.length>0 && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED){
                    openGallery();
                    break;
                }
                else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode== GALLERY_PERM_CODE && resultCode==RESULT_OK && data!=null){
            ImageUri = data.getData();
            bookImage.setImageURI(ImageUri);}
       else if(requestCode==CAMERA_PERM_CODE && resultCode==RESULT_OK && data!=null){
            //ImageUriCam = data.getData();
            //bookImage.setImageURI(ImageUriCam);

             Bundle extras = data.getExtras();
             Bitmap imageBitmap =( Bitmap)extras.get("data");
            bookImage.setImageBitmap(imageBitmap);

             saveImageToGallery(imageBitmap);
        }
    }


    private void saveImageToGallery(Bitmap imageBitmap){
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        String timeStamp = new SimpleDateFormat("yyyyMMddd_HHmmss").format(new Date()); //pentru denumirea imaginii
        String fileName = "IMG_" + timeStamp + ".jpg";

        File imageFile = new File(storageDir, fileName);
        try{
            FileOutputStream outputStream = new FileOutputStream(imageFile);
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();

            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            mediaScanIntent.setData(Uri.fromFile(imageFile));
            sendBroadcast(mediaScanIntent);

            ImageUriCam = Uri.fromFile(imageFile);

           // Toast.makeText(this, "Image send successfully!", Toast.LENGTH_SHORT).show();

        }catch (Exception e){

        }
    }



    private void ValidateProductData(){


        bTitle=bookTitle.getText().toString().trim();
        bAuthor=bookAuthor.getText().toString().trim();
        bType=bookType.getText().toString().trim();
        bPrice=bookPrice.getText().toString().trim();
        bDescription=bookDetails.getText().toString().trim();
          String phoneNumber1 = phoneNumber;

     /*   if(check==1){
          //  bPrice=bookPrice.getText().toString().trim();
            if(TextUtils.isEmpty(bPrice) || bPrice.equals("0") ){
                Toast.makeText(this, "Date incorecte! Introduceti un pret valid! ", Toast.LENGTH_SHORT).show();

            }
        } else {bookPrice.setText("0");
            bPrice=bookPrice.getText().toString().trim();}*/

    /*  if(bPrice=="1"){
        bPrice=bookPrice.getText().toString().trim();
       // ok=1;
          if (TextUtils.isEmpty(bPrice)) {
              Toast.makeText(this, "Introduceti pretul", Toast.LENGTH_SHORT).show(); ok=0;}
      if(bPrice.equals("0")) {Toast.makeText(this, "Pretul trebuie sa fie mai mare ca 0. ", Toast.LENGTH_SHORT).show();
      ok=0;}
          //bPrice=bookPrice.getText().toString().trim();
      }
       // bPrice=bookPrice.getText().toString().trim();*/



        if(check==1) {
            if (ImageUriCam == null && ImageUri == null) {
                Toast.makeText(this, "Introduceti o poza ", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(bTitle)) {
                Toast.makeText(this, "Introduceti titlul", Toast.LENGTH_SHORT).show();

            } else if (TextUtils.isEmpty(bAuthor)) {
                Toast.makeText(this, "Introduceti autorul", Toast.LENGTH_SHORT).show();

            } else if (TextUtils.isEmpty(bType)) {
                Toast.makeText(this, "Introduceti genul", Toast.LENGTH_SHORT).show();

           } else if (TextUtils.isEmpty(bPrice)) {
                Toast.makeText(this, "Introduceti pretul", Toast.LENGTH_SHORT).show();

            }
         else if ((bPrice.equals("0"))) {
            Toast.makeText(this, "Pretul nu poate fi 0", Toast.LENGTH_SHORT).show();

        }
            else if (TextUtils.isEmpty(bDescription)) {
                Toast.makeText(this, "Introduceti descrierea", Toast.LENGTH_SHORT).show();

            } else {
                StoreProductInformation();
            }
        }
        else
        {bPrice="0";
            if (ImageUriCam == null && ImageUri == null) {
                Toast.makeText(this, "Introduceti o poza ", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(bTitle)) {
                Toast.makeText(this, "Introduceti titlul", Toast.LENGTH_SHORT).show();

            } else if (TextUtils.isEmpty(bAuthor)) {
                Toast.makeText(this, "Introduceti autorul", Toast.LENGTH_SHORT).show();

            } else if (TextUtils.isEmpty(bType)) {
                Toast.makeText(this, "Introduceti genul", Toast.LENGTH_SHORT).show();

          //  } else if (TextUtils.isEmpty(bPrice)) {
             //   Toast.makeText(this, "Introduceti pretul", Toast.LENGTH_SHORT).show();

            } else if (TextUtils.isEmpty(bDescription)) {
                Toast.makeText(this, "Introduceti descrierea", Toast.LENGTH_SHORT).show();

            } else {
                StoreProductInformation();
            }

        }

    }

    private void StoreProductInformation() {


        progressBarAdd.setVisibility(View.VISIBLE);

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentData = new SimpleDateFormat("MM dd yyyy");
         saveCurrentData = currentData.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss");
        saveCurrentTime = currentTime.format(calendar.getTime());

       bookRandomKey = (saveCurrentData + saveCurrentTime);

        if(ImageUri == null){ ImageFinalUri = ImageUriCam;}
        else {ImageFinalUri = ImageUri;}
        StorageReference filePath = bookImageReference.child(ImageFinalUri.getLastPathSegment() + bookRandomKey +".jpg");

      final UploadTask uploadTask = filePath.putFile(ImageFinalUri);


        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String message = e.toString();
                Toast.makeText(AddBookActivity.this, "Error" + message, Toast.LENGTH_SHORT).show();

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
               // Toast.makeText(AddBookActivity.this, "Imaginea a fost salvata!", Toast.LENGTH_SHORT).show();

                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                      if(!task.isSuccessful()){
                          throw task.getException();
                      }

                   downloadUrl = filePath.getDownloadUrl().toString();
                      return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if(task.isSuccessful()){

                            downloadUrl = task.getResult().toString();

                           Toast.makeText(AddBookActivity.this, "Cartea a fost salvata !", Toast.LENGTH_SHORT).show();

                           // saveBookInformationToDb();
                           getPhone();
                        }
                    }
                });
            }


        });




    }

    private void getPhone() {

                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

               DatabaseReference userRef = databaseReference.child("Users");

                // A potentially time consuming task.

            userRef.child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {


                        nrtel = snapshot.child("phone").getValue(String.class);
                        saveBookInformationToDb();
                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(AddBookActivity.this, ""+ error.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });




        // if(!bPrice.equals("0")) {


    }

    private void saveBookInformationToDb() {
        new Thread(new Runnable() {
            public void run() {
                // A potentially time consuming task.
                databaseReference.child("Books").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        //trimit datele catre realtime database
                        databaseReference.child("Books").child(bookRandomKey).child("ownerNumber").setValue(nrtel);
                        databaseReference.child("Books").child(bookRandomKey).child("title").setValue(bTitle);
                        databaseReference.child("Books").child(bookRandomKey).child("author").setValue(bAuthor);
                        databaseReference.child("Books").child(bookRandomKey).child("type").setValue(bType);
                        databaseReference.child("Books").child(bookRandomKey).child("price").setValue(bPrice);
                        databaseReference.child("Books").child(bookRandomKey).child("description").setValue(bDescription);
                        databaseReference.child("Books").child(bookRandomKey).child("image").setValue(downloadUrl);
                        databaseReference.child("Books").child(bookRandomKey).child("id").setValue(bookRandomKey);



                        progressBarAdd.setVisibility(View.INVISIBLE);

                        //finish();
                        Intent intent = new Intent(AddBookActivity.this, AddBookActivity.class);
                        intent.putExtra("phoneNumber",phoneNumber);
                        startActivity(intent);
                        // startActivity(new Intent(AddBookActivity.this, AddBookActivity.class));
                        finish();
                    }
                    // }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        //  Toast.makeText(AddBookActivity.this, "Cartea nu a putut fi adaugata!", Toast.LENGTH_LONG).show();

                    }

                });


            }
        }).start();







       /* HashMap<String, Object> bookMap=  new HashMap<>();
        bookMap.put("pid",bookRandomKey);
        bookMap.put("date",saveCurrentData);
        bookMap.put("title",bookTitle);
        bookMap.put("author",bookAuthor);
        bookMap.put("type",bookType);
        bookMap.put("price",bookPrice);
        bookMap.put("details",bookDetails);
        bookMap.put("image",downloadUrl);

        dbRef.child(bookRandomKey).updateChildren(bookMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                         if(task.isSuccessful()){
                             Toast.makeText(AddBookActivity.this, "Cartea a fost adaugata cu succes!", Toast.LENGTH_SHORT).show();
                         }
                         else{
                             Toast.makeText(AddBookActivity.this, "Ceva nu a mers bine !", Toast.LENGTH_SHORT).show();
                         }
                    }
                });

        */

        ////////////////////////////////////////////////////////
       // Books book = new Books(bookRandomKey, bAuthor, bTitle, bDescription,bType,12,99);

        //dbRef.child(bookRandomKey).setValue(book);
      //  }
    }
}
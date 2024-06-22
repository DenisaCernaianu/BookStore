package com.example.bookstore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.bookstore.Model.Books;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ScanBookActivity extends AppCompatActivity {


    ImageButton scanBookBtn;

    private Uri ImageUriCam = null, ImageUriCamISBN = null;

    private Uri ImageUri = null, ImageFinalUri = null;


    private int scanCam = 0, bookCam = 0;

    private BarcodeScannerOptions barcodeScannerOptions;
    private BarcodeScanner barcodeScanner;

    private CheckBox checkBoxSale;
    private int check = 0;

    private String bTitle, bAuthor, bType, bDescription, bPrice, saveCurrentData, saveCurrentTime;

    private String bookRandomKey, downloadUrl;
    private Button addBook, backButton, addBookGallery;

    private TextView textViewPrice;

    private ImageView bookImage;
    ProgressBar progressBarAdd;

    private FirebaseAuth firebaseAuth;
    private StorageReference bookImageReference;

    private String  nrTel;

    // DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://bookstore-7c44c-default-rtdb.firebaseio.com/");
    DatabaseReference databaseReference;
    private RequestQueue mRequestQueue;

    private EditText bookTitle, bookAuthor, bookType, bookDetails, bookPrice;
    private static final int CAMERA_PERM_CODE = 100, CAPTURE_CODE = 101;

    private static final int GalleryPick = 1, GALLERY_PERM_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_book);

        scanCam = 0;

        bookTitle = findViewById(R.id.bookTitle);
        bookAuthor = findViewById(R.id.bookAuthor);
        bookType = findViewById(R.id.bookType);
        bookDetails = findViewById(R.id.bookDetails);
        scanBookBtn = findViewById(R.id.scanBookBtn);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        bookImageReference = FirebaseStorage.getInstance().getReference().child("Book Images");
        backButton = findViewById(R.id.backButton);
        addBookGallery = findViewById(R.id.addBookGallery);
        addBook = findViewById(R.id.addBook);

        bookImage = findViewById(R.id.bookImage);

        bookPrice = findViewById(R.id.bookPrice);
        textViewPrice = findViewById(R.id.textViewPrice);
        checkBoxSale = findViewById(R.id.checkBoxSale);
        progressBarAdd = findViewById(R.id.progressBarAdd);
        progressBarAdd.setVisibility(View.INVISIBLE);

        firebaseAuth = FirebaseAuth.getInstance();


        barcodeScannerOptions = new BarcodeScannerOptions.Builder()
                .setBarcodeFormats
                        (Barcode.FORMAT_ALL_FORMATS)
                .build();

        barcodeScanner = BarcodeScanning.getClient(barcodeScannerOptions);


        checkBoxSale.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {


            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (b == true) {
                    textViewPrice.setVisibility(View.VISIBLE);
                    bookPrice.setVisibility(View.VISIBLE);
                    check = 1;
                } else {
                    textViewPrice.setVisibility(View.GONE);
                    bookPrice.setVisibility(View.GONE);
                    check = 0;
                }
            }


        });

        bookImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanCam = 0;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.CAMERA) ==
                            PackageManager.PERMISSION_DENIED// ||
                            /*checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)==
                                    PackageManager.PERMISSION_DENIED*/) {

                        String[] permission = {Manifest.permission.CAMERA/*, Manifest.permission.WRITE_EXTERNAL_STORAGE*/};
                        requestPermissions(permission, CAMERA_PERM_CODE);

                    } else {
                        openCamera();
                    }
                } else {
                    //openCamera();
                }
                // openCamera();
                //openGallery();


            }

        });

        addBookGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanCam = 0;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (
                            checkSelfPermission(Manifest.permission.READ_MEDIA_IMAGES) ==
                                    PackageManager.PERMISSION_DENIED) {

                        String[] permission1 = {Manifest.permission.READ_MEDIA_IMAGES};
                        requestPermissions(permission1, GALLERY_PERM_CODE);

                    } else {
                        openGallery();
                    }
                } else {
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
                startActivity(new Intent(ScanBookActivity.this, HomeActivity.class));
                finish();


            }
        });
        scanBookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                scanCam = 1;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(android.Manifest.permission.CAMERA) ==
                            PackageManager.PERMISSION_DENIED) {

                        String[] permission = {Manifest.permission.CAMERA};
                        requestPermissions(permission, CAMERA_PERM_CODE);

                    } else {
                        openCamera();
                    }
                } else {
                    //openCamera();
                }
            }
        });


    }

    private void openGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GALLERY_PERM_CODE);
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA_PERM_CODE);
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CAMERA_PERM_CODE:
                if (grantResults.length > 0 && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED) {
                    openCamera();
                    break;
                }
            case GALLERY_PERM_CODE:
                if (grantResults.length > 0 && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED) {
                    openGallery();
                    break;
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                }

        }
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_PERM_CODE && resultCode == RESULT_OK && data != null) {
            ImageUri = data.getData();
            bookImage.setImageURI(ImageUri);
        } else if (requestCode == CAMERA_PERM_CODE && resultCode == RESULT_OK && data != null) {

            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            saveImageToGallery(imageBitmap);
            if(scanCam == 0) { bookImage.setImageBitmap(imageBitmap);}
        }
    }


    private void saveImageToGallery(Bitmap imageBitmap) {
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        String timeStamp = new SimpleDateFormat("yyyyMMddd_HHmmss").format(new Date()); //pentru denumirea imaginii
        String fileName = "IMG_" + timeStamp + ".jpg";

        File imageFile = new File(storageDir, fileName);
        try {
            FileOutputStream outputStream = new FileOutputStream(imageFile);
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();

            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            mediaScanIntent.setData(Uri.fromFile(imageFile));
            sendBroadcast(mediaScanIntent);



            if (scanCam == 1) {
                ImageUriCamISBN = Uri.fromFile(imageFile);
                processImage();
            } else {
                ImageUriCam = Uri.fromFile(imageFile);
            }



        } catch (Exception e) {

        }
    }


    private void processImage() {
        try {
            InputImage inputImage = InputImage.fromFilePath(this, ImageUriCamISBN);

            Task<List<Barcode>> barcodeResult = barcodeScanner.process(inputImage)
                    .addOnSuccessListener(new OnSuccessListener<List<Barcode>>() {
                        @Override
                        public void onSuccess(List<Barcode> barcodes) {
                            getInformationFromBarCode(barcodes);

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ScanBookActivity.this, "Failed  scanning " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } catch (Exception e) {
            Toast.makeText(this, "Faide due to" + e.getMessage(), Toast.LENGTH_SHORT).show();

        }
    }

    private void getInformationFromBarCode(List<Barcode> barcodes) {

        if(barcodes.size() == 0){
            Toast.makeText(this, "Codul de bare nu a fost identificat!", Toast.LENGTH_SHORT).show();
        }
        else {
            for (Barcode barcode : barcodes) {
                Rect bounds = barcode.getBoundingBox();
                Point[] corners = barcode.getCornerPoints();

                String rawValue = barcode.getRawValue();

                int valueType = barcode.getValueType();

                if (rawValue == null) {
                    Toast.makeText(this, "Codul de bare nu a fost identificat! va rugam reincercati!", Toast.LENGTH_SHORT).show();

                } else {
                    getBooksInfo(rawValue);
                }


            }
        }


    }


    private void getBooksInfo(String query) {

        mRequestQueue = Volley.newRequestQueue(ScanBookActivity.this);
        mRequestQueue.getCache().clear();

      String url = "https://www.googleapis.com/books/v1/volumes?q=isbn:" + query;
       // String url = "https://www.googleapis.com/books/v1/volumes?q=" + query;

        RequestQueue queue = Volley.newRequestQueue(ScanBookActivity.this);

        JsonObjectRequest booksObjrequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    JSONArray itemsArray = response.getJSONArray("items");
                    //  for (int i = 0; i < itemsArray.length(); i++) {
                    JSONObject itemsObj = itemsArray.getJSONObject(0);
                    JSONObject volumeObj = itemsObj.getJSONObject("volumeInfo");
                    String title = volumeObj.optString("title");
                    JSONArray authorsArray = volumeObj.getJSONArray("authors");
                    String auth = null;
                    if (authorsArray.length() > 1) {
                        auth = authorsArray.getString(0) + ", " + authorsArray.getString(1);

                    } else {
                        auth = authorsArray.getString(0);
                    }
                    JSONArray IdentifiersArray = volumeObj.getJSONArray("industryIdentifiers");
                    String isbn = IdentifiersArray.getJSONObject(0).optString("identifier");
                    JSONArray categgoriesArray = volumeObj.getJSONArray("categories");
                    String categ = categgoriesArray.getString(0);
                    String publisher = volumeObj.optString("publisher");
                    String publishedDate = volumeObj.optString("publishedDate");
                    String description = volumeObj.optString("description");
                    int pageCount = volumeObj.optInt("pageCount");


                    bookTitle.setText(title);
                    bookAuthor.setText(auth);
                    bookType.setText(categ);
                    bookDetails.setText(description);


                } catch (JSONException e) {
                    e.printStackTrace();
                    bookTitle.setText("");
                    bookAuthor.setText("");
                    bookType.setText("");
                    bookDetails.setText("");
                    Toast.makeText(ScanBookActivity.this, "Cartea nu a putut fi gasita!Introduceti manual informatiile!", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ScanBookActivity.this, "Error found is " + error, Toast.LENGTH_SHORT).show();
            }
        });

        queue.add(booksObjrequest);
    }


    private void ValidateProductData() {


        bTitle = bookTitle.getText().toString().trim();
        bAuthor = bookAuthor.getText().toString().trim();
        bType = bookType.getText().toString().trim();
        bPrice = bookPrice.getText().toString().trim();
        bDescription = bookDetails.getText().toString().trim();

        if (check == 1) {
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

            } else if ((bPrice.equals("0"))) {
                Toast.makeText(this, "Pretul nu poate fi 0", Toast.LENGTH_SHORT).show();

            } else if (TextUtils.isEmpty(bDescription)) {
                Toast.makeText(this, "Introduceti descrierea", Toast.LENGTH_SHORT).show();

            } else {
                StoreProductInformation();
            }
        } else {
            bPrice = "0";
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

        if (ImageUri == null) {
            ImageFinalUri = ImageUriCam;
        } else {
            ImageFinalUri = ImageUri;
        }
        StorageReference filePath = bookImageReference.child(ImageFinalUri.getLastPathSegment() + bookRandomKey + ".jpg");

        final UploadTask uploadTask = filePath.putFile(ImageFinalUri);


        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String message = e.toString();
                Toast.makeText(ScanBookActivity.this, "Error" + message, Toast.LENGTH_SHORT).show();

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Toast.makeText(AddBookActivity.this, "Imaginea a fost salvata!", Toast.LENGTH_SHORT).show();

                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }

                        downloadUrl = filePath.getDownloadUrl().toString();
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {

                            downloadUrl = task.getResult().toString();

                            //Toast.makeText(ScanBookActivity.this, "Cartea a fost salvata !", Toast.LENGTH_SHORT).show();

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


                nrTel = snapshot.child("phone").getValue(String.class);
                saveBookInformationToDb();
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ScanBookActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });


        // if(!bPrice.equals("0")) {


    }

    private void saveBookInformationToDbBUN() {
        new Thread(new Runnable() {
            public void run() {
                // A potentially time consuming task.
                databaseReference.child("Books").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        //trimit datele catre realtime database
                        databaseReference.child("Books").child(bookRandomKey).child("ownerNumber").setValue(nrTel);
                        databaseReference.child("Books").child(bookRandomKey).child("title").setValue(bTitle);
                        databaseReference.child("Books").child(bookRandomKey).child("author").setValue(bAuthor);
                        databaseReference.child("Books").child(bookRandomKey).child("type").setValue(bType);
                        databaseReference.child("Books").child(bookRandomKey).child("price").setValue(bPrice);
                        databaseReference.child("Books").child(bookRandomKey).child("description").setValue(bDescription);
                        databaseReference.child("Books").child(bookRandomKey).child("image").setValue(downloadUrl);
                        databaseReference.child("Books").child(bookRandomKey).child("id").setValue(bookRandomKey);


                        progressBarAdd.setVisibility(View.INVISIBLE);

                        //finish();
                        Intent intent = new Intent(ScanBookActivity.this, ScanBookActivity.class);
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

    }

    private void saveBookInformationToDb() {
        new Thread(new Runnable() {
            public void run() {
                // A potentially time consuming task.

                Books book  = new Books(bTitle, bAuthor, bType, bDescription, downloadUrl, bPrice, nrTel, bookRandomKey);

                databaseReference.child("Books").child(bookRandomKey).setValue(book);

                progressBarAdd.setVisibility(View.INVISIBLE);

                Intent intent = new Intent(ScanBookActivity.this, ScanBookActivity.class);
                startActivity(intent);
                finish();

              /*  databaseReference.child("Books").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        //trimit datele catre realtime database
                        databaseReference.child("Books").child(bookRandomKey).child("ownerNumber").setValue(nrTel);
                        databaseReference.child("Books").child(bookRandomKey).child("title").setValue(bTitle);
                        databaseReference.child("Books").child(bookRandomKey).child("author").setValue(bAuthor);
                        databaseReference.child("Books").child(bookRandomKey).child("type").setValue(bType);
                        databaseReference.child("Books").child(bookRandomKey).child("price").setValue(bPrice);
                        databaseReference.child("Books").child(bookRandomKey).child("description").setValue(bDescription);
                        databaseReference.child("Books").child(bookRandomKey).child("image").setValue(downloadUrl);
                        databaseReference.child("Books").child(bookRandomKey).child("id").setValue(bookRandomKey);


                        progressBarAdd.setVisibility(View.INVISIBLE);

                        //finish();
                        Intent intent = new Intent(ScanBookActivity.this, ScanBookActivity.class);
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

*/
            }
        }).start();

    }
}
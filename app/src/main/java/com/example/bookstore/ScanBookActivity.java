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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import java.util.Date;
import java.util.List;

public class ScanBookActivity extends AppCompatActivity {


    ImageButton  scanBookBtn;

    private Uri ImageUriCam = null;

    private int scanCam = 0, bookCam=0;

    private BarcodeScannerOptions barcodeScannerOptions;
    private BarcodeScanner barcodeScanner;


    private RequestQueue mRequestQueue;

    private EditText bookTitle, bookAuthor, bookType, bookDetails;
    private static final int CAMERA_PERM_CODE =100, CAPTURE_CODE=101;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_book);

        bookTitle = findViewById(R.id.bookTitle);
        bookAuthor = findViewById(R.id.bookAuthor);
        bookType=findViewById(R.id.bookType);
        bookDetails = findViewById(R.id.bookDetails);
        scanBookBtn = findViewById(R.id.scanBookBtn);


        barcodeScannerOptions = new BarcodeScannerOptions.Builder()
                .setBarcodeFormats
                        (Barcode.FORMAT_ALL_FORMATS)
                .build();

        barcodeScanner = BarcodeScanning.getClient(barcodeScannerOptions);


        scanBookBtn.setOnClickListener(new View.OnClickListener() {
              @Override
            public void onClick(View view) {

            scanCam=1;
            bookCam=0;

                 if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){
                         if (checkSelfPermission(android.Manifest.permission.CAMERA)==
                       PackageManager.PERMISSION_DENIED ){

                     String[] permission = {Manifest.permission.CAMERA};
                     requestPermissions(permission, CAMERA_PERM_CODE);

                         }
                         else
                         {
                            openCamera();
                         }
                 }else{
                    //openCamera();
                 }
              }
         });



    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA_PERM_CODE);
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case CAMERA_PERM_CODE:
                if(grantResults.length>0 && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED){
                    openCamera();
                    break;
                }

                else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                }

        }
    }
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
         if(requestCode==CAMERA_PERM_CODE && resultCode==RESULT_OK && data!=null){

            Bundle extras = data.getExtras();
            Bitmap imageBitmap =( Bitmap)extras.get("data");

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

            if(scanCam == 1) {
                processImage();
            }


        }catch (Exception e){

        }
    }



    private void processImage() {
        try {
            InputImage inputImage = InputImage.fromFilePath(this, ImageUriCam);

            Task<List<Barcode>> barcodeResult  = barcodeScanner.process(inputImage)
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
        }
        catch (Exception  e){
            Toast.makeText(this, "Faide due to"+e.getMessage(), Toast.LENGTH_SHORT).show();

        }
    }

    private void getInformationFromBarCode(List<Barcode> barcodes) {

        for(Barcode barcode : barcodes){
            Rect bounds = barcode.getBoundingBox();
            Point[] corners = barcode.getCornerPoints();

            String rawValue = barcode.getRawValue();

            int valueType = barcode.getValueType();

            if(rawValue==null){
                Toast.makeText(this, "Codul de bare nu a fost identificat! va rugam reincercati!", Toast.LENGTH_SHORT).show();

            }
            else
            {
                getBooksInfo(rawValue);
            }



        }

    }


    private void getBooksInfo(String query) {

        mRequestQueue = Volley.newRequestQueue(ScanBookActivity.this);
        mRequestQueue.getCache().clear();

        String url = "https://www.googleapis.com/books/v1/volumes?q=isbn:" + query;

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
                        if(authorsArray.length()>1){
                             auth = authorsArray.getString(0)+ ", " + authorsArray.getString(1);

                        }
                        else {auth = authorsArray.getString(0);}
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


    }
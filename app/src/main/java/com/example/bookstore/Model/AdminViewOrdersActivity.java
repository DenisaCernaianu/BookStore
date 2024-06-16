package com.example.bookstore.Model;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bookstore.FavoriteBooksActivity;
import com.example.bookstore.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AdminViewOrdersActivity extends AppCompatActivity {

    Button viewUsersBtn, viewBooksBtn;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    List<Books> list;

    List<String> listIds, listTypes, listBookType;

    ImageButton calendarBtn;

    TextView titleAdmin, NoOfOrders, favType,noBooksFavType, textViewNumberBooksType;

    String bookId, date, bookType, topTypes, genPreferat, itemType;
    RecyclerView recyclerViewAdminOrders;
    MyAdapterRecommendations adapterAdminOrders;

    AutoCompleteTextView autoCompleteTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_view_orders);

        list = new ArrayList<>();
        listIds = new ArrayList<>();
        listTypes = new ArrayList<>();
        listBookType = new ArrayList<>();
        recyclerViewAdminOrders=findViewById(R.id.recyclerviewAdminOrders);
        viewUsersBtn = findViewById(R.id.btnGoUsers);
        viewBooksBtn = findViewById(R.id.btnGoBooks);
        titleAdmin =  findViewById(R.id.pageTitleAdmin);
        adapterAdminOrders = new MyAdapterRecommendations(this, list);
        recyclerViewAdminOrders.setAdapter(adapterAdminOrders);

        GridLayoutManager gridLayout = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
        recyclerViewAdminOrders.setLayoutManager(gridLayout);
        recyclerViewAdminOrders.setHasFixedSize(true);
        calendarBtn = findViewById(R.id.calendarBtn);
        NoOfOrders = findViewById(R.id.NoOfOrders);
        favType= findViewById(R.id.favType);
        noBooksFavType = findViewById(R.id.noBooksFavType);
        autoCompleteTextView = findViewById(R.id.autoCompleteType);
        textViewNumberBooksType =  findViewById(R.id.textViewNumberBooksType);

        textViewNumberBooksType.setVisibility(View.INVISIBLE);



        date = "01 06 2024";
        titleAdmin.setText(date);
        getBooksId();

        calendarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCalendar();
            }
        });

        viewUsersBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminViewOrdersActivity.this, AdminActivity.class));
                finish();
            }
        });

        viewBooksBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminViewOrdersActivity.this, AdminViewBooksActivity.class));
                finish();
            }
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listBookType);
        autoCompleteTextView.setAdapter(adapter);

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                itemType = adapterView.getItemAtPosition(position).toString();

                int numberBooks = 0;
                for(int i=0; i< listTypes.size();i++){
                    numberBooks = Collections.frequency(listTypes, itemType);}
                String text = "Numarul de carti vandute având genul " + itemType + " este : " + String.valueOf(numberBooks);
                textViewNumberBooksType.setVisibility(View.VISIBLE);
                textViewNumberBooksType.setText(text);

            }
        });


    }

    private void getBooksId() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listIds.clear();
                for(DataSnapshot dataSnapshot: snapshot.child("Orders").getChildren()){

                    final String getId = dataSnapshot.child("bookId").getValue(String.class);
                    final String orderDate = dataSnapshot.child("orderDate").getValue(String.class);
                    if(orderDate.equals(date))
                    {listIds.add(getId);}
                }
                getBooks();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getBooks() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                listTypes.clear();
                listBookType.clear();
                for(int i=0; i<listIds.size();i++){
                    for(DataSnapshot dataSnapshot: snapshot.child("Books").getChildren()){
                        bookId = dataSnapshot.child("id").getValue(String.class);
                        bookType = dataSnapshot.child("type").getValue(String.class);

                        Books books = dataSnapshot.getValue(Books.class);
                        if(bookId.equals(listIds.get(i))){
                            list.add(books);
                            String [] tipuri = null;
                           bookType =  bookType.replace(", ",",");
                           tipuri = bookType.split(",");
                            Collections.addAll(listTypes, tipuri);

                            break;}
                    }}

                Collections.sort(listTypes, new Comparator<String>() {
                    @Override
                    public int compare(String type1, String type2) {
                        return type1.compareTo(type2);
                    }
                });

                topTypes = listTypes.toString();
                int max=0;
                genPreferat = "";

                for(int i=0; i< listTypes.size();i++){
                int nr = Collections.frequency(listTypes, listTypes.get(i));
                if(nr >max) {max=nr; genPreferat = listTypes.get(i);}
                if(!listBookType.contains(listTypes.get(i)))  listBookType.add(listTypes.get(i));
                }

                if(max==0) {textViewNumberBooksType.setVisibility(View.INVISIBLE);}
                favType.setText(genPreferat);
                String numberFavBooks = "  (" + String.valueOf(max) + ")";
                noBooksFavType.setText(numberFavBooks);


                Collections.sort(list, new Comparator<Books>() {
                    @Override
                    public int compare(Books book1, Books book2) {
                        return book1.getTitle().compareTo(book2.getTitle());
                    }
                });
                NoOfOrders.setText(String.valueOf(list.size()));

                adapterAdminOrders.notifyDataSetChanged();
                recyclerViewAdminOrders.setAdapter(new MyAdapterRecommendations(AdminViewOrdersActivity.this, list));



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void openCalendar(){

        DatePickerDialog calendar = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                String monthString = String.valueOf(month+1);
                if(monthString.length()==1) monthString = "0"+monthString;

                String dayString = String.valueOf(day);
                if(dayString.length()==1) dayString = "0"+dayString;

                date = dayString+" "+monthString+" "+String.valueOf(year);

                titleAdmin.setText(date);

                getBooksId();

            }
        }, 2024, 5, 1);
        calendar.show();
    }
}
package com.example.bookstore.Model;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bookstore.ExchangeActivity;
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

public class AdminViewBooksActivity extends AppCompatActivity {

    RecyclerView recyclerViewAdminBooks;

    List<Books> list, filteredList;

    MyAdapter adapterAdminBooks;
    Button viewUsersBtn;
    EditText searchBookAdmin;

    TextView titlePageAdmin;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_view_books);

        recyclerViewAdminBooks=findViewById(R.id.recyclerviewAdmin);
        list = new ArrayList<>();
        filteredList = new ArrayList<>();
        recyclerViewAdminBooks.setHasFixedSize(true);
        recyclerViewAdminBooks.setLayoutManager(new LinearLayoutManager(AdminViewBooksActivity.this));
        adapterAdminBooks = new MyAdapter(this, list);
        recyclerViewAdminBooks.setAdapter(adapterAdminBooks);
        viewUsersBtn =  findViewById(R.id.btnGoUsers);
        searchBookAdmin = findViewById(R.id.ETSearchAdmin);
        titlePageAdmin = findViewById(R.id.pageTitleAdmin);

        titlePageAdmin.setText("Cărți disponibile în aplicație : ");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for(DataSnapshot dataSnapshot: snapshot.child("Books").getChildren()){

                    Books books = dataSnapshot.getValue(Books.class);
                        list.add(books);
                }
                Collections.sort(list, new Comparator<Books>() {
                    @Override
                    public int compare(Books book1, Books book2) {
                        return book1.getTitle().compareTo(book2.getTitle());
                    }
                });

                adapterAdminBooks.notifyDataSetChanged();
                recyclerViewAdminBooks.setAdapter(new MyAdapter(AdminViewBooksActivity.this, list));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        viewUsersBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminViewBooksActivity.this, AdminActivity.class));
                finish();
            }
        });

        searchBookAdmin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                searchByTitle(editable.toString());
            }

        });
    }

    private void searchByTitle( String bookName) {
        filteredList.clear();


        for(int i=0; i < list.size();i++ ) {
            if ((list.get(i).getTitle().toUpperCase().contains(bookName.toUpperCase())) || (list.get(i).getAuthor().toUpperCase().contains(bookName.toUpperCase())) ) {
                filteredList.add(list.get(i));
            }
        }
        if(filteredList.size()==0){
            //aici un mesaj care sa indice ca nu s-a gasit nicio carte
            titlePageAdmin.setText("Nu s-a gasit nicio carte!");
            adapterAdminBooks.notifyDataSetChanged();
            recyclerViewAdminBooks.setAdapter(new MyAdapter(AdminViewBooksActivity.this, filteredList));


        }else {
            titlePageAdmin.setText("Cărți disponibile în aplicație : ");
            adapterAdminBooks.notifyDataSetChanged();
            recyclerViewAdminBooks.setAdapter(new MyAdapter(AdminViewBooksActivity.this, filteredList));
        }}
}
package com.example.bookstore.Model;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookstore.BookDetailsActivity;
import com.example.bookstore.EditBookActivity;
import com.example.bookstore.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MyAdapterProfile extends RecyclerView.Adapter<MyAdapterProfile.MyViewHolder> {

    private final Context context;
    private final List<Books> list;
    String priceBook;



    public MyAdapterProfile(Context context, List<Books> list){
        this.context=context;
        this.list=list;
    }
    public  MyAdapterProfile.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycleitem_profile, parent,false);


        return new MyAdapterProfile.MyViewHolder(v);


    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {




        Books books = list.get(position);

        holder.title.setText(books.getTitle());
        holder.author.setText(books.getAuthor());
        Picasso.get().load(books.getImage()).into(holder.image);
        holder.details.setText(books.getDescription());

        holder.type.setText(books.getType());
        if(!books.getPrice().equals("0")){
        holder.price.setText(books.getPrice());}
        else {
            holder.price.setText("Cartea este pusa la schimb. Nu are pret");
        }


        String id = books.getId();
         priceBook = books.getPrice();




        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, EditBookActivity.class);
                intent.putExtra("bookId",id);
                //intent.putExtra("phoneNumber",getNumber);
                context.startActivity(intent);

            }



        });



    }



    @Override
    public int getItemCount() {
        return list.size();

    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title, author, price,details, type,lei;
        ImageView image;

        Button btnEdit, btnDelete;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.titleNameProfile);
            author = itemView.findViewById(R.id.authorNameProfile);
            image = itemView.findViewById(R.id.imageNameProfile);
            price = itemView.findViewById(R.id.priceNameProfile);
            details=itemView.findViewById(R.id.DetailsNameProfile);
            type = itemView.findViewById(R.id.typeNameProfile);
            btnEdit = itemView.findViewById(R.id.btnEditBook);



        }


    }
}


package com.example.bookstore.Model;

import static android.content.Intent.getIntent;
import com.example.bookstore.*;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookstore.AddBookActivity;
import com.example.bookstore.BookDetailsActivity;
import com.example.bookstore.HomeActivity;
import com.example.bookstore.R;
import com.squareup.picasso.Picasso;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    @NonNull

    private final Context context;
    private final List<Books> list;

    public MyAdapter(Context context, List<Books> list){
        this.context=context;
        this.list=list;
    }
    public  MyAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycleitem, parent,false);


        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull  MyAdapter.MyViewHolder holder, int position) {
      Books books = list.get(position);

        holder.title.setText(books.getTitle());
        holder.author.setText(books.getAuthor());
        Picasso.get().load(books.getImage()).into(holder.image);
        holder.details.setText(books.getDescription());
        holder.type.setText(books.getType());
        holder.price.setText(books.getPrice());

        String id = books.getId();
        String price = books.getPrice();




        holder.btnDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent = new Intent(context, BookDetailsActivity.class);
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

        Button btnDetails;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.titleName);
            author = itemView.findViewById(R.id.authorName);
            image = itemView.findViewById(R.id.imageName);
            price = itemView.findViewById(R.id.priceName);
            details=itemView.findViewById(R.id.DetailsName);
            type = itemView.findViewById(R.id.typeName);
            btnDetails = itemView.findViewById(R.id.btnDetails);
        }




    }
}

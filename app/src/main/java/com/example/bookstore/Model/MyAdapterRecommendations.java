package com.example.bookstore.Model;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookstore.BookDetailsActivity;
import com.example.bookstore.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MyAdapterRecommendations extends RecyclerView.Adapter<MyAdapterRecommendations.MyViewHolder> {
    @NonNull

    private final Context context;
    private final List<Books> list;

    public MyAdapterRecommendations(Context context, List<Books> list){
        this.context=context;
        this.list=list;
    }
    public  MyAdapterRecommendations.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycleitem_recommendations, parent,false);


        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Books books = list.get(position);

        holder.title.setText(books.getTitle());
        holder.author.setText(books.getAuthor());
        Picasso.get().load(books.getImage()).into(holder.image);

        String id = books.getId();





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
        TextView title, author;
        ImageView image;

        Button btnDetails;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.titleName);
            author = itemView.findViewById(R.id.authorName);
            image = itemView.findViewById(R.id.imageName);
            btnDetails = itemView.findViewById(R.id.btnDetails);
        }




    }
}

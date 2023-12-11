package com.example.bookstore.Model;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookstore.BookDetailsActivity;
import com.example.bookstore.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MyAdapterUser extends RecyclerView.Adapter<MyAdapterUser.MyViewHolder> {
    @NonNull

    private final Context context;
    private final List<Users> list;

    public MyAdapterUser(Context context, List<Users> list){
        this.context=context;
        this.list=list;
    }
    public  MyAdapterUser.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycleitemuser, parent,false);


        return new MyAdapterUser.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull  MyAdapterUser.MyViewHolder holder, int position) {
        Users users = list.get(position);

        holder.username.setText(users.getUsername());
        holder.email.setText(users.getEmail());
        //Picasso.get().load(books.getImage()).into(holder.image);
        holder.phone.setText(users.getPhone());

        String id = users.getIdUser();




        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, AdminDeleteUserActivity.class);
                intent.putExtra("userId",id);
                //intent.putExtra("phoneNumber",getNumber);
                context.startActivity(intent);
                Toast.makeText(context, "id ul este" + id, Toast.LENGTH_SHORT).show();



          }



        });



    }



    @Override
    public int getItemCount() {
        return list.size();

    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView username, email, phone;


        Button btnDelete;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.usernameName);
            email = itemView.findViewById(R.id.emailName);
            phone = itemView.findViewById(R.id.phoneName);
            btnDelete= itemView.findViewById(R.id.btnDelete);
        }




    }
}
package com.example.appfoodorder.Adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appfoodorder.FoodList;
import com.example.appfoodorder.Home;
import com.example.appfoodorder.Interface.ItemClickListener;
import com.example.appfoodorder.Model.Category;
import com.example.appfoodorder.R;
import com.example.appfoodorder.ViewHolder.MenuViewHolder;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MenuAdapter extends RecyclerView.Adapter<MenuViewHolder> {
    List<Category> categoryList;
    Home context;

    public MenuAdapter(List<Category> categoryList, Home context) {
        this.categoryList = categoryList;
        this.context = context;
    }

    @NonNull
    @Override
    public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.menu_item,parent,false);
        return new MenuViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuViewHolder holder, int position) {
        holder.txtMenuName.setText(categoryList.get(position).getName());
        Picasso.get().load(categoryList.get(position).getImage()).into(holder.imageView);
        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                Intent foodList = new Intent(context, FoodList.class);
                foodList.putExtra("CategoryId",categoryList.get(position).getId());
                context.startActivity(foodList);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }
}

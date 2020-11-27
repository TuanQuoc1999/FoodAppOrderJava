package com.example.appfoodorder.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appfoodorder.Interface.ItemClickListener;
import com.example.appfoodorder.R;

public class FoodViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    public TextView txtFoodName;
    public ImageView imgFood;
    public TextView txtFoodPrice;

    private ItemClickListener itemClickListener;

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public FoodViewHolder(@NonNull View itemView) {
        super(itemView);
        txtFoodName = itemView.findViewById(R.id.food_name);
        imgFood = itemView.findViewById(R.id.food_image);
        txtFoodPrice = itemView.findViewById(R.id.food_price);

        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v,getAdapterPosition(),false);
    }
}

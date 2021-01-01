package com.example.appfoodorder.ViewHolder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.appfoodorder.Interface.ItemClickListener;
import com.example.appfoodorder.R;

public class CartViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {

    public TextView txt_cart_name, txt_price;
    public ElegantNumberButton btnQuantity;

    private ItemClickListener itemClickListener;

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public CartViewHolder(@NonNull View itemView) {
        super(itemView);
        txt_cart_name = itemView.findViewById(R.id.cart_item_name);
        txt_price =itemView.findViewById(R.id.cart_item_price);
        btnQuantity = itemView.findViewById(R.id.btnQuantity);

        itemView.setOnLongClickListener(this);
    }

    @Override
    public boolean onLongClick(View v) {
        itemClickListener.onClick(v,getAdapterPosition(),true);
        return true;
    }
}

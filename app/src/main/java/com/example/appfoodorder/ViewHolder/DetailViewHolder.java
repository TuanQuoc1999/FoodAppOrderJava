package com.example.appfoodorder.ViewHolder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appfoodorder.Interface.ItemClickListener;
import com.example.appfoodorder.R;

public class DetailViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView txtDetailName, txtDetailPrice, txtDetailQuantity;
    private ItemClickListener itemClickListener;

    public DetailViewHolder(@NonNull View itemView) {
        super(itemView);
        txtDetailName = itemView.findViewById(R.id.txtProductName);
        txtDetailPrice = itemView.findViewById(R.id.txtProductPrice);
        txtDetailQuantity = itemView.findViewById(R.id.txtProductQuantity);

        itemView.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v,getAdapterPosition(),false);
    }
}

package com.example.appfoodorder.ViewHolder;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appfoodorder.Interface.ItemClickListener;
import com.example.appfoodorder.R;

public class OrderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView txtOrderId, txtOrderStatus, txtOrderPhone, txtOrderAddress, txtOrderTotal, txtOrderTime;
    private ItemClickListener itemClickListener;
    public Button btnOrderLocation, btnOrderDetail, btnOrderCancel;

    public OrderViewHolder(@NonNull View itemView) {
        super(itemView);

        txtOrderTime = itemView.findViewById(R.id.order_time);
        txtOrderAddress = itemView.findViewById(R.id.order_address);
        txtOrderId = itemView.findViewById(R.id.order_id);
        txtOrderStatus = itemView.findViewById(R.id.order_status);
        txtOrderPhone = itemView.findViewById(R.id.order_phone);
        txtOrderTotal = itemView.findViewById(R.id.order_total);
        btnOrderLocation = itemView.findViewById(R.id.order_location);
        btnOrderCancel = itemView.findViewById(R.id.order_cancel);
        btnOrderDetail = itemView.findViewById(R.id.order_detail);

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

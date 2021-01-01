package com.example.appfoodorder.ViewHolder;

import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appfoodorder.Interface.ItemClickListener;
import com.example.appfoodorder.R;

public class ShowCommentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView txtUserName, txtComment;
    public RatingBar ratingBar;
    private ItemClickListener itemClickListener;

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public ShowCommentViewHolder(@NonNull View itemView) {
        super(itemView);
        txtComment = itemView.findViewById(R.id.txtComment);
        txtUserName = itemView.findViewById(R.id.txtUserName);
        ratingBar = itemView.findViewById(R.id.ratingBar);
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v,getAdapterPosition(),false);
    }
}

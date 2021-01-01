package com.example.appfoodorder.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appfoodorder.Interface.ItemClickListener;
import com.example.appfoodorder.Model.Rating;
import com.example.appfoodorder.R;
import com.example.appfoodorder.ViewHolder.ShowCommentViewHolder;

import java.util.List;

public class ShowCommentAdapter extends RecyclerView.Adapter<ShowCommentViewHolder> {
    List<Rating> ratingList;
    Context context;

    public ShowCommentAdapter(List<Rating> ratingList, Context context) {
        this.ratingList = ratingList;
        this.context = context;
    }

    @NonNull
    @Override
    public ShowCommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.comment_row,parent,false);
        return new ShowCommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShowCommentViewHolder holder, int position) {
        holder.txtUserName.setText(ratingList.get(position).getUserName());
        holder.txtComment.setText(ratingList.get(position).getComment());
        holder.ratingBar.setRating(ratingList.get(position).getRateValue());
        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return ratingList.size();
    }
}

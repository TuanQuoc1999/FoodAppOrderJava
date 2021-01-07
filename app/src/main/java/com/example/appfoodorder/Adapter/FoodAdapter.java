package com.example.appfoodorder.Adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appfoodorder.FoodDetail;
import com.example.appfoodorder.FoodList;
import com.example.appfoodorder.Interface.ItemClickListener;
import com.example.appfoodorder.Model.Food;
import com.example.appfoodorder.R;
import com.example.appfoodorder.ViewHolder.FoodViewHolder;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class FoodAdapter extends RecyclerView.Adapter<FoodViewHolder> {
    List<Food> foodList;
    FoodList context;

    public FoodAdapter(List<Food> foodList, FoodList context) {
        this.foodList = foodList;
        this.context = context;
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.food_item,parent,false);
        return new FoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        holder.txtFoodName.setText(foodList.get(position).getName());
        Locale locale = new Locale("vi","VN");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
        holder.txtFoodPrice.setText(fmt.format(foodList.get(position).getPrice()));
        Picasso.get().load("http://10.0.196.85:8080/androidAPI/img/"+foodList.get(position).getImage()).into(holder.imgFood);
        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                Intent foodDetail = new Intent(context, FoodDetail.class);
                foodDetail.putExtra("foodID",foodList.get(position).getId()+"");
                context.startActivity(foodDetail);
            }
        });
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }
}

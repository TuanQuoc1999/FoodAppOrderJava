package com.example.appfoodorder.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appfoodorder.HistoryDetail;
import com.example.appfoodorder.Interface.ItemClickListener;
import com.example.appfoodorder.Model.Detail;
import com.example.appfoodorder.R;
import com.example.appfoodorder.ViewHolder.DetailViewHolder;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class DetailAdapter extends RecyclerView.Adapter<DetailViewHolder> {
    private List<Detail> detailList;
    private HistoryDetail context;

    public DetailAdapter(List<Detail> detailList, HistoryDetail context) {
        this.detailList = detailList;
        this.context = context;
    }

    @NonNull
    @Override
    public DetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.order_detail_layout,parent,false);
        return new DetailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DetailViewHolder holder, int position) {
        holder.txtDetailName.setText(detailList.get(position).getName());
        holder.txtDetailQuantity.setText(detailList.get(position).getQuantity());

        Locale locale = new Locale("vi","VN");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
        int price = Integer.valueOf(detailList.get(position).getPrice());
        holder.txtDetailPrice.setText(fmt.format(price));

        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return detailList.size();
    }
}

package com.example.appfoodorder.Adapter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.appfoodorder.Cart;
import com.example.appfoodorder.Interface.ItemClickListener;
import com.example.appfoodorder.Model.Order;
import com.example.appfoodorder.R;
import com.example.appfoodorder.ViewHolder.CartViewHolder;
import com.example.appfoodorder.database.Database;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CartAdapter extends RecyclerView.Adapter<CartViewHolder> {
    private List<Order> listData = new ArrayList<>();
    private Cart context;

    public CartAdapter(List<Order> listData, Cart context) {
        this.listData = listData;
        this.context = context;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.cart_layout,parent,false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final CartViewHolder holder, final int position) {

        holder.btnQuantity.setNumber(String.valueOf(listData.get(position).getQuantity()));
        holder.btnQuantity.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
            @Override
            public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {
                Order order = listData.get(position);
                order.setQuantity(newValue);
                new Database(context).updateCart(order);

                // set total price
                int total = 0;
                List<Order> cart = new Database(context).getCart();
                for (Order item:cart) {
                    total += item.getPrice() * item.getQuantity();
                }
                Locale locale = new Locale("vi","VN");
                NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
                context.txtTotalPrice.setText(fmt.format(total));

                // set total price for each item
                int price = listData.get(position).getPrice()*listData.get(position).getQuantity();
                holder.txt_price.setText(fmt.format(price));
            }
        });

        Locale locale = new Locale("vi","VN");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
        int price = listData.get(position).getPrice()*listData.get(position).getQuantity();
        holder.txt_price.setText(fmt.format(price));
        holder.txt_cart_name.setText(listData.get(position).getProductName());

        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, final int position, boolean isLongClick) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                dialog.setTitle("Delete confirm!");
                dialog.setMessage("Are you sure to delete this food?");
                dialog.setCancelable(false);
                dialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Database database = new Database(context);
                        database.deleteItem(listData.get(position).getID());
                        context.loadListFood();
                    }
                });
                dialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                dialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }
}

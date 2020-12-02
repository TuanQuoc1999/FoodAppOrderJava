package com.example.appfoodorder.Adapter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.appfoodorder.Common.Common;
import com.example.appfoodorder.HistoryDetail;
import com.example.appfoodorder.Interface.ItemClickListener;
import com.example.appfoodorder.LocationActivity;
import com.example.appfoodorder.Model.History;
import com.example.appfoodorder.OrderHistory;
import com.example.appfoodorder.R;
import com.example.appfoodorder.ViewHolder.OrderViewHolder;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class OrderAdapter extends RecyclerView.Adapter<OrderViewHolder> {
    private List<History> list;
    private OrderHistory context;
    protected String urlCancelOrder = "https://orderadmin.000webhostapp.com/androidAPI/cancelorder.php";

    public OrderAdapter(List<History> list, OrderHistory context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.order_layout,parent,false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final OrderViewHolder holder, final int position) {
        holder.txtOrderId.setText(list.get(position).getIDcart());
        holder.txtOrderPhone.setText(Common.currentUser.getPhone());
        if(list.get(position).getStatus().equals("0")){
            holder.txtOrderStatus.setText("Ordered");
        }else if(list.get(position).getStatus().equals("1")){
            holder.txtOrderStatus.setText("Shipping");
        }else {
            holder.txtOrderStatus.setText("Paid");
        }
        holder.txtOrderAddress.setText(list.get(position).getAddress());
        holder.txtOrderTime.setText(list.get(position).getTime());

        Locale locale = new Locale("vi","VN");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
        int total = list.get(position).getTotal();
        holder.txtOrderTotal.setText(fmt.format(total));

        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {

            }
        });

        holder.btnOrderLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Common.isConnectedToInternet(context)){
                    Intent location = new Intent(context, LocationActivity.class);
                    location.putExtra("Address",holder.txtOrderAddress.getText().toString());
                    context.startActivity(location);
                }else {
                    Toast.makeText(context, "No internet, please check the connection", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });

        holder.btnOrderDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Common.isConnectedToInternet(context)){
                    Intent detail = new Intent(context, HistoryDetail.class);
                    detail.putExtra("id",holder.txtOrderId.getText().toString());
                    detail.putExtra("status",holder.txtOrderStatus.getText().toString());
                    detail.putExtra("address",holder.txtOrderAddress.getText().toString());
                    detail.putExtra("time",holder.txtOrderTime.getText().toString());
                    detail.putExtra("total",holder.txtOrderTotal.getText().toString());
                    context.startActivity(detail);
                }else {
                    Toast.makeText(context, "No internet, please check the connection", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });

        holder.btnOrderCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Common.isConnectedToInternet(context)){
                    if(list.get(position).getStatus().equals("0")){
                        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                        dialog.setTitle("Cancel confirm!");
                        dialog.setMessage("Are you sure to cancel this order?");
                        dialog.setCancelable(false);
                        dialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                RequestQueue requestQueue = Volley.newRequestQueue(context);
                                StringRequest cancelRequest = new StringRequest(Request.Method.POST, urlCancelOrder, new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        String message,success;
                                        try {
                                            JSONObject jsonObject = new JSONObject(response);
                                            message = jsonObject.getString("message");
                                            success = jsonObject.getString("success");
                                            Toast.makeText(context,message, Toast.LENGTH_SHORT).show();
                                            Log.d("deleteorder",success);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
                                        Log.d("deleteorder","Error!\n" + error.toString());
                                    }
                                }){
                                    @Override
                                    protected Map<String, String> getParams() throws AuthFailureError {
                                        Map<String,String> params = new HashMap<>();
                                        params.put("orderID",list.get(position).getIDcart());
                                        return params;
                                    }
                                };
                                requestQueue.add(cancelRequest);
                                notifyDataSetChanged();
                                context.finish();
                                context.startActivity(context.getIntent());
                            }
                        });
                        dialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        dialog.show();
                    }else {
                        Toast.makeText(context, "Unable to cancel this order", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(context, "No internet, please check the connection", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}

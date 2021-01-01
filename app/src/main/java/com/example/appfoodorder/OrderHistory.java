package com.example.appfoodorder;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.appfoodorder.Adapter.OrderAdapter;
import com.example.appfoodorder.Common.Common;
import com.example.appfoodorder.Model.History;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class OrderHistory extends AppCompatActivity {
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    List<History> listHistory = new ArrayList<>();
    OrderAdapter adapter = new OrderAdapter(listHistory,this);
    ProgressDialog dialog;
    ImageButton btnBack;
    String urlGetCart = "https://orderadmin.000webhostapp.com/androidAPI/getcart.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        //màn hình chờ
        dialog = new ProgressDialog(OrderHistory.this);
        dialog.setMessage("Please, wait ...");
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        anhXa();

        if(Common.isConnectedToInternet(getBaseContext())) {
            loadListOrderHistory();
        }else {
            Toast.makeText(this, "No internet, please check the connection", Toast.LENGTH_SHORT).show();
            return;
        }

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void loadListOrderHistory() {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest arrayRequest = new JsonArrayRequest(Request.Method.GET, urlGetCart, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for(int i=0; i<response.length();i++){
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);

                        String id = jsonObject.getString("idcart");
                        String time = jsonObject.getString("ordertime");
                        String status = jsonObject.getString("status");
                        int total = jsonObject.getInt("total");
                        String address = jsonObject.getString("address");
                        String phone = jsonObject.getString("phone");
                        if(phone.equals(Common.currentUser.getPhone())) {
                            listHistory.add(new History(id, time, status, total, address));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                adapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(OrderHistory.this, "Error", Toast.LENGTH_SHORT).show();
                Log.d("getCart", "Error!\n"+error.toString());
            }
        });
        requestQueue.add(arrayRequest);
        recyclerView.setAdapter(adapter);
    }

    private void anhXa() {
        recyclerView = findViewById(R.id.listOrders);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        btnBack = findViewById(R.id.btnBack);
    }
}

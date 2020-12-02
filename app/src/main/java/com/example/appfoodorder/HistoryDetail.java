package com.example.appfoodorder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.appfoodorder.Adapter.DetailAdapter;
import com.example.appfoodorder.Common.Common;
import com.example.appfoodorder.Model.Detail;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HistoryDetail extends AppCompatActivity {
    TextView txtDetailPhone, txtDetailTime, txtDetailID, txtDetailTotal, txtDetailStatus, txtDetailAddress;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    List<Detail> detailList = new ArrayList<>();
    DetailAdapter adapter = new DetailAdapter(detailList,this);
    ProgressDialog dialog;
    ImageButton btnBack;
    String id,status,address,time,total;
    String urlGetCartDetail = "https://orderadmin.000webhostapp.com/androidAPI/getcartdetail.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_detail);

        //màn hình chờ
        dialog = new ProgressDialog(this);
        dialog.setMessage("Please, wait ...");
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        anhXa();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //get intent
        if (getIntent() != null) {
            id = getIntent().getStringExtra("id");
            status = getIntent().getStringExtra("status");
            address = getIntent().getStringExtra("address");
            time = getIntent().getStringExtra("time");
            total = getIntent().getStringExtra("total");
        }

        if (!id.isEmpty() || !status.isEmpty() || !address.isEmpty() || !time.isEmpty() || !total.isEmpty()) {
            if (Common.isConnectedToInternet(getBaseContext())) {
                txtDetailStatus.setText(status);
                txtDetailTime.setText(time);
                txtDetailPhone.setText(Common.currentUser.getPhone());
                txtDetailID.setText(id);
                txtDetailAddress.setText(address);
                txtDetailTotal.setText(total);
                loadListDetail();
            } else {
                Toast.makeText(this, "No internet, please check the connection", Toast.LENGTH_SHORT).show();
                return;
            }
        }
    }
    private void loadListDetail() {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest arrayRequest = new JsonArrayRequest(Request.Method.GET, urlGetCartDetail, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i=0;i<response.length();i++){
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        String idcart = jsonObject.getString("idcart");
                        String price = jsonObject.getString("price");
                        String foodname = jsonObject.getString("foodName");
                        String quantity = jsonObject.getString("quantity");
                        if(idcart.equals(id)){
                            detailList.add(new Detail(foodname,quantity,price));
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
                Toast.makeText(HistoryDetail.this, "Error", Toast.LENGTH_SHORT).show();
                Log.d("getorderdetail","Error!\n"+error.toString());
            }
        });
        requestQueue.add(arrayRequest);
        recyclerView.setAdapter(adapter);
    }

    private void anhXa() {
        recyclerView = findViewById(R.id.recycler_detail);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        btnBack = findViewById(R.id.btnBack);
        txtDetailAddress = findViewById(R.id.order_address);
        txtDetailID = findViewById(R.id.order_id);
        txtDetailPhone = findViewById(R.id.order_phone);
        txtDetailTime = findViewById(R.id.order_time);
        txtDetailStatus = findViewById(R.id.order_status);
        txtDetailTotal = findViewById(R.id.order_total);
    }
}

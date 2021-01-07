package com.example.appfoodorder;

import android.app.AlertDialog;
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
import com.example.appfoodorder.Adapter.ShowCommentAdapter;
import com.example.appfoodorder.Common.Common;
import com.example.appfoodorder.Model.Rating;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ShowComment extends AppCompatActivity {
    List<Rating> ratingList = new ArrayList<>();
    RecyclerView recyclerView;
    ShowCommentAdapter adapter = new ShowCommentAdapter(ratingList,ShowComment.this);
    RecyclerView.LayoutManager layoutManager;
    String foodId;
    AlertDialog dialog;
    ImageButton btnBack;
    String urlGetRating = "http://10.0.196.85:8080/androidAPI/getrating.php";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_comment);
        anhXa();

        //get Intent
        if(getIntent() != null){
            foodId = getIntent().getStringExtra(Common.INTENT_FOOD_ID);
        }
        if (!foodId.isEmpty()){
            if(Common.isConnectedToInternet(getBaseContext())){
                //màn hình chờ
                dialog = new ProgressDialog(this);
                dialog.setMessage("Please, wait ...");
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
                loadRating(foodId);
            }else {
                Toast.makeText(this, "No internet, please check the connection", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void loadRating(final String Id) {
        RequestQueue requestQueue = Volley.newRequestQueue(ShowComment.this);
        JsonArrayRequest arrayRequest = new JsonArrayRequest(Request.Method.GET, urlGetRating, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for(int i = 0; i<response.length(); i++){
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        String userName = jsonObject.getString("userName");
                        String foodID = jsonObject.getString("foodId");
                        String rate = jsonObject.getString("rateValue");
                        float value = Float.parseFloat(rate);
                        String cmt = jsonObject.getString("comment");
                        if (foodID.equals(Id)) {
                            ratingList.add(new Rating(userName, foodID, value, cmt));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    adapter.notifyDataSetChanged();
                    dialog.dismiss();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ShowComment.this, "Error", Toast.LENGTH_SHORT).show();
                Log.d("getRating", "Error!\n"+error.toString());
            }
        });
        requestQueue.add(arrayRequest);
        recyclerView.setAdapter(adapter);
    }

    private void anhXa() {
        recyclerView = findViewById(R.id.recycler_comment);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        btnBack = findViewById(R.id.btnBack);
    }
}

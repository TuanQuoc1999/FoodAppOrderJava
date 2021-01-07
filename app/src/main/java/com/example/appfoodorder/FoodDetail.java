package com.example.appfoodorder;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.appfoodorder.Common.Common;
import com.example.appfoodorder.Model.Food;
import com.example.appfoodorder.Model.Order;
import com.example.appfoodorder.database.Database;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;
import com.stepstone.apprating.AppRatingDialog;
import com.stepstone.apprating.listener.RatingDialogListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class FoodDetail extends AppCompatActivity implements RatingDialogListener {
    TextView txtFoodName, txtFoodPrice, txtFoodDescription;
    ImageView imgFood;
    CollapsingToolbarLayout collapsingToolbarLayout;
    FloatingActionButton btnCart,btnRate;
    ElegantNumberButton btnNumber;
    ImageButton btnback,buttonCart;
    RatingBar ratingBar;
    Button btnShowComment;

    String foodId;
    String urlDetail = "http://10.0.196.85:8080/androidAPI/getdetail.php";
    String urlGetRating = "http://10.0.196.85:8080/androidAPI/getavg.php";
    String urlSetRating = "http://10.0.196.85:8080/androidAPI/setrating.php";
    ProgressDialog dialog;
    Food currentFood;
    Database database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);

        //màn hình chờ
        dialog = new ProgressDialog(FoodDetail.this);
        dialog.setMessage("Please, wait ...");
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        anhXa();
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppbar);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppbar);

        buttonCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Database database = new Database(getBaseContext());
                database.createTable();
                Intent intentCart = new Intent(FoodDetail.this,Cart.class);
                startActivity(intentCart);
                finish();
            }
        });

        //get FoodID
        if(getIntent()!=null){
            foodId = getIntent().getStringExtra("foodID");
        }
        if(!foodId.isEmpty()){
            if(Common.isConnectedToInternet(getBaseContext())) {
                getDetailFood(foodId);
                getRating(foodId);
            }
            else {
                Toast.makeText(this, "No internet, please check the connection", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                database = new Database(getBaseContext());
                database.createTable();
                database.addToCart(new Order(
                        currentFood.getId(),
                        currentFood.getName(),
                        Integer.parseInt(btnNumber.getNumber()),
                        currentFood.getPrice()
                ));
                Toast.makeText(FoodDetail.this, "Added to cart", Toast.LENGTH_SHORT).show();
            }
        });

        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRatingDialog();
            }
        });

        btnShowComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent showComment = new Intent(FoodDetail.this,ShowComment.class);
                showComment.putExtra(Common.INTENT_FOOD_ID,foodId);
                startActivity(showComment);
            }
        });
    }

    private void getRating(final String foodId) {
        RequestQueue requestQueue = Volley.newRequestQueue(FoodDetail.this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, urlGetRating, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for(int i=0;i<response.length();i++){
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);

                        String temp  = jsonObject.getString("trungbinh");
                        float value = Float.parseFloat(temp);
                        String id = jsonObject.getString("foodId");
                        if(id.equals(foodId)) {
                            ratingBar.setRating(value);
                            Log.d("value",value+"");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(FoodDetail.this, "Error", Toast.LENGTH_SHORT).show();
                Log.d("setrating", "Error!\n"+error.toString());
            }
        });
        requestQueue.add(jsonArrayRequest);
    }

    private void showRatingDialog() {
        new AppRatingDialog.Builder()
                .setPositiveButtonText("Rate")
                .setNegativeButtonText("Cancel")
                .setNoteDescriptions(Arrays.asList("bad","not good","ok","good","excellent"))
                .setDefaultRating(3)
                .setTitle("rate this food")
                .setDescription("rate and leave your comment")
                .setTitleTextColor(R.color.colorPrimaryDark)
                .setDescriptionTextColor(R.color.transparentBlack)
                .setHint("leave your comment here...")
                .setHintTextColor(R.color.colorPrimaryDark)
                .setCommentTextColor(R.color.colorText)
                .setCommentBackgroundColor(R.color.transparentBlack)
                .create(FoodDetail.this)
                .show();
    }


    private void getDetailFood(final String foodId) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest detailRequest = new StringRequest(Request.Method.POST, urlDetail, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    currentFood = new Food();
                    currentFood.setId(jsonObject.getInt("id"));
                    currentFood.setPrice(jsonObject.getInt("price"));
                    currentFood.setImage(jsonObject.getString("image"));
                    currentFood.setMenuId(jsonObject.getInt("cateID"));
                    currentFood.setDescription(jsonObject.getString("description"));
                    currentFood.setName(jsonObject.getString("name"));

                    Picasso.get().load(jsonObject.getString("image")).into(imgFood);
                    collapsingToolbarLayout.setTitle(jsonObject.getString("name"));
                    txtFoodDescription.setText(jsonObject.getString("description"));
                    txtFoodName.setText(jsonObject.getString("name"));
                    Locale locale = new Locale("vi","VN");
                    NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
                    txtFoodPrice.setText(fmt.format(currentFood.getPrice()));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(FoodDetail.this, "Error", Toast.LENGTH_SHORT).show();
                Log.d("detail", "Error!\n"+error.toString());
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("foodID",foodId.trim());
                return params;
            }
        };
        requestQueue.add(detailRequest);
    }

    private void anhXa() {
        btnback = findViewById(R.id.btnBack);
        txtFoodName = findViewById(R.id.food_name);
        txtFoodDescription = findViewById(R.id.food_description);
        txtFoodPrice = findViewById(R.id.food_price);
        imgFood = findViewById(R.id.img_food);
        collapsingToolbarLayout = findViewById(R.id.collapsing);
        btnCart = findViewById(R.id.btnCart);
        btnNumber = findViewById(R.id.number_button);
        ratingBar = findViewById(R.id.ratingBar);
        btnRate = findViewById(R.id.btnRate);
        btnShowComment = findViewById(R.id.buttonShowComment);
        buttonCart = findViewById(R.id.ButtonCart);
    }

    @Override
    public void onNegativeButtonClicked() {

    }

    @Override
    public void onPositiveButtonClicked(final int i, final String s) {
        RequestQueue requestQueue = Volley.newRequestQueue(FoodDetail.this);
        StringRequest ratingRequest = new StringRequest(Request.Method.POST, urlSetRating, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.getInt("success")==1) {
                        Log.d("RatingResult", jsonObject.getString("message"));
                        Toast.makeText(FoodDetail.this, "thanks for your rating", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(FoodDetail.this, jsonObject.getString("rating false"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(FoodDetail.this, "Error", Toast.LENGTH_SHORT).show();
                Log.d("setrating", "Error!\n" + error.toString());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("userPhone",Common.currentUser.getPhone());
                params.put("foodId",foodId);
                params.put("rateValue",String.valueOf(i));
                params.put("comment",s);
                return params;
            }
        };
        requestQueue.add(ratingRequest);
        finish();
        startActivity(getIntent());
    }
}

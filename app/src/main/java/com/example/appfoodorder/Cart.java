package com.example.appfoodorder;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.appfoodorder.Adapter.CartAdapter;
import com.example.appfoodorder.Common.Common;
import com.example.appfoodorder.Model.Order;
import com.example.appfoodorder.database.Database;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class Cart extends AppCompatActivity {
    ImageButton btnBack;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    public TextView txtTotalPrice;
    Button btnOrder;
    List<Order> cart = new ArrayList<>();
    CartAdapter adapter;
    int total = 0;
    final String alphabet = "123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    String s;
    EditText edtAddress;
    String urlsetCartDetail = "http://10.0.196.85:8080/androidAPI/setcartdetail.php";
    String urlsetCart = "http://10.0.196.85:8080/androidAPI/setcart.php";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        anhXa();
        loadListFood();
        btnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Common.isConnectedToInternet(getBaseContext())){
                    if(total>0){
                        showAlertDialog();
                    }else {
                        Toast.makeText(Cart.this, "Add some food to cart first!", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(Cart.this, "No internet, please check the connection", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void showAlertDialog() {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(Cart.this);
        alertDialog.setTitle("One more step!");
        alertDialog.setMessage("Ship to the address: ");

        edtAddress = new EditText(Cart.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        edtAddress.setLayoutParams(lp);
        alertDialog.setView(edtAddress);
        alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);
        alertDialog.setCancelable(false);

        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(!edtAddress.getText().toString().isEmpty()){
                    //create random CartCode
                    final int N = alphabet.length();
                    Random r = new Random();
                    char[] c =  new char[8];
                    for (int j = 0; j < 8; j++) {
                        c[j]=alphabet.charAt(r.nextInt(N));
                    }
                    s = String.copyValueOf(c);

                    //them gio hang
                    addCart();

                    //thêm chi tiết giỏ hàng
                    addCartDetail();

                    //clear giỏ hàng hiện tại
                    new Database(getBaseContext()).cleanCart();
                    finish();
                }
                else {
                    Toast.makeText(Cart.this, "Enter address, please!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alertDialog.show();
    }

    private void addCartDetail() {
        for (int i = 0; i<cart.size(); i++){
            final RequestQueue requestQueue = Volley.newRequestQueue(this);
            final int finalI = i;
            StringRequest addCartRequest = new StringRequest(Request.Method.POST, urlsetCartDetail, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(Cart.this, "Error", Toast.LENGTH_SHORT).show();
                    Log.d("addcartdetail", "Error!\n"+error.toString());
                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> params = new HashMap<>();
                    params.put("idcart",s);
                    params.put("idfood", String.valueOf(cart.get(finalI).getProductId()));
                    params.put("quantity", String.valueOf(cart.get(finalI).getQuantity()));
                    params.put("price", String.valueOf(cart.get(finalI).getPrice()));
                    return params;
                }
            };
            requestQueue.add(addCartRequest);
        }
    }

    private void addCart() {
        final RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest addCartRequest = new StringRequest(Request.Method.POST, urlsetCart, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.getString("result").equals("success")){
                        Toast.makeText(Cart.this, "Thank you for your order", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(Cart.this, "Order fail", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Cart.this, "Error", Toast.LENGTH_SHORT).show();
                Log.d("addcart", "Error!\n"+error.toString());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("idcart",s);
                params.put("phone", Common.currentUser.getPhone());
                params.put("total", String.valueOf(total));
                params.put("address", edtAddress.getText().toString());
                Date date = new Date();
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                params.put("order_time",formatter.format(date));
                return params;
            }
        };
        requestQueue.add(addCartRequest);
    }

    public void loadListFood() {
        cart = new Database(this).getCart();
        adapter = new CartAdapter(cart,this);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        //tính tổng tiền
        total = 0;
        for (Order order:cart) {
            total += order.getPrice() * order.getQuantity();
        }
        Locale locale = new Locale("vi","VN");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
        txtTotalPrice.setText(fmt.format(total));
    }

    private void anhXa() {
        recyclerView = findViewById(R.id.listCart);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        btnBack = findViewById(R.id.btnBack);
        txtTotalPrice = findViewById(R.id.total);
        btnOrder = findViewById(R.id.btnPlaceOrder);
    }
}

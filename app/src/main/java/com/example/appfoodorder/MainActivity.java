package com.example.appfoodorder;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.appfoodorder.Common.Common;
import com.example.appfoodorder.Model.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {
    Button btnDangKi,btnDangNhap;
    TextView txtSlogan;
    String urlLogin = "https://orderadmin.000webhostapp.com/androidAPI/login.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        anhXa();
        Paper.init(this);
        Typeface typeface = Typeface.createFromAsset(getAssets(),"fonts/NABILA.TTF");
        txtSlogan.setTypeface(typeface);
        btnDangNhap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent dangnhap = new Intent(MainActivity.this,DangNhap.class);
                startActivity(dangnhap);
            }
        });

        btnDangKi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent dangki = new Intent(MainActivity.this,DangKi.class);
                startActivity(dangki);
            }
        });

        //check remember
        String user = Paper.book().read(Common.USER_KEY);
        String pw = Paper.book().read(Common.PWD_KEY);
        if (user != null && pw != null){
            if (!user.isEmpty() && !pw.isEmpty()){
                login(user,pw);
            }
        }
    }

    private void login(final String user, final String pw) {
        if (Common.isConnectedToInternet(getBaseContext())){
            if (user.equals("") || pw.equals("")) {
                Toast.makeText(MainActivity.this, "please, fill in all the information", Toast.LENGTH_SHORT).show();
            } else {
                final ProgressDialog dialog = new ProgressDialog(MainActivity.this);
                dialog.setMessage("Please, wait ...");
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
                RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
                StringRequest loginRequest = new StringRequest(Request.Method.POST, urlLogin, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String message;
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getInt("success") == 1) {
                                dialog.dismiss();
                                User user = new User();
                                user.setName(jsonObject.getString("name"));
                                user.setPhone(jsonObject.getString("phone"));
                                user.setPassword(jsonObject.getString("password"));
                                user.setEmail(jsonObject.getString("email"));
                                Intent homeIntent = new Intent(MainActivity.this, Home.class);
                                Common.currentUser = user;
                                startActivity(homeIntent);
                                finish();
                            } else {
                                dialog.dismiss();
                                message = jsonObject.getString("message");
                                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
                        Log.d("signup", "Error!\n" + error.toString());
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("phone", user);
                        params.put("password", pw);
                        return params;
                    }
                };
                requestQueue.add(loginRequest);
            }
        }else {
            Toast.makeText(MainActivity.this, "No internet, please check the connection", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    private void anhXa() {
        btnDangKi = findViewById(R.id.buttonDangKi);
        btnDangNhap = findViewById(R.id.buttonDangNhap);
        txtSlogan = findViewById(R.id.textViewSlogan);
    }
}

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
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class DangKi extends AppCompatActivity {
    MaterialEditText edtName,edtPhone,edtPassword,edtMail;
    Button btnDangKi;
    TextView txtSignIn, txtSlogan;

    String urlSignup = "https://orderadmin.000webhostapp.com/androidAPI/signup.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dang_ki);
        anhXa();

        Typeface typeface = Typeface.createFromAsset(getAssets(),"fonts/NABILA.TTF");
        txtSlogan.setTypeface(typeface);

        txtSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DangKi.this,DangNhap.class);
                startActivity(intent);
                finish();
            }
        });

        btnDangKi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Common.isConnectedToInternet(getBaseContext())) {
                    String name, password, phone, mail;
                    name = edtName.getText().toString().trim();
                    password = edtPassword.getText().toString();
                    phone = edtPhone.getText().toString().trim();
                    mail = edtMail.getText().toString().trim();
                    if (name.equals("") || password.equals("") || phone.equals("") || mail.equals("")) {
                        Toast.makeText(DangKi.this, "please, fill in all the information", Toast.LENGTH_SHORT).show();
                    } else {
                        if(!edtPassword.isCharactersCountValid()){
                            Toast.makeText(DangKi.this, "password at least 6 characters", Toast.LENGTH_SHORT).show();
                        }else {
                            final ProgressDialog dialog = new ProgressDialog(DangKi.this);
                            dialog.setMessage("please, wait ...");
                            dialog.setCanceledOnTouchOutside(false);
                            dialog.show();
                            final RequestQueue requestQueue = Volley.newRequestQueue(DangKi.this);
                            StringRequest signupRequest = new StringRequest(Request.Method.POST, urlSignup, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    String message;
                                    try {
                                        JSONObject jsonObject = new JSONObject(response);
                                        if (jsonObject.getInt("success") == 1) {
                                            dialog.dismiss();
                                            message = jsonObject.getString("message");
                                            Toast.makeText(DangKi.this, message, Toast.LENGTH_SHORT).show();
                                            finish();
                                        } else {
                                            dialog.dismiss();
                                            message = jsonObject.getString("message");
                                            Toast.makeText(DangKi.this, message, Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(DangKi.this, "Error", Toast.LENGTH_SHORT).show();
                                    Log.d("sisnup", "Error!\n" + error.toString());
                                }
                            }) {
                                @Override
                                protected Map<String, String> getParams() throws AuthFailureError {
                                    Map<String, String> params = new HashMap<>();
                                    params.put("phone", edtPhone.getText().toString().trim());
                                    params.put("name", edtName.getText().toString().trim());
                                    params.put("password", edtPassword.getText().toString());
                                    params.put("mail", edtMail.getText().toString().trim());
                                    return params;
                                }
                            };
                            requestQueue.add(signupRequest);
                        }
                    }
                }else {
                    Toast.makeText(DangKi.this, "No internet, please check the connection", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
    }

    private void anhXa() {
        edtName = findViewById(R.id.editTextName);
        edtPassword = findViewById(R.id.editTextPassword);
        edtPhone = findViewById(R.id.editTextPhone);
        btnDangKi = findViewById(R.id.buttonDangKi);
        txtSlogan = findViewById(R.id.textViewSlogan);
        txtSignIn = findViewById(R.id.txtSignIn);
        edtMail = findViewById(R.id.editTextMail);
    }
}

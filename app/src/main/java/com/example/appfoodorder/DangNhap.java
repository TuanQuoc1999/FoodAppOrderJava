package com.example.appfoodorder;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
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
import com.rengwuxian.materialedittext.MaterialEditText;
import com.rey.material.widget.CheckBox;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.zip.Inflater;

import io.paperdb.Paper;

public class DangNhap extends AppCompatActivity {
    MaterialEditText edtPhone, edtPassword;
    Button btnDangNhap;
    CheckBox checkBox;
    TextView txtSignUp, txtSlogan ,txtForgotPassword;

    String urlLogin = "http://10.0.196.85:8080/androidAPI/login.php";
    String urlForgotPassword = "http://10.0.196.85:8080/androidAPI/forgotpassword.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dang_nhap);
        anhXa();

        Paper.init(this);

        Typeface typeface = Typeface.createFromAsset(getAssets(),"fonts/NABILA.TTF");
        txtSlogan.setTypeface(typeface);

        txtSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DangNhap.this,DangKi.class);
                startActivity(intent);
                finish();
            }
        });

        txtForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Common.isConnectedToInternet(getBaseContext())){
                    showDialogGetPass();
                }else {
                    Toast.makeText(DangNhap.this, "No internet, please check the connection", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });

        btnDangNhap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Common.isConnectedToInternet(getBaseContext())){

                    //save user password
                    if(checkBox.isChecked()) {
                        Paper.book().write(Common.USER_KEY, edtPhone.getText().toString());
                        Paper.book().write(Common.PWD_KEY, edtPassword.getText().toString());
                    }

                    String phone, password;
                phone = edtPhone.getText().toString().trim();
                password = edtPassword.getText().toString();
                if (phone.equals("") || password.equals("")) {
                    Toast.makeText(DangNhap.this, "please, fill in all the information", Toast.LENGTH_SHORT).show();
                } else {
                    if(!edtPassword.isCharactersCountValid()){
                        Toast.makeText(DangNhap.this, "password at least 6 characters", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        final ProgressDialog dialog = new ProgressDialog(DangNhap.this);
                        dialog.setMessage("Please, wait ...");
                        dialog.setCanceledOnTouchOutside(false);
                        dialog.show();
                        RequestQueue requestQueue = Volley.newRequestQueue(DangNhap.this);
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
                                        Intent homeIntent = new Intent(DangNhap.this, Home.class);
                                        Common.currentUser = user;
                                        startActivity(homeIntent);
                                        finish();
                                    } else {
                                        dialog.dismiss();
                                        message = jsonObject.getString("message");
                                        Toast.makeText(DangNhap.this, message, Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(DangNhap.this, "Error", Toast.LENGTH_SHORT).show();
                                Log.d("signup", "Error!\n" + error.toString());
                            }
                        }) {
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                Map<String, String> params = new HashMap<>();
                                params.put("phone", edtPhone.getText().toString().trim());
                                params.put("password", edtPassword.getText().toString());
                                return params;
                            }
                        };
                        requestQueue.add(loginRequest);
                    }
                }
            }else {
                    Toast.makeText(DangNhap.this, "No internet, please check the connection", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
    }

    private void showDialogGetPass() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Forgot Password");
        builder.setMessage("Enter your information");
        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.forgot_password_dialog,null);
        builder.setView(view);
        builder.setIcon(R.drawable.ic_security_black_24dp);
        builder.setCancelable(false);
        final MaterialEditText edtPhone = view.findViewById(R.id.editTextPhone);
        final MaterialEditText edtMail = view.findViewById(R.id.editTextMail);
        final MaterialEditText edtNewPass = view.findViewById(R.id.editTextPassword);
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(edtMail.getText().toString().trim().equals("") || edtNewPass.getText().toString().trim().equals("") || edtPhone.getText().toString().trim().equals("")){
                    Toast.makeText(DangNhap.this, "please, fill in all the information", Toast.LENGTH_SHORT).show();
                }else {
                    RequestQueue requestQueue = Volley.newRequestQueue(DangNhap.this);
                    StringRequest forgotRequest = new StringRequest(Request.Method.POST, urlForgotPassword, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            String message;
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                message = jsonObject.getString("message");
                                Toast.makeText(DangNhap.this, message, Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(DangNhap.this, "Error", Toast.LENGTH_SHORT).show();
                            Log.d("forgot","Error\n"+error.toString());
                        }
                    }){
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String,String> params = new HashMap<>();
                            params.put("phone",edtPhone.getText().toString().trim());
                            params.put("email",edtMail.getText().toString().trim());
                            params.put("password",edtNewPass.getText().toString().trim());
                            return params;
                        }
                    };
                    requestQueue.add(forgotRequest);
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }

    private void anhXa() {
        edtPhone = findViewById(R.id.editTextPhone);
        edtPassword = findViewById(R.id.editTextPassword);
        btnDangNhap = findViewById(R.id.buttonDangNhap);
        checkBox = findViewById(R.id.checkedRemember);
        txtSignUp = findViewById(R.id.txtSignUp);
        txtSlogan = findViewById(R.id.textViewSlogan);
        txtForgotPassword = findViewById(R.id.txtForgotPassword);
    }
}
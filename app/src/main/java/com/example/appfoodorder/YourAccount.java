package com.example.appfoodorder;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
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

import io.paperdb.Paper;

public class YourAccount extends AppCompatActivity {
    TextView txtName, txtPhone, txtEmail, txtSlogan;
    ImageButton btnBack;
    FoodList foodList;
    Home home;
    Button btnChangePassword, btnUpdateInfo;
    String urlChangePass = "https://orderadmin.000webhostapp.com/androidAPI/changepass.php";
    String urlUpdateInfo = "https://orderadmin.000webhostapp.com/androidAPI/updateinfo.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_account);
        anhXa();
        Paper.init(this);
        Typeface typeface = Typeface.createFromAsset(getAssets(),"fonts/NABILA.TTF");
        txtSlogan.setTypeface(typeface);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        txtEmail.setText(Common.currentUser.getEmail());
        txtPhone.setText(Common.currentUser.getPhone());
        txtName.setText(Common.currentUser.getName());
        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Common.isConnectedToInternet(getBaseContext())){
                    showDialogChangePass();
                }else {
                    Toast.makeText(YourAccount.this, "No internet, please check the connection", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
        btnUpdateInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Common.isConnectedToInternet(getBaseContext())){
                    showDialogUpdateInfo();
                }else {
                    Toast.makeText(YourAccount.this, "No internet, please check the connection", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
    }

    private void showDialogUpdateInfo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Update Information");
        builder.setMessage("Enter your information");
        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.update_info_layout,null);
        builder.setView(view);
        builder.setIcon(R.drawable.ic_account_circle_black_24dp);
        builder.setCancelable(false);
        final MaterialEditText edtName = view.findViewById(R.id.editTextName);
        final MaterialEditText edtMail = view.findViewById(R.id.editTextMail);
        final MaterialEditText edtPhone = view.findViewById(R.id.editTextPhone);
        final MaterialEditText edtPass = view.findViewById(R.id.editTextPassword);
        edtMail.setText(Common.currentUser.getEmail());
        edtName.setText(Common.currentUser.getName());
        edtPhone.setText(Common.currentUser.getPhone());
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(!edtMail.getText().toString().trim().isEmpty()&&!edtName.getText().toString().trim().isEmpty()&&!edtPhone.getText().toString().trim().isEmpty()&&!edtPass.getText().toString().isEmpty()){
                    if(edtPass.isCharactersCountValid()){
                        if(Common.currentUser.getPassword().equals(edtPass.getText().toString())){
                            RequestQueue requestQueue = Volley.newRequestQueue(YourAccount.this);
                            StringRequest stringRequest = new StringRequest(Request.Method.POST, urlUpdateInfo, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        JSONObject jsonObject = new JSONObject(response);
                                        Toast.makeText(YourAccount.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                        if(jsonObject.getInt("success") == 1) {
                                            Common.currentUser.setName(edtName.getText().toString().trim());
                                            Common.currentUser.setEmail(edtMail.getText().toString().trim());
                                            Common.currentUser.setPhone(edtPhone.getText().toString().trim());
                                            txtPhone.setText(Common.currentUser.getPhone());
                                            txtName.setText(Common.currentUser.getName());
                                            txtEmail.setText(Common.currentUser.getEmail());
                                            Paper.book().destroy();
                                        }
                                        if(jsonObject.getInt("success") == 2){
                                            Common.currentUser.setName(edtName.getText().toString().trim());
                                            Common.currentUser.setEmail(edtMail.getText().toString().trim());
                                            txtName.setText(Common.currentUser.getName());
                                            txtEmail.setText(Common.currentUser.getEmail());
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(YourAccount.this, "Error", Toast.LENGTH_SHORT).show();
                                    Log.d("updateinfo","Error\n"+error.toString());
                                }
                            }){
                                @Override
                                protected Map<String, String> getParams() throws AuthFailureError {
                                    Map<String,String> params = new HashMap<>();
                                    params.put("phone",edtPhone.getText().toString().trim());
                                    params.put("name",edtName.getText().toString().trim());
                                    params.put("mail",edtMail.getText().toString().trim());
                                    params.put("currentphone",Common.currentUser.getPhone());
                                    return params;
                                }
                            };
                            requestQueue.add(stringRequest);
                        }else {
                            Toast.makeText(YourAccount.this, "wrong password", Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        Toast.makeText(YourAccount.this, "password at least 6 character", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(YourAccount.this, "please, fill all the information", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.show();
    }

    private void showDialogChangePass() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Change Password");
        builder.setMessage("Enter your password");
        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.change_password_dialog,null);
        builder.setView(view);
        builder.setIcon(R.drawable.ic_security_black_24dp);
        builder.setCancelable(false);
        final MaterialEditText edtCurrentPass = view.findViewById(R.id.editCurrentPass);
        final MaterialEditText edtNewPass = view.findViewById(R.id.editNewPass);
        final MaterialEditText edtConfirmPass = view.findViewById(R.id.editConfirmPass);
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(edtCurrentPass.isCharactersCountValid() && edtNewPass.isCharactersCountValid() && edtConfirmPass.isCharactersCountValid()){
                    if(edtConfirmPass.getText().toString().equals(edtNewPass.getText().toString())){
                        RequestQueue requestQueue = Volley.newRequestQueue(YourAccount.this);
                        StringRequest stringRequest = new StringRequest(Request.Method.POST, urlChangePass, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    Toast.makeText(YourAccount.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                    if(jsonObject.getInt("success") == 1){
                                        Common.currentUser.setPassword(edtNewPass.getText().toString());
                                        Paper.book().destroy();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(YourAccount.this, "Error", Toast.LENGTH_SHORT).show();
                                Log.d("changepass","Error\n"+error.toString());
                            }
                        }){
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                Map<String,String> params = new HashMap<>();
                                params.put("phone",Common.currentUser.getPhone());
                                params.put("newpass",edtNewPass.getText().toString());
                                params.put("password",edtCurrentPass.getText().toString());
                                return params;
                            }
                        };
                        requestQueue.add(stringRequest);
                    }else {
                        Toast.makeText(YourAccount.this, "Wrong confirm password", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(YourAccount.this, "Password at least 6 character", Toast.LENGTH_SHORT).show();
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
        btnChangePassword = findViewById(R.id.btnChangePass);
        btnUpdateInfo = findViewById(R.id.btnUpdateInfo);
        txtEmail = findViewById(R.id.txtMail);
        txtName = findViewById(R.id.txtName);
        txtPhone = findViewById(R.id.txtPhone);
        txtSlogan = findViewById(R.id.txtSlogan);
        btnBack = findViewById(R.id.btnBack);
    }
}

package com.example.appfoodorder;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.appfoodorder.Adapter.MenuAdapter;
import com.example.appfoodorder.Common.Common;
import com.example.appfoodorder.Model.Category;
import com.example.appfoodorder.database.Database;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;

public class Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    List<Category> categoryList = new ArrayList<>();
    public TextView txtName;
    RecyclerView recyclerView_menu;
    RecyclerView.LayoutManager layoutManager;
    MenuAdapter adapter = new MenuAdapter(categoryList,this);
    ProgressDialog dialog;
    DrawerLayout drawer;
    NavigationView navigationView;
    ViewFlipper viewFlipper;
    String urlGetCategory = "https://orderadmin.000webhostapp.com/androidAPI/getcategory.php";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Menu");
        setSupportActionBar(toolbar);

        //màn hình chờ
        dialog = new ProgressDialog(Home.this);
        dialog.setMessage("Vui lòng chờ ...");
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        Paper.init(this);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cartIntent = new Intent(Home.this,Cart.class);
                startActivity(cartIntent);
            }
        });
        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawer,toolbar,R.string.navigation_drawer_open
        ,R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Set name user
        setUserName(Common.currentUser.getName());


        //quảng cáo
        viewFlipper = findViewById(R.id.viewFlipperAd);
        Animation in = AnimationUtils.loadAnimation(this,R.anim.fade_in);
        Animation out = AnimationUtils.loadAnimation(this,R.anim.fade_out);
        viewFlipper.setInAnimation(in);
        viewFlipper.setOutAnimation(out);
        viewFlipper.setFlipInterval(5000);
        viewFlipper.setAutoStart(true);
        viewFlipper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(viewFlipper.isAutoStart()){
                    viewFlipper.stopFlipping();
                    viewFlipper.showNext();
                    viewFlipper.startFlipping();
                    viewFlipper.setAutoStart(true);
                }
            }
        });

        //load menu
        recyclerView_menu = findViewById(R.id.recycler_menu);
        recyclerView_menu.setHasFixedSize(true);
        //layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        layoutManager = new GridLayoutManager(this,2);
        recyclerView_menu.setLayoutManager(layoutManager);

        if (Common.isConnectedToInternet(getBaseContext()))
            loadMenu();
        else {
            Toast.makeText(this, "Không có kết nối internet, vui lòng kiểm tra lại kết nối", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    private void setUserName(String name) {
        View headerView = navigationView.getHeaderView(0);
        txtName = headerView.findViewById(R.id.textViewName);
        txtName.setText(name);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }else {
            super.onBackPressed();
        }
    }

    private void loadMenu() {
        RequestQueue requestQueue = Volley.newRequestQueue(Home.this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, urlGetCategory, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i=0; i<response.length(); i++){
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        int ID = jsonObject.getInt("id");
                        String Name =jsonObject.getString("name");
                        String Image =jsonObject.getString("image");

                        categoryList.add(new Category(ID,Name,Image));

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
                Toast.makeText(Home.this, "lỗi", Toast.LENGTH_SHORT).show();
                Log.d("loadmenuCategory", "Lỗi!\n"+error.toString());
            }
        });
        requestQueue.add(jsonArrayRequest);
        recyclerView_menu.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.;
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.nav_cart:{
                Database database = new Database(getBaseContext());
                database.createTable();
                Intent cartIntent = new Intent(Home.this,Cart.class);
                startActivity(cartIntent);
                break;
            }
            case R.id.nav_menu:{
                break;
            }
            case R.id.nav_orders:{
                Intent orderIntent = new Intent(Home.this,OrderHistory.class);
                startActivity(orderIntent);
                break;
            }
            case R.id.nav_account:{
                Intent accountIntent = new Intent(Home.this, YourAccount.class);
                startActivity(accountIntent);
                break;
            }
            case R.id.nav_about:{
                Intent aboutIntent = new Intent(Home.this,AboutUs.class);
                startActivity(aboutIntent);
                break;
            }
            case R.id.nav_sign_out:{
                Paper.book().destroy();
                Intent logout = new Intent(Home.this,DangNhap.class);
                logout.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(logout);
                new Database(getBaseContext()).cleanCart();
                break;
            }

        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUserName(Common.currentUser.getName());
    }
}

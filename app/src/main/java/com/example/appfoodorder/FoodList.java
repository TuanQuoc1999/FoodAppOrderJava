package com.example.appfoodorder;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.appfoodorder.Adapter.FoodAdapter;
import com.example.appfoodorder.Common.Common;
import com.example.appfoodorder.Model.Food;
import com.example.appfoodorder.database.Database;
import com.google.android.material.navigation.NavigationView;
import com.mancj.materialsearchbar.MaterialSearchBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;

public class FoodList extends AppCompatActivity {
    List<Food> foodList = new ArrayList<>();
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    FoodAdapter adapter = new FoodAdapter(foodList,this);
    int categoryId;
    ProgressDialog dialog;
    Toolbar toolbar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    public TextView txtName;
    String urlGetFood = "https://orderadmin.000webhostapp.com/androidAPI/getfood.php";

    //search bar
    List<Food> foodSearch = new ArrayList<>();
    FoodAdapter searchAdapter = new FoodAdapter(foodSearch,this);
    List<String> suggestList = new ArrayList<>();
    MaterialSearchBar materialSearchBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);


        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        //màn hình chờ
        dialog = new ProgressDialog(FoodList.this);
        dialog.setMessage("Please, wait ...");
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        Paper.init(this);

        drawerLayout = findViewById(R.id.drawer_layout);
        toolbar.setNavigationIcon(R.drawable.ic_menu_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.nav_cart:{
                        Database database = new Database(getBaseContext());
                        database.createTable();
                        Intent cartIntent = new Intent(FoodList.this,Cart.class);
                        startActivity(cartIntent);
                        break;
                    }
                    case R.id.nav_menu:{
                        finish();
                        break;
                    }
                    case R.id.nav_orders:{
                        Intent orderIntent = new Intent(FoodList.this,OrderHistory.class);
                        startActivity(orderIntent);
                        break;
                    }
                    case R.id.nav_account:{
                        Intent accountIntent = new Intent(FoodList.this, YourAccount.class);
                        startActivity(accountIntent);
                        finish();
                        break;
                    }
                    case R.id.nav_about:{
                        Intent aboutIntent = new Intent(FoodList.this,AboutUs.class);
                        startActivity(aboutIntent);
                        break;
                    }
                    case R.id.nav_sign_out:{
                        Paper.book().destroy();
                        Intent logout = new Intent(FoodList.this,DangNhap.class);
                        logout.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(logout);
                        new Database(getBaseContext()).cleanCart();
                        break;
                    }

                }

                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        //Set name user
        View headerView = navigationView.getHeaderView(0);
        txtName = headerView.findViewById(R.id.textViewName);
        txtName.setText(Common.currentUser.getName());



        recyclerView = findViewById(R.id.recycler_food);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //get intent
        if(getIntent()!=null){
            categoryId = getIntent().getIntExtra("CategoryId",-1);
        }
        if(categoryId != -1){
            if (Common.isConnectedToInternet(getBaseContext()))
                loadListFood(categoryId);
            else {
                Toast.makeText(this, "No internet, please check the connection", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        //search
        materialSearchBar = findViewById(R.id.searchBar);
        loadSuggest();
        materialSearchBar.setLastSuggestions(suggestList);
        materialSearchBar.setElevation(10);
        materialSearchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //when type text, change suggest list
                List<String> suggest = new ArrayList<>();
                for(String search:suggestList){
                    if(search.toLowerCase().contains(materialSearchBar.getText().toLowerCase()))
                        suggest.add(search);
                }
                materialSearchBar.setLastSuggestions(suggest);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                //when search bar is closed restore original adapter
                if(!enabled)
                    recyclerView.setAdapter(adapter);
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                //when search finish, show the result
                startSearch(text);
            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });

    }

    private void startSearch(final CharSequence text) {
        foodSearch.clear();
        RequestQueue requestQueue = Volley.newRequestQueue(FoodList.this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, urlGetFood, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i=0; i<response.length(); i++){
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        int Id = jsonObject.getInt("id");
                        String Name =jsonObject.getString("name");
                        String Image =jsonObject.getString("image");
                        String Description = jsonObject.getString("description");
                        int Price = jsonObject.getInt("price");
                        int MenuID = jsonObject.getInt("cateID");

                        if(Name.equals(text.toString())){
                            foodSearch.add(new Food(Id,Name,Description,Image,MenuID,Price));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    searchAdapter.notifyDataSetChanged();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(FoodList.this, "lỗi", Toast.LENGTH_SHORT).show();
                Log.d("loadFood", "Lỗi!\n"+error.toString());
            }
        });
        requestQueue.add(jsonArrayRequest);
        recyclerView.setAdapter(searchAdapter);
    }

    private void loadSuggest() {
        RequestQueue requestQueue = Volley.newRequestQueue(FoodList.this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, urlGetFood, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i=0; i<response.length(); i++){
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        String Name =jsonObject.getString("name");
                        int MenuID = jsonObject.getInt("cateID");

                        if(MenuID == categoryId){
                            suggestList.add(Name);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(FoodList.this, "Error", Toast.LENGTH_SHORT).show();
                Log.d("loadsuggestlist", "Error!\n"+error.toString());
            }
        });
        requestQueue.add(jsonArrayRequest);
    }

    private void loadListFood(final int categoryId) {
        RequestQueue requestQueue = Volley.newRequestQueue(FoodList.this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, urlGetFood, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i=0; i<response.length(); i++){
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        int Id = jsonObject.getInt("id");
                        String Name =jsonObject.getString("name");
                        String Image =jsonObject.getString("image");
                        String Description = jsonObject.getString("description");
                        int Price = jsonObject.getInt("price");
                        int MenuID = jsonObject.getInt("cateID");

                        if(MenuID == categoryId){
                            foodList.add(new Food(Id,Name,Description,Image,MenuID,Price));
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
                Toast.makeText(FoodList.this, "Error", Toast.LENGTH_SHORT).show();
                Log.d("loadmenuFood", "Error!\n"+error.toString());
            }
        });
        requestQueue.add(jsonArrayRequest);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        View headerView = navigationView.getHeaderView(0);
        txtName = headerView.findViewById(R.id.textViewName);
        txtName.setText(Common.currentUser.getName());
    }
}

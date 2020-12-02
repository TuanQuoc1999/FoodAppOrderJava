package com.example.appfoodorder.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;

import com.example.appfoodorder.Model.Order;

import java.util.ArrayList;
import java.util.List;

public class Database extends SQLiteOpenHelper {
    private static final String DB_NAME = "FoodOrderDB";
    private static final int DB_VER = 1;
    public Database(Context context) {
        super(context, DB_NAME, null, DB_VER);
    }

    public void createTable(){
        String query = "create table if not exists OrderDetail(ID integer primary key autoincrement, ProductId nvarchar(200), ProductName nvarchar(200), Quantity nvarchar(200), Price nvarchar(200))";
        SQLiteDatabase database = getWritableDatabase();
        database.execSQL(query);
    }

    public List<Order> getCart(){
        SQLiteDatabase db = getWritableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        String[] sqlSelect ={"ID","ProductName", "ProductId", "Quantity", "Price",};
        String sqlTable = "OrderDetail";

        qb.setTables(sqlTable);
        Cursor c = qb.query(db,sqlSelect,null,null,null,null,null);

        final List<Order> result = new ArrayList<>();
        if(c.moveToFirst()){
            do {
                result.add(new Order(
                        c.getInt(c.getColumnIndex("ID")),
                        c.getInt(c.getColumnIndex("ProductId")),
                        c.getString(c.getColumnIndex("ProductName")),
                        c.getInt(c.getColumnIndex("Quantity")),
                        c.getInt(c.getColumnIndex("Price"))));
            } while (c.moveToNext());
        }
        return result;
    }

    public void addToCart(Order order){
        SQLiteDatabase db = getWritableDatabase();
        String query = String.format("INSERT INTO OrderDetail(ProductId,ProductName,Quantity,Price) VALUES('%s','%s','%s','%s');",
                String.valueOf(order.getProductId()),order.getProductName(),String.valueOf(order.getQuantity()),String.valueOf(order.getPrice()));
        db.execSQL(query);
    }

    public void cleanCart(){
        SQLiteDatabase db = getWritableDatabase();
        String query = String.format("DELETE FROM OrderDetail");
        db.execSQL(query);
    }

    public void deleteItem(int id){
        SQLiteDatabase db = getWritableDatabase();
        String query = String.format("DELETE FROM OrderDetail WHERE ID = '%s';", id);
        db.execSQL(query);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void updateCart(Order order) {
        SQLiteDatabase db = getWritableDatabase();
        String query = String.format("UPDATE OrderDetail SET Quantity = %s WHERE ID = %d",order.getQuantity(),order.getID());
        db.execSQL(query);
    }
}

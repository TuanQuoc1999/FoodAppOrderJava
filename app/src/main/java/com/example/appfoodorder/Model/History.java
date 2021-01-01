package com.example.appfoodorder.Model;

public class History {
    private String IDcart;
    private String Time;
    private String Status;
    private int Total;
    private String Address;

    public History(String IDcart, String time, String status, int total, String address) {
        this.IDcart = IDcart;
        Time = time;
        Status = status;
        Total = total;
        Address = address;
    }

    public History() {
    }

    public String getIDcart() {
        return IDcart;
    }

    public void setIDcart(String IDcart) {
        this.IDcart = IDcart;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public int getTotal() {
        return Total;
    }

    public void setTotal(int total) {
        Total = total;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }
}

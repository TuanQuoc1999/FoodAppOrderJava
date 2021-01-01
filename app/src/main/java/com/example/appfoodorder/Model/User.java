package com.example.appfoodorder.Model;

public class User {
    private String Phone;
    private String Name;
    private String Email;
    private String Password;

    public User() {
    }

    public User(String phone, String name, String email, String password) {
        Phone = phone;
        Name = name;
        Email = email;
        Password = password;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }
}

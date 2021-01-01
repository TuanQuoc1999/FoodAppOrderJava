package com.example.appfoodorder.Model;

public class Food {
    private String Name,Description,Image;
    private int Id, MenuId,Price;

    public Food() {
    }

    public Food(int id, String name, String description, String image, int menuId, int price) {
        Id = id;
        Name = name;
        Description = description;
        Image = image;
        MenuId = menuId;
        Price = price;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public int getMenuId() {
        return MenuId;
    }

    public void setMenuId(int menuId) {
        MenuId = menuId;
    }

    public int getPrice() {
        return Price;
    }

    public void setPrice(int price) {
        Price = price;
    }
}

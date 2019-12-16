package com.example.newweek3;

public class Camp {
    // instance variables
    private int var_id;
    private String var_state;
    private String var_name;
    private int var_fav;

    private String var_price;
    private String var_address;
    private int var_rating;
    // empty constructor
    public Camp() { }
    // constructor with all three variables
    public Camp(int id, String state, String name, int fav,String price,String address,int rating) {
        this.var_id = id;
        this.var_state = state;
        this.var_name = name;
        this.var_fav = fav;
        this.var_price = price;
        this.var_address = address;
        this.var_rating = rating;

    }
    // constructor without id
    public Camp(String state, String name, int fav, String price,String address,int rating)  {
        this.var_state = state;
        this.var_name = name;
        this.var_fav = fav;
        this.var_price = price;
        this.var_address = address;
        this.var_rating = rating;
    }
    // setters (mutators)
    public void setID(int id) { this.var_id = id; }
    public void setName(String name) { this.var_name = name; }
    public void setState(String state) { this.var_state = state; }
    public void setFav(int fav) { this.var_fav = fav; }
    public void setPrice(String price) { this.var_price = price; }
    public void setAddress(String address) { this.var_address = address; }
    public void setRating(int rating) { this.var_rating = rating; }
    // getters (accessors)
    public int getID() { return this.var_id; }
    public String getName() { return this.var_name; }
    public String getState() { return this.var_state; }
    public int getFav() { return this.var_fav; }
    public String getPrice() { return this.var_price; }
    public String getAddress() { return this.var_address; }
    public int getRating() { return this.var_rating; }
}

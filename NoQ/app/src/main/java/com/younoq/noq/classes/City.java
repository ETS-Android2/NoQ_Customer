package com.younoq.noq.classes;

/**
 * Created by Harsh Chaurasia(Phantom Boy).
 */

public class City {

    public String city_name, exists;

    public City(String cname, String exists){
        this.city_name = cname;
        this.exists = exists;
    }

    public String getCity_name() { return city_name; }

    public String getExists() { return exists; }
}

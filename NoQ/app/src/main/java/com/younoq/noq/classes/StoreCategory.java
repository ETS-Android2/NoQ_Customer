package com.younoq.noq.classes;

import java.util.List;

public class StoreCategory {

    String category_name, image_name;
    boolean availability;
    List<String> storeList;

    public StoreCategory(String category_name, String image_name, boolean availability, List<String> sList){
        this.category_name = category_name;
        this.image_name = image_name;
        this.availability = availability;
        this.storeList = sList;
    }

    public String getCategory_name() { return category_name; }

    public String getImage_name() { return image_name; }

    public boolean isAvailable() { return availability; }

    public List<String> getStoreList() { return storeList; }
}

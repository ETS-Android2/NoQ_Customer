package com.younoq.noq.networkhandler;

import com.younoq.noq.classes.ProductSearchResult;
import com.younoq.noq.classes.ProductSearchResultLight;

import org.json.JSONArray;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface NetworkApi {

    String API_URL = "http://ec2-13-234-120-100.ap-south-1.compute.amazonaws.com";


    @Multipart
    @POST("/DB/search_products_by_store.php")
    Call<ProductSearchResult> getSearchResults(@Part("search_str") RequestBody searchableItem, @Part("store_id") RequestBody store_id);

    @Multipart
    @POST("/DB/search_products_by_store_light.php")
    Call<ProductSearchResultLight> getSearchResultsLight(@Part("search_str") RequestBody searchableItem, @Part("store_id") RequestBody store_id);

    @Multipart
    @POST("/DB/search_products_by_store_and_category.php")
    Call<ProductSearchResult> getSearchResultsByCategory(@Part("search_str") RequestBody searchableItem, @Part("store_id") RequestBody store_id,
                                                         @Part("category_name") RequestBody category);

    @Multipart
    @POST("DB/retrieve_categories_new.php")
    Call<JSONArray> getCategoriesFromStore(@Part("store_id") RequestBody searchableItem);


    @Multipart
    @POST("DB/retrieve_products_list.php")
    Call<JSONArray> getProductsList(@Part("store_id") String storeId,@Part("category_name") RequestBody searchableItem);

}

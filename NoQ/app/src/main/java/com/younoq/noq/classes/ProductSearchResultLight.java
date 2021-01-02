package com.younoq.noq.classes;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ProductSearchResultLight {

    @SerializedName("response")
    private ResponseResult responseResult;
    @SerializedName("products")
    private List<List<String>> productListLight;

    public ProductSearchResultLight(ResponseResult responseResult, List<List<String>> productList) {
        this.responseResult = responseResult;
        this.productListLight = productList;
    }

    public ResponseResult getResponseResult() {
        return responseResult;
    }

    public void setResponseResult(ResponseResult responseResult) {
        this.responseResult = responseResult;
    }

    public List<List<String>> getProductList() {
        return productListLight;
    }

    public void setProductList(List<List<String>> productList) {
        this.productListLight = productList;
    }

}

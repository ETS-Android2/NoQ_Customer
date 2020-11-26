package com.younoq.noq.classes;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ProductSearchResult {

    @SerializedName("response")
    private ResponseResult responseResult;
    @SerializedName("products")
    private List<List<String>> productList;

    public ProductSearchResult(ResponseResult responseResult, List<List<String>> productList) {
        this.responseResult = responseResult;
        this.productList = productList;
    }

    public ResponseResult getResponseResult() {
        return responseResult;
    }

    public void setResponseResult(ResponseResult responseResult) {
        this.responseResult = responseResult;
    }

    public List<List<String>> getProductList() {
        return productList;
    }

    public void setProductList(List<List<String>> productList) {
        this.productList = productList;
    }

}

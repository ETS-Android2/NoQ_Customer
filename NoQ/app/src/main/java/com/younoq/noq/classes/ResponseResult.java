package com.younoq.noq.classes;

import com.google.gson.annotations.SerializedName;

public class ResponseResult {

    @SerializedName("responseCode")
    private String responseCode;

    ResponseResult(String responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

}

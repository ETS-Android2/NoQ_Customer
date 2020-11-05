package com.younoq.noq.models;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by Harsh Chaurasia(Phantom Boy).
 */

public interface Api {

    String BASE_URL = "http://ec2-13-234-120-100.ap-south-1.compute.amazonaws.com/DB/Paytm/";

    @FormUrlEncoded
    @POST("generateChecksum.php")
    Call<PChecksum> getChecksum(
            @Field("MID") String mId,
            @Field("ORDER_ID") String orderId,
            @Field("CUST_ID") String custId,
            @Field("CHANNEL_ID") String channelId,
            @Field("TXN_AMOUNT") String txnAmount,
            @Field("WEBSITE") String website,
            @Field("CALLBACK_URL") String callbackUrl,
            @Field("INDUSTRY_TYPE_ID") String industryTypeId
    );

}

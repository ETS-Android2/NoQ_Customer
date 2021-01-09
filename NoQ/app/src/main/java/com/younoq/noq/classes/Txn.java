package com.younoq.noq.classes;

/**
 * Created by Harsh Chaurasia(Phantom Boy).
 */

import org.json.JSONArray;

public class Txn {

    private String receipt_no, payment_mode, total_savings, timestamp, total_items, final_amt,
            store_name, store_addr, store_city, store_state, order_type, refund_initiated;
    private int delivery_duration;
    private JSONArray products;

    public Txn(String receipt_no, String payment_mode, String total_savings, String timestamp, String total_items, String final_amt, String store_addr, String store_name,
               String store_city, String store_state, String order_type, String ref_init, int delivery_duration, JSONArray products){

        this.receipt_no = receipt_no;
        this.payment_mode = payment_mode;
        this.total_savings = total_savings;
        this.timestamp = timestamp;
        this.total_items = total_items;
        this.final_amt = final_amt;
        this.store_addr = store_addr;
        this.store_name = store_name;
        this.store_city = store_city;
        this.store_state = store_state;
        this.order_type = order_type;
        this.refund_initiated = ref_init;
        this.delivery_duration = delivery_duration;
        this.products = products;

    }

    public String getReceipt_no() { return receipt_no; }

    public String getPayment_mode() { return payment_mode; }

    public String getTotal_savings() { return total_savings; }

    public String getTimestamp() { return timestamp; }

    public String getTotal_items() { return total_items; }

    public String getFinal_amt() { return final_amt; }

    public String getStore_name() { return store_name; }

    public void setStore_name(String store_name) { this.store_name = store_name; }

    public String getStore_addr() { return store_addr; }

    public String getStore_city() { return store_city; }

    public String getStore_state() { return store_state; }

    public JSONArray getProducts() { return products; }

    public String getOrder_type() { return order_type; }

    public String getRefund_initiated() { return refund_initiated; }

    public int getDelivery_duration() { return delivery_duration; }

    public void setProducts(JSONArray products) { this.products = products; }
}

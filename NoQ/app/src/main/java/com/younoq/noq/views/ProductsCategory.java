package com.younoq.noq.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.younoq.noq.R;
import com.younoq.noq.adapters.CategoryAdapter;
import com.younoq.noq.adapters.SearchSuggestionsAdapter;
import com.younoq.noq.classes.Category;
import com.younoq.noq.classes.ProductSearchResultLight;
import com.younoq.noq.classes.ResponseResult;
import com.younoq.noq.classes.SearchSuggestions;
import com.younoq.noq.models.AwsBackgroundWorker;
import com.younoq.noq.models.DBHelper;
import com.younoq.noq.models.RxSearchObservable;
import com.younoq.noq.models.SaveInfoLocally;
import com.younoq.noq.networkhandler.NetworkHandler;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Harsh Chaurasia(Phantom Boy).
 */

public class ProductsCategory extends AppCompatActivity {

    private SaveInfoLocally saveInfoLocally;
    private TextView tv_total_items_in_cart;
    private ImageView im_go_to_cart;
    final private String TAG = "ProductsCategory";
    private JSONArray jsonArray, jsonArray1;
    private List<Category> categoriesList;
    private RecyclerView recyclerView;
    private CategoryAdapter categoryAdapter;
    private String shoppingMethod, store_id;
    private DBHelper dbHelper;
    private SearchView searchView;
    private LinearLayout ll_go_to_cart;
    private RecyclerView searchSuggestionsRecyclerView;
    private  List<SearchSuggestions> searchSuggestionsList;
    private SearchSuggestionsAdapter searchSuggestionsAdapter;
    private Disposable disposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products_category);

        saveInfoLocally = new SaveInfoLocally(this);
        im_go_to_cart = findViewById(R.id.pc_cart);
        recyclerView = findViewById(R.id.pc_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        dbHelper = new DBHelper(this);
        ll_go_to_cart = findViewById(R.id.apc_go_to_cart);
        tv_total_items_in_cart = findViewById(R.id.pc_total_items_in_cart);

        Intent in = getIntent();
        shoppingMethod = in.getStringExtra("shoppingMethod");

        searchView = findViewById(R.id.search_view);
        searchSuggestionsRecyclerView = findViewById(R.id.search_suggestions);
        searchSuggestionsRecyclerView.setHasFixedSize(true);
        searchSuggestionsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        searchSuggestionsList = new ArrayList<>();
        searchSuggestionsAdapter = new SearchSuggestionsAdapter(getApplicationContext(), searchSuggestionsList, shoppingMethod);
        searchSuggestionsRecyclerView.setAdapter(searchSuggestionsAdapter);

        searchView.setOnSearchClickListener(view -> {
            Log.d(TAG, "onSearchClick called");
            final String queryHint = "Search in all categories";
            searchView.setQueryHint(queryHint);
            searchSuggestionsRecyclerView.setVisibility(View.VISIBLE);
        });

        ll_go_to_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(v.getContext(), CartActivity.class);
                in.putExtra("comingFrom", "ProductCategory");
                in.putExtra("shoppingMethod", shoppingMethod);
                startActivity(in);
            }
        });

        categoriesList = new ArrayList<>();

        retrieve_categories();

    }

    @Override
    protected void onStart() {
        super.onStart();
        observeSearchView();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (disposable != null) {
            disposable.dispose();
        }
    }

    void retrieve_categories() {

        final int total_items_in_cart = saveInfoLocally.getTotalItemsInCart();
        tv_total_items_in_cart.setText(String.valueOf(total_items_in_cart));

        store_id = saveInfoLocally.get_store_id();
        final String type= "retrieve_categories";
        try {
            final String res = new AwsBackgroundWorker(this).execute(type, store_id).get();
            Log.d(TAG, res);

            jsonArray = new JSONArray(res);

            for(int i = 0; i < jsonArray.length(); i++){

                jsonArray1 = jsonArray.getJSONArray(i);
                /* Log.d(TAG, "Item - "+i+" "+jsonArray1.getString(0)); */
                final int times_purchased = Integer.parseInt(jsonArray1.getString(2));
                categoriesList.add(
                        new Category(
                                jsonArray1.getString(0),
                                jsonArray1.getString(1),
                                jsonArray1.getString(3),
                                times_purchased
                        )
                );

            categoryAdapter = new CategoryAdapter(this, categoriesList, shoppingMethod);
            recyclerView.setAdapter(categoryAdapter);

            }


        } catch (ExecutionException | JSONException | InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onBackPressed() {
        if (searchSuggestionsRecyclerView.getVisibility() == View.VISIBLE) {
            searchSuggestionsRecyclerView.setVisibility(View.GONE);
        } else {

            dbHelper = new DBHelper(this);
            Log.d(TAG, "Shopping Method in Category :"+ shoppingMethod);
            new MaterialAlertDialogBuilder(this)
                    .setTitle("Do you want to Exit "+shoppingMethod+" Shopping?")
                    .setMessage(R.string.bs_exit_in_store_msg)
                    .setCancelable(false)
                    .setPositiveButton(R.string.bs_exit_yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dbHelper.Delete_all_rows();
                            /* Retrieving the Store Shopping methods related Info, from SharedPreferences. */
                            final boolean in_store = saveInfoLocally.getIs_InStore();
                            final boolean takeaway = saveInfoLocally.getIs_Takeaway();
                            final boolean home_delivery = saveInfoLocally.getIs_Home_Delivery();
                            /* Resetting the Total Items Present in the Cart. */
                            saveInfoLocally.setTotalItemsInCart(0);

                            Intent in = new Intent(ProductsCategory.this, ChooseShopType.class);
                            in.putExtra("in_store", in_store);
                            in.putExtra("takeaway", takeaway);
                            in.putExtra("home_delivery", home_delivery);
                            in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(in);
                        }
                    })
                    .setNegativeButton(R.string.bs_exit_no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            /* Toast.makeText(BarcodeScannerActivity.this, "Don't Exit", Toast.LENGTH_SHORT).show(); */
                        }
                    })
                    .show();

        }
    }

    private void doSearch(String s) {
        RequestBody body = RequestBody.create(s, MediaType.parse("text/plain"));
        RequestBody storeId = RequestBody.create(store_id, MediaType.parse("text/plain"));
        Call<ProductSearchResultLight> call =  NetworkHandler.getNetworkHandler(getApplicationContext()).getNetworkApi().getSearchResultsLight(body, storeId);

        call.enqueue(new Callback<ProductSearchResultLight>() {

            @Override
            public void onResponse(Call<ProductSearchResultLight> call, Response<ProductSearchResultLight> response) {
                Log.d(TAG, "onResponse: call" + response.isSuccessful());

                ResponseResult result = response.body().getResponseResult();
                List<List<String>> searchLightResults = response.body().getProductList();
                if (searchLightResults != null) {
                    ListIterator<List<String>> iter = searchLightResults.listIterator();
                    if (result.getResponseCode().equalsIgnoreCase("200")) {
                        searchSuggestionsList.clear();
                        while (iter.hasNext()) {
                            searchSuggestionsList.add(
                                    new SearchSuggestions(iter.next().get(0)
                                    )
                            );
                            searchSuggestionsAdapter.notifyDataSetChanged();
                        }
                    }
                }

            }

            @Override
            public void onFailure(Call<ProductSearchResultLight> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t );

                t.printStackTrace();

                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void observeSearchView() {

        disposable = RxSearchObservable.fromView(searchView)
                .debounce(300, TimeUnit.MILLISECONDS)
                .filter(text -> !text.isEmpty() && text.length() >= 3)
                .map(text -> text.toLowerCase().trim())
                .distinctUntilChanged()
                .switchMap(s -> Observable.just(s))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {
                    Log.d(TAG, "Searching for "+s);
                    doSearch(s);
                });
    }

/*    public void Go_to_Basket(View view) {

        Intent in = new Intent(this, CartActivity.class);
        in.putExtra("comingFrom", "ProductCategory");
        in.putExtra("shoppingMethod", shoppingMethod);
        startActivity(in);

    } */

}

package com.younoq.noq.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.younoq.noq.R;
import com.younoq.noq.adapters.BottomSheetCategoryAdapter;
import com.younoq.noq.adapters.ProductListAdapter;
import com.younoq.noq.classes.Category;
import com.younoq.noq.classes.Product;
import com.younoq.noq.classes.ProductSearchResult;
import com.younoq.noq.classes.ResponseResult;
import com.younoq.noq.models.AwsBackgroundWorker;
import com.younoq.noq.models.RxSearchObservable;
import com.younoq.noq.models.SaveInfoLocally;
import com.younoq.noq.networkhandler.NetworkHandler;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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

public class ProductsSearchResults extends AppCompatActivity {
    private String store_id, search_string, shoppingMethod, coming_from, store_name;
    private BottomSheetCategoryAdapter categoryAdapter;
    private RecyclerView recyclerView, bs_recyclerview;
    private List<Category> categoriesList;
    private CoordinatorLayout coordinatorLayout;
    private ProductListAdapter productListAdapter;
    private BottomSheetBehavior sheetBehavior;
    private final String TAG ="ProductList";
    private LinearLayout layout_bottomsheet;
    private JSONArray jsonArray, jsonArray1;
    private SaveInfoLocally saveInfoLocally;
    private List<Product> productList;
    private TextView tv_store_name;
    private Button btn_categories;
    private SearchView searchView;
    private Disposable disposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products_search_results);

        recyclerView = findViewById(R.id.cpsr_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        btn_categories = findViewById(R.id.asc_btn_categories);
        searchView = findViewById(R.id.cpsr_search_view);

        coordinatorLayout = findViewById(R.id.cpsr_coordinator_layout);
        saveInfoLocally = new SaveInfoLocally(this);
        tv_store_name = findViewById(R.id.cpsr_store_name);
        categoriesList= new ArrayList<>();
        productList = new ArrayList<>();

        bs_recyclerview = findViewById(R.id.bd_bottomsheet_recyclerview);
        bs_recyclerview.setHasFixedSize(true);
        bs_recyclerview.setLayoutManager(new GridLayoutManager(this, 3));
        layout_bottomsheet = findViewById(R.id.bd_bottomSheet);
        sheetBehavior = BottomSheetBehavior.from(layout_bottomsheet);

        Intent in= getIntent();
        search_string = in.getStringExtra("search_string");
        shoppingMethod = in.getStringExtra("shoppingMethod");
        coming_from = in.getStringExtra("coming_from");
        searchView.setQuery(search_string, false);
        searchView.setFocusable(true);

        retrieve_categories();

        sheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED: {
                        Log.d(TAG, "BottomSheet Expanded");
                    }
                    break;
                    case BottomSheetBehavior.STATE_COLLAPSED: {
                        Log.d(TAG, "BottomSheet Collapsed");
                    }
                    break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        btn_categories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                    sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });

        store_id = saveInfoLocally.get_store_id();
        store_name = saveInfoLocally.getStoreName() +", "+ saveInfoLocally.getStoreAddress();
        tv_store_name.setText(store_name);

        productListAdapter = new ProductListAdapter(getApplicationContext(), productList, shoppingMethod, coordinatorLayout);
        recyclerView.setAdapter(productListAdapter);
        retrieve_search_results(search_string);

    }

    @Override
    protected void onStart() {
        super.onStart();
        observeSearchView();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (disposable != null){
            disposable.dispose();
        }
    }

    void retrieve_categories() {

        store_id = saveInfoLocally.get_store_id();
        final String type= "retrieve_categories";
        try {
            final String res = new AwsBackgroundWorker(this).execute(type, store_id).get();
            Log.d(TAG, "Result in ProductList"+res);

            jsonArray = new JSONArray(res);

            for(int i = 0; i < jsonArray.length(); i++){

                jsonArray1 = jsonArray.getJSONArray(i);
//                Log.d(TAG, "Item - "+i+" "+jsonArray1.getString(0));
                final int times_purchased = Integer.parseInt(jsonArray1.getString(2));
                categoriesList.add(
                        new Category(
                                jsonArray1.getString(0),
                                jsonArray1.getString(1),
                                jsonArray1.getString(3),
                                times_purchased
                        )
                );

                categoryAdapter = new BottomSheetCategoryAdapter(this, categoriesList, shoppingMethod);
                bs_recyclerview.setAdapter(categoryAdapter);

            }


        } catch (ExecutionException | JSONException | InterruptedException e) {
            e.printStackTrace();
        }

    }

    private void retrieve_search_results(String queryString) {
        search_string = queryString;
        RequestBody body = RequestBody.create(queryString, MediaType.parse("text/plain"));
        RequestBody storeId = RequestBody.create(store_id, MediaType.parse("text/plain"));
        Call<ProductSearchResult> call =  NetworkHandler.getNetworkHandler(getApplicationContext()).getNetworkApi().getSearchResults(body, storeId);

        call.enqueue(new Callback<ProductSearchResult>() {


            @Override
            public void onResponse(Call<ProductSearchResult> call, Response<ProductSearchResult> response) {
                Log.d(TAG, "onResponse: call" + response.isSuccessful() );
                ResponseResult result = response.body().getResponseResult();
                productList.clear();
                productListAdapter.notifyDataSetChanged();
                List<List<String>> searchResults = response.body().getProductList();
                if (searchResults != null) {
                    ListIterator<List<String>> iter = searchResults.listIterator();
                    if (result.getResponseCode().equalsIgnoreCase("200")) {
                        while (iter.hasNext()) {
                            List<String> pList = iter.next();
                            productList.add(
                                    new Product(
                                    0,
                                    pList.get(1),
                                    pList.get(0),
                                    pList.get(3),
                                    pList.get(5),
                                    pList.get(7),
                                    "0",
                                    pList.get(10),
                                    pList.get(11),
                                    "0",
                                    pList.get(15),
                                    pList.get(16),
                                    pList.get(17),
                                    shoppingMethod
                                    )
                            );
                            productListAdapter.notifyDataSetChanged();
                        }

                    }
                }
            }

            @Override
            public void onFailure(Call<ProductSearchResult> call, Throwable t) {
                Log.e(TAG, "onFailure: " + call.request() );

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
                    retrieve_search_results(s);
                });
    }

    public void Go_to_Basket(View view) {
        Intent in = new Intent(this, CartActivity.class);
        in.putExtra("search_string", search_string);
        in.putExtra("comingFrom", "ProductsSearchResults");
        in.putExtra("shoppingMethod", shoppingMethod);
        startActivity(in);
    }

    @Override
    public void onBackPressed() {
        if (coming_from.equals("Cart")){
            Intent in = new Intent(ProductsSearchResults.this, ProductsCategory.class);
            in.putExtra("shoppingMethod", shoppingMethod);
            startActivity(in);
        } else {
            super.onBackPressed();
        }
    }

}
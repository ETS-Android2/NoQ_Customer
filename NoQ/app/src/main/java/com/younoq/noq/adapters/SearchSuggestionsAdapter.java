package com.younoq.noq.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.younoq.noq.R;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.younoq.noq.classes.SearchSuggestions;
import com.younoq.noq.views.ProductsList;
import com.younoq.noq.views.ProductsSearchResults;

import java.util.List;

public class SearchSuggestionsAdapter extends RecyclerView.Adapter<SearchSuggestionsAdapter.SearchAdapterViewHolder> {

    Context context;
    private String shoppingMethod;
    List<SearchSuggestions> searchSuggestionsList;
    private static final  String TAG = "SearchSuggestionAdapter";

    public SearchSuggestionsAdapter(Context ctx, List<SearchSuggestions> cList, String sMethod) {
        this.context = ctx;
        shoppingMethod = sMethod;
        searchSuggestionsList = cList;
    }

    @NonNull
    @Override
    public SearchAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.search_suggestion_list_item, parent, false);
        return new SearchSuggestionsAdapter.SearchAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchAdapterViewHolder holder, int position) {
        SearchSuggestions searchSuggestions = searchSuggestionsList.get(position);
        final String searchString = searchSuggestions.getProductName();
        holder.search_suggestion.setText(searchString);
    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "count "+searchSuggestionsList.size());
        return searchSuggestionsList.size();
    }

    class SearchAdapterViewHolder extends RecyclerView.ViewHolder {

        TextView search_suggestion;

        public SearchAdapterViewHolder(@NonNull View itemView) {
            super(itemView);

            search_suggestion = itemView.findViewById(R.id.search_suggestions_name);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    SearchSuggestions suggestions = searchSuggestionsList.get(getAdapterPosition());
                    final String search_string = suggestions.getProductName();

                    Intent in = new Intent(v.getContext(), ProductsSearchResults.class);
                    in.putExtra("coming_from", "ProductsCategory");
                    in.putExtra("shoppingMethod", shoppingMethod);
                    in.putExtra("search_string", search_string);
                    v.getContext().startActivity(in);

                }
            });

        }
    }
}

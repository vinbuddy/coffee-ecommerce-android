package com.coffee.app.ui.menu;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.coffee.app.R;
import com.coffee.app.model.Category;
import com.coffee.app.model.Product;
import com.coffee.app.shared.Constants;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MenuFragment extends Fragment implements CategoryBottomSheetFragment.OnCategoryItemClickListener {
    TextView showCategories;

    RecyclerView recyclerView;
    List<Product> products = new ArrayList<>();
    ProductAdapter productAdapter;

    View rootView;
    TextInputEditText searchbox;


    public MenuFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_menu, container, false);
        addControls();
        addEvents();

        getProductRequest();
        showCurrentCategoryLabel(0);

        return rootView;
    }

    private void addControls() {
        showCategories = rootView.findViewById(R.id.showCategories);

        recyclerView = rootView.findViewById(R.id.productRecyclerView);
        recyclerView.hasFixedSize();
        recyclerView.addItemDecoration( new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator( new DefaultItemAnimator());

        searchbox = rootView.findViewById(R.id.searchEditText);

    }

    private void addEvents() {
        showCategoryBottomSheet();
        searchProduct();

    }

    private  void searchProduct() {
        searchbox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                productAdapter.setProductList(products);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String searchText = s.toString().trim();

                productAdapter.filter(searchText);
                productAdapter.notifyDataSetChanged();

            }
        });
    }

    private void getProductRequest() {
        String url = Constants.API_URL + "/product";

        RequestQueue queue = Volley.newRequestQueue(getContext());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            products.clear();

                            JSONArray dataArray = response.getJSONArray("data");


                            for (int i = 0; i < dataArray.length(); i++) {
                                JSONObject productObject = dataArray.getJSONObject(i);

                                Product product = new Product();
                                product.setId(productObject.getInt("id"));
                                product.setName(productObject.getString("name"));
                                product.setPrice(Double.parseDouble(productObject.getString("price")));
                                product.setDescription(productObject.getString("description"));
                                product.setStatus(productObject.getString("status"));
                                product.setImage(productObject.getString("image"));
                                product.setCategory_id(productObject.getInt("category_id"));
                                product.setCategory_name(productObject.getString("category_name"));


                                products.add(product);

                            }

                            renderProducts();


                        } catch (JSONException e) {
                            e.printStackTrace();
                            // Handle JSON parsing error more gracefully
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        // Handle error response more gracefully (e.g., show user-friendly message)
                    }
                });


        queue.add(jsonObjectRequest);

    }

    private void getProductByCategoryRequest(int categoryId) {
        String url = Constants.API_URL + "/product";

        RequestQueue queue = Volley.newRequestQueue(getContext());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            products.clear();

                            JSONArray dataArray = response.getJSONArray("data");


                            for (int i = 0; i < dataArray.length(); i++) {
                                JSONObject productObject = dataArray.getJSONObject(i);

                                Product product = new Product();
                                product.setId(productObject.getInt("id"));
                                product.setName(productObject.getString("name"));
                                product.setPrice(Double.parseDouble(productObject.getString("price")));
                                product.setDescription(productObject.getString("description"));
                                product.setStatus(productObject.getString("status"));
                                product.setImage(productObject.getString("image"));
                                product.setCategory_id(productObject.getInt("category_id"));
                                product.setCategory_name(productObject.getString("category_name"));


                                if (product.getCategory_id() == categoryId)
                                    products.add(product);
                            }

                            renderProducts();


                        } catch (JSONException e) {
                            e.printStackTrace();
                            // Handle JSON parsing error more gracefully
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        // Handle error response more gracefully (e.g., show user-friendly message)
                    }
                });


        queue.add(jsonObjectRequest);

    }

    private  void renderProducts() {
        productAdapter = new ProductAdapter(getContext(),products);
        recyclerView.setAdapter(productAdapter);
    }


    private void showCategoryBottomSheet() {
        ArrayList<Category> categoryList = new ArrayList<>();
        showCategories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CategoryBottomSheetFragment bottomSheetFragment = new CategoryBottomSheetFragment(categoryList);
                bottomSheetFragment.show(getChildFragmentManager(), bottomSheetFragment.getTag());
            }
        });
    }

    private void showCurrentCategoryLabel(int categoryId) {
        if (categoryId == 0) {
            showCategories.setText("Tất cả");
        } else {
            // find category name by categoryId in categories
            Product product = products.get(0);
            showCategories.setText(product.getCategory_name());


        }
    }


    @Override
    public void onCategoryItemClick(int categoryId) {
        if (categoryId == 0)
            getProductRequest();
        else
            getProductByCategoryRequest(categoryId);

        showCurrentCategoryLabel(categoryId);

    }
}
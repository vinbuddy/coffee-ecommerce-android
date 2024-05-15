package com.coffee.app.ui.wishlist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.coffee.app.R;
import com.coffee.app.model.Product;
import com.coffee.app.shared.Constants;
import com.coffee.app.ui.detail.ProductDetailBottomSheetFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class WishlistActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    List<Product> products = new ArrayList<>();
    TextView backBtn;
    WishlistAdapter wishlistAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist);

        addControls();
        getWishListRequest();
        addEvents();
    }

    private void addControls() {
        recyclerView = (RecyclerView) findViewById(R.id.wishlistRecyclerView);

        recyclerView.hasFixedSize();
        recyclerView.addItemDecoration( new DividerItemDecoration(getApplicationContext(), LinearLayoutManager.VERTICAL));
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator( new DefaultItemAnimator());
        backBtn =(TextView) findViewById(R.id.backBtn);
    }

    private void addEvents()  {
        backPrevious();
    }

    private void backPrevious() {
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getOnBackPressedDispatcher().onBackPressed();
            }
        });
    }

    private void getWishListRequest() {
        RequestQueue queue = Volley.newRequestQueue(this);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//        String userId = user.getUid();
        String userId = Constants.TEMP_USER_ID;

        String url = Constants.API_URL + "/wishlist/" + userId;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            products.clear();

                            JSONArray dataArray = response.getJSONArray("data");


                            for (int i = 0; i < dataArray.length(); i++) {
                                JSONObject productObj = dataArray.getJSONObject(i);
                                int productId = productObj.getInt("product_id");
                                String name = productObj.getString("product_name");
                                String img = productObj.getString("product_image");
                                double price = Double.parseDouble(productObj.getString("product_price"));

                                Product product = new Product();
                                product.setId(productId);
                                product.setName(name);
                                product.setImage(img);
                                product.setPrice(price);


                                products.add(product);

                            }
                            renderWishlist();
                            showProductDetail();

                        } catch (JSONException e) {
                            e.printStackTrace();

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

    private void renderWishlist() {
        wishlistAdapter = new WishlistAdapter(getApplicationContext(), products);
        recyclerView.setAdapter(wishlistAdapter);
    }


    private void showProductDetail()
    {
        wishlistAdapter.setOnItemClickListener(new WishlistAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Product product) {
                ProductDetailBottomSheetFragment productDetailBottomSheetFragment = new ProductDetailBottomSheetFragment(product);
                productDetailBottomSheetFragment.show(getSupportFragmentManager(), productDetailBottomSheetFragment.getTag());
            }
        });
    }


}
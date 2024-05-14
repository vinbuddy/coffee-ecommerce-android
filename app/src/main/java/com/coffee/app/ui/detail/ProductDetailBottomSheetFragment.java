package com.coffee.app.ui.detail;

import android.app.Dialog;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.coffee.app.R;
import com.coffee.app.model.Category;
import com.coffee.app.model.Product;
import com.coffee.app.model.ProductSize;
import com.coffee.app.model.ProductTopping;
import com.coffee.app.shared.Constants;
import com.coffee.app.shared.Utils;
import com.coffee.app.ui.home.CoffeeProductCardGridAdapter;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ProductDetailBottomSheetFragment extends BottomSheetDialogFragment {
    Product product;
    ArrayList<ProductSize> productSizes = new ArrayList<>();
    ArrayList<ProductTopping> productToppings = new ArrayList<>();
    BottomSheetDialog dialog;
    BottomSheetBehavior<View> bottomSheetBehavior;
    View rootView;
    MaterialButton closeDetailBtn;
    TextView textViewName, textViewPrice, textViewDescription;
    ImageView imageProduct;
    RecyclerView productSizesRecyclerView, productToppingsRecyclerView;
    RecyclerView.LayoutManager productSizesLayoutManager, productToppingsLayoutManager;
    ProductSizeAdapter productSizeAdapter;
    ProductToppingAdapter productToppingAdapter;

    public ProductDetailBottomSheetFragment(Product product) {
        this.product = product;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_product_detail_bottom_sheet, container, false);

        closeDetailBtn = (MaterialButton) rootView.findViewById(R.id.closeDetailBtn);

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bottomSheetBehavior = BottomSheetBehavior.from((View) view.getParent());
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        addControls();
        renderProduct();

        getProductSizesRequest();
        getProductToppingsRequest();

        addEvents();


        // Set min height to parent view
        CoordinatorLayout bottomSheetLayout = dialog.findViewById(R.id.categoryBottomSheetLayout);
        if (bottomSheetLayout != null) {
            bottomSheetLayout.setMinimumHeight(Resources.getSystem().getDisplayMetrics().heightPixels);
        }


    }

    private void addControls() {
        textViewName = rootView.findViewById(R.id.textViewName);
        textViewPrice = rootView.findViewById(R.id.textViewPrice);
        textViewDescription = rootView.findViewById(R.id.textViewDescription);
        imageProduct = rootView.findViewById(R.id.imageProduct);

        productSizesRecyclerView = rootView.findViewById(R.id.productSizesRecyclerView);
        productToppingsRecyclerView = rootView.findViewById(R.id.productToppingsRecyclerView);

    }


    private void addEvents() {
        closeBottomSheetEvent();
    }

    private void renderProduct() {
        textViewName.setText(product.getName());
        textViewPrice.setText(Utils.formatVNCurrency(product.getPrice()));
        textViewDescription.setText(product.getDescription());
        Picasso.get().load(product.getImage()).into(imageProduct);
    }

    private void renderProductSizes() {

        if(productSizes.size() == 0) {
            return;
        }
        // Render product sizes
        productSizesLayoutManager = new LinearLayoutManager(getContext());
        productSizesRecyclerView.setLayoutManager(productSizesLayoutManager);
        productSizeAdapter = new ProductSizeAdapter(productSizes);
        productSizesRecyclerView.setAdapter(productSizeAdapter);
    }


    private void renderProductToppings() {

        if(productToppings.size() == 0) {
            return;
        }

        productToppingsLayoutManager = new LinearLayoutManager(getContext());
        productToppingsRecyclerView.setLayoutManager(productToppingsLayoutManager);
        productToppingAdapter = new ProductToppingAdapter(productToppings);
        productToppingsRecyclerView.setAdapter(productToppingAdapter);
    }


    private void getProductSizesRequest() {
        String url = Constants.API_URL + "/product/product-sizes/" + product.getId();
        RequestQueue queue = Volley.newRequestQueue(getContext());

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            productSizes.clear();

                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray dataArray = jsonObject.getJSONArray("data");

                            for (int i = 0; i < dataArray.length(); i++) {
                                JSONObject productSizeObj = dataArray.getJSONObject(i);
                                int id = productSizeObj.getInt("id");
                                int productId = productSizeObj.getInt("product_id");
                                int sizeId = productSizeObj.getInt("size_id");
                                String sizePrice = productSizeObj.getString("size_price");
                                String sizeName = productSizeObj.getString("size_name");

                                ProductSize productSize = new ProductSize();
                                productSize.setId(id);
                                productSize.setProductId(productId);
                                productSize.setSizeId(sizeId);
                                productSize.setSizePrice(Double.parseDouble(sizePrice));
                                productSize.setSizeName(sizeName);

                                productSizes.add(productSize);
                            }

                            // TODO: Update your UI here
                            renderProductSizes();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });

        queue.add(stringRequest);
    }

    private void getProductToppingsRequest() {
        String url = Constants.API_URL + "/product/product-toppings/" + product.getId();
        RequestQueue queue = Volley.newRequestQueue(getContext());

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            productSizes.clear();

                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray dataArray = jsonObject.getJSONArray("data");

                            for (int i = 0; i < dataArray.length(); i++) {
                                JSONObject productSizeObj = dataArray.getJSONObject(i);

                                int id = productSizeObj.getInt("id");
                                int productId = productSizeObj.getInt("product_id");
                                String toppingName = productSizeObj.getString("topping_name");
                                String toppingPrice = productSizeObj.getString("topping_price");

                                ProductTopping productTopping = new ProductTopping();
                                productTopping.setId(id);
                                productTopping.setProductId(productId);
                                productTopping.setToppingName(toppingName);
                                productTopping.setToppingPrice(Double.parseDouble(toppingPrice));


                                productToppings.add(productTopping);
                            }

                            // TODO: Update your UI here
                            renderProductToppings();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });

        queue.add(stringRequest);
    }


    private void closeBottomSheetEvent() {
        closeDetailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
}

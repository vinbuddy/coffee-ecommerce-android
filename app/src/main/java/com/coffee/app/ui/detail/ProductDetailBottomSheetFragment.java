package com.coffee.app.ui.detail;

import android.app.Dialog;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.coffee.app.R;
import com.coffee.app.model.Category;
import com.coffee.app.model.Product;
import com.coffee.app.model.ProductSize;
import com.coffee.app.model.ProductTopping;
import com.coffee.app.model.Wishlist;
import com.coffee.app.shared.Constants;
import com.coffee.app.shared.Utils;
import com.coffee.app.ui.home.CoffeeProductCardGridAdapter;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProductDetailBottomSheetFragment extends BottomSheetDialogFragment implements ProductSizeAdapter.OnSizeItemSizeClickListener, ProductToppingAdapter.OnToppingItemClickListener {
    Product product;
    ArrayList<ProductSize> productSizes = new ArrayList<>();
    ArrayList<ProductTopping> productToppings = new ArrayList<>();
    BottomSheetDialog dialog;
    BottomSheetBehavior<View> bottomSheetBehavior;
    View rootView;
    MaterialButton closeDetailBtn;
    TextView textViewName, textViewPrice, textViewDescription, textViewQuantity;
    ImageView imageProduct;
    RecyclerView productSizesRecyclerView, productToppingsRecyclerView;
    RecyclerView.LayoutManager productSizesLayoutManager, productToppingsLayoutManager;
    ProductSizeAdapter productSizeAdapter;
    ProductToppingAdapter productToppingAdapter;

    MaterialButton wishlistBtn, increaseQuantityBtn, decreaseQuantityBtn;

    Button addToCartBtn;

    ArrayList<Wishlist> wishlist = new ArrayList<>();

    // Data
    int quantity = 1;
    ArrayList<ProductTopping> selectedToppings;
    ProductSize selectedSize;
    double previewPrice = 0;

    public ProductDetailBottomSheetFragment(Product product) {
        this.product = product;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        Window window = dialog.getWindow();
        window.setBackgroundDrawableResource(R.color.overlay);
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
        renderQuantity();
        renderProduct();

        getProductSizesRequest();
        getProductToppingsRequest();
        getWishListRequest();

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
        wishlistBtn = rootView.findViewById(R.id.wishlistBtn);

        increaseQuantityBtn = rootView.findViewById(R.id.increaseQuantityBtn);
        decreaseQuantityBtn = rootView.findViewById(R.id.decreaseQuantityBtn);
        textViewQuantity = rootView.findViewById(R.id.textViewQuantity);

        addToCartBtn = rootView.findViewById(R.id.addToCartBtn);

    }


    private void addEvents() {
        closeBottomSheetEvent();
        toggleAddToWishlist();
        selectQuantity();
        addToCart();
    }

    private void renderPreviewPrice() {
        // render preview price with condition: selectSize price + selectToppings price + product price + quantity
        double sizePrice = selectedSize != null ? selectedSize.getSizePrice() : 0;
        double toppingPrice = 0;
        if (selectedToppings != null) {
            for (ProductTopping topping : selectedToppings) {
                toppingPrice += topping.getToppingPrice();
            }
        }


        previewPrice = (sizePrice + toppingPrice + product.getPrice()) * quantity;

        // render preview price -> setText to addToCartBtn
        addToCartBtn.setText(Utils.formatVNCurrency(previewPrice) + " • Chọn" );
    }

    private void renderQuantity() {
        textViewQuantity.setText(String.valueOf(quantity));
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
        productSizeAdapter = new ProductSizeAdapter(productSizes, this);
        productSizesRecyclerView.setAdapter(productSizeAdapter);
    }


    private void renderProductToppings() {

        if(productToppings.size() == 0) {
            return;
        }

        productToppingsLayoutManager = new LinearLayoutManager(getContext());
        productToppingsRecyclerView.setLayoutManager(productToppingsLayoutManager);
        productToppingAdapter = new ProductToppingAdapter(productToppings, this);
        productToppingsRecyclerView.setAdapter(productToppingAdapter);
    }

    private boolean isProductInWishList() {
        for (Wishlist wishlistItem : wishlist) {
            if (wishlistItem.getProductId() == product.getId()) {
                return true;
            }
        }
        return false;
    }

    private void renderActiveWishlistButton() {
        // Check if the product is in the wishlist
        if (wishlist.size() == 0) {
            return;
        }

        // Render the active wishlist button
        if (isProductInWishList()) {
            wishlistBtn.setIconResource(R.drawable.heart_fill_icon);
            wishlistBtn.setIconTintResource(R.color.heart);
        } else {
            wishlistBtn.setIconResource(R.drawable.heart_outline_icon);
            wishlistBtn.setIconTintResource(R.color.black);
        }
    }

    private void addWishlistState(int wishlistItemId) {
        Wishlist wishlistItem = new Wishlist();

        wishlistItem.setId(wishlistItemId);
        wishlistItem.setProductId(product.getId());
        wishlistItem.setProductName(product.getName());
        wishlistItem.setProductImage(product.getImage());
        wishlistItem.setProductPrice(product.getPrice());

        wishlist.add(wishlistItem);
    }

    private void removeWishlistState() {
        for (Wishlist wishlistItem : wishlist) {
            if (wishlistItem.getProductId() == product.getId()) {
                wishlist.remove(wishlistItem);
                break;
            }
        }
    }

    private Wishlist getCurrentWishlistByProductId() {
        for (Wishlist wishlistItem : wishlist) {
            if (wishlistItem.getProductId() == product.getId()) {
                return wishlistItem;
            }
        }
        return null;
    }


    // REQUESTS
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
                            renderPreviewPrice();

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
                            renderPreviewPrice();

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

    private void getWishListRequest() {
        RequestQueue queue = Volley.newRequestQueue(getContext());

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        // String userId = user.getUid();
        String userId = "IRAXCceD7USppEMMIdPU1At4vw63";
        String url = Constants.API_URL + "/wishlist/" + userId;


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            wishlist.clear();

                            JSONArray dataArray = response.getJSONArray("data");


                            for (int i = 0; i < dataArray.length(); i++) {
                                JSONObject productObj = dataArray.getJSONObject(i);

                                int id = productObj.getInt("id");
                                int productId = productObj.getInt("product_id");
                                String name = productObj.getString("product_name");
                                String img = productObj.getString("product_image");
                                double price = Double.parseDouble(productObj.getString("product_price"));

                                Wishlist wishlistItem = new Wishlist();
                                wishlistItem.setId(id);
                                wishlistItem.setProductId(productId);
                                wishlistItem.setProductName(name);
                                wishlistItem.setProductImage(img);
                                wishlistItem.setProductPrice(price);




                                wishlist.add(wishlistItem);
                            }

                            renderActiveWishlistButton();

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

    private void addToWishlistRequest() {
        String url = Constants.API_URL + "/wishlist";
        RequestQueue queue = Volley.newRequestQueue(getContext());

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        // String userId = user.getUid();
        String userId = "IRAXCceD7USppEMMIdPU1At4vw63";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        // handle response
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONObject dataObject = jsonObject.getJSONObject("data");
                            int id = dataObject.getInt("id");

                            // add current product to wishlist variable
                            addWishlistState(id);
                            renderActiveWishlistButton();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Toast.makeText(getContext(), "Đã thêm vào wishlist", Toast.LENGTH_SHORT).show();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Handle error response more gracefully (e.g., show user-friendly message)
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", userId);
                params.put("product_id", String.valueOf(product.getId()));
                return params;
            }
        };


        queue.add(stringRequest);
    }

    private void removeFromWishlistRequest() {

        String url = Constants.API_URL + "/wishlist/" + getCurrentWishlistByProductId().id;
        RequestQueue queue = Volley.newRequestQueue(getContext());


        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // handle response
                        removeWishlistState();
                        renderActiveWishlistButton();
                        Toast.makeText(getContext(), "Đã xóa khỏi wishlist", Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Handle error response more gracefully (e.g., show user-friendly message)
            }
        });

        queue.add(stringRequest);
    }

    private void addToCartRequest() {
        String url = Constants.API_URL + "/cart";
        RequestQueue queue = Volley.newRequestQueue(getContext());

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        // String userId = user.getUid();
        String userId = "IRAXCceD7USppEMMIdPU1At4vw63";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        // close current bottom sheet
                        dismiss();
                        Toast.makeText(getContext(), "Đã thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Handle error response more gracefully (e.g., show user-friendly message)
                Toast.makeText(getContext(), "Đã xảy ra lỗi: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                // Body params
                params.put("product_id", String.valueOf(product.getId()));
                params.put("user_id", userId);
                params.put("quantity", String.valueOf(quantity));
                if (selectedSize != null) {
                    params.put("size_id", String.valueOf(selectedSize.getSizeId()));
                }

                if (selectedToppings != null) {
                    for (ProductTopping topping : selectedToppings) {
                        params.put("toppings[]", String.valueOf(topping.getId()));
                    }
                }

                return params;
            }
        };

        queue.add(stringRequest);
    }



    // EVENTS
    private void closeBottomSheetEvent() {
        closeDetailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    private void toggleAddToWishlist() {
        wishlistBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // If the product is in the wishlist, remove it
                if (isProductInWishList()) {
                    removeFromWishlistRequest();
                } else {
                    addToWishlistRequest();
                }
            }
        });
    }

    private void selectQuantity() {
        increaseQuantityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quantity++;
                renderQuantity();
                renderPreviewPrice();
            }
        });

        decreaseQuantityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (quantity > 1) {
                    quantity--;
                    renderQuantity();
                    renderPreviewPrice();
                }
            }
        });
    }

    private void addToCart() {
        addToCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToCartRequest();
            }
        });
    }



    // INTERFACE METHODS
    @Override
    public void onSizeItemClick(ProductSize _selectedSize) {
        this.selectedSize = _selectedSize;
        renderPreviewPrice();
    }

    @Override
    public void onToppingItemClick(ArrayList<ProductTopping> selectedToppings) {
        this.selectedToppings = selectedToppings;
        renderPreviewPrice();
    }
}

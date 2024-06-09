package com.coffee.app.ui.cart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Xfermode;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.coffee.app.R;
import com.coffee.app.model.Cart;
import com.coffee.app.model.Product;
import com.coffee.app.shared.Constants;
import com.coffee.app.shared.Utils;
import com.coffee.app.ui.checkout.CheckoutActivity;
import com.coffee.app.ui.login.LoginActivity;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CartActivity extends AppCompatActivity {

    TextView btnBack, textViewTotalPrice, textViewTotalItem;
    RecyclerView recyclerView;
    Button btnContinue;
    CartAdapter cartAdapter;

    ShimmerFrameLayout skeletonLayout;

    ArrayList<Cart> cart = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        // Check authentication firebase if not login, redirect to login page
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Intent intent = new Intent(CartActivity.this, LoginActivity.class);
            startActivity(intent);
        }

        addControls();
        skeletonLayout.startShimmer();
        getCartRequest();
        addEvents();
    }

    public interface CartListener {
        void onUndo();
    }

    private void addControls() {
        btnBack = findViewById(R.id.btnBack);
        textViewTotalPrice =findViewById(R.id.textViewTotalPrice);
        textViewTotalItem = findViewById(R.id.textViewTotalItem);
        btnContinue = findViewById(R.id.btnContinue);

        skeletonLayout = findViewById(R.id.skeletonLayout);
        recyclerView =  findViewById(R.id.recyclerViewCart);

    }

    private void addEvents() {
        backPrevious();
        goToCheckout();
    }

    private void cartEvents() {
        swipeToDelete();
    }

    private void undoRemoveCartItem(Cart cartItem, int position) {
        cart.add(position, cartItem);
        cartAdapter.notifyDataSetChanged();
    }

    private void swipeToDelete() {
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                // Get current cart item by position
                int position = viewHolder.getAdapterPosition();
                Cart cartItem = cart.get(position);

                // Remove first
                cart.remove(position);
                cartAdapter.notifyItemRemoved(position);


                deleteCartItemRequest(cartItem.getId(), new CartListener() {
                    @Override
                    public void onUndo() {
                        undoRemoveCartItem(cartItem, position);
                    }
                });


            }

            @Override
            public float getSwipeThreshold(@NonNull RecyclerView.ViewHolder viewHolder) {
                return 1f;
            }

            @Override
            public void onChildDraw(@NonNull android.graphics.Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                setDeleteIcon(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);


            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void setDeleteIcon(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

        Paint paint = new Paint();
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

        ColorDrawable background = new ColorDrawable();
        int backgroundColor = Color.parseColor("#b80f0a");

        Drawable deleteIcon = ContextCompat.getDrawable(this, R.drawable.delete_icon);
        deleteIcon.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);

        int intrinsicWidth = deleteIcon.getIntrinsicWidth();
        int intrinsicHeight = deleteIcon.getIntrinsicHeight();

        View itemView = viewHolder.itemView;
        int itemHeight = itemView.getHeight();

        boolean isCanceled = dX == 0 && !isCurrentlyActive;

        if (isCanceled) {
           c.drawRect(itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom(), paint);
           return;
        }

        background.setColor(backgroundColor);
        background.setBounds(itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
        background.draw(c);

        int deleteIconTop = itemView.getTop() + (itemHeight - intrinsicHeight) / 2;
        int deleteIconMargin = (itemHeight - intrinsicHeight) / 2;
        int deleteIconLeft = itemView.getRight() - deleteIconMargin - intrinsicWidth;
        int deleteIconRight = itemView.getRight() - deleteIconMargin;
        int deleteIconBottom = deleteIconTop + intrinsicHeight;

        deleteIcon.setBounds(deleteIconLeft, deleteIconTop, deleteIconRight, deleteIconBottom);
        deleteIcon.draw(c);


    }

    private void backPrevious() {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getOnBackPressedDispatcher().onBackPressed();
            }
        });
    }

    private void goToCheckout() {
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CartActivity.this, CheckoutActivity.class);
                startActivity(intent);

            }
        });
    }

    private void getCartRequest() {
        RequestQueue queue = Volley.newRequestQueue(this);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = user.getUid();
//        String userId = Constants.TEMP_USER_ID;

        String url = Constants.API_URL + "/cart/" + userId;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            JSONArray dataArray = response.getJSONArray("data");

                            for (int i = 0; i < dataArray.length(); i++) {
                                Cart cartItem = new Cart();
                                JSONObject cartItemObject = dataArray.getJSONObject(i);

                                cartItem.setId(cartItemObject.getInt("id"));
                                cartItem.setProductId(cartItemObject.getInt("product_id"));
                                cartItem.setQuantity(cartItemObject.getInt("quantity"));
                                cartItem.setProductName(cartItemObject.getString("product_name"));
                                cartItem.setProductPrice(cartItemObject.getDouble("product_price"));
                                cartItem.setProductStatus(cartItemObject.getInt("product_status"));
                                cartItem.setProductImage(cartItemObject.getString("product_image"));

                                if (cartItemObject.getString("size_name") != null) {
                                    cartItem.setSizeId(cartItemObject.getInt("size_id"));
                                    cartItem.setSizeName(cartItemObject.getString("size_name"));
                                    cartItem.setSizePrice(cartItemObject.getDouble("size_price"));
                                }

                                cartItem.setOrderItemPrice(cartItemObject.getDouble("order_item_price"));

                                // Using optJSONArray instead of getJSONArray to avoid JSONException
                                JSONArray toppingsArray = cartItemObject.optJSONArray("toppings");

                                if (toppingsArray != null) {
                                    ArrayList<Cart.CartTopping> toppings = new ArrayList<>();

                                    for (int j = 0; j < toppingsArray.length(); j++) {
                                        JSONObject toppingObject = toppingsArray.getJSONObject(j);
                                        Cart.CartTopping topping = new Cart.CartTopping();
                                        topping.setToppingStorageId(toppingObject.getInt("topping_storage_id"));
                                        topping.setToppingId(toppingObject.getInt("topping_id"));
                                        topping.setToppingName(toppingObject.getString("topping_name"));
                                        topping.setToppingPrice(toppingObject.getDouble("topping_price"));
                                        toppings.add(topping);
                                    }
                                    cartItem.setToppings(toppings);
                                }

                                cart.add(cartItem);
                            }

                            renderCart();
                            cartEvents();

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

    private void deleteCartItemRequest(int cartItemId, CartListener cartListener) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = Constants.API_URL + "/cart/" + cartItemId;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.DELETE, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Handle response
                        Toast.makeText(CartActivity.this, "Đã xóa thành công", Toast.LENGTH_SHORT).show();
                        renderTotalItem();
                        renderTotalPrice();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        cartListener.onUndo();
                    }
                });

        queue.add(jsonObjectRequest);
    }

    private void renderTotalItem() {
        textViewTotalItem.setText(cart.size() + " sản phẩm đã chọn");
    }

    private void renderTotalPrice() {
        double totalPrice = 0;
        for (Cart cartItem : cart) {
            totalPrice += cartItem.getOrderItemPrice();
        }

        textViewTotalPrice.setText(Utils.formatVNCurrency(totalPrice));
    }

    private void renderCart() {
        recyclerView.hasFixedSize();
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        cartAdapter = new CartAdapter(cart);
        recyclerView.setAdapter(cartAdapter);

        skeletonLayout.stopShimmer();
        skeletonLayout.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);

        renderTotalItem();
        renderTotalPrice();

    }


}
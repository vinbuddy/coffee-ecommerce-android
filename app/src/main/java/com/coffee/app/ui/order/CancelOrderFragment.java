package com.coffee.app.ui.order;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.coffee.app.R;
import com.coffee.app.model.Cart;
import com.coffee.app.model.Order;
import com.coffee.app.shared.Constants;
import com.coffee.app.ui.cart.CartAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CancelOrderFragment extends Fragment {

    View rootView;
    ArrayList<Order> orders = new ArrayList<>();

    RecyclerView recyclerView;
    OrderAdapter orderAdapter;

    public CancelOrderFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_cancel_order, container, false);
        addControls();
        getUserOrdersRequest();
        return rootView;
    }

    private void addControls() {
        // Add controls here
        recyclerView = rootView.findViewById(R.id.recyclerViewCanceledOrder);
    }


    private void renderOrders() {
        ArrayList<Order> completedOrders = new ArrayList<>();
        for (Order order : orders) {
            if (order.getOrderStatus().equals("Đã hủy")) {
                completedOrders.add(order);
            }
        }

        recyclerView.hasFixedSize();
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));

        orderAdapter = new OrderAdapter(completedOrders);
        recyclerView.setAdapter(orderAdapter);

    }

    private void getUserOrdersRequest() {
        RequestQueue queue = Volley.newRequestQueue(getContext());
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//        String userId = user.getUid();
        String userId = Constants.TEMP_USER_ID;

        String url = Constants.API_URL + "/order/user-order/" + userId;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {


                        try {
                            JSONArray dataArray = response.getJSONArray("data");

                            for (int i = 0; i < dataArray.length(); i++) {

                                JSONObject orderObject = dataArray.getJSONObject(i);

                                String id = orderObject.getString("id");
                                String userId = orderObject.getString("user_id");
                                String paymentMethod = orderObject.getString("payment_method");
                                double totalPayment = orderObject.getDouble("total_payment");
                                String orderStatus = orderObject.getString("order_status");
                                String orderType = orderObject.getString("order_type");
                                String orderDate = orderObject.getString("order_date");
                                String orderNote = orderObject.isNull("order_note") ? null : orderObject.getString("order_note");
                                double shippingCost = orderObject.getDouble("shipping_cost");
                                String receiverName = orderObject.getString("receiver_name");
                                String phoneNumber = orderObject.getString("phone_number");
                                String address = orderObject.getString("address");
                                int storeId = orderObject.getInt("store_id");
                                int voucherId = orderObject.optInt("voucher_id");
                                String userName = orderObject.getString("user_name");
                                String email = orderObject.getString("email");
                                String avatar = orderObject.getString("avatar");
                                String storeName = orderObject.getString("store_name");
                                String voucherName = orderObject.isNull("voucher_name") ? null : orderObject.getString("voucher_name");
                                boolean isReviewed = orderObject.getBoolean("is_reviewed");

                                JSONArray orderItemsArray = orderObject.getJSONArray("order_items");
                                ArrayList<Cart> orderItems = new ArrayList<>();

                                for (int j = 0; j < orderItemsArray.length(); j++) {
                                    Cart cartItem = new Cart();
                                    JSONObject cartItemObject = orderItemsArray.getJSONObject(j);

                                    cartItem.setId(cartItemObject.getInt("id"));
                                    cartItem.setProductId(cartItemObject.getInt("product_id"));
                                    cartItem.setQuantity(cartItemObject.getInt("quantity"));
                                    cartItem.setProductName(cartItemObject.getString("product_name"));
                                    cartItem.setProductPrice(cartItemObject.getDouble("product_price"));
                                    cartItem.setProductImage(cartItemObject.getString("product_image"));
                                    cartItem.setSizeName(cartItemObject.getString("size_name"));
                                    cartItem.setSizePrice(cartItemObject.getDouble("size_price"));

                                    cartItem.setQuantity(cartItemObject.getInt("quantity"));
                                    cartItem.setOrderItemPrice(cartItemObject.getDouble("order_item_price"));

                                    // Using optJSONArray instead of getJSONArray to avoid JSONException
                                    JSONArray toppingsArray = cartItemObject.optJSONArray("toppings");

                                    if (toppingsArray != null) {
                                        ArrayList<Cart.CartTopping> toppings = new ArrayList<>();

                                        for (int z = 0; z < toppingsArray.length(); z++) {
                                            JSONObject toppingObject = toppingsArray.getJSONObject(z);
                                            Cart.CartTopping topping = new Cart.CartTopping();
                                            topping.setToppingStorageId(toppingObject.getInt("topping_storage_id"));
                                            topping.setToppingName(toppingObject.getString("topping_name"));
                                            topping.setToppingPrice(toppingObject.getDouble("topping_price"));
                                            toppings.add(topping);
                                        }
                                        cartItem.setToppings(toppings);
                                    }

                                    orderItems.add(cartItem);
                                }

                                Order order = new Order(id, userId, totalPayment, paymentMethod, orderStatus, orderType, orderDate, orderNote, shippingCost, receiverName, phoneNumber, address, storeId, voucherId, voucherName, storeName, orderItems, isReviewed);

                                orders.add(order);

                            }

                            renderOrders();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
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
}
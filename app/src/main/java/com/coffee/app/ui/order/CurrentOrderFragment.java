package com.coffee.app.ui.order;

import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.coffee.app.R;
import com.coffee.app.model.Cart;
import com.coffee.app.model.CurrentOrder;
import com.coffee.app.model.Order;
import com.coffee.app.shared.Constants;
import com.coffee.app.ui.order.detail.OrderDetailBottomSheetFragment;
import com.google.android.material.chip.Chip;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CurrentOrderFragment extends Fragment {

    View rootView;
    View viewPending, viewProcessing, viewShipping, viewCompleted;
    View viewLine1, viewLine2, viewLine3;
    Button btnCancelOrder, btnCompleteOrder;
    TextView textViewOrderId, textViewPendingTime, textViewProcessingTime, textViewShippingTime, textViewCompletedTime;
    CurrentOrder currentOrder;
    Order order;
    Chip chipOrderDetail;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_current_order, container, false);
        addControls();
        getCurrentOrderFirebase();
        return rootView;
    }

    private void addControls() {
        // Add controls here
        viewPending = rootView.findViewById(R.id.viewPending);
        viewProcessing = rootView.findViewById(R.id.viewProcessing);
        viewShipping = rootView.findViewById(R.id.viewShipping);
        viewCompleted = rootView.findViewById(R.id.viewCompleted);
        viewLine1 = rootView.findViewById(R.id.viewLine1);
        viewLine2 = rootView.findViewById(R.id.viewLine2);
        viewLine3 = rootView.findViewById(R.id.viewLine3);
        textViewOrderId = rootView.findViewById(R.id.textViewOrderId);
        textViewPendingTime = rootView.findViewById(R.id.textViewPendingTime);
        textViewProcessingTime = rootView.findViewById(R.id.textViewProcessingTime);
        textViewShippingTime = rootView.findViewById(R.id.textViewShippingTime);
        textViewCompletedTime = rootView.findViewById(R.id.textViewCompletedTime);
        btnCancelOrder = rootView.findViewById(R.id.btnCancelOrder);
        btnCompleteOrder = rootView.findViewById(R.id.btnCompleteOrder);
        chipOrderDetail = rootView.findViewById(R.id.chipOrderDetail);
    }

    private void addCurrentOrderEvents() {
        cancelOrder();
        completeOrder();
        showDetail();
    }

    private void cancelOrder() {
        btnCancelOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelOrderRequest();
            }
        });
    }

    private void completeOrder() {
        btnCompleteOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                completeOrderRequest();
            }
        });
    }

    private void cancelOrderRequest() {
        // Request cancel order from server
        RequestQueue queue = Volley.newRequestQueue(getContext());

        String url = Constants.API_URL + "/order/edit-status/" + currentOrder.getOrderId();

        StringRequest stringRequest =  new StringRequest(Request.Method.PUT, url, response -> {

            cancelOrderToFirebase();
        }, error -> {
            // Handle possible errors
            Toast.makeText(getContext(), "Error Call API", Toast.LENGTH_SHORT).show();
        }) {
            @Override
            public Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("order_status", "Đã hủy");
                return params;
            }
        };

        queue.add(stringRequest);
    }

    private void completeOrderRequest() {
        // Request cancel order from server
        RequestQueue queue = Volley.newRequestQueue(getContext());

        String url = Constants.API_URL + "/order/edit-status/" + currentOrder.getOrderId();

        StringRequest stringRequest =  new StringRequest(Request.Method.PUT, url, response -> {
            completeOrderToFirebase();
        }, error -> {
            // Handle possible errors
            Toast.makeText(getContext(), "Error Call API", Toast.LENGTH_SHORT).show();
        }) {
            @Override
            public Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("order_status", "Hoàn thành");
                return params;
            }
        };

        queue.add(stringRequest);
    }

    private void cancelOrderToFirebase() {
        DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference("orders/" + currentOrder.getOrderId());

        // Update the isCompleted field to true
        orderRef.child("isCompleted").setValue(false)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(), "Đã hủy đơn hàng", Toast.LENGTH_SHORT).show();
                    } else {
                        // Handle failure
                        Toast.makeText(getContext(), "Có lỗi xảy ra", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void completeOrderToFirebase() {
        DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference("orders/" + currentOrder.getOrderId());

        // Update the isCompleted field to true
        orderRef.child("isCompleted").setValue(true)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(), "Đã hoàn thành đơn hàng", Toast.LENGTH_SHORT).show();
                    } else {
                        // Handle failure
                        Toast.makeText(getContext(), "Có lỗi xảy ra", Toast.LENGTH_SHORT).show();
                    }
                });
    }



    private void showDetail() {
        chipOrderDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OrderDetailBottomSheetFragment orderDetailBottomSheetFragment = new OrderDetailBottomSheetFragment(order);
                orderDetailBottomSheetFragment.show(getParentFragmentManager(), orderDetailBottomSheetFragment.getTag());
            }
        });
    }

    private void getCurrentOrderFirebase() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//        String userId = user.getUid();
        String userId = Constants.TEMP_USER_ID;

        DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference("orders");
        Query userOrdersQuery = ordersRef.orderByChild("userId").equalTo(userId);

        userOrdersQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Handle the received data
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    Map<String, Object> orderData = (Map<String, Object>) childSnapshot.getValue();
                    String orderId = childSnapshot.getKey();
                    boolean isCompleted = (boolean) orderData.get("isCompleted");

                    if (orderData != null && !isCompleted) {
                        Map<String, Map<String, String>> statusesMap = (Map<String, Map<String, String>>) orderData.get("statuses");
                        ArrayList<CurrentOrder.OrderStatus> statuses = new ArrayList<>();

                        for (Map.Entry<String, Map<String, String>> entry : statusesMap.entrySet()) {
                            Map<String, String> statusData = entry.getValue();
                            String status = statusData.get("status");
                            String time = statusData.get("time");
                            statuses.add(new CurrentOrder.OrderStatus(status, time));
                        }

                        // Sort status time
                        Collections.sort(statuses, new Comparator<CurrentOrder.OrderStatus>() {
                            @Override
                            public int compare(CurrentOrder.OrderStatus o1, CurrentOrder.OrderStatus o2) {
                                try {
                                    Date date1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(o1.getTime());
                                    Date date2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(o2.getTime());
                                    return date1.compareTo(date2);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                    return 0;
                                }
                            }
                        });

                        // add current order
                        currentOrder = new CurrentOrder(orderId, userId, statuses, isCompleted);


                        getCurrentOrderRequest();
                    }

                    renderCurrentOrder();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors
                Toast.makeText(getContext(), "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getCurrentOrderRequest() {

        if (order != null) {
            return;
        }

        // Request current order from server
        RequestQueue queue = Volley.newRequestQueue(getContext());
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        String userId = Constants.TEMP_USER_ID;

        String url = Constants.API_URL + "/order/" + currentOrder.getOrderId();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject orderObject = response.getJSONObject("data");
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
                    Boolean isReviewed = orderObject.isNull("is_reviewed") ? false : orderObject.getBoolean("is_reviewed");

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
                        if (cartItemObject.getString("size_name") != null) {
                            cartItem.setSizeName(cartItemObject.getString("size_name"));
                            cartItem.setSizePrice(cartItemObject.getDouble("size_price"));
                        }
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

                    order = new Order(id, userId, totalPayment, paymentMethod, orderStatus, orderType, orderDate, orderNote, shippingCost, receiverName, phoneNumber, address, storeId, voucherId, voucherName, storeName, orderItems, isReviewed, userName, email, avatar);

                    addCurrentOrderEvents();

                    Toast.makeText(getContext(), "Order: " + order.getId(), Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, error -> {
            // Handle possible errors
            Toast.makeText(getContext(), "Error Call API", Toast.LENGTH_SHORT).show();
        });

        queue.add(jsonObjectRequest);
    }

    private void renderCurrentOrder() {
        if (currentOrder == null) {
            textViewOrderId.setText("Không có đơn hàng");
            chipOrderDetail.setEnabled(false);
            return;
        }

        // Get pending status
        ArrayList<CurrentOrder.OrderStatus> statuses = currentOrder.getStatuses();

        if (statuses.size() == 0) {
            return;
        }

        if (statuses.size() > 0) {
            // set background for viewPending
            CurrentOrder.OrderStatus pendingStatus = statuses.get(0);
            viewPending.setBackgroundResource(R.drawable.order_status_completed_shape);
            textViewPendingTime.setText(pendingStatus.getTime());
        }

        if (statuses.size() > 1) {
            // set background for viewProcessing
            CurrentOrder.OrderStatus processingStatus = statuses.get(1);
            viewProcessing.setBackgroundResource(R.drawable.order_status_completed_shape);
            textViewProcessingTime.setText(processingStatus.getTime());
            viewLine1.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.primary_variant));
        }

        if (statuses.size() > 2){
            // set background for viewShipping
            CurrentOrder.OrderStatus shippingStatus = statuses.get(2);
            viewShipping.setBackgroundResource(R.drawable.order_status_completed_shape);
            textViewShippingTime.setText(shippingStatus.getTime());
            viewLine2.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.primary_variant));
        }

        if (statuses.size() > 3) {
            // set background for viewCompleted
            CurrentOrder.OrderStatus completeStatus = statuses.get(3);
            viewCompleted.setBackgroundResource(R.drawable.order_status_completed_shape);
            textViewCompletedTime.setText(completeStatus.getTime());
            viewLine3.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.primary_variant));
        }

        // Check if the list is not empty before accessing the last status
        if (!statuses.isEmpty()) {
            // Get last status and render buttons
            CurrentOrder.OrderStatus lastStatus = statuses.get(statuses.size() - 1);

            if (statuses.size() == 1 && lastStatus.getStatus().equals("Đang chờ")) {
                btnCancelOrder.setVisibility(View.VISIBLE);
                btnCompleteOrder.setVisibility(View.GONE);
            } else {
                btnCancelOrder.setVisibility(View.GONE);
            }

            if (lastStatus.getStatus().equals("Hoàn thành")) {
                btnCancelOrder.setVisibility(View.GONE);
                btnCompleteOrder.setVisibility(View.VISIBLE);
            }
        }

        textViewOrderId.setText("Mã đơn hàng: #" + currentOrder.getOrderId());




    }

}
package com.coffee.app.ui.checkout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.coffee.app.R;
import com.coffee.app.model.Cart;
import com.coffee.app.model.ProductTopping;
import com.coffee.app.model.Store;
import com.coffee.app.model.Voucher;
import com.coffee.app.shared.Constants;
import com.coffee.app.shared.Utils;
import com.coffee.app.ui.cart.CartAdapter;
import com.google.android.material.chip.Chip;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CheckoutActivity extends AppCompatActivity implements ApplyVoucherBottomSheetFragment.OnApplyVoucherListener {

    CartAdapter cartAdapter;

    RecyclerView recyclerView;

    ImageButton btnClose;
    Button btnApplyVoucher, btnOrder;

    TextView textViewTotalPrice, textViewTotalPayment;
    TextInputEditText inputReceiverName, inputAddress, inputPhoneNumber, inputOrderNote;

    MaterialAutoCompleteTextView autoCompleteTextViewStore;

    RadioButton radioCash, radioMoMo, radioVNPay;
    Chip chipVoucher;

    Voucher currentVoucher;
    String paymentMethod = "cash";
    ArrayList<Store> stores = new ArrayList<>();

    ArrayList<Cart> cart = new ArrayList<>();

    Store selectedStore;

    double totalPrice = 0;
    double totalPayment = 0;
    final double deliveryFee = 18000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        addControls();
        getStoresRequest();
        getCartRequest();
        addEvents();

    }


    private void addControls() {

        autoCompleteTextViewStore = findViewById(R.id.autoCompleteTextViewStore);
        recyclerView = findViewById(R.id.recyclerViewSelectedItems);
        btnClose = findViewById(R.id.btnClose);
        radioCash = findViewById(R.id.radioCash);
        radioMoMo = findViewById(R.id.radioMoMo);
        radioVNPay = findViewById(R.id.radioVNPay);
        btnApplyVoucher = findViewById(R.id.btnApplyVoucher);
        chipVoucher = findViewById(R.id.chipVoucher);
        textViewTotalPayment = findViewById(R.id.textViewTotalPayment);

        textViewTotalPrice = findViewById(R.id.textViewTotalPrice);
        btnOrder = findViewById(R.id.btnOrder);

        inputReceiverName = findViewById(R.id.inputReceiverName);
        inputAddress = findViewById(R.id.inputAddress);
        inputPhoneNumber = findViewById(R.id.inputPhoneNumber);
        inputOrderNote = findViewById(R.id.inputOrderNote);

    }

    private void addEvents() {
        closeCheckoutActivity();
        selectPaymentMethod();
        openApplyVoucherBottomSheet();
        removeAppliedVoucher();

        createOrder();
        selectStore();
    }


    private void removeAppliedVoucher() {
        chipVoucher.setOnCloseIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Remove applied voucher here
                currentVoucher = null;
                renderAppliedVoucher();
            }
        });
    }

    private void addStoreEvents() {
        showStoresAfterFocused();
    }

    private void selectPaymentMethod() {
        // Select payment method here
        radioCash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radioCash.setChecked(true);
                radioMoMo.setChecked(false);
                radioVNPay.setChecked(false);
                paymentMethod = "cash";
            }
        });
        radioMoMo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radioCash.setChecked(false);
                radioMoMo.setChecked(true);
                radioVNPay.setChecked(false);
                paymentMethod = "momo";
            }
        });
        radioVNPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radioCash.setChecked(false);
                radioMoMo.setChecked(false);
                radioVNPay.setChecked(true);
                paymentMethod = "vnpay";
            }
        });
    }

    private void selectStore() {
        // Select store here
        autoCompleteTextViewStore.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Store currentStore = stores.get(position);
                selectedStore = currentStore;

            }
        });
    }


    private void closeCheckoutActivity() {
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void showStoresAfterFocused()
    {
        autoCompleteTextViewStore.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    autoCompleteTextViewStore.showDropDown();
                }
            }
        });
    }

    private void openApplyVoucherBottomSheet() {
        // Open apply voucher bottom sheet here
        btnApplyVoucher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApplyVoucherBottomSheetFragment applyVoucherBottomSheetFragment = new ApplyVoucherBottomSheetFragment();
                applyVoucherBottomSheetFragment.show(getSupportFragmentManager(), applyVoucherBottomSheetFragment.getTag());
            }
        });
    }

    // REQUESTS
    private void getStoresRequest() {
        // Get stores request here
        String url = Constants.API_URL + "/store";

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            stores.clear();

                            JSONArray dataArray = response.getJSONArray("data");


                            for (int i = 0; i < dataArray.length(); i++) {
                                JSONObject storeObject = dataArray.getJSONObject(i);

                                Store store = new Store();
                                store.setId(storeObject.getInt("id"));
                                store.setStoreName(storeObject.getString("store_name"));
                                store.setAddress(storeObject.getString("address"));
                                store.setCity(storeObject.getString("city"));
                                store.setGoogleMapLocation(storeObject.getString("google_map_location"));
                                store.setOpenTime(storeObject.getString("open_time"));
                                store.setCloseTime(storeObject.getString("close_time"));
                                store.setDistrict(storeObject.getString("district"));
                                store.setWard(storeObject.optString("ward")); // use optString in case of null value
                                store.setImage(storeObject.getString("image"));

                                stores.add(store);

                            }

                            renderStores();
                            addStoreEvents();


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

    private void getCartRequest() {
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//        String userId = user.getUid();
        String userId = Constants.TEMP_USER_ID;

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
                                cartItem.setSizeId(cartItemObject.getInt("size_id"));
                                cartItem.setSizeName(cartItemObject.getString("size_name"));
                                cartItem.setSizePrice(cartItemObject.getDouble("size_price"));

                                cartItem.setQuantity(cartItemObject.getInt("quantity"));
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


    //
    private void renderStores() {
        // Render to autocomplete text view
        String[] storeNames = new String[stores.size()];
        for (int i = 0; i < stores.size(); i++) {
            storeNames[i] = stores.get(i).getStoreName();
        }

        autoCompleteTextViewStore.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, storeNames));
    }

    private void renderTotalPrice() {

        for (Cart cartItem : cart) {
            totalPrice += cartItem.getOrderItemPrice();
        }

        textViewTotalPrice.setText(Utils.formatVNCurrency(totalPrice));
    }

    private void renderTotalPayment() {
        totalPayment = totalPrice + deliveryFee;

        if (currentVoucher != null) {
            totalPayment -= currentVoucher.getDiscount_price();
        }

        textViewTotalPayment.setText(Utils.formatVNCurrency(totalPayment));

    }

    private void renderCart() {
        recyclerView.hasFixedSize();
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        cartAdapter = new CartAdapter(cart);
        recyclerView.setAdapter(cartAdapter);

        renderTotalPrice();
        renderTotalPayment();

    }

    private void renderAppliedVoucher() {
        // Check if voucher is applied
        if (currentVoucher != null) {
            btnApplyVoucher.setVisibility(View.GONE);
            chipVoucher.setVisibility(View.VISIBLE);
            chipVoucher.setText("Giảm " + Utils.formatVNCurrency(currentVoucher.getDiscount_price()));
        } else {

            btnApplyVoucher.setVisibility(View.VISIBLE);
            chipVoucher.setVisibility(View.GONE);
            chipVoucher.setText("");
        }

        // render summary price
        renderTotalPayment();
    }


    private void createOrder() {
        btnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cart.size() == 0 || stores.size() == 0) {
                    return;
                }

                if (paymentMethod == "cash") {
                    createOrderWithCashRequest();
                } else if (paymentMethod == "momo") {

                } else {

                }
            }
        });

    }


    private void createOrderWithCashRequest() {
        String url = Constants.API_URL + "/order";
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        // String userId = user.getUid();
        String userId = Constants.TEMP_USER_ID;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(getApplicationContext(), "Đặt hàng thành công", Toast.LENGTH_SHORT).show();

                        // Send request to firebase realtime to create current order

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Đã xảy ra lỗi: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public byte[] getBody() {
                Map<String, Object> params = new HashMap<>();

                // Body params
                params.put("order_id", Utils.generateOrderId());
                params.put("user_id", userId);
                params.put("payment_method", paymentMethod);
                params.put("order_status", "Đang chờ");
                params.put("order_type", "online");
                params.put("shipping_cost", (int)deliveryFee);
                params.put("total_payment", (int)totalPayment);
                params.put("order_note", inputOrderNote.getText().toString());
                params.put("address", inputAddress.getText().toString());
                params.put("receiver_name", inputReceiverName.getText().toString());
                params.put("phone_number", inputPhoneNumber.getText().toString());
                params.put("store_id", selectedStore.getId());

                if (currentVoucher != null) {
                    params.put("voucher_id", currentVoucher.getId());
                }

                try {
                    JSONArray orderItems = new JSONArray();

                    for (Cart cartItem : cart) {
                        JSONObject orderItemObject = new JSONObject();
                        orderItemObject.put("id", cartItem.getId());
                        orderItemObject.put("product_id", cartItem.getProductId());
                        orderItemObject.put("quantity", cartItem.getQuantity());
                        orderItemObject.put("product_name", cartItem.getProductName());
                        orderItemObject.put("product_price", cartItem.getProductPrice());
                        orderItemObject.put("product_status", cartItem.getProductStatus());
                        orderItemObject.put("product_image", cartItem.getProductImage());
                        orderItemObject.put("size_id", cartItem.getSizeId());
                        orderItemObject.put("size_name", cartItem.getSizeName());
                        orderItemObject.put("size_price", cartItem.getSizePrice());
                        orderItemObject.put("order_item_price", cartItem.getOrderItemPrice());

                        // Add toppings if available
                        if (cartItem.getToppings() != null) {
                            JSONArray toppingsArray = new JSONArray();
                            for (Cart.CartTopping topping : cartItem.getToppings()) {
                                JSONObject toppingObject = new JSONObject();
                                toppingObject.put("topping_storage_id", topping.getToppingStorageId());
                                toppingObject.put("topping_id", topping.getToppingId());
                                toppingObject.put("topping_name", topping.getToppingName());
                                toppingObject.put("topping_price", topping.getToppingPrice());
                                toppingsArray.put(toppingObject);
                            }
                            orderItemObject.put("toppings", toppingsArray);
                        }

                        orderItems.put(orderItemObject);
                    }

                    params.put("order_items", orderItems);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return new JSONObject(params).toString().getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }
        };

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(jsonObjectRequest);
    }


    @Override
    public void onApplyVoucher(Voucher voucher) {
        Toast.makeText(getApplicationContext(), "Voucher applied", Toast.LENGTH_SHORT).show();
        // Apply voucher here
        // Implement apply voucher logic here
        this.currentVoucher = voucher;
        renderAppliedVoucher();
    }

}
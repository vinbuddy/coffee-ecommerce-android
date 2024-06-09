package com.coffee.app.ui.store;

import android.content.Intent;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.coffee.app.R;
import com.coffee.app.model.Product;
import com.coffee.app.model.Store;
import com.coffee.app.shared.Constants;
import com.coffee.app.ui.cart.CartActivity;
import com.coffee.app.ui.menu.ProductAdapter;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class StoreFragment extends Fragment {

    RecyclerView recyclerView;
    StoreAdapter storeAdapter;
    Spinner storeLocationSpinner;
    TextInputEditText searchStoreInput;

    TextView textViewCartBadge;

    View rootView;

    ImageButton btnCart;

    ShimmerFrameLayout skeletonLayout;

    ArrayList<Store> stores = new ArrayList<>();

    public StoreFragment() {
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
        rootView = inflater.inflate(R.layout.fragment_store, container, false);
        addControls();

        skeletonLayout.startShimmer();

        getStoresRequest();
        getTotalCartItemsRequest();


        return rootView;
    }

    private void addControls() {
        // Add controls here
        recyclerView = rootView.findViewById(R.id.storeRecyclerView);
        recyclerView.hasFixedSize();
        recyclerView.addItemDecoration( new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator( new DefaultItemAnimator());

        storeLocationSpinner = rootView.findViewById(R.id.storeLocationSpinner);
        searchStoreInput = rootView.findViewById(R.id.searchStoreInput);

        textViewCartBadge = rootView.findViewById(R.id.textViewCartBadge);
        btnCart = rootView.findViewById(R.id.btnCart);
        skeletonLayout = rootView.findViewById(R.id.skeletonLayout);
    }

    private void addEvents() {
        // Add events here
        searchStores();
        selectStoreLocation();
        showStoreDetailBottomSheet();
        navigateToCartActivity();
    }

    private void navigateToCartActivity() {
        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CartActivity.class);
                startActivity(intent);
            }
        });
    }

    private void getStoresRequest() {
        // Get stores request here
        String url = Constants.API_URL + "/store";

        RequestQueue queue = Volley.newRequestQueue(getContext());

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
                            addEvents();


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

    private void getTotalCartItemsRequest() {
        RequestQueue queue = Volley.newRequestQueue(getActivity());

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            return;
        }
                String userId = user.getUid();
        //String userId = Constants.TEMP_USER_ID;

        String url = Constants.API_URL + "/cart/total/" + userId;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    // Handle the response
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        int totalItems = jsonObject.getInt("data");
                        textViewCartBadge.setText(String.valueOf(totalItems));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }, error -> {
            Toast.makeText(getActivity(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
        });

        queue.add(stringRequest);
    }

    private ArrayList<Store> getStoresInSelectedLocation() {
        ArrayList<Store> storesInSelectedLocation = stores.stream().filter(store -> store.getCity().equals(storeLocationSpinner.getSelectedItem().toString())).collect(Collectors.toCollection(ArrayList::new));
        return storesInSelectedLocation;
    }

    private void renderStores() {

        initStoreLocationToSpinner();

        storeAdapter = new StoreAdapter(stores);
        recyclerView.setAdapter(storeAdapter);


        skeletonLayout.startShimmer();
        skeletonLayout.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    private void initStoreLocationToSpinner() {
        // Get unique cities from stores
        ArrayList<String> locations = new ArrayList<>();
        for (Store store : stores) {
            if (!locations.contains(store.getCity())) {
                locations.add(store.getCity());
            }
        }

        storeLocationSpinner.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, locations));
    }



    private void searchStores() {
        searchStoreInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                ArrayList<Store> storesInSelectedLocation = stores.stream().filter(store -> store.getCity().equals(storeLocationSpinner.getSelectedItem().toString())).collect(Collectors.toCollection(ArrayList::new));
                storeAdapter.setStores(storesInSelectedLocation);

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String name = s.toString();
                storeAdapter.filterStoreByName(name);
            }
        });
    }


    private void selectStoreLocation() {
        storeLocationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                String location = storeLocationSpinner.getSelectedItem().toString();


                if (position == 0) {
                    storeAdapter.setStores(getStoresInSelectedLocation());
                } else {
                    storeAdapter.filterStoreByLocation(location);

                }

                storeAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
                //storeAdapter.filterStoreByLocation(storeLocationSpinner.getSelectedItem().toString());
            }
        });
    }

    private void showStoreDetailBottomSheet() {
        storeAdapter.setOnItemClickListener(new StoreAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Store store) {
                StoreDetailBottomSheetFragment storeDetailBottomSheetFragment = new StoreDetailBottomSheetFragment(store);
                storeDetailBottomSheetFragment.show(getParentFragmentManager(), storeDetailBottomSheetFragment.getTag());
            }
        });
    }
}
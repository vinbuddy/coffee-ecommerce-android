package com.coffee.app.ui.voucher;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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
import com.coffee.app.model.Voucher;
import com.coffee.app.shared.Constants;
import com.coffee.app.shared.VolleySingleon;
import com.coffee.app.ui.cart.CartActivity;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class VoucherFragment extends Fragment {

    RecyclerView recyclerView;
    List<Voucher> listVoucher = new ArrayList<>();
    VoucherAdapter voucherAdapter;
    RequestQueue requestQueue;
    View rootView;
    TextView textViewCartBadge;
    ImageButton btnCart;

    ShimmerFrameLayout skeletonLayout;

    public VoucherFragment() {
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
        rootView = inflater.inflate(R.layout.fragment_voucher, container, false);

        addControls();

        skeletonLayout.startShimmer();

        getTotalCartItemsRequest();

        addEvents();
        fetchVouchers();

        return rootView;
    }

    private void addControls() {
        skeletonLayout = rootView.findViewById(R.id.skeletonLayout);
        textViewCartBadge = rootView.findViewById(R.id.textViewCartBadge);
        btnCart = rootView.findViewById(R.id.btnCart);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.voucherRecyclerView);
        recyclerView.hasFixedSize();
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        requestQueue = VolleySingleon.getmInstance(getContext()).getRequestQueue();
    }

    void fetchVouchers() {
        String url = Constants.API_URL + "/voucher";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            listVoucher.clear();

                            JSONArray dataArray = response.getJSONArray("data");


                            for (int i = 0; i < dataArray.length(); i++) {
                                JSONObject voucherObj = dataArray.getJSONObject(i);
                                int id = voucherObj.getInt("id");
                                String voucher_name = voucherObj.getString("voucher_name");
                                String description = voucherObj.getString("description");
                                String start_date = voucherObj.getString("start_date");
                                String end_date = voucherObj.getString("end_date");
                                String img = voucherObj.getString("image");
                                double discount_price = Double.parseDouble(voucherObj.getString("discount_price"));
                                String discount_type = voucherObj.getString("discount_type");
                                double min_order_price = Double.parseDouble(voucherObj.getString("min_order_price"));
                                Voucher voucher = new Voucher(id, voucher_name, description, start_date, end_date, img, discount_price, discount_type, min_order_price);

                                listVoucher.add(voucher);
                            }

                            voucherAdapter = new VoucherAdapter(getContext(), listVoucher);
                            showVoucherDetail();
                            recyclerView.setAdapter(voucherAdapter);

                            skeletonLayout.stopShimmer();
                            skeletonLayout.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);


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


        requestQueue.add(jsonObjectRequest);
    }

    private void addEvents() {
       btnCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), CartActivity.class);
                    startActivity(intent);
                }
            });

    }
    void showVoucherDetail() {
        voucherAdapter.setOnItemClickListener(new VoucherAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Voucher voucher) {
                VoucherDetailBottomSheetFragment voucherDetailBottomSheetFragment = new VoucherDetailBottomSheetFragment(voucher);
                voucherDetailBottomSheetFragment.show(getChildFragmentManager(), voucherDetailBottomSheetFragment.getTag());
            }
        });
    }

    private void getTotalCartItemsRequest() {
        RequestQueue queue = Volley.newRequestQueue(getActivity());

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        //        String userId = user.getUid();
        String userId = Constants.TEMP_USER_ID;

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
}
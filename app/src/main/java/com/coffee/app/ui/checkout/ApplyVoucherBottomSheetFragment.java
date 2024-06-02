package com.coffee.app.ui.checkout;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.coffee.app.R;
import com.coffee.app.model.Voucher;
import com.coffee.app.shared.Constants;
import com.coffee.app.shared.VolleySingleon;
import com.coffee.app.ui.menu.CategoryBottomSheetFragment;
import com.coffee.app.ui.voucher.VoucherAdapter;
import com.coffee.app.ui.voucher.VoucherDetailBottomSheetFragment;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class ApplyVoucherBottomSheetFragment extends BottomSheetDialogFragment {

    BottomSheetDialog dialog;
    View rootView;
    RecyclerView recyclerView;
    ApplyVoucherAdapter applyVoucherAdapter;

    RequestQueue requestQueue;

    ImageButton btnClose;

    List<Voucher> listVoucher = new ArrayList<>();

    public ApplyVoucherBottomSheetFragment() {
        // Required empty public constructor
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);

        Window window = dialog.getWindow();
        window.setBackgroundDrawableResource(R.color.overlay);

        return dialog;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView =  inflater.inflate(R.layout.fragment_apply_voucher_bottom_sheet, container, false);

        addControls();
        fetchVouchers();
        addEvents();

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Set min height to parent view
        CoordinatorLayout bottomSheetLayout = dialog.findViewById(R.id.applyVoucherBottomSheetLayout);
        if (bottomSheetLayout != null) {
            // Set a temporary height for the BottomSheet
            View parentView = (View) view.getParent();
            DisplayMetrics displayMetrics = new DisplayMetrics();
            requireActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int screenHeight = displayMetrics.heightPixels;
            parentView.getLayoutParams().height = (int) (screenHeight / (1.5));
            parentView.requestLayout();
        }
    }

    private void addControls() {
        recyclerView = (RecyclerView) rootView.findViewById(R.id.voucherRecyclerView);
        recyclerView.hasFixedSize();
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        requestQueue = VolleySingleon.getmInstance(getContext()).getRequestQueue();

        btnClose = rootView.findViewById(R.id.btnClose);
    }

    private void fetchVouchers() {
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

                            applyVoucherAdapter = new ApplyVoucherAdapter(getContext(), listVoucher);
                            showVoucherDetail();
                            applyVoucher();
                            recyclerView.setAdapter(applyVoucherAdapter);


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

    private void applyVoucher() {
        applyVoucherAdapter.setOnApplyVoucherClickListener(new ApplyVoucherAdapter.OnApplyVoucherClickListener() {
            @Override
            public void onItemClick(Voucher voucher) {
//                Toast.makeText(getContext(), voucher.getVoucher_name(), Toast.LENGTH_SHORT).show();
                applyVoucherListener.onApplyVoucher(voucher);
                dismiss();
            }
        });
    }
    private void addEvents() {
        closeBottomSheetEvent();

    }
    void showVoucherDetail() {
        applyVoucherAdapter.setOnItemClickListener(new ApplyVoucherAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Voucher voucher) {
                VoucherDetailBottomSheetFragment voucherDetailBottomSheetFragment = new VoucherDetailBottomSheetFragment(voucher);
                voucherDetailBottomSheetFragment.show(getChildFragmentManager(), voucherDetailBottomSheetFragment.getTag());
            }
        });
    }

    void closeBottomSheetEvent() {
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }



    public interface OnApplyVoucherListener {
        void onApplyVoucher(Voucher voucher);
    }

    private OnApplyVoucherListener applyVoucherListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            applyVoucherListener = (OnApplyVoucherListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException("Parent fragment must implement OnItemClickListener");
        }
    }

}
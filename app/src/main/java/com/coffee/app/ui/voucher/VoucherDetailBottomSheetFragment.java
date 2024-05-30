package com.coffee.app.ui.voucher;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.coffee.app.MainActivity;
import com.coffee.app.R;
import com.coffee.app.model.Voucher;
import com.coffee.app.shared.Utils;
import com.coffee.app.shared.VolleySingleon;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class VoucherDetailBottomSheetFragment extends BottomSheetDialogFragment {

    Voucher voucher;
    TextView VoucherNameDetail, start_date_detail, end_date_detail, discount_price_detail, min_order_price_detail, voucherDescription;
    MaterialButton closeVoucherDetailBtn;
    Button btn_open_menu;
    BottomSheetDialog dialog;
    BottomSheetBehavior<View> bottomSheetBehavior;
    View rootView;
    RequestQueue requestQueue;

    public VoucherDetailBottomSheetFragment(Voucher voucher) {
        this.voucher = voucher;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_voucher_detail_bottom_sheet, container, false);
        closeVoucherDetailBtn = (MaterialButton)rootView.findViewById(R.id.closeVoucherDetailBtn);
        return  rootView;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bottomSheetBehavior = BottomSheetBehavior.from((View) view.getParent());
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        requestQueue = VolleySingleon.getmInstance(getContext()).getRequestQueue();
        addControls();
        fetchVoucher();
        addEvents();

        // Set min height to parent view
        CoordinatorLayout bottomSheetLayout = dialog.findViewById(R.id.VoucherDetailBottomSheetLayout);
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
    void addControls() {
        VoucherNameDetail = (TextView) rootView.findViewById(R.id.VoucherNameDetail);
        start_date_detail = (TextView) rootView.findViewById(R.id.start_date_detail);
        end_date_detail = (TextView) rootView.findViewById(R.id.end_date_detail);
        discount_price_detail = (TextView) rootView.findViewById(R.id.discount_price_detail);
        min_order_price_detail = (TextView) rootView.findViewById(R.id.min_order_price_detail);
        voucherDescription = (TextView) rootView.findViewById(R.id.voucherDescription);
        btn_open_menu = (Button) rootView.findViewById(R.id.open_menu);
    }
    void fetchVoucher() {

        VoucherNameDetail.setText(voucher.getVoucher_name());

        String start_date = convertISO8601ToDateTime(voucher.getStart_date());
        start_date_detail.setText(start_date);

        String end_date = convertISO8601ToDateTime(voucher.getEnd_date());
        end_date_detail.setText(end_date);

        discount_price_detail.setText(Utils.formatVNCurrency(voucher.getDiscount_price()));
        min_order_price_detail.setText(Utils.formatVNCurrency(voucher.getMin_order_price()));
        voucherDescription.setText(voucher.getDescription());
    }
    public static String convertISO8601ToDateTime(String iso8601Date) {
        SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
        isoFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());

        try {
            Date date = isoFormat.parse(iso8601Date);
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
    private void addEvents() {
        closeBottomSheetEvent();
        openMenu();
    }
    private void closeBottomSheetEvent() {
        closeVoucherDetailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
    private void openMenu()
    {
        btn_open_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MainActivity.class);
                startActivity(intent);
            }
        });
    }
    void loadFragment(Fragment fragment, boolean isAppInit) {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if (isAppInit) {
            fragmentTransaction.add(R.id.frameLayout, fragment);
        } else {
            fragmentTransaction.replace(R.id.frameLayout, fragment);
        }

        fragmentTransaction.replace(R.id.frameLayout, fragment);

        fragmentTransaction.commit();
    }
}
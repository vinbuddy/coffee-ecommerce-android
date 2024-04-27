package com.coffee.app.ui.detail;

import android.app.Dialog;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.coffee.app.R;
import com.coffee.app.model.Category;
import com.coffee.app.model.Product;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

public class ProductDetailBottomSheetFragment extends BottomSheetDialogFragment {
    ArrayList<Product> products;
    BottomSheetDialog dialog;
    BottomSheetBehavior<View> bottomSheetBehavior;
    View rootView;
    MaterialButton closeDetailBtn;

    public ProductDetailBottomSheetFragment(ArrayList<Product> products) {
        this.products = products;
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

        addEvents();
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bottomSheetBehavior = BottomSheetBehavior.from((View)view.getParent());
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        // Set min height to parent view
        CoordinatorLayout bottomSheetLayout = dialog.findViewById(R.id.categoryBottomSheetLayout);
        if (bottomSheetLayout != null) {
            bottomSheetLayout.setMinimumHeight(Resources.getSystem().getDisplayMetrics().heightPixels);
        }


    }

    private void addEvents () {
        closeBottomSheetEvent();
    }

    private  void closeBottomSheetEvent() {
        closeDetailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
}

package com.coffee.app.ui.detail;

import android.app.Dialog;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.coffee.app.R;
import com.coffee.app.model.Category;
import com.coffee.app.model.Product;
import com.coffee.app.shared.Utils;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ProductDetailBottomSheetFragment extends BottomSheetDialogFragment {
    Product product;
    BottomSheetDialog dialog;
    BottomSheetBehavior<View> bottomSheetBehavior;
    View rootView;
    MaterialButton closeDetailBtn;
    TextView textViewName,textViewPrice,textViewDescription;
    ImageView imageProduct;

    public ProductDetailBottomSheetFragment(Product product) {
        this.product = product;
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

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bottomSheetBehavior = BottomSheetBehavior.from((View)view.getParent());
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        addControls();
        renderProduct();
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

    }


    private void addEvents () {
        closeBottomSheetEvent();
    }

    private void renderProduct() {
        textViewName.setText(product.getName());
        textViewPrice.setText(Utils.formatVNCurrency(product.getPrice()));
        textViewDescription.setText(product.getDescription());
        Picasso.get().load(product.getImage()).into(imageProduct);
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

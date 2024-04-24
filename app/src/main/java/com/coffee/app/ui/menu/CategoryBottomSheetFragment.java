package com.coffee.app.ui.menu;

import android.app.Dialog;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.coffee.app.R;
import com.coffee.app.model.Category;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;

public class CategoryBottomSheetFragment extends BottomSheetDialogFragment {
    ArrayList<Category> categories;
    BottomSheetDialog dialog;
    BottomSheetBehavior<View> bottomSheetBehavior;
    View rootView;
    TextView closeBottomSheet;

    public CategoryBottomSheetFragment(ArrayList<Category> categories) {
        this.categories = categories;
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
        rootView = inflater.inflate(R.layout.fragment_category_bottom_sheet, container, false);

        closeBottomSheet = (TextView) rootView.findViewById(R.id.closeBottomSheet);

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
//            Window window = dialog.getWindow();
//            window.setBackgroundDrawableResource(R.color.black);
//            bottomSheetBehavior.setPeekHeight((int) (0.9 * Resources.getSystem().getDisplayMetrics().heightPixels));
            bottomSheetLayout.setMinimumHeight(Resources.getSystem().getDisplayMetrics().heightPixels);
        }


    }

    private void addEvents () {
        closeBottomSheetEvent();
    }

    private  void closeBottomSheetEvent() {
        closeBottomSheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }



}

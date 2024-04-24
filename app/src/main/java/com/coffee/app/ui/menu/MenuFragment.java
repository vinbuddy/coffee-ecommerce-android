package com.coffee.app.ui.menu;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.coffee.app.R;
import com.coffee.app.model.Category;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;

public class MenuFragment extends Fragment {
    TextView showCategories;

    View rootView;

    public MenuFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_menu, container, false);
        addControls();
        addEvents();
        return rootView;
    }

    private void addControls() {
        showCategories = (TextView) rootView.findViewById(R.id.showCategories);
    }

    private void addEvents() {
        showCategoryBottomSheet();
    }

    private void showCategoryBottomSheet() {
        ArrayList<Category> categoryList = new ArrayList<>();
        showCategories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CategoryBottomSheetFragment bottomSheetFragment = new CategoryBottomSheetFragment(categoryList);
                bottomSheetFragment.show(getChildFragmentManager(), bottomSheetFragment.getTag());
            }
        });
    }
}
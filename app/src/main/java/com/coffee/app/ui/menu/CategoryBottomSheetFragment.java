package com.coffee.app.ui.menu;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.coffee.app.R;
import com.coffee.app.model.Category;
import com.coffee.app.shared.Constants;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CategoryBottomSheetFragment extends BottomSheetDialogFragment {
    ArrayList<Category> categories;
    BottomSheetDialog dialog;
    BottomSheetBehavior<View> bottomSheetBehavior;
    View rootView;
    TextView closeBottomSheet;
    RecyclerView recyclerView;
    RequestQueue requestQueue;
    ProductAdapter productAdapter;

    CategoryAdapter categoryAdapter ;

    public CategoryBottomSheetFragment(ArrayList<Category> categories) {
        this.categories = categories;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);

        Window window = dialog.getWindow();
        window.setBackgroundDrawableResource(R.color.overlay);


        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_category_bottom_sheet, container, false);


        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bottomSheetBehavior = BottomSheetBehavior.from((View)view.getParent());
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);


        closeBottomSheet = (TextView) view.findViewById(R.id.closeBottomSheet);

        recyclerView  = view.findViewById(R.id.categoryListView);
        recyclerView.hasFixedSize();
        recyclerView.addItemDecoration( new DividerItemDecoration(getContext(),LinearLayoutManager.VERTICAL));
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator( new DefaultItemAnimator());


        addEvents();
        getCategoriesRequest();

        // Set min height to parent view
        CoordinatorLayout bottomSheetLayout = dialog.findViewById(R.id.categoryBottomSheetLayout);


        if (bottomSheetLayout != null) {
            // Set a temporary height for the BottomSheet
            View parentView = (View) view.getParent();
            DisplayMetrics displayMetrics = new DisplayMetrics();
            requireActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int screenHeight = displayMetrics.heightPixels;
            parentView.getLayoutParams().height = screenHeight / 2;
            parentView.requestLayout();
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

    private void clickOnCategoryItem() {
        categoryAdapter.setOnItemClickListener(new CategoryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Category category) {
                categoryItemClickListener.onCategoryItemClick(category.id);
                dismiss();
            }
        });

    }

    private  void renderCategories() {
        categoryAdapter = new CategoryAdapter(categories);
        clickOnCategoryItem();
        recyclerView.setAdapter(categoryAdapter);


        // Update the height of the BottomSheet after data has been loaded
        View parentView = (View) rootView.getParent();
        parentView.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
        parentView.requestLayout();
    }

    private void getCategoriesRequest() {
        String url = Constants.API_URL + "/category";
        RequestQueue queue = Volley.newRequestQueue(getContext());



        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            categories.clear();
                            categories.add(new Category(0, "Tất cả"));

                            JSONArray dataArray = response.getJSONArray("data");


                            for (int i = 0; i < dataArray.length(); i++) {
                                JSONObject categoryObject = dataArray.getJSONObject(i);

                                int id = categoryObject.getInt("id");
                                String name = categoryObject.getString("category_name");
                                Category category = new Category(id, name);
                                categories.add(category);

                            }

                            renderCategories();



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


    public interface OnCategoryItemClickListener {
        void onCategoryItemClick(int categoryId);
    }

    private OnCategoryItemClickListener categoryItemClickListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            categoryItemClickListener = (OnCategoryItemClickListener) getParentFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException("Parent fragment must implement OnItemClickListener");
        }
    }



}

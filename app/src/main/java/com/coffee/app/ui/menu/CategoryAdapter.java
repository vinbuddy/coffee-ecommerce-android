package com.coffee.app.ui.menu;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.coffee.app.R;
import com.coffee.app.model.Category;

import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.MyViewHolder> {
    private static List<Category> categories;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(Category category);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        CoordinatorLayout coordinatorLayout;
        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.categoryName);
            coordinatorLayout = (CoordinatorLayout) view.findViewById(R.id.categoryBottomSheetLayout);
        }

    }

    public CategoryAdapter(List<Category> categories) {
        this.categories = categories;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from((parent.getContext())).inflate(R.layout.category_list_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Category category = categories.get(position);

        holder.name.setText(category.getCategory_name());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(category);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return categories.size();

    }

    public static List<Category> filterData(Category clickedCategory) {
        List<Category> filteredCategories = new ArrayList<>();
        for (Category category : categories) {
            if (category.getId() == clickedCategory.getId()) {
                filteredCategories.add(category);
            }
        }
        return filteredCategories;
    }
}
package com.coffee.app.ui.detail;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.coffee.app.R;
import com.coffee.app.model.Product;
import com.coffee.app.model.ProductSize;
import com.coffee.app.model.ProductTopping;
import com.coffee.app.shared.Utils;
import com.coffee.app.ui.home.CoffeeProductCardGridAdapter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ProductToppingAdapter extends RecyclerView.Adapter<ProductToppingAdapter.MyViewHolder> {
    Context context;
    ArrayList<ProductTopping> productToppings;
    ArrayList<ProductTopping> selectedTopings = new ArrayList<>();

    LayoutInflater inflater;

    private OnToppingItemClickListener onToppingItemClickListener;

    public interface OnToppingItemClickListener {
        void onToppingItemClick(ArrayList<ProductTopping> selectedTopings);
    }



    public ProductToppingAdapter(ArrayList<ProductTopping> productToppings, OnToppingItemClickListener onToppingItemClickListener) {
        this.productToppings = productToppings != null ? productToppings : new ArrayList<>();
        this.onToppingItemClickListener = onToppingItemClickListener;
    }

    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_toppings_item, parent, false);
        return new MyViewHolder(view);
    }

    public int getItemCount() {
        return productToppings.size();
    }

    public void onBindViewHolder(MyViewHolder holder, int position) {
        if (productToppings.isEmpty()) {
            return;
        }
        ProductTopping productTopping = productToppings.get(position);

        holder.textViewToppingPrice.setText(Utils.formatVNCurrency(productTopping.getToppingPrice()));
        holder.checkBoxToppingName.setText(productTopping.getToppingName());

        holder.checkBoxToppingName.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                selectedTopings.add(productTopping);
            } else {
                selectedTopings.remove(position);
            }

            onToppingItemClickListener.onToppingItemClick(selectedTopings);
        });
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textViewToppingPrice;
        CheckBox checkBoxToppingName;

        public MyViewHolder(View itemView) {
            super(itemView);

            textViewToppingPrice = itemView.findViewById(R.id.textViewToppingPrice);
            checkBoxToppingName =  itemView.findViewById(R.id.checkBoxToppingName);
        }
    }

}


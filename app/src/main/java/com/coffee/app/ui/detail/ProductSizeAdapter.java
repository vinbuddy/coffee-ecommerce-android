package com.coffee.app.ui.detail;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.coffee.app.R;
import com.coffee.app.model.Product;
import com.coffee.app.model.ProductSize;
import com.coffee.app.shared.Utils;
import com.coffee.app.ui.home.CoffeeProductCardGridAdapter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ProductSizeAdapter extends RecyclerView.Adapter<ProductSizeAdapter.MyViewHolder> {
    Context context;
    ArrayList<ProductSize> productSizes;

    LayoutInflater inflater;



    public ProductSizeAdapter(ArrayList<ProductSize> productSizes) {
        this.productSizes = productSizes;
    }

    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_sizes_item, parent, false);
        return new MyViewHolder(view);
    }

    public int getItemCount() {
        return productSizes.size();
    }

    public void onBindViewHolder(MyViewHolder holder, int position) {
        ProductSize productSize = productSizes.get(position);

        holder.textViewSizePrice.setText(Utils.formatVNCurrency(productSize.getSizePrice()));
        holder.radioButtonSizeName.setText(productSize.getSizeName());

//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (onItemClickListener != null) {
//                    onItemClickListener.onItemClick(product);
//                }
//            }
//        });
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textViewSizePrice;
        RadioButton radioButtonSizeName;

        public MyViewHolder(View itemView) {
            super(itemView);

            textViewSizePrice = itemView.findViewById(R.id.textViewSizePrice);
            radioButtonSizeName = (RadioButton) itemView.findViewById(R.id.radioBtnSizeName);
        }
    }

}


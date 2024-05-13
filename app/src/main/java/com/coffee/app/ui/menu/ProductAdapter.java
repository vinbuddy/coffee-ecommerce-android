package com.coffee.app.ui.menu;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.coffee.app.R;
import com.coffee.app.model.Product;
import com.coffee.app.shared.Utils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.MyViewHolder> {
    private static List<Product> products;
    Context context;
    private ProductAdapter.OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(Product product);
    }

    public void setOnItemClickListener(ProductAdapter.OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public  TextView price;
        public ImageView image;

        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.textViewName);
            image =(ImageView) view.findViewById(R.id.imageProduct);
            price =(TextView) view.findViewById(R.id.textViewPrice);
        }


    }

    public ProductAdapter(Context context , List<Product> categories) {
        this.products = categories;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from((parent.getContext())).inflate(R.layout.product_card_list_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Product  product= products.get(position);
        holder.name.setText(product.getName());
        holder.price.setText(Utils.formatVNCurrency(product.getPrice()));

        Picasso.get().load(product.getImage()).into(holder.image);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(product);
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return products.size();

    }

    public static List<Product> getListProduct() {
        return products;
    }

    public static void setProductList(List<Product> listProduct) {
        ProductAdapter.products = listProduct;
    }

    public static void filter(String text) {
        List<Product> filteredList = new ArrayList<>();
        for (Product product : products) {
            if (product.getName().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(product);
            }
        }
        filterList(filteredList);
    }

    public static void filterCategory(int id) {
        List<Product> filteredList = new ArrayList<>();
        for (Product product : products) {
            if (product.getCategory_id() == id) {
                filteredList.add(product);
            }
        }
        filterList(filteredList);
    }


    public static void filterList(List<Product> filteredList) {
        products = filteredList;
    }

}

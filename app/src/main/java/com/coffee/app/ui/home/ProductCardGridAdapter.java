package com.coffee.app.ui.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.coffee.app.R;
import com.coffee.app.model.Product;

import java.util.ArrayList;

public class ProductCardGridAdapter extends BaseAdapter {
    Context context;
    ArrayList<Product> productList;

    LayoutInflater inflater;

    public ProductCardGridAdapter(Context context, ArrayList<Product> productList) {
        this.context = context;
        this.productList = productList;
    }

    @Override
    public int getCount() {
        return productList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (view == null){

            view = inflater.inflate(R.layout.product_card_grid_item,null);

        }
        Product product = productList.get(position);

        ImageView imageView = view.findViewById(R.id.productGridImage);
        TextView textView = view.findViewById(R.id.productGridName);

//        imageView.setImageResource(flag.getImage());
//        textView.setText(flag.getCountry());

        return view;
    }
}

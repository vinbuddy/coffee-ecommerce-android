package com.coffee.app.ui.wishlist;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.coffee.app.R;
import com.coffee.app.model.Product;
import com.coffee.app.shared.Constants;
import com.google.android.material.button.MaterialButton;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WishlistAdapter extends RecyclerView.Adapter<WishlistAdapter.MyViewHolder> {
    private static List<Product> products;
    Context context;
    private WishlistAdapter.OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(Product product);

    }

    public void setOnItemClickListener(WishlistAdapter.OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public  TextView price;
        public ImageView image;
        public MaterialButton btnRemove;

        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.tvHeading);
            image =(ImageView) view.findViewById(R.id.title_image);
            price =(TextView) view.findViewById(R.id.tvPrice);
            btnRemove= (MaterialButton) view.findViewById(R.id.btnRemove);
        }


    }

    public WishlistAdapter(Context context ,List<Product> categories) {
        this.products = categories;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from((parent.getContext())).inflate(R.layout.product_card_wishlist, parent, false);
        return new MyViewHolder(itemView);
    }

    @SuppressLint("RecyclerView")
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Product  product= products.get(position);

        holder.name.setText(product.getName());
        holder.price.setText(String.valueOf(product.getPrice()));
        Picasso.get().load(product.getImage()).into(holder.image);

        holder.btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = Constants.API_URL + "/wishlist/" + product.getId();

                RequestQueue queue = Volley.newRequestQueue(context);

                StringRequest request = new StringRequest(Request.Method.DELETE, url, new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        products.remove(position);
                        notifyDataSetChanged();
                        Toast.makeText(context, "Product removed from wishlist", Toast.LENGTH_SHORT).show();
                    }
                }, new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

                queue.add(request);

            }

        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(product);
                    Toast.makeText(v.getContext(), "Size " + product.getName(),Toast.LENGTH_SHORT).show();
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







}
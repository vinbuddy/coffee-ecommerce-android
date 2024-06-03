package com.coffee.app.ui.cart;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.coffee.app.R;
import com.coffee.app.model.Cart;
import com.coffee.app.shared.Utils;
import com.google.android.material.button.MaterialButton;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.MyViewHolder> {

    ArrayList<Cart> cart;

    public CartAdapter(ArrayList<Cart> cart) {
        this.cart = cart;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cart_list_item, parent, false);

        return new MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Cart cartItem = cart.get(position);

        holder.name.setText(cartItem.getProductName());
        holder.price.setText(Utils.formatVNCurrency(cartItem.getOrderItemPrice()));

        if (cartItem.getSizeName() == null) {
            holder.quantitySize.setText("x" + cartItem.getQuantity());
        } else {
            holder.quantitySize.setText("x" + cartItem.getQuantity() + ", " + cartItem.getSizeName());
        }

        //holder.quantitySize.setText("x" + cartItem.getQuantity() + ", " + cartItem.getSizeName());
        Picasso.get().load(cartItem.getProductImage()).into(holder.image);

        if(cartItem.getToppings() != null && cartItem.getToppings().size() > 0){
            String toppings = "";

            for(int i = 0; i < cartItem.getToppings().size(); i++){
                toppings += cartItem.getToppings().get(i).getToppingName();
                if(i != cartItem.getToppings().size() - 1){
                    toppings += ", ";
                }
            }
            holder.toppings.setText(toppings);

        } else{
            holder.toppings.setText("");
        }

    }

    @Override
    public int getItemCount() {
        return cart.size();
    }




    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public  TextView price;
        public TextView quantitySize;
        public  TextView toppings;
        public ImageView image;
        public MaterialButton btnRemove;

        public MyViewHolder(View view) {
            super(view);

            name = (TextView) view.findViewById(R.id.textViewName);
            image =(ImageView) view.findViewById(R.id.imageProduct);
            price =(TextView) view.findViewById(R.id.textViewPrice);
            quantitySize =(TextView) view.findViewById(R.id.textViewQuantitySize);
            toppings =(TextView) view.findViewById(R.id.textViewToppings);


            btnRemove= (MaterialButton) view.findViewById(R.id.btnRemove);
        }


    }
}

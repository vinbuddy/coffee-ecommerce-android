package com.coffee.app.ui.order;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.coffee.app.R;
import com.coffee.app.model.Cart;
import com.coffee.app.model.Order;
import com.coffee.app.shared.Utils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.MyViewHolder> {

    ArrayList<Order> orders;

    public OrderAdapter(ArrayList<Order> orders) {
        this.orders = orders;
    }

    @Override
    public OrderAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.order_card_list_item, parent, false);

        return new OrderAdapter.MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(OrderAdapter.MyViewHolder holder, int position) {
        Order order = orders.get(position);

        holder.textViewOrderId.setText("#" +order.getId());
        holder.textViewOrderDate.setText("Ngày đặt: " + Utils.formatDateTimeISO8601(order.getOrderDate()));
        holder.textViewTotalPayment.setText("Tổng tiền: " + Utils.formatVNCurrency(order.getTotalPayment()));
        holder.chipOrderStatus.setText(order.getOrderStatus());


        // Set color for chip
        if (order.getOrderStatus().equals("Hoàn thành")) {
            String successBgColor = "#a2e9c1";
            String successColor = "#17c964";
            // set chip bg transparent
            holder.chipOrderStatus.setChipBackgroundColor(ColorStateList.valueOf(Color.TRANSPARENT));
            holder.chipOrderStatus.setChipStrokeColor(ColorStateList.valueOf(Color.parseColor(successColor)));
            holder.chipOrderStatus.setTextColor(Color.parseColor(successColor));
        } else if (order.getOrderStatus().equals("Đã hủy")) {
            String dangerBgColor = "#faa0bf";
            String dangerColor = "#f31260";
            holder.chipOrderStatus.setChipBackgroundColor(ColorStateList.valueOf(Color.TRANSPARENT));
            holder.chipOrderStatus.setChipStrokeColor(ColorStateList.valueOf(Color.parseColor(dangerColor)));
            holder.chipOrderStatus.setTextColor(Color.parseColor(dangerColor));
        }


    }

    @Override
    public int getItemCount() {
        return orders.size();
    }




    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textViewOrderId, textViewOrderDate, textViewTotalPayment;
        Chip chipOrderStatus;

        public MyViewHolder(View view) {
            super(view);
            textViewOrderId = view.findViewById(R.id.textViewOrderId);
            textViewOrderDate = view.findViewById(R.id.textViewOrderDate);
            textViewTotalPayment = view.findViewById(R.id.textViewTotalPayment);
            chipOrderStatus = view.findViewById(R.id.chipOrderStatus);

        }


    }
}

package com.coffee.app.ui.checkout;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.coffee.app.R;
import com.coffee.app.model.Voucher;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class ApplyVoucherAdapter extends RecyclerView.Adapter<ApplyVoucherAdapter.MyViewHolder> {
    private static List<Voucher> listVoucher;
    Context context;

    private ApplyVoucherAdapter.OnItemClickListener onItemClickListener;

    private ApplyVoucherAdapter.OnApplyVoucherClickListener onApplyVoucherClickListener;
    public interface OnItemClickListener {
        void onItemClick(Voucher voucher);
    }

    public interface OnApplyVoucherClickListener {
        void onItemClick(Voucher voucher);
    }

    public void setOnItemClickListener(ApplyVoucherAdapter.OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public void setOnApplyVoucherClickListener(ApplyVoucherAdapter.OnApplyVoucherClickListener listener) {
        this.onApplyVoucherClickListener = listener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView voucher_name;
        public TextView end_date;
        public ImageView image;
        public Button btnApply;

        public MyViewHolder(View view) {
            super(view);
            image = (ImageView) view.findViewById(R.id.imageVoucher);
            voucher_name = (TextView) view.findViewById(R.id.tv_name_cus_voucher);
            end_date = (TextView) view.findViewById(R.id.tv_exp_cus_voucher);
            btnApply = (Button) view.findViewById(R.id.btnApplyVoucher);
        }
    }

    public ApplyVoucherAdapter(Context context, List<Voucher> vouchers) {
        this.listVoucher = vouchers;
        this.context = context;
    }

    @NonNull
    @Override
    public ApplyVoucherAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from((parent.getContext())).inflate(R.layout.apply_voucher_list_item, parent, false);
        return new ApplyVoucherAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ApplyVoucherAdapter.MyViewHolder holder, int position) {
        Voucher voucher = listVoucher.get(position);
        holder.voucher_name.setText(voucher.getVoucher_name());

        String end_date = convertISO8601ToDateTime(voucher.getEnd_date());
        holder.end_date.setText(end_date);
        Picasso.get().load(voucher.getImage()).into(holder.image);
        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(voucher);
                }
            }
        });

        holder.btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onApplyVoucherClickListener != null) {
                    onApplyVoucherClickListener.onItemClick(voucher);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return listVoucher.size();
    }
    public static List<Voucher> getListVoucher() {
        return listVoucher;
    }

    public static void setListVoucher(List<Voucher> listVoucher) {
        ApplyVoucherAdapter.listVoucher = listVoucher;
    }
    public static String convertISO8601ToDateTime(String iso8601Date) {
        SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
        isoFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());

        try {
            Date date = isoFormat.parse(iso8601Date);
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

}

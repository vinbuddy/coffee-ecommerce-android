package com.coffee.app.ui.store;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.coffee.app.R;
import com.coffee.app.model.Product;
import com.coffee.app.model.Store;
import com.coffee.app.ui.menu.ProductAdapter;
import com.google.android.material.imageview.ShapeableImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class StoreAdapter extends RecyclerView.Adapter<StoreAdapter.StoreViewHolder> {

    private static ArrayList<Store> stores;
    private static ArrayList<Store> allStores;

    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(Store store);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }


    public StoreAdapter(ArrayList<Store> stores) {
        this.stores = stores;
        this.allStores = new ArrayList<>(stores); // Update the copy as well
        notifyDataSetChanged();

    }

    @NonNull
    @Override
    public StoreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.store_card_list_item, parent, false);
        return new StoreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StoreViewHolder holder, int position) {
        Store store = stores.get(position);

        Picasso.get().load(store.getImage()).into(holder.imageStore);
        holder.storeName.setText(store.getStoreName());

        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(store);
            }
        });
    }

    @Override
    public int getItemCount() {
        return stores.size();
    }

    static class StoreViewHolder extends RecyclerView.ViewHolder {
        TextView storeName;
        ShapeableImageView imageStore;

        public StoreViewHolder(@NonNull View itemView) {
            super(itemView);
            imageStore = itemView.findViewById(R.id.imageStore);
            storeName = itemView.findViewById(R.id.textViewStoreName);
        }

    }

    public void setStores(ArrayList<Store> _stores) {
        stores = _stores;
    }

    public void filterStoreByName(String name) {
        ArrayList<Store> filteredStores = new ArrayList<>();
        for (Store store : allStores) {
            if (store.getStoreName().toLowerCase().contains(name.toLowerCase())) {
                filteredStores.add(store);
            }
        }
        filterList(filteredStores);
    }

    public void filterStoreByLocation(String location) {
        ArrayList<Store> storesInSelectedLocation = allStores.stream().filter(store -> store.getCity().equals(location)).collect(Collectors.toCollection(ArrayList::new));
        filterList(storesInSelectedLocation);
    }

    public void filterList(ArrayList<Store> filteredList) {
        stores = filteredList;
        notifyDataSetChanged();

    }
}
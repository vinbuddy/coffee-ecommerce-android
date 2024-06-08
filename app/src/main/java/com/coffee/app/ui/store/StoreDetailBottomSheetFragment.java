package com.coffee.app.ui.store;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.coffee.app.R;
import com.coffee.app.model.Store;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.squareup.picasso.Picasso;

public class StoreDetailBottomSheetFragment extends BottomSheetDialogFragment {

    BottomSheetDialog dialog;
    MaterialButton closeDetailBtn;

    ImageView imageStore;

    TextView textViewName, textViewAddress, textViewTime;

    Button btnViewMap;
    View rootView;

    Store store;

    public StoreDetailBottomSheetFragment(Store store) {
        // Required empty public constructor
        this.store = store;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        Window window = dialog.getWindow();
        window.setBackgroundDrawableResource(R.color.overlay);
        return dialog;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_store_detail_bottom_sheet, container, false);

        closeDetailBtn = (MaterialButton) rootView.findViewById(R.id.closeDetailBtn);
        textViewName = (TextView) rootView.findViewById(R.id.textViewName);
        textViewAddress = (TextView) rootView.findViewById(R.id.textViewAddress);
        textViewTime = (TextView) rootView.findViewById(R.id.textViewTime);
        btnViewMap = (Button) rootView.findViewById(R.id.btnViewMap);
        imageStore = (ImageView) rootView.findViewById(R.id.imageStore);

        renderStoreDetail();
        addEvents();

        return rootView;
    }

    private void addEvents() {
        closeDetailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnViewMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open map
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(store.getGoogleMapLocation()));
                startActivity(intent);
            }
        });
    }

    private void renderStoreDetail() {
        // Render store detail

        Picasso.get().load(store.getImage()).into(imageStore);
        textViewName.setText(store.getStoreName());
        textViewAddress.setText("Địa chỉ: " + store.getAddress());
        textViewTime.setText("Giờ mở cửa: " + store.getOpenTime() + " - " + store.getCloseTime());

    }
}
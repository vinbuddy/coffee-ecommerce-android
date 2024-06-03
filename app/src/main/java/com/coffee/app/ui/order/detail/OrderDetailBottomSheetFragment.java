package com.coffee.app.ui.order.detail;

import android.app.Dialog;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.coffee.app.R;
import com.coffee.app.model.Cart;
import com.coffee.app.model.Order;
import com.coffee.app.shared.Utils;
import com.coffee.app.shared.VolleySingleon;
import com.coffee.app.ui.cart.CartAdapter;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;
import com.squareup.picasso.Picasso;


public class OrderDetailBottomSheetFragment extends BottomSheetDialogFragment {

    BottomSheetDialog dialog;
    View rootView;
    RecyclerView recyclerView;

    ImageButton btnClose;

    ImageView imageAvatar;
    TextView textViewOrderId, textViewOrderDate, textViewPaymentMethod, textViewUserName, textViewEmail, textViewAddress, textViewPhoneNumber, textViewReceiverName, textViewOrderNote, textViewTotalPrice, textViewTotalPayment;
    Chip chipOrderStatus, chipVoucher;

    BottomSheetBehavior<View> bottomSheetBehavior;

    Order order;
    CartAdapter cartAdapter;

    public OrderDetailBottomSheetFragment(Order order) {
        // Required empty public constructor
        this.order = order;
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
        rootView = inflater.inflate(R.layout.fragment_order_detail_bottom_sheet, container, false);
        addControls();
        addEvents();
        renderOrder();
        return  rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bottomSheetBehavior = BottomSheetBehavior.from((View) view.getParent());
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        // Set min height to parent view
        CoordinatorLayout bottomSheetLayout = dialog.findViewById(R.id.orderDetailBottomSheetLayout);

        if (bottomSheetLayout != null) {
            // Set a temporary height for the BottomSheet
            View parentView = (View) view.getParent();
            DisplayMetrics displayMetrics = new DisplayMetrics();
            requireActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int screenHeight = displayMetrics.heightPixels;
            parentView.getLayoutParams().height = (int) (screenHeight / (1.1));
            parentView.requestLayout();
        }
    }


    private void addEvents() {
        closeBottomSheet();
    }

    private void closeBottomSheet() {
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    private void addControls() {
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerViewOrderDetail);
        recyclerView.hasFixedSize();
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        btnClose = rootView.findViewById(R.id.btnClose);

        imageAvatar = rootView.findViewById(R.id.imageAvatar);
        textViewOrderId = rootView.findViewById(R.id.textViewOrderId);
        textViewOrderDate = rootView.findViewById(R.id.textViewOrderDate);
        textViewPaymentMethod = rootView.findViewById(R.id.textViewPaymentMethod);
        textViewUserName = rootView.findViewById(R.id.textViewUserName);
        textViewEmail = rootView.findViewById(R.id.textViewEmail);
        textViewAddress = rootView.findViewById(R.id.textViewAddress);
        textViewPhoneNumber = rootView.findViewById(R.id.textViewPhoneNumber);
        textViewReceiverName = rootView.findViewById(R.id.textViewReceiverName);
        textViewOrderNote = rootView.findViewById(R.id.textViewOrderNote);
        textViewTotalPrice = rootView.findViewById(R.id.textViewTotalPrice);
        textViewTotalPayment = rootView.findViewById(R.id.textViewTotalPayment);
        chipOrderStatus = rootView.findViewById(R.id.chipOrderStatus);
        chipVoucher = rootView.findViewById(R.id.chipVoucher);

    }

    private void renderOrder() {
        if (order == null) return;

        // Set data for order
        textViewOrderId.setText(order.getId());
        textViewOrderDate.setText(Utils.formatDateTimeISO8601(order.getOrderDate()));

        String paymentMethod = "";
        if (order.getPaymentMethod() == "cash") {
            paymentMethod = "Tiền mặt";
        } else if (order.getPaymentMethod() == "momo") {
            paymentMethod = "Ví MoMo";
        } else  {
            paymentMethod = "Ví VNPay";
        }

        textViewPaymentMethod.setText(paymentMethod);
        textViewUserName.setText(order.getUserName());
        textViewEmail.setText(order.getEmail());
        textViewAddress.setText(order.getAddress());
        textViewPhoneNumber.setText(order.getPhoneNumber());
        textViewReceiverName.setText(order.getReceiverName());
        textViewOrderNote.setText(order.getOrderNote());

        // Loop in order items
        double totalPrice = 0;
        for (Cart orderItem: order.getOrderItems()) {
            totalPrice += orderItem.getOrderItemPrice();

        }
        textViewTotalPrice.setText(Utils.formatVNCurrency(totalPrice));

        textViewTotalPayment.setText(Utils.formatVNCurrency(order.getTotalPayment()));
        chipOrderStatus.setText(order.getOrderStatus());

        Picasso.get().load(order.getAvatar()).into(imageAvatar);

        if (order.getVoucherName() != null && !order.getVoucherName().isEmpty()) {
            chipVoucher.setVisibility(View.VISIBLE);
            chipVoucher.setText(order.getVoucherName());
        } else {
            chipVoucher.setVisibility(View.GONE);
            chipVoucher.setText("");

        }

        // Render recycler view
        cartAdapter = new CartAdapter(order.getOrderItems());
        recyclerView.setAdapter(cartAdapter);

    }
}
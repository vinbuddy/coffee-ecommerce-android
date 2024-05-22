package com.coffee.app.model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CartBadgeViewModel extends ViewModel {
    private MutableLiveData<Integer> cartBadge = new MutableLiveData<>(0);

    public LiveData<Integer> getCartBadge() {
        return cartBadge;
    }

    public void setCartBadge(int value) {
        cartBadge.setValue(value);
    }
}

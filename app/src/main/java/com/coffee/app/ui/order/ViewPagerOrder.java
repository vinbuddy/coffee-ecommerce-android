package com.coffee.app.ui.order;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ViewPagerOrder extends FragmentStateAdapter {
    public ViewPagerOrder(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position)
        {
            case 0:
                return new CurrentOrderFragment();
            case 1:
                return new CompleteOrderFragment();
            case 2:
                return new CancelOrderFragment();
            default:
                return new CurrentOrderFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}

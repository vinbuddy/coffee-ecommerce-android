package com.coffee.app.ui.order;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.coffee.app.R;
import com.google.android.material.tabs.TabLayout;

public class OrderFragment extends Fragment {

    TabLayout tabOrder;
    View rootView;

    public OrderFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_order, container, false);
        addControls();
        addEvents();

        return rootView;
    }

    private void addControls() {
        tabOrder = rootView.findViewById(R.id.tabOrder);
    }


    private void addEvents() {
        renderOrderTab();
    }


    private void renderOrderTab() {
        tabOrder.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // Load fragment by tab position

                switch (tab.getPosition()) {
                    case 0:
                        loadFragment(new CurrentOrderFragment(), false);
                        break;
                    case 1:
                        loadFragment(new CompleteOrderFragment(), false);
                        break;
                    case 2:
                        loadFragment(new CancelOrderFragment(), false);
                        break;
                    default:

                        break;
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });


        // Load default fragment
        loadFragment(new CurrentOrderFragment(), true);
    }

    private void loadFragment(Fragment fragment, boolean isAppInit) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if (isAppInit) {
            fragmentTransaction.add(R.id.orderFrameLayout, fragment);
        } else {
            fragmentTransaction.replace(R.id.orderFrameLayout, fragment);
        }

        fragmentTransaction.replace(R.id.orderFrameLayout, fragment);

        fragmentTransaction.commit();
    }
}
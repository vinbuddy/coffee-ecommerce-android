package com.coffee.app.ui.home;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.coffee.app.R;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.models.SlideModel;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    ImageSlider bannerSlider;
    TextView textViewWelcome;
    View rootView;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_home, container, false);

        addControls();
        renderBanner();
        renderWelcomeMessage();

        return rootView;
    }

    private void addControls() {
        bannerSlider = (ImageSlider) rootView.findViewById(R.id.bannerSlider);
        textViewWelcome = (TextView) rootView.findViewById(R.id.textViewWelcome);
    }

    private void renderBanner() {
        List<SlideModel> bannerList = new ArrayList<>();
        bannerList.add(new SlideModel(R.drawable.banner1, ScaleTypes.CENTER_CROP));
        bannerList.add(new SlideModel(R.drawable.banner2, ScaleTypes.CENTER_CROP));

        if (bannerSlider != null ) {
            bannerSlider.setImageList(bannerList);
        }

    }

    private void renderWelcomeMessage() {
        // Get the current user name in firebase
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String name = user.getDisplayName();
            textViewWelcome.setText("Xin chào " + name + " 👋");
        }
    }
}
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

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    ImageSlider bannerSlider;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        addControls(rootView);
        renderBanner();

        return rootView;
    }

    private void addControls(View rootView) {
        bannerSlider = (ImageSlider) rootView.findViewById(R.id.bannerSlider);
    }

    private void renderBanner() {
        List<SlideModel> bannerList = new ArrayList<>();
        bannerList.add(new SlideModel(R.drawable.banner1, ScaleTypes.CENTER_CROP));
        bannerList.add(new SlideModel(R.drawable.banner2, ScaleTypes.CENTER_CROP));

        if (bannerSlider != null ) {
            bannerSlider.setImageList(bannerList);
        }

    }
}
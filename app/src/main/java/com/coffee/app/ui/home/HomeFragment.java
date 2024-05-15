package com.coffee.app.ui.home;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.coffee.app.R;
import com.coffee.app.model.Product;
import com.coffee.app.shared.Constants;
import com.coffee.app.ui.detail.ProductDetailBottomSheetFragment;
import com.coffee.app.ui.menu.ProductAdapter;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.models.SlideModel;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    ImageSlider bannerSlider;
    TextView textViewWelcome;

    RecyclerView recyclerViewTeaProducts, recyclerViewCoffeeProducts;

    RecyclerView.LayoutManager teaProductLayoutManager, coffeeProductLayoutManager;
    TeaProductCardGridAdapter teaProductAdapter;
        CoffeeProductCardGridAdapter coffeeProductAdapter;

    View rootView;


    ArrayList<Product> teaProductList = new ArrayList<>();
    ArrayList<Product> coffeeProductList = new ArrayList<>();

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

        getProductRequest();


        return rootView;
    }

    private void addControls() {
        bannerSlider = (ImageSlider) rootView.findViewById(R.id.bannerSlider);
        textViewWelcome = (TextView) rootView.findViewById(R.id.textViewWelcome);
        recyclerViewTeaProducts = (RecyclerView) rootView.findViewById(R.id.teaProductRecyclerView);
        recyclerViewCoffeeProducts = (RecyclerView) rootView.findViewById(R.id.coffeeProductRecyclerView);

    }

    private void getProductRequest() {
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        queue.getCache().clear();
        String url = Constants.API_URL + "/product";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    // Handle the response
                   try {
                       JSONObject jsonObject = new JSONObject(response);
                       JSONArray dataArray = jsonObject.getJSONArray("data");

                       for (int i = 0; i < dataArray.length(); i++) {
                           JSONObject productObject = dataArray.getJSONObject(i);

                           Product product = new Product();
                           product.setId(productObject.getInt("id"));
                           product.setName(productObject.getString("name"));
                           product.setPrice(Double.parseDouble(productObject.getString("price")));
                           product.setDescription(productObject.getString("description"));
                           product.setStatus(productObject.getString("status"));
                           product.setImage(productObject.getString("image"));
                           product.setCategory_id(productObject.getInt("category_id"));
                           product.setCategory_name(productObject.getString("category_name"));

                           // Depending on the category, add the product to the appropriate list
                           if (product.getCategory_name().equals("Trà")) {
                               teaProductList.add(product);
                           } else if (product.getCategory_name().equals("Cà phê")) {
                               coffeeProductList.add(product);
                           }

                       }

                       // After all products are added to the lists, render the products by category
                       renderTeaProducts();
                       renderCoffeeProducts();
                   } catch (Exception e) {
                       e.printStackTrace();
                       // Parse the response and update your UI
                   }
                }, error -> {
            Toast.makeText(getActivity(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
        });

        queue.add(stringRequest);
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


    private void renderTeaProducts() {
        teaProductLayoutManager = new GridLayoutManager(getContext(), 2);
        recyclerViewTeaProducts.setLayoutManager(teaProductLayoutManager);
        teaProductAdapter = new TeaProductCardGridAdapter(teaProductList);
        recyclerViewTeaProducts.setAdapter(teaProductAdapter);

        teaProductAdapter.setOnItemClickListener(new TeaProductCardGridAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Product product) {
                ProductDetailBottomSheetFragment productDetailBottomSheetFragment = new ProductDetailBottomSheetFragment(product);
                productDetailBottomSheetFragment.show(getChildFragmentManager(), productDetailBottomSheetFragment.getTag());
            }
        });

    }

    private void renderCoffeeProducts() {


        coffeeProductLayoutManager = new GridLayoutManager(getContext(), 2);
        recyclerViewCoffeeProducts.setLayoutManager(coffeeProductLayoutManager);
        coffeeProductAdapter = new CoffeeProductCardGridAdapter(coffeeProductList);
        recyclerViewCoffeeProducts.setAdapter(coffeeProductAdapter);

        coffeeProductAdapter.setOnItemClickListener(new CoffeeProductCardGridAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Product product) {
                ProductDetailBottomSheetFragment productDetailBottomSheetFragment = new ProductDetailBottomSheetFragment(product);
                productDetailBottomSheetFragment.show(getChildFragmentManager(), productDetailBottomSheetFragment.getTag());
            }
        });

    }
}
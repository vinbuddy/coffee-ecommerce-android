package com.coffee.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.coffee.app.model.CartBadgeViewModel;
import com.coffee.app.model.User;
import com.coffee.app.model.UserViewModel;
import com.coffee.app.shared.Constants;
import com.coffee.app.ui.home.HomeFragment;
import com.coffee.app.ui.menu.MenuFragment;
import com.coffee.app.ui.order.OrderFragment;
import com.coffee.app.ui.others.OthersFragment;
import com.coffee.app.ui.store.StoreFragment;
import com.coffee.app.ui.voucher.VoucherFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {
    BottomNavigationView bottomNav;
     FrameLayout frameLayout;
     UserViewModel userViewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addControls();

        if (getIntent()!= null && getIntent().hasExtra("open_fragment")) {
            String fragmentName = getIntent().getStringExtra("open_fragment");
            renderSpecificFragment(fragmentName);
            getIntent().removeExtra("open_fragment");
        } else {
            renderCurrentFragment();
        }

        getCurrentUserRequest();
    }

    private void addControls() {

        bottomNav = findViewById(R.id.bottomNav);
        frameLayout = findViewById(R.id.frameLayout);

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
    }

    private void getCurrentUserRequest() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            return;
        }

        String userId = user.getUid();
        String url = Constants.API_URL + "/user/" + userId;

        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);
                JSONObject data = jsonObject.getJSONObject("data");


                String user_name = data.getString("user_name");
                String id = data.getString("id");
                String email = data.getString("email");
                String avatar = data.getString("avatar");

                User currentUser = new User();
                currentUser.setId(id);
                currentUser.setUserName(user_name);
                currentUser.setEmail(email);
                currentUser.setAvatar(avatar);

                Toast.makeText(this, "Đã đăng nhập tài khoản", Toast.LENGTH_SHORT).show();

                userViewModel.setCurrentUser(currentUser);

            } catch (Exception e) {
                System.out.println(e);
            }
        }, error -> {
            Toast.makeText(this, error.getMessage(), Toast.LENGTH_SHORT).show();
        });

        queue.add(stringRequest);
    }

    private void renderCurrentFragment() {
        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.navHome) {
                    loadFragment(new HomeFragment(), false);
                } else if (itemId == R.id.navMenu) {
                    loadFragment(new MenuFragment(), false);
                } else if (itemId == R.id.navStore) {
                    loadFragment(new StoreFragment(), false);
                } else if (itemId == R.id.navVoucher) {
                    loadFragment(new VoucherFragment(), false);
                }  else {
                    loadFragment(new OthersFragment(), false);
                }

                return true;
            }
        });

        loadFragment(new HomeFragment(), true);
    }

    private void renderSpecificFragment(String fragmentName) {
        if (fragmentName.equals("HomeFragment")) {
            loadFragment(new HomeFragment(), false);
        } else if (fragmentName.equals("MenuFragment")) {
            loadFragment(new MenuFragment(), false);
        } else if (fragmentName.equals("StoreFragment")) {
            loadFragment(new StoreFragment(), false);
        } else if (fragmentName.equals("VoucherFragment")) {
            loadFragment(new VoucherFragment(), false);
        } else if (fragmentName.equals("OrderFragment")) {
            loadFragment(new OrderFragment(), false);
        }
        else {
            loadFragment(new OthersFragment(), false);

        }

    }
    void loadFragment(Fragment fragment, boolean isAppInit) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if (isAppInit) {
            fragmentTransaction.add(R.id.frameLayout, fragment);
        } else {
            fragmentTransaction.replace(R.id.frameLayout, fragment);

        }

        fragmentTransaction.replace(R.id.frameLayout, fragment);

        fragmentTransaction.commit();
    }
}
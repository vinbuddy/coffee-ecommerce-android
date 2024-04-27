package com.coffee.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.coffee.app.ui.home.HomeFragment;
import com.coffee.app.ui.menu.MenuFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
//import android.widget.Button;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
//    TextView textView;
//    Button btnLogout;
//    FirebaseAuth auth;
    final int SPASH_TIME_OUT = 3000;
    BottomNavigationView bottomNav;
     FrameLayout frameLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addControls();
        renderCurrentFragment();

//        addControls();
//
//        renderCurrentFragment();

//        auth = FirebaseAuth.getInstance();
//
//        FirebaseUser currentUser = auth.getCurrentUser();
//        textView = findViewById(R.id.testTextView);
//        btnLogout = findViewById(R.id.btnLogout);
//
//        textView.setText(currentUser.getDisplayName());
//
//        btnLogout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (currentUser.getDisplayName() != "") {
//                    auth.signOut();
//                    Toast.makeText(getApplicationContext(), "Logout successfully", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
    }

    private void addControls() {
        //FirebaseUser currentUser = auth.getCurrentUser();
//        textView = findViewById(R.id.testTextView);
//        btnLogout = findViewById(R.id.btnLogout);

        bottomNav = findViewById(R.id.bottomNav);
        frameLayout = findViewById(R.id.frameLayout);


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
                }

                return true;
            }
        });

        loadFragment(new HomeFragment(), true);
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
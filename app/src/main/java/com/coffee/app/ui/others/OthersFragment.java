package com.coffee.app.ui.others;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.coffee.app.R;
import com.coffee.app.ui.cart.CartActivity;
import com.coffee.app.ui.login.LoginActivity;
import com.coffee.app.ui.wishlist.WishlistActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class OthersFragment extends Fragment {
    TextView textViewLogin, textViewLogout, textViewProfile, textViewOrder, textViewCart, textViewWishlist;
    View rootView;

    public OthersFragment() {
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
        rootView = inflater.inflate(R.layout.fragment_others, container, false);

        addControls();
        renderLoginOrLogoutView();

        addEvents();

        return rootView;
    }

    private void addControls() {
        textViewLogin = (TextView) rootView.findViewById(R.id.textViewLogin);
        textViewLogout = (TextView) rootView.findViewById(R.id.textViewLogout);
        textViewProfile = (TextView) rootView.findViewById(R.id.textViewProfile);
        textViewOrder = (TextView) rootView.findViewById(R.id.textViewOrder);
        textViewCart = (TextView) rootView.findViewById(R.id.textViewCart);
        textViewWishlist = (TextView) rootView.findViewById(R.id.textViewWishlist);
    }

    private void addEvents() {
        handleLogout();
        navigateToLogin();
        navigateToWishlist();
        navigateToCart();
    }

    private void renderLoginOrLogoutView() {
        // Check if the user is logged in or not
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            // Show the logout button
            textViewLogout.setVisibility(View.VISIBLE);
            textViewLogin.setVisibility(View.GONE);
        } else {
            // Show the login button
            textViewLogin.setVisibility(View.VISIBLE);
            textViewLogout.setVisibility(View.GONE);
        }
    }

    private void handleLogout() {
        textViewLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build();
                GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(getActivity(), googleSignInOptions);

                googleSignInClient.signOut()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    // Sign out from Firebase
                                    FirebaseAuth.getInstance().signOut();
                                    renderLoginOrLogoutView();
                                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(getActivity(), "Đăng xuất thất bại", Toast.LENGTH_SHORT).show();
                                }

                            }
                        });
            }
        });
    }

    private void navigateToLogin() {
        textViewLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to the login activity
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    private void navigateToProfile() {
        textViewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to the profile activity
                // Intent intent = new Intent(getActivity(), ProfileActivity.class);
                // startActivity(intent);
            }
        });
    }

    private void navigateToWishlist() {
        textViewWishlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to the profile activity
                 Intent intent = new Intent(getActivity(), WishlistActivity.class);
                 startActivity(intent);
            }
        });
    }

    private void navigateToCart() {
        textViewCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to the profile activity
                 Intent intent = new Intent(getActivity(), CartActivity.class);
                 startActivity(intent);
            }
        });
    }
}
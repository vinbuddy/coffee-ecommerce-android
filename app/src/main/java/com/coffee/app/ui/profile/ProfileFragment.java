package com.coffee.app.ui.profile;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.coffee.app.R;
import com.coffee.app.model.User;
import com.coffee.app.model.UserViewModel;
import com.coffee.app.shared.Constants;
import com.coffee.app.ui.cart.CartActivity;
import com.coffee.app.ui.login.LoginActivity;
import com.coffee.app.ui.others.OthersFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ProfileFragment extends Fragment {

    ImageView imageAvatar;

    TextView btnBack;
    Button btnUpdateProfile, btnRemoveAvatar;
    TextInputEditText inputName, inputEmail;

    View rootView;

    Uri imageUri;

    UserViewModel userViewModel;

    boolean isNameChanged = false;

    public ProfileFragment() {
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
        rootView =  inflater.inflate(R.layout.fragment_profile, container, false);

        // Check authentication firebase if not login, redirect to login page
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Intent intent = new Intent(getContext(), LoginActivity.class);
            startActivity(intent);
        }

        addControls();
        renderProfile();
        addEvents();

        return rootView;
    }


    // INTERFACE
    private interface OnProfileUpdateListener {
        void onAfterUploaded(String imageURL);
    }


    // After selected image from gallery
    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == getActivity().RESULT_OK) {
                if (result.getData() != null) {
                    imageUri = result.getData().getData();
                    Picasso.get().load(imageUri).into(imageAvatar);

                    btnRemoveAvatar.setVisibility(View.VISIBLE);
                }
            }
        }
    });


    private void addControls() {
        imageAvatar = rootView.findViewById(R.id.imageAvatar);
        inputName = rootView.findViewById(R.id.inputName);
        inputEmail = rootView.findViewById(R.id.inputEmail);
        btnBack = rootView.findViewById(R.id.btnBack);
        btnUpdateProfile = rootView.findViewById(R.id.btnUpdateProfile);
        btnRemoveAvatar = rootView.findViewById(R.id.btnRemoveAvatar);

        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);

    }

    private void addEvents() {
        backPrevious();
        uploadImage();
        removeAvatar();
        typingName();
        updateProfile();
    }

    private void updateProfile() {
        btnUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageUri != null) {
                    // Update name and avatar
                    uploadAvatarFirebase(new OnProfileUpdateListener() {
                        @Override
                        public void onAfterUploaded(String imageURL) {
                            updateProfileFirebase(imageURL, inputName.getText().toString());
                        }
                    });

                } else {
                    // Update name only
                    updateProfileFirebase(null, inputName.getText().toString());
                }
            }
        });
    }


    private void uploadAvatarFirebase(OnProfileUpdateListener listener) {
        ContentResolver cR = getActivity().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        String fileExtension = mime.getExtensionFromMimeType(cR.getType(imageUri));

        StorageReference fileReference = FirebaseStorage.getInstance().getReference("images/avatar")
                .child(String.valueOf(System.currentTimeMillis()) + fileExtension);


        fileReference.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
            fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                listener.onAfterUploaded(uri.toString());

            });
        }).addOnFailureListener(e -> {
            Toast.makeText(getContext(), "Failed to upload image", Toast.LENGTH_SHORT).show();
        });
    }

    private void updateProfileFirebase(String imageURL, String name) {
        // update profile in Firebase
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


        user.updateProfile(new com.google.firebase.auth.UserProfileChangeRequest.Builder()
                .setDisplayName(isNameChanged ? name : user.getDisplayName())
                .setPhotoUri(imageURL != null ? Uri.parse(imageURL) : user.getPhotoUrl())
                .build()).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

                // Update profile request
                updateProfileRequest(imageURL, name);
                Toast.makeText(getContext(), "Cập nhật thông tin thành công", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Failed to update profile", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateProfileRequest(String imageURL, String name) {
        // Call api to update profile
        User currentUser = userViewModel.getCurrentUser().getValue();
        String userId = currentUser.getId();
        //String userId = Constants.TEMP_USER_ID;
        String url = Constants.API_URL + "/user/" + userId;

        RequestQueue queue = Volley.newRequestQueue(getContext());

        StringRequest request = new StringRequest(Request.Method.PUT, url, response -> {
            // handle response
            try {
                JSONObject jsonObject  = new JSONObject(response);
                JSONObject data = jsonObject.getJSONObject("data");


                String user_name = data.getString("user_name");
                String id = data.getString("id");
                String email = data.getString("email");
                String avatar = data.getString("avatar");

                User updatedUser = new User();

                updatedUser.setId(id);
                updatedUser.setUserName(user_name);
                updatedUser.setEmail(email);
                updatedUser.setAvatar(avatar);

                userViewModel.setCurrentUser(updatedUser);

            } catch (JSONException e) {
                throw new RuntimeException(e);
            }


        }, error -> {
            Toast.makeText(getContext(), "Failed to update profile", Toast.LENGTH_SHORT).show();
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> param = new HashMap<>();

                if (imageURL != null && isNameChanged) {
                    param.put("avatar", imageURL);
                    param.put("user_name", name);
                } else if (imageURL != null && !isNameChanged) {
                    param.put("avatar", imageURL);
                } else if (isNameChanged && imageURL == null) {
                    param.put("user_name", name);
                }

                return param;
            }
        };

        queue.add(request);
    }


    private void removeAvatar() {
        btnRemoveAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User currentUser = userViewModel.getCurrentUser().getValue();

                Picasso.get().load(currentUser.getAvatar()).into(imageAvatar);
                btnRemoveAvatar.setVisibility(View.GONE);
                imageUri = null;
            }
        });
    }

    private void typingName() {
        // Listen to typing name
        inputName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Do nothing
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Check name changed or not
                User currentUser = userViewModel.getCurrentUser().getValue();
                if (!s.toString().trim().equals(currentUser.getAvatar())) {
                    isNameChanged = true;
                } else {
                    isNameChanged = false;
                }
            }
        });
    }

    private void backPrevious() {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OthersFragment fragment = new OthersFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, fragment).commit();
            }
        });
    }

    private void uploadImage() {
        imageAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                activityResultLauncher.launch(intent);
            }
        });
    }
    private void renderProfile() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        userViewModel.getCurrentUser().observe(getViewLifecycleOwner(), new Observer<User>() {
            @Override
            public void onChanged(@Nullable User currentUser) {
                boolean isUserNull = currentUser == null;

                Toast.makeText(getContext(), String.valueOf(isUserNull), Toast.LENGTH_SHORT).show();


                if (currentUser != null) {
                    inputName.setText(currentUser.getUserName());
                    inputEmail.setText(currentUser.getEmail());
                    Picasso.get().load(currentUser.getAvatar()).into(imageAvatar);
                }
            }
        });
    }
}
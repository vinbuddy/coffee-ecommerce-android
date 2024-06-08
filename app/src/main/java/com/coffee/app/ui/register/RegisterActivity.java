package com.coffee.app.ui.register;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.coffee.app.MainActivity;
import com.coffee.app.R;
import com.coffee.app.shared.Constants;
import com.coffee.app.shared.interfaces.Authentication;
import com.coffee.app.ui.login.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    TextInputEditText inputName, inputEmail, inputPassword;
    TextView tvErrorMessage, tvLoginRedirect;
    Button btnRegister;
    FirebaseAuth auth;
    ImageView imageLogo;

    ProgressBar loadingBar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        addControls();

        auth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = auth.getCurrentUser();

        if (firebaseUser != null) {
            // When user already sign in redirect to main activity
            startActivity(new Intent(getApplicationContext(), MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            return;
        }

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        tvLoginRedirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectToLogin();
            }
        });

        imageLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });

    }

    private void addControls () {

        inputEmail = findViewById(R.id.inputEmail);
        inputName = findViewById(R.id.inputName);
        inputPassword = findViewById(R.id.inputPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvErrorMessage = findViewById(R.id.tvErrorMessage);
        tvLoginRedirect = findViewById(R.id.tvLoginRedirect);
        imageLogo = findViewById(R.id.imageLogo);
        loadingBar = findViewById(R.id.loadingBar);
    }

    private void registerUser() {
        boolean isValidForm = validateForm();
        if (isValidForm) {
            String username = inputName.getText().toString().trim();
            String email = inputEmail.getText().toString().trim();
            String password = inputPassword.getText().toString().trim();


            loadingBar.setVisibility(View.VISIBLE);

            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        FirebaseUser user = task.getResult().getUser();
                        updateUserName(user, username);
                    } else {
                        tvErrorMessage.setText("Đăng ký thất bại");
                        loadingBar.setVisibility(View.GONE);
                    }


                }
            });
        }
    }

    private void updateUserName(FirebaseUser user, String username) {
        UserProfileChangeRequest updateProfileRequest = new UserProfileChangeRequest.Builder().setDisplayName(username).build();
        user.updateProfile(updateProfileRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {

                    // Create account in database
                    checkExistUser(user, isExist -> {
                        if (isExist) {
                            tvErrorMessage.setText("Tài khoản đã tồn tại");
                            loadingBar.setVisibility(View.GONE);
                        } else {
                            createUserAccountRequest(user);
                        }
                    });
                } else {
                    tvErrorMessage.setText("Cập nhật thông tin vào firebase thất bại");
                }
            }
        });
    }

    private void createUserAccountRequest(FirebaseUser userInfo) {
        String url = Constants.API_URL + "/auth/create-user-account";
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        loadingBar.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    } catch (Exception e) {

                    }
                }, error -> {
                    loadingBar.setVisibility(View.GONE);
                    tvErrorMessage.setText("Đăng ký thất bại");
                    Toast.makeText(getApplicationContext(), "Đăng ký thất bại", Toast.LENGTH_SHORT).show();

        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> param = new HashMap<>();
                param.put("id", userInfo.getUid());
                param.put("name", userInfo.getDisplayName());
                param.put("avatar", String.valueOf(userInfo.getPhotoUrl()));
                param.put("name", userInfo.getDisplayName());
                param.put("account_type", "email");

                return param;
            }
        };

        queue.add(stringRequest);


    }

    private void checkExistUser(FirebaseUser userInfo, Authentication existListener) {

        String url = Constants.API_URL + "/user/" + userInfo.getUid();
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            JSONObject data = jsonResponse.getJSONObject("data");


                            if (!data.has("id")) {
                                existListener.onUserExist(false);
                                return;
                            }

                            boolean isExist = data.getString("id").equals(userInfo.getUid());
                            existListener.onUserExist(isExist);

                        } catch (Exception e) {
                            e.printStackTrace();
                            existListener.onUserExist(false);
                        }
                    }
                }, error -> {
            // Handle error
            Toast.makeText(this, error.getMessage(), Toast.LENGTH_SHORT).show();
        });

        queue.add(stringRequest);


    }


    private boolean validateForm() {
        boolean isValidForm = true;
        String username = inputName.getText().toString().trim();
        String email = inputEmail.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();
        if (username.isEmpty()) {
            inputName.setError("Vui lòng nhập tên người dùng");
            isValidForm = false;
        }
        if (email.isEmpty()) {
            inputEmail.setError("Vui lòng nhập email");
            isValidForm = false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            inputEmail.setError("Vui lòng nhập đúng định dạng email");
            isValidForm = false;
        }
        if (password.isEmpty()) {
            inputPassword.setError("Vui lòng nhập password");
            isValidForm = false;
        }
        return isValidForm;
    }


    private void redirectToLogin() {
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
    }
}
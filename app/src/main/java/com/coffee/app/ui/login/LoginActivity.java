package com.coffee.app.ui.login;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class LoginActivity extends AppCompatActivity {
    TextInputEditText inputEmail, inputPassword;
    TextView tvErrorMessage, tvRegisterRedirect;
    ProgressBar progressBarLoading;
    ImageView imageLogo;
    Button btnLogin, btnGoogle;
    FirebaseAuth auth;
    GoogleSignInOptions googleOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = auth.getCurrentUser();

        addControls();
        if (firebaseUser != null) {
            // When user already sign in redirect to main activity
            startActivity(new Intent(getApplicationContext(), MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            return;
        }

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();
        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
        GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this);

        if (googleSignInAccount != null){
            finish();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            return;
        }

        ActivityResultLauncher<Intent> mActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        // Handle the Intent
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                        try {
                            GoogleSignInAccount account = task.getResult(ApiException.class);
                            if (account != null) {
                                loginGoogle(account);
                            }
                        } catch (ApiException e) {
                            Log.w("TAG", "Google sign in failed", e);
                        }
                    }
                });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginWithEmail();
            }
        });

        btnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = googleSignInClient.getSignInIntent();
                signInIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                mActivityResultLauncher.launch(signInIntent);
            }
        });


        imageLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        // Check condition
        if (requestCode == 1234) {
            // When request code is equal to 100 initialize task
            Task<GoogleSignInAccount> signInAccountTask = GoogleSignIn.getSignedInAccountFromIntent(data);
                // check condition
               try {
                   GoogleSignInAccount googleSignInAccount = signInAccountTask.getResult(ApiException.class);
                   loginGoogle(googleSignInAccount);
               } catch (ApiException e) {
                      Log.d("TAG", "onActivityResult: " + e.getMessage());
               }
        }
    }

    private void addControls() {

        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnGoogle = findViewById(R.id.btnGoogle);
        tvErrorMessage = findViewById(R.id.tvErrorMessage);
        tvRegisterRedirect = findViewById(R.id.tvRegisterRedirect);
        imageLogo = findViewById(R.id.imageLogo);
    }

    private boolean validateForm() {
        boolean isValidForm = true;
        String email = inputEmail.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();
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

    private void loginGoogle(GoogleSignInAccount account) {
        AuthCredential authCredential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        auth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    AuthResult result = task.getResult();
                    FirebaseUser user = result.getUser();

                    if (user == null) {
                        tvErrorMessage.setText("Đăng nhập thất bại");
                        return;
                    }

                    checkExistUser(user, isExist -> {
                        if (isExist) {
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        } else {
                            createUserAccountRequest(user);
                        }
                    });


                } else {
                    // When task is unsuccessful
                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loginWithEmail() {
        boolean isValidForm = validateForm();

        if(isValidForm) {
            String email = inputEmail.getText().toString().trim();
            String password = inputPassword.getText().toString().trim();

//            progressBarLoading.setVisibility(View.VISIBLE);
//            progressBarLoading.setIndeterminate(false);
//            progressBarLoading.setMax(1);
//            progressBarLoading.setProgress(1);

            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        AuthResult result = task.getResult();
                        FirebaseUser user = result.getUser();

                        if (user == null) {
                            tvErrorMessage.setText("Đăng nhập thất bại");
                        } else {
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            finish();
                        }

                    } else {
                        tvErrorMessage.setText("Đăng nhập thất bại");
                    }
                }
            });
        }
    }

    private void createUserAccountRequest(FirebaseUser userInfo) {
        // URL of your server-side script that handles account creation
        String url = Constants.API_URL + "/auth/create-user-account";
        // Create a new user account
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        Toast.makeText(getApplicationContext(), "Thêm thành công", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    } catch (Exception e) {

                    }
                }, error -> {
            Toast.makeText(getApplicationContext(), "Thêm thất bại", Toast.LENGTH_SHORT).show();

        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> param = new HashMap<>();
                param.put("id", userInfo.getUid());
                param.put("name", userInfo.getDisplayName());
                param.put("avatar", String.valueOf(userInfo.getPhotoUrl()));
                param.put("name", userInfo.getDisplayName());
                param.put("account_type", "google");

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



}
package com.coffee.app.ui.login;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.coffee.app.MainActivity;
import com.coffee.app.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity {
    TextInputEditText inputEmail, inputPassword;
    TextView tvErrorMessage, tvRegisterRedirect;
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

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(getApplicationContext(), googleSignInOptions);
        GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this);

        if (googleSignInAccount != null){
            finish();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginWithEmail();
            }
        });

        btnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = googleSignInClient.getSignInIntent();
                // Start activity for result
                startActivityForResult(intent, 100);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Check condition
        if (requestCode == 100) {
            // When request code is equal to 100 initialize task
            Task<GoogleSignInAccount> signInAccountTask = GoogleSignIn.getSignedInAccountFromIntent(data);
            // check condition
            if (signInAccountTask.isSuccessful()) {
                String s = "Google sign in successful";
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();


                try {

                    GoogleSignInAccount googleSignInAccount = signInAccountTask.getResult(ApiException.class);

                    if (googleSignInAccount != null) {
                        AuthCredential authCredential = GoogleAuthProvider.getCredential(googleSignInAccount.getIdToken(), null);

                        auth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if (task.isSuccessful()) {
                                    AuthResult result = task.getResult();
                                    FirebaseUser user = result.getUser();

                                    // Store user info

                                    // When task is successful redirect to main activity
                                    startActivity(new Intent(getApplicationContext(), MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));

                                    Toast.makeText(getApplicationContext(), "Firebase authentication successful", Toast.LENGTH_SHORT).show();
                                } else {
                                    // When task is unsuccessful
                                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                                }
                            }
                        });
                    }
                } catch (ApiException e) {
                    e.printStackTrace();
                }
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

    private void loginWithEmail() {
        boolean isValidForm = validateForm();

        if(isValidForm) {
            String email = inputEmail.getText().toString().trim();
            String password = inputPassword.getText().toString().trim();
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // Store user info
                        AuthResult result = task.getResult();
                        FirebaseUser user = result.getUser();

                        // Show toast message
                        Toast.makeText(getApplicationContext(), "Đăng nhập thành công", Toast.LENGTH_SHORT).show();

                        // Redirect to main activity
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    } else {

                        tvErrorMessage.setText("Đăng nhập thất bại");
                    }
                }
            });
        }
    }


}
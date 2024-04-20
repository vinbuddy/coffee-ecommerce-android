package com.coffee.app.ui.register;

import androidx.annotation.NonNull;
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
import com.coffee.app.ui.login.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class RegisterActivity extends AppCompatActivity {
    TextInputEditText inputName, inputEmail, inputPassword;
    TextView tvErrorMessage, tvLoginRedirect;
    Button btnRegister;
    FirebaseAuth auth;



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

    }

    private void addControls () {

        inputEmail = findViewById(R.id.inputEmail);
        inputName = findViewById(R.id.inputName);
        inputPassword = findViewById(R.id.inputPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvErrorMessage = findViewById(R.id.tvErrorMessage);
        tvLoginRedirect = findViewById(R.id.tvLoginRedirect);
    }

    private void handleSuccess() {
        Toast.makeText(getApplicationContext(), "Đăng ký thành công", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }

    private void registerUser() {
        boolean isValidForm = validateForm();
        if (isValidForm) {
            String username = inputName.getText().toString().trim();
            String email = inputEmail.getText().toString().trim();
            String password = inputPassword.getText().toString().trim();
            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        FirebaseUser user = task.getResult().getUser();
                        updateUserName(user, username);
                    } else {
                        handleError(task);
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
                    handleSuccess();
                } else {
                    handleError(task);
                }
            }
        });
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

    private void handleError(Task<?> task) {
        String errorMessage = task.getException().getMessage();
        tvErrorMessage.setText(errorMessage);
    }

    private void redirectToLogin() {
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
    }
}
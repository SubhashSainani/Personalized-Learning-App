package com.example.personalizedlearning.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.personalizedlearning.R;
import com.example.personalizedlearning.models.User;
import com.example.personalizedlearning.utilities.SharedPrefManager;

public class LoginActivity extends AppCompatActivity {
    private EditText etUsername, etPassword;
    private SharedPrefManager sharedPrefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Fade in animation for the entire activity
        findViewById(android.R.id.content).startAnimation(
                AnimationUtils.loadAnimation(this, R.anim.fade_in));

        sharedPrefManager = new SharedPrefManager(this);

        if (sharedPrefManager.isLoggedIn()) {
            startActivity(new Intent(this, HomeActivity.class));
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
            return;
        }

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        Button btnLogin = findViewById(R.id.btnLogin);
        TextView tvRegister = findViewById(R.id.tvRegister);

        // Bounce animation for the login button
        Animation bounce = AnimationUtils.loadAnimation(this, R.anim.bounce);
        btnLogin.startAnimation(bounce);

        btnLogin.setOnClickListener(v -> {
            v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));

            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter username and password", Toast.LENGTH_SHORT).show();
                etUsername.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake));
                return;
            }

            if (username.equals(password)) {
                User user = new User(username, "user@example.com", "1234567890", password);
                sharedPrefManager.saveUser(user);

                startActivity(new Intent(this, HomeActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
            } else {
                Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                etPassword.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake));
            }
        });

        tvRegister.setOnClickListener(v -> {
            v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));
            startActivity(new Intent(this, RegistrationActivity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
    }
}
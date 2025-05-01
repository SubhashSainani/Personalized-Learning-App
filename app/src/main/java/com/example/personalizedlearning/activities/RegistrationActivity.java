package com.example.personalizedlearning.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.personalizedlearning.R;
import com.example.personalizedlearning.models.User;
import com.example.personalizedlearning.utilities.SharedPrefManager;

public class RegistrationActivity extends AppCompatActivity {
    private EditText etUsername, etEmail, etConfirmEmail, etPassword, etConfirmPassword, etPhone;
    private SharedPrefManager sharedPrefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        // Slide up animation for the registration form
        findViewById(R.id.etUsername).startAnimation(
                AnimationUtils.loadAnimation(this, R.anim.slide_up));

        sharedPrefManager = new SharedPrefManager(this);

        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        etConfirmEmail = findViewById(R.id.etConfirmEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        etPhone = findViewById(R.id.etPhone);
        Button btnRegister = findViewById(R.id.btnRegister);

        // Pulse animation for the register button
        Animation pulse = AnimationUtils.loadAnimation(this, R.anim.pulse);
        btnRegister.startAnimation(pulse);

        btnRegister.setOnClickListener(v -> {
            v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));

            String username = etUsername.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String confirmEmail = etConfirmEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String confirmPassword = etConfirmPassword.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();

            if (username.isEmpty() || email.isEmpty() || confirmEmail.isEmpty() ||
                    password.isEmpty() || confirmPassword.isEmpty() || phone.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                etUsername.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake));
                return;
            }

            if (!email.equals(confirmEmail)) {
                Toast.makeText(this, "Emails don't match", Toast.LENGTH_SHORT).show();
                etConfirmEmail.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake));
                return;
            }

            if (!password.equals(confirmPassword)) {
                Toast.makeText(this, "Passwords don't match", Toast.LENGTH_SHORT).show();
                etConfirmPassword.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake));
                return;
            }

            User user = new User(username, email, phone, password);
            sharedPrefManager.saveUser(user);

            startActivity(new Intent(this, InterestsActivity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            finish();
        });
    }
}
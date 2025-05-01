package com.example.personalizedlearning.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import com.example.personalizedlearning.R;
import com.example.personalizedlearning.models.User;
import com.example.personalizedlearning.utilities.SharedPrefManager;
import java.util.ArrayList;
import java.util.List;

public class InterestsActivity extends AppCompatActivity {
    private Button btnNext;
    private SharedPrefManager sharedPrefManager;
    private User currentUser;
    private TextView tvSelectionCount;
    private List<AppCompatButton> interestButtons = new ArrayList<>();
    private List<String> selectedInterests = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interests);

        sharedPrefManager = new SharedPrefManager(this);
        currentUser = sharedPrefManager.getUser();
        tvSelectionCount = findViewById(R.id.tvSelectionCount);
        btnNext = findViewById(R.id.btnNext);

        initializeInterestButtons();

        btnNext.setOnClickListener(v -> {
            v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));

            if (selectedInterests.size() < 1) {
                Toast.makeText(this, "Please select at least one interest", Toast.LENGTH_SHORT).show();
                return;
            }

            if (selectedInterests.size() > 10) {
                Toast.makeText(this, "You can select up to 10 interests", Toast.LENGTH_SHORT).show();
                return;
            }

            currentUser.setInterests(selectedInterests);
            sharedPrefManager.saveUser(currentUser);

            startActivity(new Intent(this, LoginActivity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            finish();
        });
    }

    private void initializeInterestButtons() {
        int[] buttonIds = {
                R.id.interestButton1, R.id.interestButton2, R.id.interestButton3,
                R.id.interestButton4, R.id.interestButton5, R.id.interestButton6,
                R.id.interestButton7, R.id.interestButton8, R.id.interestButton9,
                R.id.interestButton10, R.id.interestButton11, R.id.interestButton12,
                R.id.interestButton13, R.id.interestButton14, R.id.interestButton15,
                R.id.interestButton16, R.id.interestButton17, R.id.interestButton18,
                R.id.interestButton19, R.id.interestButton20, R.id.interestButton21
        };

        for (int id : buttonIds) {
            AppCompatButton button = findViewById(id);
            if (button != null) {
                interestButtons.add(button);
                setupButtonListener(button);
            }
        }
    }

    private void setupButtonListener(AppCompatButton button) {
        button.setOnClickListener(v -> {
            String interest = button.getText().toString();

            if (selectedInterests.contains(interest)) {
                selectedInterests.remove(interest);
                animateButtonDeselect(button);
            } else {
                if (selectedInterests.size() < 10) {
                    selectedInterests.add(interest);
                    animateButtonSelect(button);
                } else {
                    Toast.makeText(this, "Maximum 10 interests allowed", Toast.LENGTH_SHORT).show();
                    button.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake));
                }
            }

            updateSelectionCount();
        });
    }

    private void animateButtonSelect(AppCompatButton button) {
        button.setSelected(true);

        AnimationSet animationSet = new AnimationSet(true);
        Animation scaleUp = new ScaleAnimation(
                1f, 1.1f, 1f, 1.1f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        scaleUp.setDuration(200);

        Animation scaleDown = new ScaleAnimation(
                1.1f, 1f, 1.1f, 1f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        scaleDown.setDuration(200);
        scaleDown.setStartOffset(200);

        animationSet.addAnimation(scaleUp);
        animationSet.addAnimation(scaleDown);
        button.startAnimation(animationSet);

        button.setBackgroundResource(R.drawable.interest_button_selected);
    }

    private void animateButtonDeselect(AppCompatButton button) {
        button.setSelected(false);
        Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
        button.startAnimation(shake);
        button.setBackgroundResource(R.drawable.interest_button_background);
    }

    private void updateSelectionCount() {
        tvSelectionCount.setText(getString(R.string.selected_count, selectedInterests.size(), 10));
    }
}
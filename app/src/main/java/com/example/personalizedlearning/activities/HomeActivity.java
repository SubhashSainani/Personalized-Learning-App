package com.example.personalizedlearning.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.example.personalizedlearning.R;
import com.example.personalizedlearning.models.Quiz;
import com.example.personalizedlearning.models.User;
import com.example.personalizedlearning.services.ApiService;
import com.example.personalizedlearning.utilities.SharedPrefManager;
import java.util.List;
import java.util.Random;

public class HomeActivity extends AppCompatActivity {
    private TextView tvWelcome, tvTaskStatus;
    private CardView cvTask;
    private ProgressBar progressBar;
    private SharedPrefManager sharedPrefManager;
    private User currentUser;
    private ApiService apiService;
    private Quiz currentQuiz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        View rootView = findViewById(android.R.id.content);
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        rootView.startAnimation(fadeIn);

        sharedPrefManager = new SharedPrefManager(this);
        currentUser = sharedPrefManager.getUser();
        apiService = new ApiService(this);

        tvWelcome = findViewById(R.id.tvWelcome);
        tvTaskStatus = findViewById(R.id.tvTaskStatus);
        cvTask = findViewById(R.id.cvTask);
        progressBar = findViewById(R.id.progressBar);
        ImageView ivProfile = findViewById(R.id.ivProfile);

        animateWelcomeMessage();
        loadTask();

        cvTask.setOnClickListener(v -> {
            if (currentQuiz != null) {
                v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));
                Intent intent = new Intent(HomeActivity.this, QuizActivity.class);
                intent.putExtra("quiz", currentQuiz);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        ivProfile.setOnClickListener(v -> {
            v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));
            Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
    }

    private void animateWelcomeMessage() {
        tvWelcome.setText(getString(R.string.welcome_message, currentUser.getUsername()));
        Animation bounce = AnimationUtils.loadAnimation(this, R.anim.bounce);
        tvWelcome.startAnimation(bounce);
    }

    private void loadTask() {
        List<String> interests = currentUser.getInterests();
        if (interests == null || interests.isEmpty()) {
            tvTaskStatus.setText("You have 0 tasks due");
            cvTask.setVisibility(View.GONE);
            return;
        }

        String selectedTopic = selectTopicBasedOnPerformance(interests);
        showLoadingState();

        apiService.getQuiz(selectedTopic, new ApiService.QuizCallback() {
            @Override
            public void onSuccess(Quiz quiz) {
                runOnUiThread(() -> {
                    hideLoadingState();
                    currentQuiz = quiz;

                    if (quiz.getQuestions() != null && !quiz.getQuestions().isEmpty()) {
                        showTaskAvailable(quiz, selectedTopic);
                        animateTaskCard();
                    } else {
                        showNoTasksAvailable();
                    }
                });
            }

            @Override
            public void onError(String errorMessage) {
                runOnUiThread(() -> {
                    hideLoadingState();
                    showNoTasksAvailable();
                    Toast.makeText(HomeActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private String selectTopicBasedOnPerformance(List<String> interests) {
        SharedPreferences prefs = getSharedPreferences("QuizPerformance", MODE_PRIVATE);
        String leastAttemptedTopic = interests.get(0);
        int minAttempts = prefs.getInt(leastAttemptedTopic + "_attempts", 0);

        for (String topic : interests) {
            int attempts = prefs.getInt(topic + "_attempts", 0);
            if (attempts < minAttempts) {
                leastAttemptedTopic = topic;
                minAttempts = attempts;
            }
        }

        return leastAttemptedTopic;
    }

    private void animateTaskCard() {
        Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);
        cvTask.startAnimation(slideUp);
    }

    private void showLoadingState() {
        progressBar.setVisibility(View.VISIBLE);
        cvTask.setVisibility(View.GONE);
        tvTaskStatus.setText("Loading your task...");
    }

    private void hideLoadingState() {
        progressBar.setVisibility(View.GONE);
    }

    private void showTaskAvailable(Quiz quiz, String topic) {
        tvTaskStatus.setText("You have 1 task due");
        cvTask.setVisibility(View.VISIBLE);

        TextView tvTaskTitle = cvTask.findViewById(R.id.tv_task_title);
        TextView tvTaskDescription = cvTask.findViewById(R.id.tv_task_description);

        tvTaskTitle.setText("Generated Task: " + topic);
        tvTaskDescription.setText("Test your knowledge about " + topic);
    }

    private void showNoTasksAvailable() {
        tvTaskStatus.setText("You have 0 tasks due");
        cvTask.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh user data when returning to home
        currentUser = sharedPrefManager.getUser();
        if (currentUser != null) {
            animateWelcomeMessage();
        }
    }
}
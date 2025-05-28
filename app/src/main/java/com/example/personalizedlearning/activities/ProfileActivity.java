package com.example.personalizedlearning.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.example.personalizedlearning.R;
import com.example.personalizedlearning.models.User;
import com.example.personalizedlearning.services.ApiService;
import com.example.personalizedlearning.utilities.SharedPrefManager;

public class ProfileActivity extends AppCompatActivity {
    private TextView tvUsername, tvEmail, tvTotalQuestions, tvCorrectAnswers, tvIncorrectAnswers,
            tvNotification, tvAISummaryDescription;
    private MaterialButton btnShare, btnHistory;
    private ImageView ivProfile, ivBack;
    private CardView cvProfile, cvTotalQuestions, cvCorrectAnswers, cvIncorrectAnswers, cvPaymentPlans;
    private SharedPrefManager sharedPrefManager;
    private User currentUser;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Fade in animation for the entire activity
        findViewById(android.R.id.content).startAnimation(
                AnimationUtils.loadAnimation(this, R.anim.fade_in));

        sharedPrefManager = new SharedPrefManager(this);
        currentUser = sharedPrefManager.getUser();
        apiService = new ApiService(this);

        initializeViews();
        setupClickListeners();
        loadUserData();
        loadAISummary();
        animateElements();
    }

    private void initializeViews() {
        tvUsername = findViewById(R.id.tvUsername);
        tvEmail = findViewById(R.id.tvEmail);
        tvTotalQuestions = findViewById(R.id.tvTotalQuestions);
        tvCorrectAnswers = findViewById(R.id.tvCorrectAnswers);
        tvIncorrectAnswers = findViewById(R.id.tvIncorrectAnswers);
        tvNotification = findViewById(R.id.tvNotification);
        tvAISummaryDescription = findViewById(R.id.tvAISummaryDescription);
        btnShare = findViewById(R.id.btnShare);
        btnHistory = findViewById(R.id.btnHistory);
        ivProfile = findViewById(R.id.ivProfile);
        ivBack = findViewById(R.id.ivBack);

        cvProfile = findViewById(R.id.cvProfile);
        cvTotalQuestions = findViewById(R.id.cvTotalQuestions);
        cvCorrectAnswers = findViewById(R.id.cvCorrectAnswers);
        cvIncorrectAnswers = findViewById(R.id.cvIncorrectAnswers);
        cvPaymentPlans = findViewById(R.id.cvPaymentPlans);
    }

    private void setupClickListeners() {
        ivBack.setOnClickListener(v -> {
            v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });

        btnShare.setOnClickListener(v -> {
            v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));
            showShareBottomSheet();
        });

        btnHistory.setOnClickListener(v -> {
            v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));
            Intent intent = new Intent(this, HistoryActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        cvPaymentPlans.setOnClickListener(v -> {
            v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));
            Intent intent = new Intent(this, UpgradeActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        ivProfile.setOnClickListener(v -> {
            v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.pulse));
            Toast.makeText(this, "Profile picture clicked", Toast.LENGTH_SHORT).show();
        });
    }

    private void loadUserData() {
        if (currentUser != null) {
            tvUsername.setText(currentUser.getUsername());
            // Use actual email from user registration
            tvEmail.setText(currentUser.getEmail() != null && !currentUser.getEmail().isEmpty()
                    ? currentUser.getEmail() : "user@gmail.com");

            // Load statistics from SharedPreferences
            int totalQuestions = sharedPrefManager.getTotalQuestions();
            int correctAnswers = sharedPrefManager.getCorrectAnswers();
            int incorrectAnswers = totalQuestions - correctAnswers;

            tvTotalQuestions.setText(String.valueOf(totalQuestions));
            tvCorrectAnswers.setText(String.valueOf(correctAnswers));
            tvIncorrectAnswers.setText(String.valueOf(incorrectAnswers));

            // Set notification text
            if (totalQuestions > 0 && correctAnswers == totalQuestions) {
                tvNotification.setText("You have attempted all correct");
            } else if (totalQuestions > 0) {
                tvNotification.setText("Keep practicing to improve your score");
            } else {
                tvNotification.setText("Start taking quizzes to see your progress");
            }
        }
    }

    private void loadAISummary() {
        int incorrectAnswers = sharedPrefManager.getTotalQuestions() - sharedPrefManager.getCorrectAnswers();
        if (incorrectAnswers > 0) {
            // Generate AI summary based on user's performance
            String summaryPrompt = "Generate a short 5-6 word motivational summary for a student who got "
                    + incorrectAnswers + " questions wrong out of " + sharedPrefManager.getTotalQuestions()
                    + " total questions. Focus on improvement and encouragement.";

            // Call API for AI summary
            generateAISummary(summaryPrompt);
        } else {
            tvAISummaryDescription.setText("Excellent performance! Keep it up!");
        }
    }

    private void generateAISummary(String prompt) {
        // You can call your existing API service here
        // For now, I'll provide some dynamic summaries based on performance
        int totalQuestions = sharedPrefManager.getTotalQuestions();
        int correctAnswers = sharedPrefManager.getCorrectAnswers();
        double accuracy = totalQuestions > 0 ? ((double) correctAnswers / totalQuestions) * 100 : 0;

        String summary;
        if (accuracy >= 90) {
            summary = "Outstanding! You're almost perfect!";
        } else if (accuracy >= 80) {
            summary = "Great job! Minor improvements needed.";
        } else if (accuracy >= 70) {
            summary = "Good work! Focus on weak areas.";
        } else if (accuracy >= 60) {
            summary = "Keep practicing! You're getting better.";
        } else if (accuracy >= 50) {
            summary = "Review fundamentals and practice more.";
        } else {
            summary = "Focus on basics and retry.";
        }

        tvAISummaryDescription.setText(summary);
    }

    private void showShareBottomSheet() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_share, null);
        bottomSheetDialog.setContentView(bottomSheetView);

        // Get user data for sharing
        int totalQuestions = sharedPrefManager.getTotalQuestions();
        int correctAnswers = sharedPrefManager.getCorrectAnswers();
        double accuracy = totalQuestions > 0 ? ((double) correctAnswers / totalQuestions) * 100 : 0;

        String shareText = String.format(
                "Check out my learning progress!\n\n" +
                        "👤 Username: %s\n" +
                        "📊 Total Questions: %d\n" +
                        "✅ Correct Answers: %d\n" +
                        "🎯 Accuracy: %.1f%%\n\n" +
                        "Join me on the Personalized Learning App!",
                currentUser.getUsername(),
                totalQuestions,
                correctAnswers,
                accuracy
        );

        // Setup click listeners for sharing options
        bottomSheetView.findViewById(R.id.ivWhatsApp).setOnClickListener(v -> {
            shareToApp("com.whatsapp", shareText);
            bottomSheetDialog.dismiss();
        });

        bottomSheetView.findViewById(R.id.ivFacebook).setOnClickListener(v -> {
            shareToApp("com.facebook.katana", shareText);
            bottomSheetDialog.dismiss();
        });

        bottomSheetView.findViewById(R.id.ivTwitter).setOnClickListener(v -> {
            shareToApp("com.twitter.android", shareText);
            bottomSheetDialog.dismiss();
        });

        bottomSheetView.findViewById(R.id.ivInstagram).setOnClickListener(v -> {
            shareToApp("com.instagram.android", shareText);
            bottomSheetDialog.dismiss();
        });

        bottomSheetView.findViewById(R.id.ivTelegram).setOnClickListener(v -> {
            shareToApp("org.telegram.messenger", shareText);
            bottomSheetDialog.dismiss();
        });

        bottomSheetView.findViewById(R.id.ivEmail).setOnClickListener(v -> {
            shareViaEmail(shareText);
            bottomSheetDialog.dismiss();
        });

        bottomSheetView.findViewById(R.id.ivCopy).setOnClickListener(v -> {
            copyToClipboard(shareText);
            bottomSheetDialog.dismiss();
        });

        bottomSheetView.findViewById(R.id.ivMore).setOnClickListener(v -> {
            shareGeneric(shareText);
            bottomSheetDialog.dismiss();
        });

        bottomSheetDialog.show();
    }

    private void shareToApp(String packageName, String text) {
        try {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.setPackage(packageName);
            intent.putExtra(Intent.EXTRA_TEXT, text);
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "App not installed", Toast.LENGTH_SHORT).show();
        }
    }

    private void shareViaEmail(String text) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "My Learning Progress");
        intent.putExtra(Intent.EXTRA_TEXT, text);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(Intent.createChooser(intent, "Share via Email"));
        } else {
            Toast.makeText(this, "No email app found", Toast.LENGTH_SHORT).show();
        }
    }

    private void copyToClipboard(String text) {
        android.content.ClipboardManager clipboard =
                (android.content.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        android.content.ClipData clip = android.content.ClipData.newPlainText("Profile Data", text);
        clipboard.setPrimaryClip(clip);

        Toast.makeText(this, "Profile data copied to clipboard", Toast.LENGTH_SHORT).show();
    }

    private void shareGeneric(String text) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, text);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(Intent.createChooser(intent, "Share Profile"));
        } else {
            Toast.makeText(this, "No sharing apps found", Toast.LENGTH_SHORT).show();
        }
    }

    private void animateElements() {
        // Animate profile card
        Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);
        cvProfile.startAnimation(slideUp);

        // Animate stat cards with staggered delay
        Animation fadeInDelay = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        fadeInDelay.setStartOffset(200);
        cvTotalQuestions.startAnimation(fadeInDelay);

        fadeInDelay = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        fadeInDelay.setStartOffset(300);
        cvCorrectAnswers.startAnimation(fadeInDelay);

        fadeInDelay = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        fadeInDelay.setStartOffset(400);
        cvIncorrectAnswers.startAnimation(fadeInDelay);

        fadeInDelay = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        fadeInDelay.setStartOffset(500);
        cvPaymentPlans.startAnimation(fadeInDelay);

        fadeInDelay = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        fadeInDelay.setStartOffset(600);
        btnShare.startAnimation(fadeInDelay);

        fadeInDelay = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        fadeInDelay.setStartOffset(700);
        btnHistory.startAnimation(fadeInDelay);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUserData(); // Refresh data when returning to profile
        loadAISummary(); // Refresh AI summary
    }
}
package com.example.personalizedlearning.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;
import com.example.personalizedlearning.R;
import com.example.personalizedlearning.models.User;
import com.example.personalizedlearning.utilities.QRCodeGenerator;
import com.example.personalizedlearning.utilities.SharedPrefManager;
import com.google.zxing.WriterException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ShareProfileActivity extends AppCompatActivity {
    private TextView tvUsername, tvTotalQuestions, tvCorrectAnswers, tvIncorrectAnswers;
    private ImageView ivBack, ivQRCode, ivWhatsApp, ivFacebook, ivTwitter, ivInstagram,
            ivTelegram, ivEmail, ivCopy, ivMore;
    private Button btnGenerateQR;
    private CardView cvProfile, cvShareOptions;
    private SharedPrefManager sharedPrefManager;
    private User currentUser;
    private String shareableProfileData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_profile);

        // Fade in animation for the entire activity
        findViewById(android.R.id.content).startAnimation(
                AnimationUtils.loadAnimation(this, R.anim.fade_in));

        sharedPrefManager = new SharedPrefManager(this);
        currentUser = sharedPrefManager.getUser();

        initializeViews();
        setupClickListeners();
        loadUserData();
        generateShareableData();
    }

    private void initializeViews() {
        tvUsername = findViewById(R.id.tvUsername);
        tvTotalQuestions = findViewById(R.id.tvTotalQuestions);
        tvCorrectAnswers = findViewById(R.id.tvCorrectAnswers);
        tvIncorrectAnswers = findViewById(R.id.tvIncorrectAnswers);
        ivBack = findViewById(R.id.ivBack);
        ivQRCode = findViewById(R.id.ivQRCode);
        btnGenerateQR = findViewById(R.id.btnGenerateQR);
        cvProfile = findViewById(R.id.cvProfile);
        cvShareOptions = findViewById(R.id.cvShareOptions);

        // Share platform icons
        ivWhatsApp = findViewById(R.id.ivWhatsApp);
        ivFacebook = findViewById(R.id.ivFacebook);
        ivTwitter = findViewById(R.id.ivTwitter);
        ivInstagram = findViewById(R.id.ivInstagram);
        ivTelegram = findViewById(R.id.ivTelegram);
        ivEmail = findViewById(R.id.ivEmail);
        ivCopy = findViewById(R.id.ivCopy);
        ivMore = findViewById(R.id.ivMore);
    }

    private void setupClickListeners() {
        ivBack.setOnClickListener(v -> {
            v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });

        btnGenerateQR.setOnClickListener(v -> {
            v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));
            generateQRCode();
        });

        ivWhatsApp.setOnClickListener(v -> shareToWhatsApp());
        ivFacebook.setOnClickListener(v -> shareToFacebook());
        ivTwitter.setOnClickListener(v -> shareToTwitter());
        ivInstagram.setOnClickListener(v -> shareToInstagram());
        ivTelegram.setOnClickListener(v -> shareToTelegram());
        ivEmail.setOnClickListener(v -> shareViaEmail());
        ivCopy.setOnClickListener(v -> copyToClipboard());
        ivMore.setOnClickListener(v -> shareGeneric());
    }

    private void loadUserData() {
        if (currentUser != null) {
            tvUsername.setText(currentUser.getUsername());

            int totalQuestions = sharedPrefManager.getTotalQuestions();
            int correctAnswers = sharedPrefManager.getCorrectAnswers();
            int incorrectAnswers = totalQuestions - correctAnswers;

            tvTotalQuestions.setText(String.valueOf(totalQuestions));
            tvCorrectAnswers.setText(String.valueOf(correctAnswers));
            tvIncorrectAnswers.setText(String.valueOf(incorrectAnswers));
        }
    }

    private void generateShareableData() {
        if (currentUser != null) {
            int totalQuestions = sharedPrefManager.getTotalQuestions();
            int correctAnswers = sharedPrefManager.getCorrectAnswers();
            double accuracy = totalQuestions > 0 ? ((double) correctAnswers / totalQuestions) * 100 : 0;

            shareableProfileData = String.format(
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
        }
    }

    private void generateQRCode() {
        try {
            QRCodeGenerator qrGenerator = new QRCodeGenerator();
            Bitmap qrBitmap = qrGenerator.generateQRCode(shareableProfileData, 300, 300);

            ivQRCode.setImageBitmap(qrBitmap);
            ivQRCode.setVisibility(View.VISIBLE);

            // Animate QR code appearance
            ivQRCode.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));

            Toast.makeText(this, "QR Code generated! Share it with others", Toast.LENGTH_SHORT).show();

        } catch (WriterException e) {
            Toast.makeText(this, "Error generating QR code", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void shareToWhatsApp() {
        try {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.setPackage("com.whatsapp");
            intent.putExtra(Intent.EXTRA_TEXT, shareableProfileData);
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "WhatsApp not installed", Toast.LENGTH_SHORT).show();
        }
    }

    private void shareToFacebook() {
        try {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.setPackage("com.facebook.katana");
            intent.putExtra(Intent.EXTRA_TEXT, shareableProfileData);
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Facebook not installed", Toast.LENGTH_SHORT).show();
        }
    }

    private void shareToTwitter() {
        try {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.setPackage("com.twitter.android");
            intent.putExtra(Intent.EXTRA_TEXT, shareableProfileData);
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Twitter not installed", Toast.LENGTH_SHORT).show();
        }
    }

    private void shareToInstagram() {
        try {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.setPackage("com.instagram.android");
            intent.putExtra(Intent.EXTRA_TEXT, shareableProfileData);
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Instagram not installed", Toast.LENGTH_SHORT).show();
        }
    }

    private void shareToTelegram() {
        try {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.setPackage("org.telegram.messenger");
            intent.putExtra(Intent.EXTRA_TEXT, shareableProfileData);
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Telegram not installed", Toast.LENGTH_SHORT).show();
        }
    }

    private void shareViaEmail() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "My Learning Progress");
        intent.putExtra(Intent.EXTRA_TEXT, shareableProfileData);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(Intent.createChooser(intent, "Share via Email"));
        } else {
            Toast.makeText(this, "No email app found", Toast.LENGTH_SHORT).show();
        }
    }

    private void copyToClipboard() {
        android.content.ClipboardManager clipboard =
                (android.content.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        android.content.ClipData clip = android.content.ClipData.newPlainText("Profile Data", shareableProfileData);
        clipboard.setPrimaryClip(clip);

        Toast.makeText(this, "Profile data copied to clipboard", Toast.LENGTH_SHORT).show();
    }

    private void shareGeneric() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, shareableProfileData);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(Intent.createChooser(intent, "Share Profile"));
        } else {
            Toast.makeText(this, "No sharing apps found", Toast.LENGTH_SHORT).show();
        }
    }
}
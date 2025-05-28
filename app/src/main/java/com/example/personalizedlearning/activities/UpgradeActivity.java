package com.example.personalizedlearning.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import com.example.personalizedlearning.R;
import com.example.personalizedlearning.utilities.SharedPrefManager;
import com.example.personalizedlearning.dialogs.StripePaymentBottomSheet;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;

public class UpgradeActivity extends AppCompatActivity implements StripePaymentBottomSheet.PaymentListener {
    private static final String STRIPE_PUBLISHABLE_KEY = "pk_test_your_stripe_publishable_key_here";

    private TextView tvUpgradeTitle;
    private ImageView ivBack;
    private CardView cvStarter, cvIntermediate, cvAdvanced;
    private Button btnPurchaseStarter, btnPurchaseIntermediate, btnPurchaseAdvanced;
    private SharedPrefManager sharedPrefManager;

    private PaymentSheet paymentSheet;
    private String selectedPlan = "";
    private String selectedPrice = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upgrade);

        // Initialize Stripe
        PaymentConfiguration.init(getApplicationContext(), STRIPE_PUBLISHABLE_KEY);
        paymentSheet = new PaymentSheet(this, this::onPaymentSheetResult);

        // Fade in animation for the entire activity
        findViewById(android.R.id.content).startAnimation(
                AnimationUtils.loadAnimation(this, R.anim.fade_in));

        sharedPrefManager = new SharedPrefManager(this);
        initializeViews();
        setupClickListeners();
        animateElements();
    }

    private void initializeViews() {
        tvUpgradeTitle = findViewById(R.id.tvUpgradeTitle);
        ivBack = findViewById(R.id.ivBack);
        cvStarter = findViewById(R.id.cvStarter);
        cvIntermediate = findViewById(R.id.cvIntermediate);
        cvAdvanced = findViewById(R.id.cvAdvanced);
        btnPurchaseStarter = findViewById(R.id.btnPurchaseStarter);
        btnPurchaseIntermediate = findViewById(R.id.btnPurchaseIntermediate);
        btnPurchaseAdvanced = findViewById(R.id.btnPurchaseAdvanced);
    }

    private void setupClickListeners() {
        ivBack.setOnClickListener(v -> {
            v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });

        btnPurchaseStarter.setOnClickListener(v -> {
            v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));
            showPaymentBottomSheet("Starter Plan", "9.99");
        });

        btnPurchaseIntermediate.setOnClickListener(v -> {
            v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));
            showPaymentBottomSheet("Intermediate Plan", "19.99");
        });

        btnPurchaseAdvanced.setOnClickListener(v -> {
            v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));
            showPaymentBottomSheet("Advanced Plan", "29.99");
        });
    }

    private void animateElements() {
        // Animate cards with staggered delay
        Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);
        slideUp.setStartOffset(100);
        cvStarter.startAnimation(slideUp);

        slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);
        slideUp.setStartOffset(200);
        cvIntermediate.startAnimation(slideUp);

        slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);
        slideUp.setStartOffset(300);
        cvAdvanced.startAnimation(slideUp);
    }

    private void showPaymentBottomSheet(String planName, String price) {
        selectedPlan = planName;
        selectedPrice = price;

        StripePaymentBottomSheet bottomSheet = StripePaymentBottomSheet.newInstance(planName, price);
        bottomSheet.setPaymentListener(this);
        bottomSheet.show(getSupportFragmentManager(), "PaymentBottomSheet");
    }

    @Override
    public void onPaymentInitiated(String planName, String price) {
        // Show loading
        findViewById(R.id.progressBar).setVisibility(View.VISIBLE);


        simulatePaymentProcess(planName, price);
    }

    @Override
    public void onPaymentCancelled() {
        Toast.makeText(this, "Payment cancelled", Toast.LENGTH_SHORT).show();
    }

    private void simulatePaymentProcess(String planName, String price) {
        // Simulate payment processing with a delay
        new android.os.Handler().postDelayed(() -> {
            findViewById(R.id.progressBar).setVisibility(View.GONE);

            // Clear any previous purchase and set the new one
            sharedPrefManager.setPurchasedPlan(planName);

            Toast.makeText(this, "Payment successful! You now have " + planName,
                    Toast.LENGTH_LONG).show();

            // Update UI to reflect purchase (this will reset other buttons)
            updatePurchaseStatus(planName);

        }, 2000);
    }

    private void updatePurchaseStatus(String purchasedPlan) {
        // Reset all buttons to default state first
        resetAllButtons();

        // Reset all cards to default color
        resetAllCards();

        // Update only the purchased plan button and card
        if (purchasedPlan.equals("Starter Plan")) {
            btnPurchaseStarter.setText("Purchased");
            btnPurchaseStarter.setEnabled(false);
            btnPurchaseStarter.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.green));
            cvStarter.setCardBackgroundColor(ContextCompat.getColor(this, R.color.green));
            btnPurchaseStarter.startAnimation(AnimationUtils.loadAnimation(this, R.anim.bounce));
        } else if (purchasedPlan.equals("Intermediate Plan")) {
            btnPurchaseIntermediate.setText("Purchased");
            btnPurchaseIntermediate.setEnabled(false);
            btnPurchaseIntermediate.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.green));
            cvIntermediate.setCardBackgroundColor(ContextCompat.getColor(this, R.color.green));
            btnPurchaseIntermediate.startAnimation(AnimationUtils.loadAnimation(this, R.anim.bounce));
        } else if (purchasedPlan.equals("Advanced Plan")) {
            btnPurchaseAdvanced.setText("Purchased");
            btnPurchaseAdvanced.setEnabled(false);
            btnPurchaseAdvanced.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.green));
            cvAdvanced.setCardBackgroundColor(ContextCompat.getColor(this, R.color.green));
            btnPurchaseAdvanced.startAnimation(AnimationUtils.loadAnimation(this, R.anim.bounce));
        }
    }

    private void resetAllButtons() {
        // Reset all buttons to default state with dark_green color
        btnPurchaseStarter.setText("Purchase");
        btnPurchaseStarter.setEnabled(true);
        btnPurchaseStarter.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.dark_green));

        btnPurchaseIntermediate.setText("Purchase");
        btnPurchaseIntermediate.setEnabled(true);
        btnPurchaseIntermediate.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.dark_green));

        btnPurchaseAdvanced.setText("Purchase");
        btnPurchaseAdvanced.setEnabled(true);
        btnPurchaseAdvanced.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.dark_green));
    }

    private void resetAllCards() {
        // Reset all cards to blue_light color
        cvStarter.setCardBackgroundColor(ContextCompat.getColor(this, R.color.blue_light));
        cvIntermediate.setCardBackgroundColor(ContextCompat.getColor(this, R.color.blue_light));
        cvAdvanced.setCardBackgroundColor(ContextCompat.getColor(this, R.color.blue_light));
    }

    private void onPaymentSheetResult(PaymentSheetResult paymentSheetResult) {
        if (paymentSheetResult instanceof PaymentSheetResult.Completed) {
            // Payment successful
            sharedPrefManager.setPurchasedPlan(selectedPlan);
            Toast.makeText(this, "Payment successful! You now have " + selectedPlan,
                    Toast.LENGTH_LONG).show();
            updatePurchaseStatus(selectedPlan);
        } else if (paymentSheetResult instanceof PaymentSheetResult.Canceled) {
            // Payment cancelled
            Toast.makeText(this, "Payment cancelled", Toast.LENGTH_SHORT).show();
        } else if (paymentSheetResult instanceof PaymentSheetResult.Failed) {
            // Payment failed
            PaymentSheetResult.Failed failed = (PaymentSheetResult.Failed) paymentSheetResult;
            Toast.makeText(this, "Payment failed: " + failed.getError().getLocalizedMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Check if user has already purchased any plan
        String purchasedPlan = sharedPrefManager.getPurchasedPlan();
        if (purchasedPlan != null && !purchasedPlan.isEmpty()) {
            updatePurchaseStatus(purchasedPlan);
        }
    }
}
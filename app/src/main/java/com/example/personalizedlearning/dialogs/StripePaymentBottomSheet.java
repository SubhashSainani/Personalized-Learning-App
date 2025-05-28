package com.example.personalizedlearning.dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.example.personalizedlearning.R;

public class StripePaymentBottomSheet extends BottomSheetDialogFragment {

    private static final String ARG_PLAN_NAME = "plan_name";
    private static final String ARG_PRICE = "price";

    private TextView tvPlanName, tvPrice, tvPlanDescription;
    private Button btnPayWithCard, btnPayWithGooglePay, btnCancel;
    private ImageView ivClose;

    private String planName;
    private String price;
    private PaymentListener paymentListener;

    public interface PaymentListener {
        void onPaymentInitiated(String planName, String price);
        void onPaymentCancelled();
    }

    public static StripePaymentBottomSheet newInstance(String planName, String price) {
        StripePaymentBottomSheet fragment = new StripePaymentBottomSheet();
        Bundle args = new Bundle();
        args.putString(ARG_PLAN_NAME, planName);
        args.putString(ARG_PRICE, price);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            planName = getArguments().getString(ARG_PLAN_NAME);
            price = getArguments().getString(ARG_PRICE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_stripe_payment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeViews(view);
        setupData();
        setupClickListeners();
    }

    private void initializeViews(View view) {
        tvPlanName = view.findViewById(R.id.tvPlanName);
        tvPrice = view.findViewById(R.id.tvPrice);
        tvPlanDescription = view.findViewById(R.id.tvPlanDescription);
        btnPayWithCard = view.findViewById(R.id.btnPayWithCard);
        btnPayWithGooglePay = view.findViewById(R.id.btnPayWithGooglePay);
        btnCancel = view.findViewById(R.id.btnCancel);
        ivClose = view.findViewById(R.id.ivClose);

        // Get the plan card to set its background dynamically
        CardView planCard = view.findViewById(R.id.cvPlanCard);

        // Set card background based on plan
        if (planName != null) {
            if (planName.equals("Intermediate Plan")) {
                planCard.setCardBackgroundColor(getResources().getColor(R.color.blue_light));
            } else {
                planCard.setCardBackgroundColor(getResources().getColor(R.color.blue_light));
            }
        }
    }

    private void setupData() {
        tvPlanName.setText(planName);
        tvPrice.setText("$" + price + "/month");

        // Set plan description based on plan name
        String description = getPlanDescription(planName);
        tvPlanDescription.setText(description);
    }

    private String getPlanDescription(String planName) {
        switch (planName) {
            case "Starter Plan":
                return "• Essential quiz generation\n• Basic analytics\n• Email support";
            case "Intermediate Plan":
                return "• Advanced quiz generation\n• Detailed analytics\n• Priority support\n• Custom topics";
            case "Advanced Plan":
                return "• Premium quiz generation\n• AI-powered insights\n• 24/7 support\n• Advanced customization\n• Export features";
            default:
                return "Premium features included";
        }
    }

    private void setupClickListeners() {
        ivClose.setOnClickListener(v -> {
            if (paymentListener != null) {
                paymentListener.onPaymentCancelled();
            }
            dismiss();
        });

        btnCancel.setOnClickListener(v -> {
            if (paymentListener != null) {
                paymentListener.onPaymentCancelled();
            }
            dismiss();
        });

        btnPayWithCard.setOnClickListener(v -> {
            if (paymentListener != null) {
                paymentListener.onPaymentInitiated(planName, price);
            }
            dismiss();
        });

        btnPayWithGooglePay.setOnClickListener(v -> {
            if (paymentListener != null) {
                paymentListener.onPaymentInitiated(planName, price);
            }
            dismiss();
        });
    }

    public void setPaymentListener(PaymentListener listener) {
        this.paymentListener = listener;
    }
}
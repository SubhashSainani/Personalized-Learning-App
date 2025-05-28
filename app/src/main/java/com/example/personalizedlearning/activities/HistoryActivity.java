package com.example.personalizedlearning.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.personalizedlearning.R;
import com.example.personalizedlearning.adapters.HistoryAdapter;
import com.example.personalizedlearning.models.HistoryItem;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HistoryActivity extends AppCompatActivity {
    private RecyclerView rvHistory;
    private TextView tvHistoryTitle;
    private ImageView ivBack;
    private List<HistoryItem> historyItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        // Fade in animation for the entire activity
        findViewById(android.R.id.content).startAnimation(
                AnimationUtils.loadAnimation(this, R.anim.fade_in));

        initializeViews();
        setupClickListeners();
        loadHistoryData();
        setupRecyclerView();
    }

    private void initializeViews() {
        rvHistory = findViewById(R.id.rvHistory);
        tvHistoryTitle = findViewById(R.id.tvHistoryTitle);
        ivBack = findViewById(R.id.ivBack);
    }

    private void setupClickListeners() {
        ivBack.setOnClickListener(v -> {
            v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });
    }

    private void loadHistoryData() {
        historyItems = new ArrayList<>();
        SharedPreferences prefs = getSharedPreferences("QuizPerformance", MODE_PRIVATE);
        Map<String, ?> allPrefs = prefs.getAll();

        for (Map.Entry<String, ?> entry : allPrefs.entrySet()) {
            String key = entry.getKey();
            if (key.endsWith("_attempts")) {
                String topic = key.replace("_attempts", "");
                int attempts = prefs.getInt(key, 0);
                int totalScore = prefs.getInt(topic + "_totalScore", 0);
                int totalPossible = prefs.getInt(topic + "_totalPossible", 0);

                if (attempts > 0) {
                    double accuracy = totalPossible > 0 ? ((double) totalScore / totalPossible) * 100 : 0;
                    HistoryItem item = new HistoryItem(topic, attempts, totalScore, totalPossible, accuracy);
                    historyItems.add(item);
                }
            }
        }

        // Sort by attempts (most recent first)
        historyItems.sort((a, b) -> Integer.compare(b.getAttempts(), a.getAttempts()));
    }

    private void setupRecyclerView() {
        rvHistory.setLayoutManager(new LinearLayoutManager(this));
        HistoryAdapter adapter = new HistoryAdapter(this, historyItems);
        rvHistory.setAdapter(adapter);

        // Add animation to RecyclerView
        rvHistory.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(
                this, R.anim.layout_animation_fall_down));
    }
}
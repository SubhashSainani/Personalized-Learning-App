package com.example.personalizedlearning.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.personalizedlearning.R;
import com.example.personalizedlearning.adapters.QuestionResultAdapter;
import com.example.personalizedlearning.models.QuizResult;

public class ResultsActivity extends AppCompatActivity {
    private TextView tvResultsTitle;
    private RecyclerView rvResults;
    private Button btnContinue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        // Fade in animation for results
        findViewById(android.R.id.content).startAnimation(
                AnimationUtils.loadAnimation(this, R.anim.fade_in));

        QuizResult result = getIntent().getParcelableExtra("quizResult");
        if (result == null) {
            finish();
            return;
        }

        tvResultsTitle = findViewById(R.id.tvResultsTitle);
        rvResults = findViewById(R.id.rvResults);
        btnContinue = findViewById(R.id.btnContinue);

        tvResultsTitle.setText("Your Results");

        rvResults.setLayoutManager(new LinearLayoutManager(this));
        QuestionResultAdapter adapter = new QuestionResultAdapter(result);
        rvResults.setAdapter(adapter);

        btnContinue.setOnClickListener(v -> {
            v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));
            startActivity(new Intent(this, HomeActivity.class));
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            finish();
        });
    }
}
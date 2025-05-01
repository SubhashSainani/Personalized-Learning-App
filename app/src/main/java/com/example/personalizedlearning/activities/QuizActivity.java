package com.example.personalizedlearning.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.personalizedlearning.R;
import com.example.personalizedlearning.adapters.QuizAdapter;
import com.example.personalizedlearning.models.Question;
import com.example.personalizedlearning.models.Quiz;
import com.example.personalizedlearning.models.QuizResult;
import java.util.ArrayList;
import java.util.List;

public class QuizActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private Button btnSubmit;
    private Quiz quiz;
    private List<String> userAnswers;
    private TextView tvGeneratedBy, tvTaskTitle, tvTaskDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        tvGeneratedBy = findViewById(R.id.tv_generated_by);
        tvTaskTitle = findViewById(R.id.tv_task_title);
        tvTaskDescription = findViewById(R.id.tv_task_description);

        quiz = getIntent().getParcelableExtra("quiz");
        if (quiz == null || quiz.getQuestions() == null || quiz.getQuestions().isEmpty()) {
            finish();
            return;
        }

        tvTaskTitle.setText("Generated Task 1");
        tvTaskDescription.setText("Test your knowledge about " + quiz.getTopic());

        userAnswers = new ArrayList<>(quiz.getQuestions().size());
        for (int i = 0; i < quiz.getQuestions().size(); i++) {
            userAnswers.add("");
        }

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new QuizAdapter(this, quiz.getQuestions(), userAnswers));

        btnSubmit = findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(v -> {
            v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));
            calculateResults();
        });
    }

    private void calculateResults() {
        int score = 0;
        for (int i = 0; i < quiz.getQuestions().size(); i++) {
            String userAnswer = userAnswers.get(i).trim();
            String correctAnswer = quiz.getQuestions().get(i).getCorrectAnswer().trim();

            if (userAnswer.equals(correctAnswer)) {
                score++;
            }
        }

        saveQuizPerformance(quiz.getTopic(), score, quiz.getQuestions().size());

        QuizResult result = new QuizResult(quiz, userAnswers, score);
        Intent intent = new Intent(this, ResultsActivity.class);
        intent.putExtra("quizResult", result);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void saveQuizPerformance(String topic, int score, int totalQuestions) {
        SharedPreferences prefs = getSharedPreferences("QuizPerformance", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        int attempts = prefs.getInt(topic + "_attempts", 0);
        editor.putInt(topic + "_attempts", attempts + 1);

        int totalScore = prefs.getInt(topic + "_totalScore", 0);
        editor.putInt(topic + "_totalScore", totalScore + score);

        int totalPossible = prefs.getInt(topic + "_totalPossible", 0);
        editor.putInt(topic + "_totalPossible", totalPossible + totalQuestions);

        editor.apply();
    }
}
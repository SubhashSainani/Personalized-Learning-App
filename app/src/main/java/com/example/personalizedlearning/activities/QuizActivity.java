package com.example.personalizedlearning.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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
import com.example.personalizedlearning.utilities.SharedPrefManager;
import java.util.ArrayList;
import java.util.List;

public class QuizActivity extends AppCompatActivity {
    private static final String TAG = "QuizActivity";
    private RecyclerView recyclerView;
    private Button btnSubmit;
    private Quiz quiz;
    private List<String> userAnswers;
    private TextView tvGeneratedBy, tvTaskTitle, tvTaskDescription;
    private SharedPrefManager sharedPrefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        sharedPrefManager = new SharedPrefManager(this);

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
        int totalQuestions = quiz.getQuestions().size();

        Log.d(TAG, "=== Quiz Results Calculation ===");
        Log.d(TAG, "Total Questions: " + totalQuestions);

        for (int i = 0; i < quiz.getQuestions().size(); i++) {
            Question question = quiz.getQuestions().get(i);
            String userAnswer = userAnswers.get(i).trim();
            String correctAnswer = question.getCorrectAnswer().trim();

            Log.d(TAG, "Question " + (i + 1) + ":");
            Log.d(TAG, "  User Answer: '" + userAnswer + "'");
            Log.d(TAG, "  Correct Answer: '" + correctAnswer + "'");

            // Enhanced answer comparison
            boolean isCorrect = false;

            if (userAnswer.equals(correctAnswer)) {
                // Direct match
                isCorrect = true;
            } else {
                // Check if user answer matches the option text for the correct letter
                List<String> options = question.getOptions();
                if (correctAnswer.length() == 1 && correctAnswer.matches("[ABCD]")) {
                    int correctIndex = correctAnswer.charAt(0) - 'A';
                    if (correctIndex >= 0 && correctIndex < options.size()) {
                        String correctOptionText = options.get(correctIndex);
                        if (userAnswer.equals(correctOptionText)) {
                            isCorrect = true;
                        }
                    }
                } else if (options.contains(userAnswer) && correctAnswer.equals(userAnswer)) {
                    // Direct text match
                    isCorrect = true;
                }
            }

            if (isCorrect) {
                score++;
                Log.d(TAG, "  Result: CORRECT ✓");
            } else {
                Log.d(TAG, "  Result: INCORRECT ✗");
            }
        }

        Log.d(TAG, "Final Score: " + score + "/" + totalQuestions);
        Log.d(TAG, "Accuracy: " + ((double)score/totalQuestions * 100) + "%");

        // Update statistics in SharedPrefManager
        sharedPrefManager.updateQuizStats(totalQuestions, score);

        // Log updated statistics
        Log.d(TAG, "=== Updated Statistics ===");
        Log.d(TAG, "Total Questions (All Time): " + sharedPrefManager.getTotalQuestions());
        Log.d(TAG, "Correct Answers (All Time): " + sharedPrefManager.getCorrectAnswers());

        // Save topic-specific performance
        saveQuizPerformance(quiz.getTopic(), score, totalQuestions);

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

        Log.d(TAG, "=== Topic Performance Saved ===");
        Log.d(TAG, "Topic: " + topic);
        Log.d(TAG, "Attempts: " + (attempts + 1));
        Log.d(TAG, "Total Score: " + (totalScore + score));
        Log.d(TAG, "Total Possible: " + (totalPossible + totalQuestions));
    }
}
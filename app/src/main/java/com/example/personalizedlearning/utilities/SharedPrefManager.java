package com.example.personalizedlearning.utilities;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.personalizedlearning.models.User;
import com.google.gson.Gson;

public class SharedPrefManager {
    private static final String PREF_NAME = "PersonalizedLearningPref";
    private static final String KEY_USER = "user";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_TOTAL_QUESTIONS = "totalQuestions";
    private static final String KEY_CORRECT_ANSWERS = "correctAnswers";
    private static final String KEY_PURCHASED_PLAN = "purchasedPlan";

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context context;
    private Gson gson = new Gson();

    public SharedPrefManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void saveUser(User user) {
        String userJson = gson.toJson(user);
        editor.putString(KEY_USER, userJson);
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.apply();
    }

    public User getUser() {
        String userJson = pref.getString(KEY_USER, null);
        if (userJson != null) {
            return gson.fromJson(userJson, User.class);
        }
        return null;
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public void logout() {
        editor.clear();
        editor.apply();
    }

    // New methods for statistics tracking
    public void updateQuizStats(int questionsAnswered, int correctAnswers) {
        int currentTotal = getTotalQuestions();
        int currentCorrect = getCorrectAnswers();

        editor.putInt(KEY_TOTAL_QUESTIONS, currentTotal + questionsAnswered);
        editor.putInt(KEY_CORRECT_ANSWERS, currentCorrect + correctAnswers);
        editor.apply();
    }

    public int getTotalQuestions() {
        return pref.getInt(KEY_TOTAL_QUESTIONS, 0);
    }

    public int getCorrectAnswers() {
        return pref.getInt(KEY_CORRECT_ANSWERS, 0);
    }

    // Methods for purchased plans
    public void setPurchasedPlan(String planName) {
        editor.putString(KEY_PURCHASED_PLAN, planName);
        editor.apply();
    }

    public String getPurchasedPlan() {
        return pref.getString(KEY_PURCHASED_PLAN, "");
    }

    public boolean hasPurchasedPlan() {
        String plan = getPurchasedPlan();
        return plan != null && !plan.isEmpty();
    }
}
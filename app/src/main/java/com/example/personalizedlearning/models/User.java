package com.example.personalizedlearning.models;

import java.util.List;

public class User {
    private String username;
    private String email;
    private String phone;
    private String password;
    private List<String> interests;

    // Additional fields for subscription and stats (optional)
    private String subscriptionPlan = "free";
    private int totalQuestions = 0;
    private int correctAnswers = 0;
    private double accuracy = 0.0;

    public User(String username, String email, String phone, String password) {
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.password = password;
    }

    // Existing getters and setters
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getPassword() { return password; }
    public List<String> getInterests() { return interests; }

    public void setInterests(List<String> interests) { this.interests = interests; }

    // Additional getters and setters for subscription features
    public String getSubscriptionPlan() { return subscriptionPlan; }
    public void setSubscriptionPlan(String subscriptionPlan) { this.subscriptionPlan = subscriptionPlan; }

    public int getTotalQuestions() { return totalQuestions; }
    public void setTotalQuestions(int totalQuestions) { this.totalQuestions = totalQuestions; }

    public int getCorrectAnswers() { return correctAnswers; }
    public void setCorrectAnswers(int correctAnswers) { this.correctAnswers = correctAnswers; }

    public double getAccuracy() { return accuracy; }
    public void setAccuracy(double accuracy) { this.accuracy = accuracy; }

    // Utility methods
    public void updateStats(int questionsAnswered, int correctAnswers) {
        this.totalQuestions += questionsAnswered;
        this.correctAnswers += correctAnswers;
        this.accuracy = (this.totalQuestions > 0) ?
                (double) this.correctAnswers / this.totalQuestions * 100 : 0.0;
    }

    public boolean isPremiumUser() {
        return subscriptionPlan != null &&
                !subscriptionPlan.equals("free") &&
                !subscriptionPlan.isEmpty();
    }
}
package com.example.personalizedlearning.models;

public class HistoryItem {
    private String topic;
    private int attempts;
    private int totalScore;
    private int totalPossible;
    private double accuracy;

    public HistoryItem(String topic, int attempts, int totalScore, int totalPossible, double accuracy) {
        this.topic = topic;
        this.attempts = attempts;
        this.totalScore = totalScore;
        this.totalPossible = totalPossible;
        this.accuracy = accuracy;
    }

    // Getters
    public String getTopic() { return topic; }
    public int getAttempts() { return attempts; }
    public int getTotalScore() { return totalScore; }
    public int getTotalPossible() { return totalPossible; }
    public double getAccuracy() { return accuracy; }

    // Setters
    public void setTopic(String topic) { this.topic = topic; }
    public void setAttempts(int attempts) { this.attempts = attempts; }
    public void setTotalScore(int totalScore) { this.totalScore = totalScore; }
    public void setTotalPossible(int totalPossible) { this.totalPossible = totalPossible; }
    public void setAccuracy(double accuracy) { this.accuracy = accuracy; }
}
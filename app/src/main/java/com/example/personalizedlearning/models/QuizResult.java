package com.example.personalizedlearning.models;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.List;

public class QuizResult implements Parcelable {
    private Quiz quiz;
    private List<String> userAnswers;
    private int score;

    public QuizResult(Quiz quiz, List<String> userAnswers, int score) {
        this.quiz = quiz;
        this.userAnswers = userAnswers;
        this.score = score;
    }

    protected QuizResult(Parcel in) {
        quiz = in.readParcelable(Quiz.class.getClassLoader());
        userAnswers = in.createStringArrayList();
        score = in.readInt();
    }

    public static final Creator<QuizResult> CREATOR = new Creator<QuizResult>() {
        @Override
        public QuizResult createFromParcel(Parcel in) {
            return new QuizResult(in);
        }

        @Override
        public QuizResult[] newArray(int size) {
            return new QuizResult[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(quiz, flags);
        dest.writeStringList(userAnswers);
        dest.writeInt(score);
    }

    // Getters
    public Quiz getQuiz() { return quiz; }
    public List<String> getUserAnswers() { return userAnswers; }
    public int getScore() { return score; }
}
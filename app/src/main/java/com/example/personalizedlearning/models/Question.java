package com.example.personalizedlearning.models;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.List;

public class Question implements Parcelable {
    private String question;
    private List<String> options;
    private String correctAnswer;
    private String explanation;

    public Question(String question, List<String> options, String correctAnswer, String explanation) {
        this.question = question;
        this.options = options;
        this.correctAnswer = correctAnswer;
        this.explanation = explanation;
    }

    protected Question(Parcel in) {
        question = in.readString();
        options = in.createStringArrayList();
        correctAnswer = in.readString();
        explanation = in.readString();
    }

    public static final Creator<Question> CREATOR = new Creator<Question>() {
        @Override
        public Question createFromParcel(Parcel in) {
            return new Question(in);
        }

        @Override
        public Question[] newArray(int size) {
            return new Question[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(question);
        dest.writeStringList(options);
        dest.writeString(correctAnswer);
        dest.writeString(explanation);
    }


    // Getters
    public String getQuestion() { return question; }
    public List<String> getOptions() { return options; }
    public String getCorrectAnswer() { return correctAnswer; }
    public String getExplanation() { return explanation; }
}
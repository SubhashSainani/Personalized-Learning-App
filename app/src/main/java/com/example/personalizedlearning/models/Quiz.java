package com.example.personalizedlearning.models;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.List;

public class Quiz implements Parcelable {
    private String topic;
    private List<Question> questions;

    public Quiz(String topic, List<Question> questions) {
        this.topic = topic;
        this.questions = questions;
    }

    protected Quiz(Parcel in) {
        topic = in.readString();
        questions = in.createTypedArrayList(Question.CREATOR);
    }

    public static final Creator<Quiz> CREATOR = new Creator<Quiz>() {
        @Override
        public Quiz createFromParcel(Parcel in) {
            return new Quiz(in);
        }

        @Override
        public Quiz[] newArray(int size) {
            return new Quiz[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(topic);
        dest.writeTypedList(questions);
    }

    // Getters
    public String getTopic() { return topic; }
    public List<Question> getQuestions() { return questions; }
}
package com.example.personalizedlearning.utilities;

import com.example.personalizedlearning.models.Question;
import com.example.personalizedlearning.models.Quiz;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DummyData {
    private static final Map<String, List<Question>> TOPIC_QUESTIONS = new HashMap<>();

    static {
        TOPIC_QUESTIONS.put("Algorithms", Arrays.asList(
                new Question("What is binary search time complexity?",
                        Arrays.asList("O(n)", "O(log n)", "O(n²)", "O(1)"),
                        "B", "Dummy: Binary search divides the search space in half each time")
        ));

        TOPIC_QUESTIONS.put("Data Structures", Arrays.asList(
                new Question("Which uses LIFO principle?",
                        Arrays.asList("Queue", "Stack", "Array", "Linked List"),
                        "B", "Dummy: Stack uses Last-In-First-Out")
        ));

        // Add more topics matching your interest buttons
    }

    public static Quiz getDummyQuiz(String topic) {
        if (TOPIC_QUESTIONS.containsKey(topic)) {
            return new Quiz(topic, new ArrayList<>(TOPIC_QUESTIONS.get(topic)));
        }
        return null;
    }
}
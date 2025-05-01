package com.example.personalizedlearning.services;

import android.content.Context;
import android.util.Log;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.personalizedlearning.models.Question;
import com.example.personalizedlearning.models.Quiz;
import com.example.personalizedlearning.utilities.DummyData;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class ApiService {
    private static final String TAG = "ApiService";
    private static final String BASE_URL = "http://10.0.2.2:5000/";
    private final RequestQueue requestQueue;
    private final Context context;

    public ApiService(Context context) {
        this.context = context;
        this.requestQueue = Volley.newRequestQueue(context);
    }

    public void getQuiz(String topic, final QuizCallback callback) {
        String url = BASE_URL + "getQuiz?topic=" + topic;
        Log.d(TAG, "Making request to: " + url);

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET, url, null,
                response -> {
                    try {
                        Log.d(TAG, "Response received: " + response.toString());
                        List<Question> questions = parseQuizResponse(response);
                        Quiz quiz = new Quiz(topic, questions);
                        callback.onSuccess(quiz);
                    } catch (JSONException e) {
                        Log.e(TAG, "JSON parsing error", e);
                        Quiz dummyQuiz = DummyData.getDummyQuiz(topic);
                        if (dummyQuiz != null) {
                            callback.onSuccess(dummyQuiz);
                        } else {
                            callback.onError("Error parsing quiz data");
                        }
                    }
                },
                error -> {
                    String errorMsg = "Unknown error";
                    if (error.networkResponse != null) {
                        errorMsg = "HTTP " + error.networkResponse.statusCode;
                        if (error.networkResponse.data != null) {
                            errorMsg += ": " + new String(error.networkResponse.data);
                        }
                    } else if (error.getMessage() != null) {
                        errorMsg = error.getMessage();
                    }
                    Log.e(TAG, "Error fetching quiz: " + errorMsg);

                    Quiz dummyQuiz = DummyData.getDummyQuiz(topic);
                    if (dummyQuiz != null) {
                        callback.onSuccess(dummyQuiz);
                    } else {
                        callback.onError("Server error: " + errorMsg);
                    }
                }
        );

        request.setRetryPolicy(new DefaultRetryPolicy(
                30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(request);
    }

    private List<Question> parseQuizResponse(JSONObject response) throws JSONException {
        List<Question> questions = new ArrayList<>();
        JSONArray quizArray = response.getJSONArray("quiz");

        for (int i = 0; i < quizArray.length(); i++) {
            JSONObject quizQuestion = quizArray.getJSONObject(i);
            String question = quizQuestion.getString("question");
            String correctAnswer = quizQuestion.getString("correct_answer");
            JSONArray optionsArray = quizQuestion.getJSONArray("options");

            List<String> options = new ArrayList<>();
            for (int j = 0; j < optionsArray.length(); j++) {
                options.add(optionsArray.getString(j));
            }

            String explanation = quizQuestion.optString("explanation", "No explanation provided");
            questions.add(new Question(question, options, correctAnswer, explanation));
        }

        return questions;
    }

    public interface QuizCallback {
        void onSuccess(Quiz quiz);
        void onError(String errorMessage);
    }
}
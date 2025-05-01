package com.example.personalizedlearning.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.personalizedlearning.R;
import com.example.personalizedlearning.models.QuizResult;
import com.example.personalizedlearning.models.Question;
import java.util.List;

public class QuestionResultAdapter extends RecyclerView.Adapter<QuestionResultAdapter.QuestionResultViewHolder> {
    private final QuizResult quizResult;
    private int lastPosition = -1;

    public QuestionResultAdapter(QuizResult quizResult) {
        this.quizResult = quizResult;
    }

    @NonNull
    @Override
    public QuestionResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_question_result, parent, false);
        return new QuestionResultViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionResultViewHolder holder, int position) {
        Question question = quizResult.getQuiz().getQuestions().get(position);
        String correctAnswer = getFullAnswerText(question);

        holder.tvQuestionNumber.setText((position + 1) + ".");
        holder.tvQuestionTitle.setText("Question " + (position + 1));
        holder.tvCorrectAnswer.setText(correctAnswer);

        // Animation for each item
        setAnimation(holder.itemView, position);
    }

    private void setAnimation(View viewToAnimate, int position) {
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(viewToAnimate.getContext(),
                    position % 2 == 0 ? R.anim.slide_in_left : R.anim.slide_in_right);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    private String getFullAnswerText(Question question) {
        String correctOption = question.getCorrectAnswer();
        List<String> options = question.getOptions();

        if (correctOption.equals("A") && options.size() > 0) {
            return "Correct: " + options.get(0);
        } else if (correctOption.equals("B") && options.size() > 1) {
            return "Correct: " + options.get(1);
        } else if (correctOption.equals("C") && options.size() > 2) {
            return "Correct: " + options.get(2);
        } else if (correctOption.equals("D") && options.size() > 3) {
            return "Correct: " + options.get(3);
        }
        return "Correct: " + correctOption;
    }

    @Override
    public int getItemCount() {
        return quizResult.getQuiz().getQuestions().size();
    }

    static class QuestionResultViewHolder extends RecyclerView.ViewHolder {
        TextView tvQuestionNumber, tvQuestionTitle, tvCorrectAnswer;

        public QuestionResultViewHolder(@NonNull View itemView) {
            super(itemView);
            tvQuestionNumber = itemView.findViewById(R.id.tv_question_number);
            tvQuestionTitle = itemView.findViewById(R.id.tv_question_title);
            tvCorrectAnswer = itemView.findViewById(R.id.tv_correct_answer);
        }
    }
}
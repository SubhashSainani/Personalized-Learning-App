package com.example.personalizedlearning.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.personalizedlearning.R;
import com.example.personalizedlearning.models.Question;

import java.util.ArrayList;
import java.util.List;

public class QuizAdapter extends RecyclerView.Adapter<QuizAdapter.QuestionViewHolder> {
    private static final String TAG = "QuizAdapter";
    private Context context;
    private List<Question> questions;
    private List<String> userAnswers;
    private List<Boolean> expandedStates;
    private int lastExpandedPosition = 0;

    public QuizAdapter(Context context, List<Question> questions, List<String> userAnswers) {
        this.context = context;
        this.questions = questions;
        this.userAnswers = userAnswers;
        this.expandedStates = new ArrayList<>();

        for (int i = 0; i < questions.size(); i++) {
            expandedStates.add(i == 0);
        }
    }

    @Override
    public QuestionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_question, parent, false);
        return new QuestionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(QuestionViewHolder holder, int position) {
        Question question = questions.get(position);

        holder.tv_question_number.setText((position + 1) + ".");
        holder.tvQuestion.setText("Question " + (position + 1));
        holder.tv_question_text.setText(question.getQuestion());

        // Set initial rotation
        holder.iv_question_status.setRotation(expandedStates.get(position) ? 90 : 0);

        // Set initial visibility
        holder.rgOptions.setVisibility(expandedStates.get(position) ? View.VISIBLE : View.GONE);

        // Single click handler for the entire header
        holder.questionHeader.setOnClickListener(v -> {
            int currentPosition = holder.getAdapterPosition();
            if (currentPosition == RecyclerView.NO_POSITION) return;

            if (expandedStates.get(currentPosition)) {
                // Collapse
                animateCollapse(holder.rgOptions);
                rotateArrow(holder.iv_question_status, false);
                expandedStates.set(currentPosition, false);
            } else if (canExpand(currentPosition)) {
                if (lastExpandedPosition != currentPosition) {
                    // Collapse previous
                    expandedStates.set(lastExpandedPosition, false);
                    notifyItemChanged(lastExpandedPosition);
                }
                // Expand current
                animateExpand(holder.rgOptions);
                rotateArrow(holder.iv_question_status, true);
                expandedStates.set(currentPosition, true);
                lastExpandedPosition = currentPosition;
            } else {
                Animation shake = AnimationUtils.loadAnimation(context, R.anim.shake);
                holder.questionHeader.startAnimation(shake);
                Toast.makeText(context, "Please answer the previous question first", Toast.LENGTH_SHORT).show();
            }
        });

        List<String> options = question.getOptions();
        holder.rbOption1.setText(options.get(0));
        holder.rbOption2.setText(options.get(1));
        holder.rbOption3.setText(options.get(2));

        if (options.size() > 3) {
            holder.rbOption4.setVisibility(View.VISIBLE);
            holder.rbOption4.setText(options.get(3));
        } else {
            holder.rbOption4.setVisibility(View.GONE);
        }

        // Set checked state based on stored answer
        holder.rgOptions.setOnCheckedChangeListener(null);
        String selectedAnswer = userAnswers.get(position);

        // Clear all selections first
        holder.rgOptions.clearCheck();

        // Set the correct selection
        if (!selectedAnswer.isEmpty()) {
            if (selectedAnswer.equals(options.get(0))) {
                holder.rbOption1.setChecked(true);
            } else if (selectedAnswer.equals(options.get(1))) {
                holder.rbOption2.setChecked(true);
            } else if (selectedAnswer.equals(options.get(2))) {
                holder.rbOption3.setChecked(true);
            } else if (options.size() > 3 && selectedAnswer.equals(options.get(3))) {
                holder.rbOption4.setChecked(true);
            }
        }

        // Handle answer selection
        holder.rgOptions.setOnCheckedChangeListener((group, checkedId) -> {
            int pos = holder.getAdapterPosition();
            if (pos == RecyclerView.NO_POSITION) return;

            String selectedOption = "";

            if (checkedId == holder.rbOption1.getId()) {
                selectedOption = holder.rbOption1.getText().toString();
            } else if (checkedId == holder.rbOption2.getId()) {
                selectedOption = holder.rbOption2.getText().toString();
            } else if (checkedId == holder.rbOption3.getId()) {
                selectedOption = holder.rbOption3.getText().toString();
            } else if (checkedId == holder.rbOption4.getId()) {
                selectedOption = holder.rbOption4.getText().toString();
            }

            // Store the selected option text (not the letter)
            userAnswers.set(pos, selectedOption);

            Log.d(TAG, "Question " + (pos + 1) + " answered:");
            Log.d(TAG, "  Selected: '" + selectedOption + "'");
            Log.d(TAG, "  Correct Answer: '" + questions.get(pos).getCorrectAnswer() + "'");

            // Auto-expand next question if available
            if (pos < questions.size() - 1 && !expandedStates.get(pos + 1)) {
                expandedStates.set(pos + 1, true);
                notifyItemChanged(pos + 1);
            }
        });

        // Entry animation - Fix for lastExpandedPosition logic
        if (position == 0 || expandedStates.get(position)) {
            Animation animation = AnimationUtils.loadAnimation(context,
                    position % 2 == 0 ? R.anim.slide_in_right : R.anim.slide_in_left);
            holder.itemView.startAnimation(animation);
        }
    }

    private void rotateArrow(ImageView imageView, boolean expand) {
        float fromDegree = imageView.getRotation();
        float toDegree = expand ? 90 : 0;

        RotateAnimation rotate = new RotateAnimation(
                fromDegree, toDegree,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(200);
        rotate.setFillAfter(true);
        imageView.startAnimation(rotate);
    }

    private void animateExpand(View view) {
        view.setVisibility(View.VISIBLE);
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.slide_down);
        view.startAnimation(animation);
    }

    private void animateCollapse(View view) {
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.slide_up);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        view.startAnimation(animation);
    }

    private boolean canExpand(int position) {
        return position == 0 || !userAnswers.get(position - 1).isEmpty();
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }

    @Override
    public void onViewDetachedFromWindow(QuestionViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.itemView.clearAnimation();
    }

    public static class QuestionViewHolder extends RecyclerView.ViewHolder {
        TextView tvQuestion, tv_question_number, tv_question_text;
        ImageView iv_question_status;
        RadioGroup rgOptions;
        RadioButton rbOption1, rbOption2, rbOption3, rbOption4;
        LinearLayout questionHeader;

        public QuestionViewHolder(View itemView) {
            super(itemView);
            tvQuestion = itemView.findViewById(R.id.tvQuestion);
            tv_question_number = itemView.findViewById(R.id.tv_question_number);
            tv_question_text = itemView.findViewById(R.id.tv_question_text);
            iv_question_status = itemView.findViewById(R.id.iv_question_status);
            rgOptions = itemView.findViewById(R.id.rgOptions);
            rbOption1 = itemView.findViewById(R.id.rbOption1);
            rbOption2 = itemView.findViewById(R.id.rbOption2);
            rbOption3 = itemView.findViewById(R.id.rbOption3);
            rbOption4 = itemView.findViewById(R.id.rbOption4);
            questionHeader = itemView.findViewById(R.id.question_header);
        }
    }
}
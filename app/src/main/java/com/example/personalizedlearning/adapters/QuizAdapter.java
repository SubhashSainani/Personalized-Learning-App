package com.example.personalizedlearning.adapters;

import android.content.Context;
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
            if (expandedStates.get(position)) {
                // Collapse
                animateCollapse(holder.rgOptions);
                rotateArrow(holder.iv_question_status, false);
                expandedStates.set(position, false);
            } else if (canExpand(position)) {
                if (lastExpandedPosition != position) {
                    // Collapse previous
                    expandedStates.set(lastExpandedPosition, false);
                    notifyItemChanged(lastExpandedPosition);
                }
                // Expand current
                animateExpand(holder.rgOptions);
                rotateArrow(holder.iv_question_status, true);
                expandedStates.set(position, true);
                lastExpandedPosition = position;
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

        // Set checked state
        holder.rgOptions.setOnCheckedChangeListener(null);
        String selectedAnswer = userAnswers.get(position);
        if (selectedAnswer.equals(holder.rbOption1.getText().toString())) {
            holder.rbOption1.setChecked(true);
        } else if (selectedAnswer.equals(holder.rbOption2.getText().toString())) {
            holder.rbOption2.setChecked(true);
        } else if (selectedAnswer.equals(holder.rbOption3.getText().toString())) {
            holder.rbOption3.setChecked(true);
        } else if (selectedAnswer.equals(holder.rbOption4.getText().toString())) {
            holder.rbOption4.setChecked(true);
        } else {
            holder.rgOptions.clearCheck();
        }

        // Handle answer selection
        holder.rgOptions.setOnCheckedChangeListener((group, checkedId) -> {
            int pos = holder.getAdapterPosition();
            if (checkedId == holder.rbOption1.getId()) {
                userAnswers.set(pos, holder.rbOption1.getText().toString());
            } else if (checkedId == holder.rbOption2.getId()) {
                userAnswers.set(pos, holder.rbOption2.getText().toString());
            } else if (checkedId == holder.rbOption3.getId()) {
                userAnswers.set(pos, holder.rbOption3.getText().toString());
            } else if (checkedId == holder.rbOption4.getId()) {
                userAnswers.set(pos, holder.rbOption4.getText().toString());
            }

            // Auto-expand next question if available
            if (pos < questions.size() - 1 && !expandedStates.get(pos + 1)) {
                expandedStates.set(pos + 1, true);
                notifyItemChanged(pos + 1);
            }
        });

        // Entry animation
        if (position > lastExpandedPosition) {
            Animation animation = AnimationUtils.loadAnimation(context,
                    position % 2 == 0 ? R.anim.slide_in_right : R.anim.slide_in_left);
            holder.itemView.startAnimation(animation);
            lastExpandedPosition = position;
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
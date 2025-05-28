package com.example.personalizedlearning.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.personalizedlearning.R;
import com.example.personalizedlearning.models.HistoryItem;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {
    private Context context;
    private List<HistoryItem> historyItems;
    private int lastPosition = -1;

    public HistoryAdapter(Context context, List<HistoryItem> historyItems) {
        this.context = context;
        this.historyItems = historyItems;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_history, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        HistoryItem item = historyItems.get(position);

        holder.tvQuestionNumber.setText(String.valueOf(position + 1) + ".");
        holder.tvTopicName.setText("Question " + (position + 1));
        holder.tvTopicDescription.setText("You last did " + item.getAttempts() + " questions from " + item.getTopic());

        // Set status based on accuracy
        if (item.getAccuracy() >= 80) {
            holder.ivStatus.setImageResource(R.drawable.ic_check_green);
        } else if (item.getAccuracy() >= 60) {
            holder.ivStatus.setImageResource(R.drawable.ic_check_yellow);
        } else {
            holder.ivStatus.setImageResource(R.drawable.ic_close_red);
        }

        // Set additional info
        holder.tvAnswer.setText(String.format("Score: %d/%d (%.1f%% accuracy)",
                item.getTotalScore(), item.getTotalPossible(), item.getAccuracy()));

        // Animation for each item
        setAnimation(holder.itemView, position);
    }

    private void setAnimation(View viewToAnimate, int position) {
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(context,
                    position % 2 == 0 ? R.anim.slide_in_left : R.anim.slide_in_right);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    @Override
    public int getItemCount() {
        return historyItems.size();
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull HistoryViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.itemView.clearAnimation();
    }

    static class HistoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvQuestionNumber, tvTopicName, tvTopicDescription, tvAnswer;
        ImageView ivStatus;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvQuestionNumber = itemView.findViewById(R.id.tv_question_number);
            tvTopicName = itemView.findViewById(R.id.tv_topic_name);
            tvTopicDescription = itemView.findViewById(R.id.tv_topic_description);
            tvAnswer = itemView.findViewById(R.id.tv_answer);
            ivStatus = itemView.findViewById(R.id.iv_status);
        }
    }
}
package com.example.dailyplanner.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dailyplanner.R;
import com.example.dailyplanner.model.Task;

import java.util.ArrayList;
import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    public interface OnTaskStatusChangeListener {
        void onTaskStatusChanged(Task task, boolean isCompleted);
    }

    private List<Task> tasks = new ArrayList<>();
    private final boolean isHistoryMode;
    private OnTaskStatusChangeListener listener;

    public TaskAdapter(boolean isHistoryMode, OnTaskStatusChangeListener listener) {
        this.isHistoryMode = isHistoryMode;
        this.listener = listener;
    }

    public void setTasks(List<Task> newTasks) {
        this.tasks = new ArrayList<>(newTasks);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = tasks.get(position);
        holder.bind(task);
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    class TaskViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvTitle;
        private final TextView tvDesc;
        private final TextView tvTime;
        private final TextView tvStatusBadge;
        private final CheckBox cbDone;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_item_title);
            tvDesc = itemView.findViewById(R.id.tv_item_desc);
            tvTime = itemView.findViewById(R.id.tv_item_time);
            tvStatusBadge = itemView.findViewById(R.id.tv_status_badge);
            cbDone = itemView.findViewById(R.id.cb_task_done);
        }

        public void bind(Task task) {
            tvTitle.setText(task.getTitle());

            if (task.getDescription() != null && !task.getDescription().trim().isEmpty()) {
                tvDesc.setVisibility(View.VISIBLE);
                tvDesc.setText(task.getDescription());
            } else {
                tvDesc.setVisibility(View.GONE);
            }

            if (task.getTime() != null && !task.getTime().trim().isEmpty()) {
                tvTime.setVisibility(View.VISIBLE);
                tvTime.setText("⏰ " + task.getTime());
            } else {
                tvTime.setVisibility(View.GONE);
            }

            if (isHistoryMode) {
                cbDone.setVisibility(View.GONE);
                tvStatusBadge.setVisibility(View.VISIBLE);
            } else {
                tvStatusBadge.setVisibility(View.GONE);
                cbDone.setVisibility(View.VISIBLE);

                cbDone.setOnCheckedChangeListener(null);
                cbDone.setChecked(task.isCompleted());

                cbDone.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (listener != null) {
                        listener.onTaskStatusChanged(task, isChecked);
                    }
                });
            }
        }
    }
}

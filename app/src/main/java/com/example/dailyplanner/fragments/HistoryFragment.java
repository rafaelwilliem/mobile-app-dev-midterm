package com.example.dailyplanner.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dailyplanner.R;
import com.example.dailyplanner.adapter.TaskAdapter;
import com.example.dailyplanner.model.Task;
import com.example.dailyplanner.repository.TaskManager;

import java.util.List;

public class HistoryFragment extends Fragment {

    private RecyclerView rvHistory;
    private TextView tvEmptyHistory;
    private TaskAdapter taskAdapter;

    private final TaskManager.OnDataChangeListener dataChangeListener = this::loadHistory;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        rvHistory = view.findViewById(R.id.rv_history);
        tvEmptyHistory = view.findViewById(R.id.tv_empty_history);

        setupRecyclerView();
        loadHistory();

        return view;
    }

    private void setupRecyclerView() {
        taskAdapter = new TaskAdapter(true, null);
        rvHistory.setLayoutManager(new LinearLayoutManager(getContext()));
        rvHistory.setAdapter(taskAdapter);
    }

    private void loadHistory() {
        List<Task> completedTasks = TaskManager.getInstance().getCompletedTasks();
        if (taskAdapter != null) {
            taskAdapter.setTasks(completedTasks);
        }

        if (tvEmptyHistory != null && rvHistory != null) {
            if (completedTasks.isEmpty()) {
                tvEmptyHistory.setVisibility(View.VISIBLE);
                rvHistory.setVisibility(View.GONE);
            } else {
                tvEmptyHistory.setVisibility(View.GONE);
                rvHistory.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadHistory();
        TaskManager.getInstance().addListener(dataChangeListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        TaskManager.getInstance().removeListener(dataChangeListener);
    }
}

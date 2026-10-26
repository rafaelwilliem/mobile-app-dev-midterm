package com.example.dailyplanner.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dailyplanner.AddTaskActivity;
import com.example.dailyplanner.R;
import com.example.dailyplanner.adapter.TaskAdapter;
import com.example.dailyplanner.model.Task;
import com.example.dailyplanner.repository.TaskManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class TaskListFragment extends Fragment {

    private RecyclerView rvTasks;
    private TextView tvEmptyTasks;
    private FloatingActionButton fabAddTask;
    private TaskAdapter taskAdapter;

    // Explicit Intent launcher untuk menerima hasil dari AddTaskActivity
    private final ActivityResultLauncher<Intent> addTaskLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Task newTask = (Task) result.getData().getSerializableExtra(AddTaskActivity.EXTRA_NEW_TASK);
                    if (newTask != null) {
                        TaskManager.getInstance().addTask(newTask);
                        loadTasks();
                        Toast.makeText(getContext(), "Tugas baru berhasil ditambahkan!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    private final TaskManager.OnDataChangeListener dataChangeListener = this::loadTasks;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_task_list, container, false);

        rvTasks = view.findViewById(R.id.rv_tasks);
        tvEmptyTasks = view.findViewById(R.id.tv_empty_tasks);
        fabAddTask = view.findViewById(R.id.fab_add_task);

        setupRecyclerView();

        // Navigasi ke AddTaskActivity via Explicit Intent
        fabAddTask.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddTaskActivity.class);
            addTaskLauncher.launch(intent);
        });

        loadTasks();

        return view;
    }

    private void setupRecyclerView() {
        taskAdapter = new TaskAdapter(false, (task, isCompleted) -> {
            TaskManager.getInstance().markTaskCompleted(task.getId(), isCompleted);
            loadTasks();
            if (isCompleted && getContext() != null) {
                Toast.makeText(getContext(), "Tugas dipindahkan ke Riwayat!", Toast.LENGTH_SHORT).show();
            }
        });

        rvTasks.setLayoutManager(new LinearLayoutManager(getContext()));
        rvTasks.setAdapter(taskAdapter);
    }

    private void loadTasks() {
        List<Task> pendingTasks = TaskManager.getInstance().getPendingTasks();
        if (taskAdapter != null) {
            taskAdapter.setTasks(pendingTasks);
        }

        if (tvEmptyTasks != null && rvTasks != null) {
            if (pendingTasks.isEmpty()) {
                tvEmptyTasks.setVisibility(View.VISIBLE);
                rvTasks.setVisibility(View.GONE);
            } else {
                tvEmptyTasks.setVisibility(View.GONE);
                rvTasks.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadTasks();
        TaskManager.getInstance().addListener(dataChangeListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        TaskManager.getInstance().removeListener(dataChangeListener);
    }
}

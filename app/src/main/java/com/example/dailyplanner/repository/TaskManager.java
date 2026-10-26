package com.example.dailyplanner.repository;

import com.example.dailyplanner.model.Task;

import java.util.ArrayList;
import java.util.List;

/**
 * In-memory repository sederhana untuk mengelola daftar tugas
 * sesuai rekomendasi proyek (tanpa backend/database cloud).
 */
public class TaskManager {
    private static TaskManager instance;
    private final List<Task> taskList;

    public interface OnDataChangeListener {
        void onDataChanged();
    }

    private final List<OnDataChangeListener> listeners = new ArrayList<>();

    private TaskManager() {
        taskList = new ArrayList<>();
        // Inisialisasi beberapa data dummy awal
        taskList.add(new Task("Belajar Android Fragment", "Pahami siklus hidup dan transaksi Fragment", "09:00"));
        taskList.add(new Task("Implementasi Focus Timer Service", "Buat background countdown timer dengan BroadcastReceiver", "14:00"));
        Task completedSample = new Task("Setup Project & Git", "Membuat repositori dan inisialisasi Android Studio", "08:00");
        completedSample.setCompleted(true);
        taskList.add(completedSample);
    }

    public static synchronized TaskManager getInstance() {
        if (instance == null) {
            instance = new TaskManager();
        }
        return instance;
    }

    public List<Task> getPendingTasks() {
        List<Task> pending = new ArrayList<>();
        for (Task task : taskList) {
            if (!task.isCompleted()) {
                pending.add(task);
            }
        }
        return pending;
    }

    public List<Task> getCompletedTasks() {
        List<Task> completed = new ArrayList<>();
        for (Task task : taskList) {
            if (task.isCompleted()) {
                completed.add(task);
            }
        }
        return completed;
    }

    public void addTask(Task task) {
        if (task != null) {
            taskList.add(0, task);
            notifyDataChanged();
        }
    }

    public void markTaskCompleted(String taskId, boolean isCompleted) {
        for (Task task : taskList) {
            if (task.getId().equals(taskId)) {
                task.setCompleted(isCompleted);
                break;
            }
        }
        notifyDataChanged();
    }

    public void addListener(OnDataChangeListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removeListener(OnDataChangeListener listener) {
        listeners.remove(listener);
    }

    private void notifyDataChanged() {
        for (OnDataChangeListener listener : listeners) {
            if (listener != null) {
                listener.onDataChanged();
            }
        }
    }
}

package com.example.dailyplanner.repository;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.dailyplanner.model.Task;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Repository yang mengelola daftar tugas dan menyimpannya secara permanen
 * ke SharedPreferences agar data tidak hilang saat aplikasi ditutup/reboot.
 */
public class TaskManager {
    private static final String PREF_NAME = "DailyPlannerPrefs";
    private static final String KEY_TASKS = "KEY_TASKS_LIST";

    private static TaskManager instance;
    private final List<Task> taskList;
    private SharedPreferences sharedPreferences;

    public interface OnDataChangeListener {
        void onDataChanged();
    }

    private final List<OnDataChangeListener> listeners = new ArrayList<>();

    private TaskManager() {
        taskList = new ArrayList<>();
    }

    public static synchronized TaskManager getInstance() {
        if (instance == null) {
            instance = new TaskManager();
        }
        return instance;
    }

    public void init(Context context) {
        if (sharedPreferences == null && context != null) {
            sharedPreferences = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            loadFromPreferences();
        }
    }

    private void loadFromPreferences() {
        if (sharedPreferences == null) return;
        String jsonString = sharedPreferences.getString(KEY_TASKS, null);
        taskList.clear();
        if (jsonString != null && !jsonString.isEmpty()) {
            try {
                JSONArray jsonArray = new JSONArray(jsonString);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    String id = obj.optString("id");
                    String title = obj.optString("title");
                    String desc = obj.optString("description");
                    String time = obj.optString("time");
                    boolean isCompleted = obj.optBoolean("isCompleted");
                    String priority = obj.optString("priority", Task.PRIORITY_MEDIUM);
                    taskList.add(new Task(id, title, desc, time, isCompleted, priority));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            // Data awal jika pertama kali aplikasi dibuka
            taskList.add(new Task("Belajar Android Fragment", "Pahami siklus hidup dan transaksi Fragment", "09:00", Task.PRIORITY_HIGH));
            taskList.add(new Task("Implementasi Focus Timer Service", "Buat background countdown timer dengan BroadcastReceiver", "14:00", Task.PRIORITY_HIGH));
            Task completedSample = new Task("Setup Project & Git", "Membuat repositori dan inisialisasi Android Studio", "08:00", Task.PRIORITY_LOW);
            completedSample.setCompleted(true);
            taskList.add(completedSample);
            saveToPreferences();
        }
    }

    private void saveToPreferences() {
        if (sharedPreferences == null) return;
        try {
            JSONArray jsonArray = new JSONArray();
            for (Task task : taskList) {
                JSONObject obj = new JSONObject();
                obj.put("id", task.getId());
                obj.put("title", task.getTitle());
                obj.put("description", task.getDescription());
                obj.put("time", task.getTime());
                obj.put("isCompleted", task.isCompleted());
                obj.put("priority", task.getPriority());
                jsonArray.put(obj);
            }
            sharedPreferences.edit().putString(KEY_TASKS, jsonArray.toString()).apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            saveToPreferences();
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
        saveToPreferences();
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
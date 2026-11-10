package com.example.dailyplanner.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.dailyplanner.R;
import com.example.dailyplanner.model.Task;
import com.example.dailyplanner.repository.TaskManager;
import com.example.dailyplanner.service.FocusTimerService;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DashboardFragment extends Fragment {

    private TextView tvPendingCount;
    private TextView tvCompletedCount;
    private TextView tvCountdown;
    private TextView tvTimerStatus;
    private Spinner spinnerTasks;
    private Button btnStartTimer;
    private Button btnPauseTimer;
    private Button btnResetTimer;
    private Button btnMarkFocusedDone;

    private Button btn5m, btn15m, btn25m, btn45m, btn60m;

    private long selectedDurationMs = 25 * 60 * 1000L; // Default 25 Min
    private Task selectedTask = null;
    private List<Task> currentPendingList = new ArrayList<>();
    private boolean isReceiverRegistered = false;
    private boolean isTimerRunningState = false;

    private final BroadcastReceiver timerReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && FocusTimerService.BROADCAST_TIMER_UPDATE.equals(intent.getAction())) {
                long timeLeft = intent.getLongExtra(FocusTimerService.EXTRA_TIME_LEFT, selectedDurationMs);
                boolean isRunning = intent.getBooleanExtra(FocusTimerService.EXTRA_IS_RUNNING, false);
                boolean isFinished = intent.getBooleanExtra(FocusTimerService.EXTRA_IS_FINISHED, false);
                boolean isIdle = intent.getBooleanExtra(FocusTimerService.EXTRA_IS_IDLE, true);

                updateTimerUI(timeLeft, isRunning, isFinished, isIdle);
            }
        }
    };

    private final TaskManager.OnDataChangeListener dataChangeListener = () -> {
        updateSummaryCounts();
        setupTaskSpinner();
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        if (getContext() != null) {
            TaskManager.getInstance().init(getContext());
        }

        tvPendingCount = view.findViewById(R.id.tv_pending_count);
        tvCompletedCount = view.findViewById(R.id.tv_completed_count);
        tvCountdown = view.findViewById(R.id.tv_countdown);
        tvTimerStatus = view.findViewById(R.id.tv_timer_status);
        spinnerTasks = view.findViewById(R.id.spinner_tasks);
        btnStartTimer = view.findViewById(R.id.btn_start_timer);
        btnPauseTimer = view.findViewById(R.id.btn_pause_timer);
        btnResetTimer = view.findViewById(R.id.btn_reset_timer);
        btnMarkFocusedDone = view.findViewById(R.id.btn_mark_focused_done);

        btn5m = view.findViewById(R.id.btn_duration_5m);
        btn15m = view.findViewById(R.id.btn_duration_15m);
        btn25m = view.findViewById(R.id.btn_duration_25m);
        btn45m = view.findViewById(R.id.btn_duration_45m);
        btn60m = view.findViewById(R.id.btn_duration_60m);

        setupDurationButtons();
        setupTimerButtons();
        setupTaskSpinner();
        updateSummaryCounts();
        updateCountdownDisplay(selectedDurationMs);

        return view;
    }

    private void setupDurationButtons() {
        View.OnClickListener durationClickListener = v -> {
            if (isTimerRunningState) {
                Toast.makeText(getContext(), "Reset timer terlebih dahulu untuk mengubah durasi!", Toast.LENGTH_SHORT).show();
                return;
            }

            int id = v.getId();
            if (id == R.id.btn_duration_5m) {
                selectedDurationMs = 5 * 60 * 1000L;
            } else if (id == R.id.btn_duration_15m) {
                selectedDurationMs = 15 * 60 * 1000L;
            } else if (id == R.id.btn_duration_25m) {
                selectedDurationMs = 25 * 60 * 1000L;
            } else if (id == R.id.btn_duration_45m) {
                selectedDurationMs = 45 * 60 * 1000L;
            } else if (id == R.id.btn_duration_60m) {
                selectedDurationMs = 60 * 60 * 1000L;
            }

            updateDurationButtonsUI();
            updateCountdownDisplay(selectedDurationMs);
            sendServiceAction(FocusTimerService.ACTION_SET_DURATION);
        };

        btn5m.setOnClickListener(durationClickListener);
        btn15m.setOnClickListener(durationClickListener);
        btn25m.setOnClickListener(durationClickListener);
        btn45m.setOnClickListener(durationClickListener);
        btn60m.setOnClickListener(durationClickListener);

        updateDurationButtonsUI();
    }

    private void updateDurationButtonsUI() {
        if (getContext() == null) return;
        int activeBg = ContextCompat.getColor(getContext(), R.color.primary);
        int activeText = ContextCompat.getColor(getContext(), R.color.white);
        int inactiveText = ContextCompat.getColor(getContext(), R.color.text_primary);

        resetButtonStyle(btn5m, selectedDurationMs == 5 * 60 * 1000L, activeBg, activeText, inactiveText);
        resetButtonStyle(btn15m, selectedDurationMs == 15 * 60 * 1000L, activeBg, activeText, inactiveText);
        resetButtonStyle(btn25m, selectedDurationMs == 25 * 60 * 1000L, activeBg, activeText, inactiveText);
        resetButtonStyle(btn45m, selectedDurationMs == 45 * 60 * 1000L, activeBg, activeText, inactiveText);
        resetButtonStyle(btn60m, selectedDurationMs == 60 * 60 * 1000L, activeBg, activeText, inactiveText);
    }

    private void resetButtonStyle(Button btn, boolean isActive, int activeBg, int activeText, int inactiveText) {
        if (isActive) {
            btn.setBackgroundColor(activeBg);
            btn.setTextColor(activeText);
        } else {
            btn.setBackgroundColor(Color.TRANSPARENT);
            btn.setTextColor(inactiveText);
        }
    }

    private void setupTaskSpinner() {
        if (getContext() == null || spinnerTasks == null) return;

        currentPendingList = TaskManager.getInstance().getPendingTasks();
        List<String> taskTitles = new ArrayList<>();
        taskTitles.add("🎯 Fokus Umum (Tanpa Tugas Spesifik)");

        for (Task t : currentPendingList) {
            taskTitles.add("📋 " + t.getTitle());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, taskTitles);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTasks.setAdapter(adapter);

        spinnerTasks.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    selectedTask = null;
                } else if (position - 1 < currentPendingList.size()) {
                    selectedTask = currentPendingList.get(position - 1);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedTask = null;
            }
        });
    }

    private void setupTimerButtons() {
        btnStartTimer.setOnClickListener(v -> sendServiceAction(FocusTimerService.ACTION_START));
        btnPauseTimer.setOnClickListener(v -> sendServiceAction(FocusTimerService.ACTION_PAUSE));
        btnResetTimer.setOnClickListener(v -> {
            sendServiceAction(FocusTimerService.ACTION_RESET);
            updateCountdownDisplay(selectedDurationMs);
            tvTimerStatus.setText("Siap untuk fokus");
            btnMarkFocusedDone.setVisibility(View.GONE);
        });

        btnMarkFocusedDone.setOnClickListener(v -> {
            if (selectedTask != null) {
                TaskManager.getInstance().markTaskCompleted(selectedTask.getId(), true);
                Toast.makeText(getContext(), "Tugas '" + selectedTask.getTitle() + "' dipindahkan ke Riwayat!", Toast.LENGTH_SHORT).show();
                btnMarkFocusedDone.setVisibility(View.GONE);
                setupTaskSpinner();
            }
        });
    }

    private void sendServiceAction(String action) {
        if (getContext() != null) {
            Intent serviceIntent = new Intent(getContext(), FocusTimerService.class);
            serviceIntent.setAction(action);
            serviceIntent.putExtra(FocusTimerService.EXTRA_DURATION_MS, selectedDurationMs);
            if (selectedTask != null) {
                serviceIntent.putExtra(FocusTimerService.EXTRA_TASK_TITLE, selectedTask.getTitle());
            }

            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && FocusTimerService.ACTION_START.equals(action)) {
                    getContext().startForegroundService(serviceIntent);
                } else {
                    getContext().startService(serviceIntent);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void updateTimerUI(long timeLeftInMillis, boolean isRunning, boolean isFinished, boolean isIdle) {
        isTimerRunningState = isRunning;
        updateCountdownDisplay(timeLeftInMillis);

        String taskLabel = selectedTask != null ? " (" + selectedTask.getTitle() + ")" : "";

        if (isFinished) {
            tvTimerStatus.setText("🎉 Sesi fokus selesai!" + taskLabel);
            if (selectedTask != null) {
                btnMarkFocusedDone.setVisibility(View.VISIBLE);
                btnMarkFocusedDone.setText("✅ Tandai '" + selectedTask.getTitle() + "' Selesai");
            } else {
                btnMarkFocusedDone.setVisibility(View.GONE);
            }
            if (getContext() != null) {
                Toast.makeText(getContext(), R.string.timer_finished_notif, Toast.LENGTH_LONG).show();
            }
        } else if (isRunning) {
            tvTimerStatus.setText("🔥 Sesi fokus sedang berjalan..." + taskLabel);
            btnMarkFocusedDone.setVisibility(View.GONE);
        } else if (isIdle) {
            tvTimerStatus.setText("Siap untuk fokus" + taskLabel);
            btnMarkFocusedDone.setVisibility(View.GONE);
        } else {
            tvTimerStatus.setText("⏸️ Sesi fokus dijeda" + taskLabel);
            btnMarkFocusedDone.setVisibility(View.GONE);
        }
    }

    private void updateCountdownDisplay(long millis) {
        if (tvCountdown == null) return;
        int minutes = (int) (millis / 1000) / 60;
        int seconds = (int) (millis / 1000) % 60;
        tvCountdown.setText(String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds));
    }

    private void updateSummaryCounts() {
        if (tvPendingCount != null && tvCompletedCount != null) {
            int pending = TaskManager.getInstance().getPendingTasks().size();
            int completed = TaskManager.getInstance().getCompletedTasks().size();
            tvPendingCount.setText(String.valueOf(pending));
            tvCompletedCount.setText(String.valueOf(completed));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateSummaryCounts();
        setupTaskSpinner();
        TaskManager.getInstance().addListener(dataChangeListener);

        if (getContext() != null && !isReceiverRegistered) {
            IntentFilter filter = new IntentFilter(FocusTimerService.BROADCAST_TIMER_UPDATE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                getContext().registerReceiver(timerReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
            } else {
                getContext().registerReceiver(timerReceiver, filter);
            }
            isReceiverRegistered = true;
        }

        sendServiceAction(FocusTimerService.ACTION_GET_STATUS);
    }

    @Override
    public void onPause() {
        super.onPause();
        TaskManager.getInstance().removeListener(dataChangeListener);
        if (getContext() != null && isReceiverRegistered) {
            try {
                getContext().unregisterReceiver(timerReceiver);
            } catch (IllegalArgumentException ignored) {}
            isReceiverRegistered = false;
        }
    }
}
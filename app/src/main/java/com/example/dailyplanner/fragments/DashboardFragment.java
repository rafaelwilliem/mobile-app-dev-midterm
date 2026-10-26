package com.example.dailyplanner.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.dailyplanner.R;
import com.example.dailyplanner.repository.TaskManager;
import com.example.dailyplanner.service.FocusTimerService;

import java.util.Locale;

public class DashboardFragment extends Fragment {

    private TextView tvPendingCount;
    private TextView tvCompletedCount;
    private TextView tvCountdown;
    private TextView tvTimerStatus;
    private Button btnStartTimer;
    private Button btnPauseTimer;
    private Button btnResetTimer;

    private boolean isReceiverRegistered = false;

    private final BroadcastReceiver timerReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && FocusTimerService.BROADCAST_TIMER_UPDATE.equals(intent.getAction())) {
                long timeLeft = intent.getLongExtra(FocusTimerService.EXTRA_TIME_LEFT, FocusTimerService.DEFAULT_DURATION_MS);
                boolean isRunning = intent.getBooleanExtra(FocusTimerService.EXTRA_IS_RUNNING, false);
                boolean isFinished = intent.getBooleanExtra(FocusTimerService.EXTRA_IS_FINISHED, false);
                boolean isIdle = intent.getBooleanExtra(FocusTimerService.EXTRA_IS_IDLE, false);

                updateTimerUI(timeLeft, isRunning, isFinished, isIdle);
            }
        }
    };

    private final TaskManager.OnDataChangeListener dataChangeListener = this::updateSummaryCounts;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        tvPendingCount = view.findViewById(R.id.tv_pending_count);
        tvCompletedCount = view.findViewById(R.id.tv_completed_count);
        tvCountdown = view.findViewById(R.id.tv_countdown);
        tvTimerStatus = view.findViewById(R.id.tv_timer_status);
        btnStartTimer = view.findViewById(R.id.btn_start_timer);
        btnPauseTimer = view.findViewById(R.id.btn_pause_timer);
        btnResetTimer = view.findViewById(R.id.btn_reset_timer);

        setupTimerButtons();
        updateSummaryCounts();

        return view;
    }

    private void setupTimerButtons() {
        btnStartTimer.setOnClickListener(v -> sendServiceAction(FocusTimerService.ACTION_START));
        btnPauseTimer.setOnClickListener(v -> {
            if (!FocusTimerService.isIdle()) {
                sendServiceAction(FocusTimerService.ACTION_PAUSE);
            }
        });
        btnResetTimer.setOnClickListener(v -> {
            if (!FocusTimerService.isIdle() || FocusTimerService.isTimerRunning()) {
                sendServiceAction(FocusTimerService.ACTION_RESET);
            }
            tvCountdown.setText(R.string.default_timer_display);
            tvTimerStatus.setText("Siap untuk fokus 25 menit");
        });
    }

    private void sendServiceAction(String action) {
        if (getContext() != null) {
            Intent serviceIntent = new Intent(getContext(), FocusTimerService.class);
            serviceIntent.setAction(action);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && FocusTimerService.ACTION_START.equals(action)) {
                getContext().startForegroundService(serviceIntent);
            } else {
                getContext().startService(serviceIntent);
            }
        }
    }

    private void updateTimerUI(long timeLeftInMillis, boolean isRunning, boolean isFinished, boolean isIdle) {
        long displayMillis = (isIdle && !isFinished) ? FocusTimerService.DEFAULT_DURATION_MS : timeLeftInMillis;
        int minutes = (int) (displayMillis / 1000) / 60;
        int seconds = (int) (displayMillis / 1000) % 60;
        tvCountdown.setText(String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds));

        if (isFinished) {
            tvTimerStatus.setText("🎉 Sesi fokus selesai! Istirahat sejenak.");
            if (getContext() != null) {
                Toast.makeText(getContext(), R.string.timer_finished_notif, Toast.LENGTH_LONG).show();
            }
        } else if (isRunning) {
            tvTimerStatus.setText("🔥 Sesi fokus sedang berjalan...");
        } else if (isIdle) {
            tvTimerStatus.setText("Siap untuk fokus 25 menit");
        } else {
            tvTimerStatus.setText("⏸️ Sesi fokus dijeda");
        }
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

        // Sinkronisasi state awal langsung dari service tanpa perlu start service saat masih idle
        updateTimerUI(
                FocusTimerService.getTimeLeftInMillis(),
                FocusTimerService.isTimerRunning(),
                false,
                FocusTimerService.isIdle()
        );
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

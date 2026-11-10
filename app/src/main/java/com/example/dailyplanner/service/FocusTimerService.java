package com.example.dailyplanner.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.dailyplanner.MainActivity;
import com.example.dailyplanner.R;

import java.util.Locale;

public class FocusTimerService extends Service {

    public static final String ACTION_START = "com.example.dailyplanner.ACTION_START";
    public static final String ACTION_PAUSE = "com.example.dailyplanner.ACTION_PAUSE";
    public static final String ACTION_RESET = "com.example.dailyplanner.ACTION_RESET";
    public static final String ACTION_SET_DURATION = "com.example.dailyplanner.ACTION_SET_DURATION";
    public static final String ACTION_GET_STATUS = "com.example.dailyplanner.ACTION_GET_STATUS";

    public static final String BROADCAST_TIMER_UPDATE = "com.example.dailyplanner.TIMER_UPDATE";
    public static final String EXTRA_TIME_LEFT = "EXTRA_TIME_LEFT";
    public static final String EXTRA_IS_RUNNING = "EXTRA_IS_RUNNING";
    public static final String EXTRA_IS_FINISHED = "EXTRA_IS_FINISHED";
    public static final String EXTRA_IS_IDLE = "EXTRA_IS_IDLE";
    public static final String EXTRA_DURATION_MS = "EXTRA_DURATION_MS";
    public static final String EXTRA_TASK_TITLE = "EXTRA_TASK_TITLE";

    private static final String CHANNEL_ID = "FocusTimerChannel";
    private static final int NOTIFICATION_ID = 101;
    public static final long DEFAULT_DURATION_MS = 25 * 60 * 1000; // Default 25 Menit

    private CountDownTimer countDownTimer;
    private long selectedDurationMs = DEFAULT_DURATION_MS;
    private long timeLeftInMillis = DEFAULT_DURATION_MS;
    private boolean isTimerRunning = false;
    private boolean isIdle = true;
    private String currentTaskTitle = "";

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            if (intent.hasExtra(EXTRA_TASK_TITLE)) {
                currentTaskTitle = intent.getStringExtra(EXTRA_TASK_TITLE);
            }

            String action = intent.getAction();
            if (intent.hasExtra(EXTRA_DURATION_MS) && !isTimerRunning
                    && (ACTION_SET_DURATION.equals(action) || isIdle)) {
                selectedDurationMs = intent.getLongExtra(EXTRA_DURATION_MS, DEFAULT_DURATION_MS);
                timeLeftInMillis = selectedDurationMs;
            }

            if (action != null) {
                switch (action) {
                    case ACTION_START:
                        startTimer();
                        break;
                    case ACTION_PAUSE:
                        pauseTimer();
                        break;
                    case ACTION_RESET:
                        resetTimer();
                        break;
                    case ACTION_SET_DURATION:
                        if (!isTimerRunning) {
                            isIdle = true;
                            broadcastUpdate(false);
                        }
                        break;
                    case ACTION_GET_STATUS:
                        broadcastUpdate(false);
                        break;
                }
            }
        }
        return START_STICKY;
    }

    private void startTimer() {
        if (isTimerRunning) return;

        if (timeLeftInMillis <= 0) {
            timeLeftInMillis = selectedDurationMs;
        }

        isIdle = false;
        isTimerRunning = true;
        try {
            Notification notification = buildNotification(formatTime(timeLeftInMillis));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                startForeground(NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                startForeground(NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_MANIFEST);
            } else {
                startForeground(NOTIFICATION_ID, notification);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                broadcastUpdate(false);
                updateNotification(formatTime(timeLeftInMillis));
            }

            @Override
            public void onFinish() {
                isTimerRunning = false;
                isIdle = true;
                timeLeftInMillis = 0;
                broadcastUpdate(true);
                updateNotification(getString(R.string.timer_finished_notif));
                try {
                    stopForeground(false);
                } catch (Exception ignored) {}
            }
        }.start();

        broadcastUpdate(false);
    }

    private void pauseTimer() {
        if (!isTimerRunning) return;

        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        isTimerRunning = false;
        isIdle = false;
        broadcastUpdate(false);
        updateNotification("Dijeda: " + formatTime(timeLeftInMillis));
        try {
            stopForeground(false);
        } catch (Exception ignored) {}
    }

    private void resetTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        isTimerRunning = false;
        isIdle = true;
        timeLeftInMillis = selectedDurationMs;
        broadcastUpdate(false);
        try {
            stopForeground(true);
        } catch (Exception ignored) {}
        stopSelf();
    }

    private void broadcastUpdate(boolean isFinished) {
        Intent broadcastIntent = new Intent(BROADCAST_TIMER_UPDATE);
        broadcastIntent.setPackage(getPackageName());
        broadcastIntent.putExtra(EXTRA_TIME_LEFT, timeLeftInMillis);
        broadcastIntent.putExtra(EXTRA_IS_RUNNING, isTimerRunning);
        broadcastIntent.putExtra(EXTRA_IS_FINISHED, isFinished);
        broadcastIntent.putExtra(EXTRA_IS_IDLE, isIdle);
        broadcastIntent.putExtra(EXTRA_DURATION_MS, selectedDurationMs);
        broadcastIntent.putExtra(EXTRA_TASK_TITLE, currentTaskTitle);
        sendBroadcast(broadcastIntent);
    }

    private String formatTime(long millis) {
        int minutes = (int) (millis / 1000) / 60;
        int seconds = (int) (millis / 1000) % 60;
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    getString(R.string.notification_channel_name),
                    NotificationManager.IMPORTANCE_LOW
            );
            channel.setDescription(getString(R.string.notification_channel_desc));
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    private Notification buildNotification(String contentText) {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, notificationIntent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
        );

        String title = (currentTaskTitle != null && !currentTaskTitle.isEmpty())
                ? "Fokus: " + currentTaskTitle
                : getString(R.string.focus_timer_title);

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(contentText)
                .setSmallIcon(R.drawable.ic_task_list)
                .setContentIntent(pendingIntent)
                .setOngoing(isTimerRunning)
                .build();
    }

    private void updateNotification(String contentText) {
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.notify(NOTIFICATION_ID, buildNotification(contentText));
        }
    }

    @Override
    public void onDestroy() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
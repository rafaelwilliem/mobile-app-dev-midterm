package com.example.dailyplanner;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.dailyplanner.model.Task;
import com.example.dailyplanner.repository.TaskManager;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Locale;

public class AddTaskActivity extends AppCompatActivity {

    public static final String EXTRA_NEW_TASK = "EXTRA_NEW_TASK";

    private TextInputLayout layoutTaskTitle;
    private TextInputEditText etTaskTitle;
    private TextInputEditText etTaskDesc;
    private TimePicker timePicker;
    private Button btnSaveTask;
    private Button btnCancel;
    private Button btnPriorityLow;
    private Button btnPriorityMedium;
    private Button btnPriorityHigh;

    private String selectedPriority = Task.PRIORITY_MEDIUM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.title_add_task);
        }

        initViews();
        setupListeners();
    }

    private void initViews() {
        layoutTaskTitle = findViewById(R.id.layout_task_title);
        etTaskTitle = findViewById(R.id.et_task_title);
        etTaskDesc = findViewById(R.id.et_task_desc);
        timePicker = findViewById(R.id.time_picker);
        btnSaveTask = findViewById(R.id.btn_save_task);
        btnCancel = findViewById(R.id.btn_cancel);
        btnPriorityLow = findViewById(R.id.btn_priority_low);
        btnPriorityMedium = findViewById(R.id.btn_priority_medium);
        btnPriorityHigh = findViewById(R.id.btn_priority_high);

        timePicker.setIs24HourView(true);
        updatePriorityButtonsUI();
    }

    private void setupListeners() {
        etTaskTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (layoutTaskTitle.getError() != null) {
                    layoutTaskTitle.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnPriorityLow.setOnClickListener(v -> {
            selectedPriority = Task.PRIORITY_LOW;
            updatePriorityButtonsUI();
        });

        btnPriorityMedium.setOnClickListener(v -> {
            selectedPriority = Task.PRIORITY_MEDIUM;
            updatePriorityButtonsUI();
        });

        btnPriorityHigh.setOnClickListener(v -> {
            selectedPriority = Task.PRIORITY_HIGH;
            updatePriorityButtonsUI();
        });

        btnSaveTask.setOnClickListener(v -> saveTask());
        btnCancel.setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });
    }

    private void updatePriorityButtonsUI() {
        int lowBg = ContextCompat.getColor(this, R.color.priority_low);
        int medBg = ContextCompat.getColor(this, R.color.priority_medium);
        int highBg = ContextCompat.getColor(this, R.color.priority_high);
        int white = ContextCompat.getColor(this, R.color.white);
        int textDark = ContextCompat.getColor(this, R.color.text_primary);

        setButtonStyle(btnPriorityLow, Task.PRIORITY_LOW.equals(selectedPriority), lowBg, white, textDark);
        setButtonStyle(btnPriorityMedium, Task.PRIORITY_MEDIUM.equals(selectedPriority), medBg, white, textDark);
        setButtonStyle(btnPriorityHigh, Task.PRIORITY_HIGH.equals(selectedPriority), highBg, white, textDark);
    }

    private void setButtonStyle(Button btn, boolean isSelected, int activeBgColor, int activeTextColor, int inactiveTextColor) {
        if (isSelected) {
            btn.setBackgroundColor(activeBgColor);
            btn.setTextColor(activeTextColor);
        } else {
            btn.setBackgroundColor(Color.TRANSPARENT);
            btn.setTextColor(inactiveTextColor);
        }
    }

    private void saveTask() {
        String title = etTaskTitle.getText() != null ? etTaskTitle.getText().toString().trim() : "";
        String desc = etTaskDesc.getText() != null ? etTaskDesc.getText().toString().trim() : "";

        if (title.isEmpty()) {
            layoutTaskTitle.setError(getString(R.string.error_empty_title));
            etTaskTitle.requestFocus();
            Toast.makeText(this, R.string.error_empty_title, Toast.LENGTH_SHORT).show();
            return;
        }

        int hour;
        int minute;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            hour = timePicker.getHour();
            minute = timePicker.getMinute();
        } else {
            hour = timePicker.getCurrentHour();
            minute = timePicker.getCurrentMinute();
        }
        String formattedTime = String.format(Locale.getDefault(), "%02d:%02d", hour, minute);

        Task newTask = new Task(title, desc, formattedTime, selectedPriority);

        TaskManager.getInstance().init(this);
        TaskManager.getInstance().addTask(newTask);

        setResult(RESULT_OK);
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        setResult(RESULT_CANCELED);
        finish();
        return true;
    }
}
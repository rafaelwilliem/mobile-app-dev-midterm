package com.example.dailyplanner;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dailyplanner.model.Task;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        // Aktifkan tombol back di action bar
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

        // Set time picker format 24 jam
        timePicker.setIs24HourView(true);
    }

    private void setupListeners() {
        // Hilangkan error saat user mengetik
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

        btnSaveTask.setOnClickListener(v -> saveTask());
        btnCancel.setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });
    }

    private void saveTask() {
        String title = etTaskTitle.getText() != null ? etTaskTitle.getText().toString().trim() : "";
        String desc = etTaskDesc.getText() != null ? etTaskDesc.getText().toString().trim() : "";

        // Validasi input agar aplikasi tidak crash
        if (title.isEmpty()) {
            layoutTaskTitle.setError(getString(R.string.error_empty_title));
            etTaskTitle.requestFocus();
            Toast.makeText(this, R.string.error_empty_title, Toast.LENGTH_SHORT).show();
            return;
        }

        // Ambil waktu dari TimePicker
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

        // Buat objek Task
        Task newTask = new Task(title, desc, formattedTime);

        // Kirimkan kembali ke Activity pemanggil melalui Intent extras
        Intent resultIntent = new Intent();
        resultIntent.putExtra(EXTRA_NEW_TASK, newTask);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        setResult(RESULT_CANCELED);
        finish();
        return true;
    }
}

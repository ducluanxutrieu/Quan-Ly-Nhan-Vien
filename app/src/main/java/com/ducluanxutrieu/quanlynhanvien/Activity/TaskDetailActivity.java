package com.ducluanxutrieu.quanlynhanvien.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.ducluanxutrieu.quanlynhanvien.Interface.TransferTask;
import com.ducluanxutrieu.quanlynhanvien.Item.Task;
import com.ducluanxutrieu.quanlynhanvien.R;

public class TaskDetailActivity extends AppCompatActivity {
    private EditText inputTitleTask, inputContentTask;
    private Button btnCancel, btnSave;
    private TransferTask transferTask;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);

        mapping();

        Intent intent = getIntent();
        final Task task = (Task) intent.getSerializableExtra("task");
        inputTitleTask.setText(task.getTaskTitle());
        inputContentTask.setText(task.getTaskContent());

        inputTitleTask.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() > 0){
                    btnSave.setEnabled(true);
                }else {
                    btnSave.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = inputTitleTask.getText().toString();
                String content = inputContentTask.getText().toString();
                if (!title.isEmpty() && !content.isEmpty()) {
                    task.setTaskTitle(title);
                    task.setTaskContent(content);
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void mapping() {
        inputTitleTask = findViewById(R.id.input_title_task_edit_activity);
        inputContentTask = findViewById(R.id.input_content_task_edit_text_activity);
        btnCancel = findViewById(R.id.btn_cancel_task_activity);
        btnSave = findViewById(R.id.btn_save_task_activity);
    }
}

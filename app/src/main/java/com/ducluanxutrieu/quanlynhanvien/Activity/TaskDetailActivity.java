package com.ducluanxutrieu.quanlynhanvien.Activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ducluanxutrieu.quanlynhanvien.Models.Tasks;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.ducluanxutrieu.quanlynhanvien.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;

import java.util.HashMap;
import java.util.Map;

public class TaskDetailActivity extends AppCompatActivity {
    private EditText inputTitleTask, inputContentTask;
    private Button btnCancel, btnSave;

    Tasks task;
    String keyTask;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);

        mapping();

        Intent intent = getIntent();
        final String signal = intent.getStringExtra("signal");

        if (signal.equals("edit")) {
            task = (Tasks) intent.getSerializableExtra("task");
            keyTask = intent.getStringExtra("key");
            inputTitleTask.setText(task.getTaskTitle());
            inputContentTask.setText(task.getTaskContent());
        }
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
                    if (task == null) {
                        addTask(title, content);
                    } else {
                        task.setTaskTitle(title);
                        task.setTaskContent(content);
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("task/" + FirebaseAuth.getInstance().getUid());
                        reference.child(keyTask).setValue(task);
                    }
                }
                finish();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void mapping() {
        inputTitleTask = findViewById(R.id.input_title_task_edit_activity);
        inputContentTask = findViewById(R.id.input_content_task_edit_text_activity);
        btnCancel = findViewById(R.id.btn_cancel_task_activity);
        btnSave = findViewById(R.id.btn_save_task_activity);
    }

    private Task<String> addTask(String title, String content){
        FirebaseFunctions mFunctions;
        mFunctions = FirebaseFunctions.getInstance();
        Map<String, Object> map = new HashMap<>();
        map.put("taskTitle", title);
        map.put("taskContent", content);

        return mFunctions.getHttpsCallable("addTask")
                .call(map)
                .continueWith(new Continuation<HttpsCallableResult, String>() {
                    @Override
                    public String then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        return (String) task.getResult().getData();
                    }
                });

    }
}

package com.ducluanxutrieu.quanlynhanvien.Activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.button.MaterialButton;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.ducluanxutrieu.quanlynhanvien.Models.RequestItem;
import com.ducluanxutrieu.quanlynhanvien.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;

import java.util.HashMap;
import java.util.Map;

public class RequestDetailActivity extends AppCompatActivity {
    //Firebase
    FirebaseFunctions mFunctions;

TextView name;
TextInputEditText offType, startDate, endDate, reason, note;
CheckBox checkBox;
MaterialButton btnSend, btnCancel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_detail);

        mapping();
        mFunctions = FirebaseFunctions.getInstance();

        Intent intent = getIntent();
        final RequestItem requestItem = (RequestItem) intent.getSerializableExtra("request");
        if (requestItem != null){
            name.setText(requestItem.getName());
            offType.setText(requestItem.getOffType());
            startDate.setText(requestItem.getStartDate());
            endDate.setText(requestItem.getEndDate());
            reason.setText(requestItem.getText());
            note.setText(requestItem.getNote());
            checkBox.setChecked(requestItem.isAccept());
        }

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkBox.isChecked()){
                    Toast.makeText(RequestDetailActivity.this, getString(R.string.denied_user_off), Toast.LENGTH_SHORT).show();
                    requestItem.setAccept(false);
                    setAccept(requestItem);
                    //DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                }else {
                    requestItem.setAccept(true);
                    setAccept(requestItem);
                }
                addMessage(note.getText().toString(), requestItem.getUid()).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RequestDetailActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
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
        name = findViewById(R.id.request_detail_name);
        offType = findViewById(R.id.request_detail_off_type);
        startDate = findViewById(R.id.request_detail_start_date);
        endDate = findViewById(R.id.request_detail_end_date);
        reason = findViewById(R.id.request_detail_reason);
        note = findViewById(R.id.request_detail_note);
        checkBox = findViewById(R.id.request_detail_accept);
        btnCancel = findViewById(R.id.request_detail_cancel);
        btnSend = findViewById(R.id.request_detail_send);
    }

    private Task<String> addMessage(String text, String toUid) {
        // Create the arguments to the callable function.
        Map<String, Object> data = new HashMap<>();
        data.put("text", text);
        data.put("to", toUid);
        data.put("push", true);

        return mFunctions
                .getHttpsCallable("addMessage")
                .call(data)
                .continueWith(new Continuation<HttpsCallableResult, String>() {
                    @Override
                    public String then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        // This continuation runs on either success or failure, but if the task
                        // has failed then getResult() will throw an Exception which will be
                        // propagated down.
                        return (String) task.getResult().getData();
                    }
                });
    }

    private void setAccept(RequestItem requestItem){
        DatabaseReference referenceRequest = FirebaseDatabase.getInstance().getReference().child("request_from_staff/" + "/" + requestItem.getRequestKey());
        referenceRequest.setValue(requestItem);
    }
}

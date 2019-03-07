package com.ducluanxutrieu.quanlynhanvien.Activity;

import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ducluanxutrieu.quanlynhanvien.Dialog.DatePicker;
import com.ducluanxutrieu.quanlynhanvien.Interface.TransferSignal;
import com.ducluanxutrieu.quanlynhanvien.Models.RequestItem;
import com.ducluanxutrieu.quanlynhanvien.Models.TokenUser;
import com.ducluanxutrieu.quanlynhanvien.MyTask;
import com.ducluanxutrieu.quanlynhanvien.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class AskOffDayActivity extends AppCompatActivity implements TransferSignal {
    TextView choseADay;
    Button send, cancel;
    EditText inputContent;
    String date;
    String token;


    private FirebaseUser mFirebaseUser;
    private FirebaseDatabase mFireDatabase;
    private DatabaseReference mReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask_off_day);

        FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mFireDatabase = FirebaseDatabase.getInstance();
        mReference = mFireDatabase.getReference().child("request_from_staff");

        mapping();
        getTokenAdmin();

        choseADay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePicker();
                newFragment.show(getSupportFragmentManager(), "timePicker");
            }
        });

        inputContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() > 0){
                    send.setEnabled(true);
                }else {
                    send.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = inputContent.getText().toString();
                RequestItem requestItem = new RequestItem(
                        date,
                        mFirebaseUser.getDisplayName(),
                        mFirebaseUser.getEmail(),
                        content,
                        getTimeNow(),
                        false
                        );
                DatabaseReference ref = mFireDatabase.getReference().child("day_off/" + mFirebaseUser.getEmail().replace(".", ""));
                requestItem.setRequestKey(mReference.push().getKey());
                mReference.child(requestItem.getRequestKey()).setValue(requestItem);
                ref.child(requestItem.getRequestKey()).setValue(requestItem);
                pushNotification(content);
                finish();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getDate();
    }

    private void mapping() {
        choseADay = findViewById(R.id.btn_chose_day);
        send = findViewById(R.id.btn_send_ask_off);
        cancel = findViewById(R.id.btn_cancel_ask_off);
        inputContent = findViewById(R.id.input_content_ask);
    }

    @Override
    public void onTransferSignal(String signalMessage, String message) {
        choseADay.setText(message);
    }

    private String getTimeNow() {
        String time;
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        time = hour + ":" + minute;
        return time;
    }

    private void getDate(){
        Calendar calendar = Calendar.getInstance();
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH) + 1;
        int year = calendar.get(Calendar.YEAR);
        date = dayOfMonth + "/" + month + "/" + year;
        choseADay.setText(date);
    }

    private void getTokenAdmin(){
        DatabaseReference reference = mFireDatabase.getReference().child("token/admin@gmailcom");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                TokenUser tokenUser = dataSnapshot.getValue(TokenUser.class);
                if (tokenUser != null){
                    token = tokenUser.getTokenU();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void pushNotification(String content) {
        MyTask myTask = new MyTask(AskOffDayActivity.this);

        final String BASE_URL = "https://quan-ly-nhan-vien.firebaseapp.com/api";
        String title = "@@" + mFirebaseUser.getDisplayName();
        content = choseADay.getText().toString() + "| " + content;
        Log.i(".AskOffDay", content);
        String to = token;
        String query = "{\"data\":{\"title\":\""+title +"\",\"message\":\""+ content + "\"},\"to\":\""+to+"\"}";
        Log.i("QUERY", query);
        myTask.execute(BASE_URL, query);
    }
}

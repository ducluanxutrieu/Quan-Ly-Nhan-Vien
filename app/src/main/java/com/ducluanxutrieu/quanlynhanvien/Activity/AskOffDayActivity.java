package com.ducluanxutrieu.quanlynhanvien.Activity;

import android.os.AsyncTask;
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
import android.widget.Toast;

import com.ducluanxutrieu.quanlynhanvien.Dialog.DatePicker;
import com.ducluanxutrieu.quanlynhanvien.Interface.TransferSignal;
import com.ducluanxutrieu.quanlynhanvien.Item.RequestItem;
import com.ducluanxutrieu.quanlynhanvien.Item.TokenUser;
import com.ducluanxutrieu.quanlynhanvien.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
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
                RequestItem requestItem = new RequestItem(
                        date,
                        mFirebaseUser.getDisplayName(),
                        mFirebaseUser.getEmail(),
                        inputContent.getText().toString(),
                        getTimeNow(),
                        false
                        );
                DatabaseReference ref = mFireDatabase.getReference().child("day_off/" + mFirebaseUser.getEmail().replace(".", ""));
                mReference.push().setValue(requestItem);
                ref.push().setValue(requestItem);
                pushNoti();
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
                }else return;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void pushNoti() {
        AsyncTask<String, Void ,String> task = new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... strings) {
                String BASE_URL = strings[0];
                String query = strings[1];
                StringBuilder result = new StringBuilder();
                try {
                    URL url = new URL(BASE_URL);
                    HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
                    httpCon.setDoOutput(true);
                    httpCon.setDoInput(true);
                    httpCon.setRequestMethod("POST");
                    httpCon.setRequestProperty("Accept", "application/json");
                    httpCon.setRequestProperty("Content-Type", "application/json");

                    OutputStream os = httpCon.getOutputStream();
                    OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");
                    osw.write(query);
                    osw.flush();
                    osw.close();
                    os.close();  //don't forget to close the OutputStream
                    httpCon.connect();

                    //read the inputstream and print it
                    InputStream inputStream = httpCon.getInputStream();
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    String line = "";

                    //send message result
                    while((line = bufferedReader.readLine()) != null) {
                        result.append(line);
                    }
                    bufferedReader.close();
                    inputStreamReader.close();
                    inputStream.close();
                    System.out.println(result.toString());
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return result.toString();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                Toast.makeText(AskOffDayActivity.this, s, Toast.LENGTH_SHORT).show();
            }
        };

        final String BASE_URL = "https://quan-ly-nhan-vien.firebaseapp.com/api";
        String name = "@@" + mFirebaseUser.getDisplayName();
        String message = inputContent.getText().toString();
        message = choseADay + "| " + message;
        String to = token;
        //{"data":{"title":"luanxx","message":"cac"},"to":"ejgJWQ"}
        String query = "{\"data\":{\"title\":\""+name +"\",\"message\":\""+ message + "\"},\"to\":\""+to+"\"}";
        Log.i("QUERY", query);
        task.execute(BASE_URL, query);
    }
}

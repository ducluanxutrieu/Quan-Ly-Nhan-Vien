package com.ducluanxutrieu.quanlynhanvien.Activity;

import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.ducluanxutrieu.quanlynhanvien.Models.Users;
import com.ducluanxutrieu.quanlynhanvien.MyTask;
import com.ducluanxutrieu.quanlynhanvien.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class EditUserActivity extends AppCompatActivity {
    TextInputEditText name, email, password, phone, position;
    Button add, cancel;
    String signal;
    Users users;

    static String finalName;
    static String finalEmail;
    static String finalPassword;
    static String finalPhone;
    static String finalPosition;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);

        Intent intent = getIntent();
        users = (Users) intent.getSerializableExtra("user");
        signal = intent.getStringExtra("signal");

        mapping();
        if (signal.equals("update")){
            name.setText(users.getName());
            email.setText(users.getEmail());
            password.setText(users.getPassword());
            phone.setText(users.getPhone());
            position.setText(users.getPosition());
        }
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalName = name.getText().toString();
                finalEmail = email.getText().toString();
                finalPassword = password.getText().toString();
                finalPhone = phone.getText().toString();
                finalPosition = position.getText().toString();
                updateAccount();
                finish();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void mapping() {
        name = findViewById(R.id.edit_text_name_dialog);
        email = findViewById(R.id.edit_text_email_dialog);
        password = findViewById(R.id.edit_text_password_dialog);
        phone = findViewById(R.id.edit_text_phone_dialog);
        position = findViewById(R.id.edit_text_position_dialog);
        add = findViewById(R.id.btn_add);
        cancel = findViewById(R.id.btn_cancel);
    }

    private void updateAccount(){
        if (finalPhone.startsWith("0")){
            finalPhone = "+84" + finalPhone.substring(1);
        }

        final String BASE_URL = "https://quan-ly-nhan-vien.firebaseapp.com/" + signal;
        String TAG = ".EditUserActivity";
        Log.i(TAG, BASE_URL);
        //final String BASE_URL = "http://10.0.3.2:5000/create";
        Map<String, String> map = new HashMap<>();
        map.put("name", finalName);
        map.put("email", finalEmail);
        map.put("password", finalPassword);
        map.put("phone", finalPhone);
        if (signal.equals("update")) {
            map.put("uid", users.getUid());
        }
        JSONObject jsonObject = new JSONObject(map);
        String query = jsonObject.toString();
        Log.i("QUERY", query);
        new MyTask(EditUserActivity.this).execute(BASE_URL, query);
    }
    public static void updateUserToDatabase(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference mReference = FirebaseDatabase.getInstance().getReference();
        if (user != null) {
            Users users = new Users(finalName, finalEmail, finalPassword, finalPhone, finalPosition, user.getUid(), false);
            String emailChange = finalEmail.replace(".", "");
            mReference.child("user/" + emailChange).setValue(users);
        }
    }

    public static void addAccountToDatabase(String s){
        String uid = s.substring(7);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference mReference = FirebaseDatabase.getInstance().getReference();
        if (user != null) {
            Users users = new Users(finalName, finalEmail, finalPassword, finalPhone, finalPosition, uid, false);
            String emailChange = finalEmail.replace(".", "");
            mReference.child("user/" + emailChange).setValue(users);
        }
    }
}
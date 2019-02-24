package com.ducluanxutrieu.quanlynhanvien.Activity;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.ducluanxutrieu.quanlynhanvien.R;
import com.ducluanxutrieu.quanlynhanvien.Users;
import com.firebase.ui.auth.data.model.User;

public class UserInfoActivity extends AppCompatActivity {
    TextView name, phone, email, password, position;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        mapping();
        show();
    }

    private void mapping() {
        name = findViewById(R.id.name_user_info);
        email = findViewById(R.id.email_user_info);
        position = findViewById(R.id.position_user_info);
        phone = findViewById(R.id.phone_user_info);
        password = findViewById(R.id.password_user_info);
    }

    private void show() {
        Intent intent = getIntent();
        Users users = (Users) intent.getSerializableExtra("user");
        name.setText(users.getName());
        email.setText(users.getEmail());
        position.setText(users.getPosition());
        phone.setText(users.getPhone());
        password.setText(users.getPassword());

    }
}

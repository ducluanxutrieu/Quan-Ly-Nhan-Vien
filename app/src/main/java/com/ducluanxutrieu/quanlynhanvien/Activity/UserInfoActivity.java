package com.ducluanxutrieu.quanlynhanvien.Activity;

import android.content.Intent;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.ducluanxutrieu.quanlynhanvien.R;
import com.ducluanxutrieu.quanlynhanvien.Item.Users;

public class UserInfoActivity extends AppCompatActivity {
    TextView phone, email, password, position;
    CollapsingToolbarLayout mCollapsingToolbarLayout;
    Toolbar toolbar;
    Users users;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        Intent intent = getIntent();
        users = (Users) intent.getSerializableExtra("user");
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mCollapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);
        mCollapsingToolbarLayout.setTitle(users.getName());
        mCollapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(R.color.colorAccent));

        mapping();
        show();
    }

    private void mapping() {
        email = findViewById(R.id.email_user_info);
        position = findViewById(R.id.position_user_info);
        phone = findViewById(R.id.phone_user_info);
        password = findViewById(R.id.password_user_info);
    }

    private void show() {
        email.setText("Email: " + users.getEmail());
        position.setText(getString(R.string.position_in_company) + ": " + users.getPosition());
        phone.setText(getString( R.string.phone_number) + ": " + users.getPhone());
        password.setText(getString(R.string.password) + ": " + users.getPassword());

    }
}

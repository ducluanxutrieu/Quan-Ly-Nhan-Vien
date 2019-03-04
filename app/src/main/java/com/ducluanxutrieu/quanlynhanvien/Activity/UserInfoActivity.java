package com.ducluanxutrieu.quanlynhanvien.Activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.ducluanxutrieu.quanlynhanvien.Item.RequestItem;
import com.ducluanxutrieu.quanlynhanvien.R;
import com.ducluanxutrieu.quanlynhanvien.Item.Users;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UserInfoActivity extends AppCompatActivity {
    TextView phone, email, password, position, numberOffDays;
    CollapsingToolbarLayout mCollapsingToolbarLayout;
    Toolbar toolbar;
    Users users;
    DatabaseReference mDatabaseReference;
    List<RequestItem> requestItemList;
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

        show();
        mapping();

        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("day_off/" + users.getEmail().replace(".", ""));
        requestItemList = new ArrayList<>();
        mDatabaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                RequestItem item = dataSnapshot.getValue(RequestItem.class);
                if (item != null){
                    requestItemList.add(item);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        numberOffDays.setText(getString(R.string.number_off_days) + ": " + requestItemList.size());
    }

    private void mapping() {
        email = findViewById(R.id.email_user_info);
        position = findViewById(R.id.position_user_info);
        phone = findViewById(R.id.phone_user_info);
        password = findViewById(R.id.password_user_info);
        numberOffDays = findViewById(R.id.number_off_day_info);
    }

    private void show() {
        email.setText(String.format("Email: %s", users.getEmail()));
        position.setText(String.format("%s: %s", getString(R.string.position_in_company), users.getPosition()));
        phone.setText(String.format("%s: %s", getString(R.string.phone_number), users.getPhone()));
        password.setText(String.format("%s: %s", getString(R.string.password), users.getPassword()));

    }
}

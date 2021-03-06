package com.ducluanxutrieu.quanlynhanvien.Activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ducluanxutrieu.quanlynhanvien.Models.RequestItem;
import com.ducluanxutrieu.quanlynhanvien.Models.Staff;
import com.ducluanxutrieu.quanlynhanvien.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class UserInfoActivity extends AppCompatActivity {
    TextInputEditText phone, email, position, numberOffDays;
    TextView name;
    ImageView imageProfile;
    Staff staff;
    DatabaseReference mDatabaseReference;
    List<RequestItem> requestItemList;
    ChildEventListener mChildEventListener;
    int daysOffDeny = 0, daysOffAccept = 0;
    FloatingActionButton fab;

    final static public String TAG = ".USerInfoLogin";
    boolean isAdmin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        final Intent intent = getIntent();
        staff = (Staff) intent.getSerializableExtra("user");
        isAdmin = intent.getBooleanExtra("isAdmin", false);

        mapping();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(UserInfoActivity.this, EditUserActivity.class);
                intent1.putExtra("user", staff);
                intent1.putExtra("signal", "updateUser");
                intent1.putExtra("isAdmin", isAdmin);
                startActivity(intent1);
            }
        });

        show();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("day_off/" + staff.getUid());
        requestItemList = new ArrayList<>();

        if (mChildEventListener == null){
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    RequestItem item = dataSnapshot.getValue(RequestItem.class);
                    if (item != null) {
                        requestItemList.add(item);
                        if (item.isAccept()){
                            daysOffAccept ++;
                        }else {
                            daysOffDeny ++;
                        }
                        Log.i(TAG, requestItemList.get(0).toString());
                        numberOffDays.setText(requestItemList.size() + "");
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
            };
            mDatabaseReference.addChildEventListener(mChildEventListener);
        }

        numberOffDays.setText(requestItemList.size() + "");
    }


    private void mapping() {
        email = findViewById(R.id.email_profile_email);
        position = findViewById(R.id.user_profile_position);
        phone = findViewById(R.id.user_profile_phone);
        numberOffDays = findViewById(R.id.user_profile_off_day);
        fab = findViewById(R.id.fab_info);
        name = findViewById(R.id.user_profile_name);
        imageProfile = findViewById(R.id.user_profile_photo);
    }

    private void show() {
        email.setText(staff.getEmail());
        position.setText(staff.getPosition());
        phone.setText(staff.getPhone());
        name.setText(staff.getName());
        Glide.with(UserInfoActivity.this).load(staff.getAvatarUrl()).apply(RequestOptions.circleCropTransform()).into(imageProfile);
    }
}

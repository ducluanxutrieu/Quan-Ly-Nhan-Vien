package com.ducluanxutrieu.quanlynhanvien.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.ducluanxutrieu.quanlynhanvien.Dialog.ChangePassword;
import com.ducluanxutrieu.quanlynhanvien.Fragment.FriendsListFragment;
import com.ducluanxutrieu.quanlynhanvien.Fragment.RequestListFragment;
import com.ducluanxutrieu.quanlynhanvien.Fragment.StaffListFragment;
import com.ducluanxutrieu.quanlynhanvien.Fragment.TasksFragment;
import com.ducluanxutrieu.quanlynhanvien.Interface.TransferTask;
import com.ducluanxutrieu.quanlynhanvien.Models.Friend;
import com.ducluanxutrieu.quanlynhanvien.Interface.TransferSignal;
import com.ducluanxutrieu.quanlynhanvien.Models.TokenUser;
import com.ducluanxutrieu.quanlynhanvien.PushNotificationManager;
import com.ducluanxutrieu.quanlynhanvien.R;
import com.ducluanxutrieu.quanlynhanvien.Models.Users;
import com.ducluanxutrieu.quanlynhanvien.Adapter.ViewPagerAdapter;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

public class MainActivity extends AppCompatActivity
        implements TransferSignal, TransferTask {

    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private ViewPagerAdapter mViewPagerAdapter;

    private DatabaseReference mUsersReference;
    private DatabaseReference mFriendReference;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFireUser;

    private boolean isUserAdmin = false;
    private boolean userAlreadyFriend = false;

    private String rootEmailWithoutDot;
    private static String TAG = "tokentoken";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        PushNotificationManager.getInstance().init(this);
        mViewPager = findViewById(R.id.view_pager);
        mTabLayout = findViewById(R.id.tab_layout);
        toolbar = findViewById(R.id.toolbar_main);

        setSupportActionBar(toolbar);

        mViewPager.setSaveFromParentEnabled(false);

        mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mViewPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);

        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        mUsersReference = mFirebaseDatabase.getReference();
        mFriendReference = mFirebaseDatabase.getReference();

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFireUser = mFirebaseAuth.getCurrentUser();
        if (mFireUser !=  null) {
            toolbar.setTitle(mFireUser.getDisplayName());

            mViewPagerAdapter.addFragment(new FriendsListFragment(), "Friends List");
            mViewPagerAdapter.addFragment(new TasksFragment(), "Tasks");
            mViewPagerAdapter.notifyDataSetChanged();
            checkAdmin();
        }

        PushNotificationManager.getInstance().init(this);

        GoogleApiAvailability.getInstance().makeGooglePlayServicesAvailable(MainActivity.this);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                FirebaseInstanceId.getInstance().getInstanceId()
                        .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                            @Override
                            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                if (!task.isSuccessful()) {
                                    return;
                                }
                                // Get new Instance ID token
                                String token = task.getResult().getToken();
                                // Log and toast
                                TokenUser tokenUser1 = new TokenUser(token);
                                mUsersReference.child("token/" + rootEmailWithoutDot).setValue(tokenUser1);
                            }
                        });
            }
        });
        thread.start();
    }

    private void checkAdmin() {
        SharedPreferences sharedPreferences = getSharedPreferences("com.ducluanxutrieu.quanlynhanvien", 0);
        rootEmailWithoutDot = sharedPreferences.getString("email", null);
        if (rootEmailWithoutDot != null){
            rootEmailWithoutDot = rootEmailWithoutDot.replace(".", "");
        } else {
            return;
        }
        DatabaseReference oneItemValue = mUsersReference.child("user" + "/" + rootEmailWithoutDot);
        oneItemValue.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Users userAdmin = dataSnapshot.getValue(Users.class);
                isUserAdmin = userAdmin.isAdmin();
                if (isUserAdmin) {
                    mViewPagerAdapter.addFragment(new StaffListFragment(), getString(R.string.staff_list));
                    mViewPagerAdapter.addFragment(new RequestListFragment(), getString(R.string.request_list));
                    mViewPagerAdapter.notifyDataSetChanged();
                    mTabLayout.getTabAt(0).setIcon(R.drawable.avatar);
                    mTabLayout.getTabAt(1).setIcon(R.drawable.list);
                    mTabLayout.getTabAt(2).setIcon(R.drawable.staff);
                    mTabLayout.getTabAt(3).setIcon(R.drawable.request_list);
                    mViewPager.setOffscreenPageLimit(3);
                }else {
                    mTabLayout.getTabAt(0).setIcon(R.drawable.avatar);
                    mTabLayout.getTabAt(1).setIcon(R.drawable.list);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.sign_out: {
                mFirebaseAuth.signOut();
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("token/" + rootEmailWithoutDot);
                reference.removeValue();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;
            }

            case R.id.change_password: {
                ChangePassword changePassword = new ChangePassword();
                changePassword.show(getSupportFragmentManager(), "ChangePassword");
                break;
            }
            case R.id.ask_for_day_off: {
                Intent intent = new Intent(MainActivity.this, AskOffDayActivity.class);
                startActivity(intent);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTransferSignal(String signalMessage, String message) {
        if (signalMessage.equals("AddFriend")){
                final String emailInput = message.replace(".", "");
                final String rootEmail = mFireUser.getEmail().replace(".", "");
                checkFriendAlreadyExist(rootEmail, emailInput);
                if (!userAlreadyFriend) {
                    try {
                    mFriendReference.child("user/" + emailInput).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Users users = dataSnapshot.getValue(Users.class);
                            if (users != null) {
                                String name = users.getName();
                                String uid = users.getUid();
                                if (name != null && uid != null) {
                                    Friend friend = new Friend(users.getName(), users.getEmail(), users.getUid(), "");
                                    mFriendReference.child("friend_ship/" + rootEmail + "/" + emailInput).setValue(friend);
                                    Toast.makeText(getApplicationContext(), "Add " + friend.getName() + " succesful", Toast.LENGTH_LONG).show();
                                }
                            }else {
                                Toast.makeText(MainActivity.this, "Could't find your friend!", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }

                    });
                    }catch (Exception e){
                        e.printStackTrace();
                        Toast.makeText(MainActivity.this, "Could't find your friend!", Toast.LENGTH_LONG).show();
                    }
                }else {
                    Toast.makeText(MainActivity.this, "Your friend already exist!", Toast.LENGTH_LONG).show();
                }
        }
    }

    @Override
    public void onTransferTask(String title, String content) {
       TasksFragment tasksFragment = (TasksFragment) mViewPagerAdapter.getItem(1);
       tasksFragment.addNewTask(title,content);
    }

    private void checkFriendAlreadyExist(String rootEmail, String s1) {
        try {
            mFriendReference.child("friend_ship/" + rootEmail + "/" + s1).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Friend friend = dataSnapshot.getValue(Friend.class);

                    //userAlready = false if friend == null
                    userAlreadyFriend = friend != null;
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }catch (Exception e){
            e.printStackTrace();
            userAlreadyFriend = false;
        }
    }


}
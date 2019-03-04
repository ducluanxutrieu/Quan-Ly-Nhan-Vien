package com.ducluanxutrieu.quanlynhanvien.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.ducluanxutrieu.quanlynhanvien.Dialog.ChangePassword;
import com.ducluanxutrieu.quanlynhanvien.Fragment.FriendsListFragment;
import com.ducluanxutrieu.quanlynhanvien.Fragment.RequestListFragment;
import com.ducluanxutrieu.quanlynhanvien.Fragment.StaffListFragment;
import com.ducluanxutrieu.quanlynhanvien.Fragment.TasksFragment;
import com.ducluanxutrieu.quanlynhanvien.Interface.TransferTask;
import com.ducluanxutrieu.quanlynhanvien.Item.Friend;
import com.ducluanxutrieu.quanlynhanvien.Interface.TransferData;
import com.ducluanxutrieu.quanlynhanvien.Interface.TransferSignal;
import com.ducluanxutrieu.quanlynhanvien.Item.TokenUser;
import com.ducluanxutrieu.quanlynhanvien.PushNotificationManager;
import com.ducluanxutrieu.quanlynhanvien.R;
import com.ducluanxutrieu.quanlynhanvien.Item.Users;
import com.ducluanxutrieu.quanlynhanvien.Adapter.ViewPagerAdapter;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity
        implements TransferData, TransferSignal, TransferTask {

    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private ViewPagerAdapter mViewPagerAdapter;

    FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mUsersReference;
    private DatabaseReference mFriendReference;
    private FirebaseAuth mFirebaseAuth;
    FirebaseUser mFireUser;

    private boolean isUserAdmin = false;
    private boolean userAlreadyFriend = false;

    private String rootEmailWithoutDot;
    public String password, email;
    private static String TAG = "tokentoken";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        PushNotificationManager.getInstance().init(this);
        mViewPager = findViewById(R.id.view_pager);
        mTabLayout = findViewById(R.id.tab_layout);

        mViewPager.setSaveFromParentEnabled(false);

        mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mViewPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mUsersReference = mFirebaseDatabase.getReference();
        mFriendReference = mFirebaseDatabase.getReference();

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFireUser = mFirebaseAuth.getCurrentUser();
        if (mFireUser !=  null) {
            Log.i("USERSTATE", "Ok");
            setTitle(mFireUser.getDisplayName());
            mViewPagerAdapter.addFragment(new FriendsListFragment(), "Friends List");
            mViewPagerAdapter.addFragment(new TasksFragment(), "Tasks");
            mViewPagerAdapter.notifyDataSetChanged();
            checkAdmin();
        } else {
            Log.i("USERSTATE", "Null");
        }

        PushNotificationManager.getInstance().init(this);

        GoogleApiAvailability.getInstance().makeGooglePlayServicesAvailable(MainActivity.this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                FirebaseInstanceId.getInstance().getInstanceId()
                        .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                            @Override
                            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                if (!task.isSuccessful()) {
                                    Log.w(TAG, "getInstanceId failed", task.getException());
                                    return;
                                }
                                // Get new Instance ID token
                                String token = task.getResult().getToken();
                                Log.i("kiemtra1", token);
                                // Log and toast
                                TokenUser tokenUser1 = new TokenUser(token);
                                mUsersReference.child("token/" + rootEmailWithoutDot).setValue(tokenUser1);
                            }
                        });
            }
        });

    }

    private void checkAdmin() {
        SharedPreferences sharedPreferences = getSharedPreferences("com.ducluanxutrieu.quanlynhanvien", 0);
        rootEmailWithoutDot = sharedPreferences.getString("email", null);
        Log.i("USERSTATE", "checkAdmin1");
        if (rootEmailWithoutDot != null){
            rootEmailWithoutDot = rootEmailWithoutDot.replace(".", "");
        } else {
            return;
        }
        Log.i("USERSTATE", "checkAdmin2");
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
            case R.id.action_settings: return  true;

            case R.id.sign_out: {
                mFirebaseAuth.signOut();
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
    public void onTransferDialog(final String name, final String email, final String password, final String phone, final String position) {
        final FirebaseAuth mAuth;
        mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isComplete()) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            FirebaseUser user = mAuth.getCurrentUser();
                            UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder().setDisplayName(name).build();
                            if (user != null) {
                                user.updateProfile(profileUpdate);
                                String uid = user.getUid();
                                Users users = new Users(name, email, password, phone, position, uid, false);
                                String emailChange = email.replace(".", "");
                                mUsersReference.child("user/" + emailChange).setValue(users);
                            }
                            signInAgain();
                        }
                    }).start();
                } else {
                    Toast.makeText(MainActivity.this, getString(R.string.have_a_problem_with_create_new_account_member), Toast.LENGTH_LONG).show();
                }
            }
        });
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
    private void signInAgain() {
        SharedPreferences sharedPreferences = getSharedPreferences("com.ducluanxutrieu.quanlynhanvien", 0);
        String email = sharedPreferences.getString("email", null);
        String password = sharedPreferences.getString("password", null);

        if (email != null && password != null) {
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password);
        }
    }
}
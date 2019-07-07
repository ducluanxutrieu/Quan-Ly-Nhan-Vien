package com.ducluanxutrieu.quanlynhanvien.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.ducluanxutrieu.quanlynhanvien.DepthTransformation;
import com.ducluanxutrieu.quanlynhanvien.Fragment.FriendsListFragment;
import com.ducluanxutrieu.quanlynhanvien.Fragment.RequestListFragment;
import com.ducluanxutrieu.quanlynhanvien.Fragment.StaffListFragment;
import com.ducluanxutrieu.quanlynhanvien.Fragment.TasksFragment;
import com.ducluanxutrieu.quanlynhanvien.Interface.TransferSignal;
import com.ducluanxutrieu.quanlynhanvien.Models.Staff;
import com.ducluanxutrieu.quanlynhanvien.MyTask;
import com.ducluanxutrieu.quanlynhanvien.PushNotificationManager;
import com.ducluanxutrieu.quanlynhanvien.R;
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
import com.roger.catloadinglibrary.CatLoadingView;

import java.util.HashMap;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements TransferSignal {

    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private ViewPagerAdapter mViewPagerAdapter;

    private DatabaseReference mUsersReference;
    static DatabaseReference mFriendReference;
    private FirebaseAuth mFirebaseAuth;

    private boolean isUserAdmin = false;

    private static String rootUid;
    private Staff userAdmin;

    static private final String TAG = "MainActivity";

    //Cat loading
    static public CatLoadingView mCatView;

    //MyTask myTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Show cat loading
        mCatView = new CatLoadingView();
        mCatView.show(getSupportFragmentManager(), "Show in main activity");

        Toolbar toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

        mViewPager = findViewById(R.id.view_pager);
        mTabLayout = findViewById(R.id.tab_layout);
        toolbar = findViewById(R.id.toolbar_main);

        setSupportActionBar(toolbar);
        DepthTransformation depthTransformation = new DepthTransformation();
        mViewPager.setSaveFromParentEnabled(false);

        mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mViewPagerAdapter);
        mViewPager.setPageTransformer(true, depthTransformation);
        mTabLayout.setupWithViewPager(mViewPager);

        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        mUsersReference = mFirebaseDatabase.getReference();
        mFriendReference = mFirebaseDatabase.getReference();
        mFirebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser mFireUser = mFirebaseAuth.getCurrentUser();

        //pushNotification to
        PushNotificationManager.getInstance().init(this);
        rootUid = mFirebaseAuth.getUid();

        if (mFireUser != null) {
            toolbar.setTitle(mFireUser.getDisplayName());
            mViewPagerAdapter.addFragment(new FriendsListFragment());
            mViewPagerAdapter.addFragment(new TasksFragment());
            mViewPagerAdapter.notifyDataSetChanged();
            checkAdmin();
        }


        GoogleApiAvailability.getInstance().makeGooglePlayServicesAvailable(MainActivity.this);

        getToken();
    }

    private void getToken() {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (task.isSuccessful()) {
                            String token = Objects.requireNonNull(task.getResult()).getToken();
                            // Log and toast
                            HashMap<String, Object> map = new HashMap<>();
                            map.put(token, true);
                            mUsersReference.child("users/" + rootUid+"/notificationTokens").updateChildren(map);
                        }
                    }
                });
    }

    private void checkAdmin() {
        DatabaseReference oneItemValue = mUsersReference.child("users/" + rootUid);

        oneItemValue.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userAdmin = dataSnapshot.getValue(Staff.class);
                isUserAdmin = userAdmin.isAdmin();
                if (isUserAdmin) {
                    mViewPagerAdapter.addFragment(new StaffListFragment());
                    mViewPagerAdapter.addFragment(new RequestListFragment());
                    mViewPagerAdapter.notifyDataSetChanged();

                    mTabLayout.getTabAt(0).setIcon(R.drawable.ic_message_blue_24dp);
                    mTabLayout.getTabAt(1).setIcon(R.drawable.ic_list_tasks_24px);
                    mTabLayout.getTabAt(2).setIcon(R.drawable.ic_people_blue_24dp);
                    mTabLayout.getTabAt(3).setIcon(R.drawable.ic_request_list_blue_24dp);
                    mViewPager.setOffscreenPageLimit(3);
                }else {
                    mTabLayout.getTabAt(0).setIcon(R.drawable.ic_message_blue_24dp);
                    mTabLayout.getTabAt(1).setIcon(R.drawable.ic_list_tasks_24px);
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
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("token/" + rootUid);
                reference.removeValue();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;
            }

            case R.id.my_info: {
                Intent intent = new Intent(MainActivity.this, UserInfoActivity.class);
                intent.putExtra("user", userAdmin);
                startActivity(intent);
                break;
            }
            case R.id.ask_for_day_off: {
                Intent intent = new Intent(MainActivity.this, AskOffDayActivity.class);
                startActivity(intent);
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTransferSignal(String signalMessage, String email) {
        if (signalMessage.equals("AddFriend")) {
            FriendsListFragment friendListFragment = (FriendsListFragment) mViewPagerAdapter.getItem(0);
            friendListFragment.getEmailFriend(email);
        }
    }

    private void getUser(String email) {

        MyTask task = new MyTask(MainActivity.this);

        final String BASE_URL = "https://quan-ly-nhan-vien.firebaseapp.com/api";
        //final String BASE_URL = "http://localhost:5000/api";

        String query = "{\"data\":{\"email\":\""+email +"\"}}";
        Log.i("QUERY", query);
        task.execute(BASE_URL, query);
    }

    /*public static void getUidFromServer(String uid){
        if (checkFriendAlreadyExist(uid)) {
            try {
                mFriendReference.child("staff/" + uid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Staff staff = dataSnapshot.getValue(Staff.class);
                        if (staff != null) {
                            String name = staff.getName();
                            String uid = staff.getUid();
                            if (name != null && uid != null) {
                                Friend friend = new Friend(staff.getName(), staff.getUid(), "", staff.getAvatarUrl());
                                mFriendReference.child("friend_ship/" + mFireUser.getUid() + "/" + uid).setValue(friend);
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }*/
}
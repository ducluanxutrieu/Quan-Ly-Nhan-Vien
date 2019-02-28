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

import com.ducluanxutrieu.quanlynhanvien.Fragment.FriendsListFragment;
import com.ducluanxutrieu.quanlynhanvien.Fragment.StaffListFragment;
import com.ducluanxutrieu.quanlynhanvien.Fragment.TasksFragment;
import com.ducluanxutrieu.quanlynhanvien.Item.Friend;
import com.ducluanxutrieu.quanlynhanvien.Interface.TransferData;
import com.ducluanxutrieu.quanlynhanvien.Interface.TransferSignal;
import com.ducluanxutrieu.quanlynhanvien.PushNotificationManager;
import com.ducluanxutrieu.quanlynhanvien.R;
import com.ducluanxutrieu.quanlynhanvien.Item.TokenUser;
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

public class MainActivity extends AppCompatActivity
        implements TransferData, TransferSignal {

    ViewPager mViewPager;
    TabLayout mTabLayout;
    ViewPagerAdapter mViewPagerAdapter;

    FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mUsersReference;
    private DatabaseReference mFriendReference;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseAuth mFireUser;

    private boolean isUserAdmin = false;
    private boolean userAlreadyFriend = false;

    private String rootEmail;
    private static String TAG = "tokentoken";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        PushNotificationManager.getInstance().init(this);
        mViewPager = findViewById(R.id.view_pager);
        mTabLayout = findViewById(R.id.tab_layout);
        mViewPager.setSaveFromParentEnabled(false);
        mViewPager.setOffscreenPageLimit(1);

        mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFireUser = mFirebaseAuth;

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mUsersReference = mFirebaseDatabase.getReference();
        mFriendReference = mFirebaseDatabase.getReference();

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = mFireUser.getCurrentUser();
                if (user !=  null) {
                    setTitle(user.getDisplayName());
                }
            }
        };

        mViewPagerAdapter.addFragment(new FriendsListFragment(), "Friends List");
        mViewPagerAdapter.addFragment(new TasksFragment(), "Tasks");


        GoogleApiAvailability.getInstance().makeGooglePlayServicesAvailable(MainActivity.this);

        checkAdmin();

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
                        mUsersReference.child("token/" + rootEmail).setValue(tokenUser1);
                    }
                });

        mViewPager.setOffscreenPageLimit(3);
    }


    private void checkAdmin() {
        SharedPreferences sharedPreferences = getSharedPreferences("com.ducluanxutrieu.quanlynhanvien", 0);
        rootEmail = sharedPreferences.getString("email", ".").replace(".", "");
        DatabaseReference oneItemValue = mUsersReference.child("user" + "/" + rootEmail);
        oneItemValue.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Users userAdmin = dataSnapshot.getValue(Users.class);
                isUserAdmin = userAdmin.isAdmin();
                if (isUserAdmin){
                    mViewPagerAdapter.addFragment(new StaffListFragment(), getString(R.string.staff_list));
                }
                mViewPager.setAdapter(mViewPagerAdapter);
                mTabLayout.setupWithViewPager(mViewPager);
                mTabLayout.getTabAt(0).setIcon(R.drawable.avatar);
                mTabLayout.getTabAt(1).setIcon(R.drawable.list);
                if (mViewPagerAdapter.getCount() == 3) {
                    mTabLayout.getTabAt(2).setIcon(R.drawable.staff);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
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
                mFireUser.signOut();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                finish();
                startActivity(intent);
            }
            break;
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
                            user.updateProfile(profileUpdate);
                            String uid = user.getUid();
                            Users users = new Users(name, email, password, phone, position, uid, false);
                            String emailChange = email.replace(".", "");
                            mUsersReference.child("user/" + emailChange).setValue(users);
                            //Toast.makeText(MainActivity.this, getString(R.string.add_new_member_complete), Toast.LENGTH_LONG).show();
                            mFirebaseAuth = mFireUser;
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
                final String rootEmail = mFireUser.getCurrentUser().getEmail().replace(".", "");
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

    private void checkFriendAlreadyExist(String rootEmail, String s1) {
        try {
            mFriendReference.child("friend_ship/" + rootEmail + "/" + s1).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Friend friend = dataSnapshot.getValue(Friend.class);
                    if (friend == null) {
                        userAlreadyFriend = false;
                    } else {
                        userAlreadyFriend = true;
                    }
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

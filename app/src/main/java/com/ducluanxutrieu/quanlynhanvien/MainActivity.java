package com.ducluanxutrieu.quanlynhanvien;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.ducluanxutrieu.quanlynhanvien.Admin.StaffListFragment;
import com.ducluanxutrieu.quanlynhanvien.Admin.TasksFragment;
import com.ducluanxutrieu.quanlynhanvien.Dialog.AddNewAccountFragmentDialog;
import com.ducluanxutrieu.quanlynhanvien.Interface.TransferData;
import com.ducluanxutrieu.quanlynhanvien.Interface.TransferSignal;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, TransferData, TransferSignal {

    ViewPager mViewPager;
    TabLayout mTabLayout;
    ViewPagerAdapter mViewPagerAdapter;
    private TasksFragment tasks_fragment;
    private StaffListFragment staffList_fragment;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mUsersReference;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private ChildEventListener mChildEvenListener;

    public static final String TAG = "this_is_tag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mViewPager = findViewById(R.id.view_pager);
        mTabLayout = findViewById(R.id.tab_layout);

        mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mUsersReference = mFirebaseDatabase.getReference().child("user");

        mFirebaseAuth = FirebaseAuth.getInstance();
/*        if (mFirebaseAuth.getCurrentUser() == null){
            startActivityForResult(
                    AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(Arrays.asList(
                            new AuthUI.IdpConfig.EmailBuilder().build()
                    ))
                    .build(), 1
            );
        }*/

        /*if (staffList_fragment == null){
            staffList_fragment = new StaffListFragment();
        }*/

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null){
                    onSignedOutCleanUp();
                }else {
                    onSignedInInitialize();
                }
            }
        };


        mViewPagerAdapter.addFragment(new StaffListFragment(), getString(R.string.staff_list));
        mViewPagerAdapter.addFragment(new TasksFragment(), "Tasks");

        mViewPager.setAdapter(mViewPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);

        mTabLayout.getTabAt(0).setIcon(R.drawable.common_google_signin_btn_icon_light);
        mTabLayout.getTabAt(1).setIcon(R.drawable.staff);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void onSignedOutCleanUp() {
        detachDatabaseReadListener();
    }

    private void detachDatabaseReadListener() {
        if (mChildEvenListener != null){
            mUsersReference.removeEventListener(mChildEvenListener);
            mChildEvenListener = null;
        }
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

    private void onSignedInInitialize() {
        attachDatabaseReadListener();
    }

    private void attachDatabaseReadListener() {
        Log.i("kiemtra", "3");
        if (mChildEvenListener == null){
            mChildEvenListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    Log.i("kiemtra", "4");
                    Users users = dataSnapshot.getValue(Users.class);
                    staffList_fragment = (StaffListFragment) mViewPagerAdapter.getItem(0);
                    Log.i("kiemtra", users.toString());
                    staffList_fragment.onAddUser(users);
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
            mUsersReference.addChildEventListener(mChildEvenListener);
        }
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id){
            case R.id.action_settings: return  true;
            case R.id.language_setting:
            {
                changeLanguage();
                break;
            }
            case R.id.sign_out: {
                mFirebaseAuth.signOut();
            }
            break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void changeLanguage() {

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_camera:
                // Handle the camera action
                break;
            case R.id.nav_gallery: {

            } break;
            case R.id.nav_slideshow: {

            } break;
            case R.id.nav_manage: {

            } break;
            case R.id.nav_share: {

            } break;
            case R.id.nav_send: {

            }
            break;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onTransferDialog(String name, final String email, final String password, final String phone, final String position) {
        FirebaseAuth mAuth;
        mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isComplete()) {
                    Log.d(TAG, TAG);
                    Toast.makeText(getApplicationContext(), getString(R.string.add_new_member_complete), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.have_a_problem_with_create_new_account_member), Toast.LENGTH_LONG).show();
                }
            }
        });
        FirebaseUser user = mAuth.getCurrentUser();
        UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder().setDisplayName(name).build();
        user.updateProfile(profileUpdate);

        Users users = new Users(name, email, password, phone, position);
        mUsersReference.child(mUsersReference.push().getKey()).setValue(users);
    }

    @Override
    public void onTransferSignal(String signalMessage) {
        if (signalMessage.equals("ShowDialogAddAccount")){
            showDialogAddAccount();
        }
    }

    private void showDialogAddAccount(){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        AddNewAccountFragmentDialog add_new_account_fragment_dialog =new AddNewAccountFragmentDialog();
        add_new_account_fragment_dialog.show(transaction, "dialog");
    }


}

package com.ducluanxutrieu.quanlynhanvien.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import com.google.android.material.tabs.TabLayout
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

import android.view.Menu
import android.view.MenuItem

import com.ducluanxutrieu.quanlynhanvien.util.DepthTransformation
import com.ducluanxutrieu.quanlynhanvien.fragment.FriendsListFragment
import com.ducluanxutrieu.quanlynhanvien.fragment.RequestListFragment
import com.ducluanxutrieu.quanlynhanvien.fragment.StaffListFragment
import com.ducluanxutrieu.quanlynhanvien.fragment.TasksFragment
import com.ducluanxutrieu.quanlynhanvien.interfaces.TransferSignal
import com.ducluanxutrieu.quanlynhanvien.models.Staff
import com.ducluanxutrieu.quanlynhanvien.util.PushNotificationManager
import com.ducluanxutrieu.quanlynhanvien.R
import com.ducluanxutrieu.quanlynhanvien.adapter.ViewPagerAdapter
import com.google.android.gms.common.GoogleApiAvailability
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.InstanceIdResult
import com.roger.catloadinglibrary.CatLoadingView

import java.util.HashMap
import java.util.Objects

class MainActivity : AppCompatActivity(), TransferSignal {

    private var mViewPager: ViewPager? = null
    private var mTabLayout: TabLayout? = null
    private var mViewPagerAdapter: ViewPagerAdapter? = null

    private var mUsersReference: DatabaseReference? = null
    private var mFirebaseAuth: FirebaseAuth? = null

    private var isUserAdmin = false
    private var userAdmin: Staff? = null

    //MyTask myTask;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Show cat loading
        mCatView = CatLoadingView()
        mCatView.show(supportFragmentManager, "Show in main activity")

        //Mapping
        mViewPager = findViewById(R.id.view_pager)
        mTabLayout = findViewById(R.id.tab_layout)
        val toolbar = findViewById<Toolbar>(R.id.toolbar_main)


        //ViewPager with TabLayout
        val depthTransformation = DepthTransformation()
        mViewPager!!.isSaveFromParentEnabled = false
        mViewPagerAdapter = ViewPagerAdapter(supportFragmentManager)
        mViewPager!!.adapter = mViewPagerAdapter
        mViewPager!!.setPageTransformer(true, depthTransformation)
        mTabLayout!!.setupWithViewPager(mViewPager)

        //Firebase
        val mFirebaseDatabase = FirebaseDatabase.getInstance()
        mUsersReference = mFirebaseDatabase.reference
        mFriendReference = mFirebaseDatabase.reference
        mFirebaseAuth = FirebaseAuth.getInstance()
        val mFireUser = mFirebaseAuth!!.currentUser

        //Update toolbar
        setSupportActionBar(toolbar)
        if (mFireUser != null) {
            supportActionBar!!.setTitle(mFireUser.displayName)
        }

        //pushNotification to
        PushNotificationManager.instance.init(this)
        rootUid = mFirebaseAuth!!.uid

        if (mFireUser != null) {
            toolbar.title = mFireUser.displayName
            mViewPagerAdapter!!.addFragment(FriendsListFragment())
            mViewPagerAdapter!!.addFragment(TasksFragment())
            mViewPagerAdapter!!.notifyDataSetChanged()
            checkAdmin()
        }


        GoogleApiAvailability.getInstance().makeGooglePlayServicesAvailable(this@MainActivity)

        getToken()
    }

    private fun getToken() {
        FirebaseInstanceId.getInstance().instanceId
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val token = Objects.requireNonNull<InstanceIdResult>(task.result).token
                        // Log and toast
                        val map = HashMap<String, Any>()
                        map[token] = true
                        mUsersReference!!.child("users/$rootUid/notificationTokens").updateChildren(map)
                    }
                }
    }

    private fun checkAdmin() {
        val oneItemValue = mUsersReference!!.child("users/" + rootUid!!)

        oneItemValue.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                userAdmin = dataSnapshot.getValue(Staff::class.java)
                isUserAdmin = userAdmin!!.isAdmin
                if (isUserAdmin) {
                    mViewPagerAdapter!!.addFragment(StaffListFragment())
                    mViewPagerAdapter!!.addFragment(RequestListFragment())
                    mViewPagerAdapter!!.notifyDataSetChanged()

                    mTabLayout!!.getTabAt(0)!!.setIcon(R.drawable.ic_message_blue_24dp)
                    mTabLayout!!.getTabAt(1)!!.setIcon(R.drawable.ic_list_tasks_24px)
                    mTabLayout!!.getTabAt(2)!!.setIcon(R.drawable.ic_people_blue_24dp)
                    mTabLayout!!.getTabAt(3)!!.setIcon(R.drawable.ic_request_list_blue_24dp)
                    mViewPager!!.offscreenPageLimit = 3
                } else {
                    mTabLayout!!.getTabAt(0)!!.setIcon(R.drawable.ic_message_blue_24dp)
                    mTabLayout!!.getTabAt(1)!!.setIcon(R.drawable.ic_list_tasks_24px)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.sign_out -> {
                mFirebaseAuth!!.signOut()
                val reference = FirebaseDatabase.getInstance().reference.child("token/" + rootUid!!)
                reference.removeValue()
                val intent = Intent(this@MainActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }

            R.id.my_info -> {
                val intent = Intent(this@MainActivity, UserInfoActivity::class.java)
                intent.putExtra("user", userAdmin)
                startActivity(intent)
            }
            R.id.ask_for_day_off -> {
                val intent = Intent(this@MainActivity, AskOffDayActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onTransferSignal(signalMessage: String, message: String) {
        if (signalMessage == "AddFriend") {
            val friendListFragment = mViewPagerAdapter!!.getItem(0) as FriendsListFragment
            friendListFragment.getEmailFriend(message)
        }
    }

    companion object {
        internal lateinit var mFriendReference: DatabaseReference

        private var rootUid: String? = null

        //Cat loading
        @SuppressLint("StaticFieldLeak")
        lateinit var mCatView: CatLoadingView
    }
}
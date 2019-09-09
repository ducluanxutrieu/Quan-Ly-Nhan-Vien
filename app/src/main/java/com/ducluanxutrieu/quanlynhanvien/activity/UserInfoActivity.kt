package com.ducluanxutrieu.quanlynhanvien.activity

import android.content.Intent
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView

import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.ducluanxutrieu.quanlynhanvien.models.RequestItem
import com.ducluanxutrieu.quanlynhanvien.models.Staff
import com.ducluanxutrieu.quanlynhanvien.R
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

import java.util.ArrayList

class UserInfoActivity : AppCompatActivity() {
    private lateinit var phone: TextInputEditText
    private lateinit var email: TextInputEditText
    private lateinit var position: TextInputEditText
    private lateinit var numberOffDays: TextInputEditText
    private lateinit var name: TextView
    private lateinit var imageProfile: ImageView
    private lateinit var staff: Staff
    private lateinit var mDatabaseReference: DatabaseReference
    private lateinit var requestItemList: MutableList<RequestItem>
    private var mChildEventListener: ChildEventListener? = null
    internal var daysOffDeny = 0
    internal var daysOffAccept = 0
    private lateinit var fab: FloatingActionButton
    private var isAdmin: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_info)
        val intent = intent
        staff = intent.getSerializableExtra("user") as Staff
        isAdmin = intent.getBooleanExtra("isAdmin", false)

        mapping()

        fab.setOnClickListener {
            val intent1 = Intent(this@UserInfoActivity, EditUserActivity::class.java)
            intent1.putExtra("user", staff)
            intent1.putExtra("signal", "updateUser")
            intent1.putExtra("isAdmin", isAdmin)
            startActivity(intent1)
        }

        show()
        mDatabaseReference = FirebaseDatabase.getInstance().reference.child("day_off/" + staff.uid!!)
        requestItemList = ArrayList()

        if (mChildEventListener == null) {
            mChildEventListener = object : ChildEventListener {
                override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                    val item = dataSnapshot.getValue(RequestItem::class.java)
                    if (item != null) {
                        requestItemList.add(item)
                        if (item.isAccept) {
                            daysOffAccept++
                        } else {
                            daysOffDeny++
                        }
                        Log.i(TAG, requestItemList[0].toString())
                        numberOffDays.setText(requestItemList.size.toString())
                    }
                }

                override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {

                }

                override fun onChildRemoved(dataSnapshot: DataSnapshot) {

                }

                override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {

                }

                override fun onCancelled(databaseError: DatabaseError) {

                }
            }
            mDatabaseReference.addChildEventListener(mChildEventListener!!)
        }

        numberOffDays.setText(requestItemList.size.toString())
    }


    private fun mapping() {
        email = findViewById(R.id.email_profile_email)
        position = findViewById(R.id.user_profile_position)
        phone = findViewById(R.id.user_profile_phone)
        numberOffDays = findViewById(R.id.user_profile_off_day)
        fab = findViewById(R.id.fab_info)
        name = findViewById(R.id.user_profile_name)
        imageProfile = findViewById(R.id.user_profile_photo)
    }

    private fun show() {
        email.setText(staff.email)
        position.setText(staff.position)
        phone.setText(staff.phone)
        name.text = staff.name
        Glide.with(this@UserInfoActivity).load(staff.avatarUrl).apply(RequestOptions.circleCropTransform()).into(imageProfile)
    }

    companion object {
        const val TAG = ".USerInfoLogin"
    }
}

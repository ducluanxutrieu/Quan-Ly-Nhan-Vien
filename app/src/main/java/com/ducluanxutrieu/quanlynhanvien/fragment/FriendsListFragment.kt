package com.ducluanxutrieu.quanlynhanvien.fragment

import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ducluanxutrieu.quanlynhanvien.R
import com.ducluanxutrieu.quanlynhanvien.activity.MainActivity
import com.ducluanxutrieu.quanlynhanvien.adapter.FriendsListAdapter
import com.ducluanxutrieu.quanlynhanvien.dialog.AddNewFriend
import com.ducluanxutrieu.quanlynhanvien.models.Friend
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.gms.tasks.Task
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.functions.FirebaseFunctions
import java.util.*

class FriendsListFragment : Fragment() {
    private var mRecyclerViewFriends: RecyclerView? = null
    private var mFAB: FloatingActionButton? = null

    private var mDatabaseReference: DatabaseReference? = null
    private var mFunctions: FirebaseFunctions? = null

    private var mFriendsAdapter: FriendsListAdapter? = null
    private var rootUid: String? = null
    private var userAlready = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_friends_list, container, false)

        mRecyclerViewFriends = rootView.findViewById(R.id.recycler_view_friends_list)
        mFAB = rootView.findViewById(R.id.fab_add_friend)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //getInstance
        //Firebase
        val mFirebaseDatabase = FirebaseDatabase.getInstance()
        val mFirebaseAuth = FirebaseAuth.getInstance()
        mFunctions = FirebaseFunctions.getInstance()

        rootUid = mFirebaseAuth.uid

        mDatabaseReference = mFirebaseDatabase.reference.child("friend_ship/" + rootUid!!)

        mFAB!!.setOnClickListener {
            val addNewFriend = AddNewFriend()
            assert(fragmentManager != null)
            addNewFriend.show(fragmentManager!!, "friend")
        }
    }


    fun getEmailFriend(email: String) {
        if (checkFriendAlreadyExist(email)) {
            Toast.makeText(context, "This friend already exist!", Toast.LENGTH_SHORT).show()
        } else {
            addFriend(email).addOnSuccessListener { s -> Toast.makeText(context, s, Toast.LENGTH_SHORT).show() }
                    .addOnFailureListener { e -> Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show() }
        }
    }

    private fun addFriend(text: String): Task<String> {
        // Create the arguments to the callable function.
        val data = HashMap<String, Any>()
        data["email"] = text

        return mFunctions!!
                .getHttpsCallable("addFriend")
                .call(data)
                .continueWith { task ->
                    // This continuation runs on either success or failure, but if the task
                    // has failed then getResult() will throw an Exception which will be
                    // propagated down.
                    task.result!!.data as String
                }
    }

    private fun checkFriendAlreadyExist(s1: String): Boolean {

        try {
            mDatabaseReference!!.child(s1).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val friend = dataSnapshot.getValue(Friend::class.java)

                    //userAlready = false if friend == null
                    userAlready = friend != null
                }

                override fun onCancelled(databaseError: DatabaseError) {

                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }

        return userAlready
    }

    override fun onStart() {
        super.onStart()
        val query = FirebaseDatabase.getInstance()
                .reference
                .child("friend_ship/" + rootUid!!)
                .limitToLast(50)
        val itemBuilder = FirebaseRecyclerOptions.Builder<Friend>()
        itemBuilder.setQuery(query, Friend::class.java)
        val options = itemBuilder.build()

        //mFriendsAdapter = new FriendsListAdapter(friendList, view.getContext());
        mFriendsAdapter = FriendsListAdapter(options)
        mFriendsAdapter!!.startListening()
        val layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        mRecyclerViewFriends!!.layoutManager = layoutManager
        mRecyclerViewFriends!!.adapter = mFriendsAdapter

        //Enable swipe left or right to delete an item
        enableSwipe()

        MainActivity.mCatView.dismiss()

    }

    override fun onStop() {
        super.onStop()
        mFriendsAdapter!!.stopListening()
    }

    private fun enableSwipe() {
        val simpleItemTouchCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, viewHolder1: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, i: Int) {
                val position = viewHolder.adapterPosition
                if (i == ItemTouchHelper.LEFT || i == ItemTouchHelper.RIGHT) {
                    val friend = mFriendsAdapter!!.getItem(position)
                    val key = mFriendsAdapter!!.getRef(position).key
                    mFriendsAdapter!!.getRef(position).removeValue()
                    val snackBar = Snackbar.make(view!!, "Removed a friend.", Snackbar.LENGTH_LONG)
                    snackBar.setAction("UNDO") { mFriendsAdapter!!.restoreItem(friend, key!!) }
                    snackBar.setActionTextColor(Color.YELLOW)
                    snackBar.show()
                }
            }

            override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
                ItemTouchHelper.Callback.getDefaultUIUtil().onDraw(c, recyclerView,
                        viewHolder.itemView, dX, dY,
                        actionState, isCurrentlyActive)
            }

        }
        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
        itemTouchHelper.attachToRecyclerView(mRecyclerViewFriends)
    }
}

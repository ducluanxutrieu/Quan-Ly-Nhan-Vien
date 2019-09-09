package com.ducluanxutrieu.quanlynhanvien.activity

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ducluanxutrieu.quanlynhanvien.adapter.MessageAdapter
import com.ducluanxutrieu.quanlynhanvien.models.MessageItem
import com.ducluanxutrieu.quanlynhanvien.R
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.messaging.FirebaseMessaging
import java.util.*

class ChatsActivity : AppCompatActivity() {
    //recycler
    internal lateinit var layoutManager: LinearLayoutManager
    internal lateinit var mChatRecyclerView: RecyclerView
    private lateinit var query: Query

    private lateinit var mInputChatEditText: EditText
    internal lateinit var mSendImageButton: ImageButton
    internal lateinit var mMessageAdapter: MessageAdapter
    private lateinit var mMessageItems: MutableList<MessageItem>

    //Firebase
    private lateinit var mMessageFireDatabase: FirebaseDatabase
    private lateinit var mFunctions: FirebaseFunctions
    private lateinit var mFirebaseMessage: FirebaseMessaging
    private lateinit var mMessageFireAuth: FirebaseAuth
    private var mUserFire: FirebaseUser? = null

    private var rootName: String? = null
    private var uidFriend: String? = null
    private var nameFriend: String? = null


    //loadmore
    private lateinit var loadmoreProgress: ProgressBar
    private var limitToLoad = 50

    private val timeNow: String
        get() {
            val time: String
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)
            time = "$hour:$minute"
            return time
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chats)
        mapping()

        getInstance()

        getRootName()

        getDataFromIntent()

        getChatTextChanged()
    }

    fun getInstance(){
        mMessageItems = ArrayList()

        mFunctions = FirebaseFunctions.getInstance()
        mMessageFireDatabase = FirebaseDatabase.getInstance()
        mMessageFireAuth = FirebaseAuth.getInstance()
        mFirebaseMessage = FirebaseMessaging.getInstance()
        mUserFire = mMessageFireAuth.currentUser
    }

    private fun getRootName() {
        if (mMessageFireAuth.currentUser != null) {
            rootName = mMessageFireAuth.currentUser!!.displayName
        }
    }

    private fun getDataFromIntent() {
        uidFriend = intent.getStringExtra("uid")
        nameFriend = intent.getStringExtra("name")
        title = nameFriend
    }

    fun buttonSendListener(view : View){
        addMessage(mInputChatEditText.text.toString())
        mInputChatEditText.setText("")
    }

    private fun getChatTextChanged() {
        mInputChatEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                mSendImageButton.isEnabled = s.toString().trim { it <= ' ' }.isNotEmpty()
            }

            override fun afterTextChanged(s: Editable) {

            }
        })
    }

    private fun addMessage(text: String): Task<String> {
        // Create the arguments to the callable function.
        val data = HashMap<String, String>()
        data["text"] = text
        data["timeStamp"] = timeNow
        data["to"] = uidFriend!!

        return mFunctions
                .getHttpsCallable("addMessage")
                .call(data)
                .continueWith { task ->
                    // This continuation runs on either success or failure, but if the task
                    // has failed then getResult() will throw an Exception which will be
                    // propagated down.
                    task.result!!.data as String?
                }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        mMessageItems.clear()
    }

    private fun mapping() {
        mChatRecyclerView = findViewById(R.id.chats_recycler_view)
        mInputChatEditText = findViewById(R.id.input_chats_edit_text)
        mSendImageButton = findViewById(R.id.send_message_image_button)
        loadmoreProgress = findViewById(R.id.loadmore_progress)
    }

    override fun onStart() {
        super.onStart()
        query = FirebaseDatabase.getInstance().reference.child("messages" + "/" + mUserFire!!.uid + "/" + uidFriend).limitToLast(limitToLoad)
        val itemBuilder = FirebaseRecyclerOptions.Builder<MessageItem>()
        itemBuilder.setQuery(query, MessageItem::class.java)
        val options = itemBuilder.build()

        layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mChatRecyclerView.layoutManager = layoutManager
        mMessageAdapter = MessageAdapter(options)
        mMessageAdapter.startListening()
        mChatRecyclerView.adapter = mMessageAdapter

        mMessageAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                layoutManager.smoothScrollToPosition(mChatRecyclerView, null, mMessageAdapter.itemCount - 1)
            }
        })
    }

    override fun onStop() {
        super.onStop()
        mMessageAdapter.stopListening()
    }
}
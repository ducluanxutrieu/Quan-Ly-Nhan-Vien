package com.ducluanxutrieu.quanlynhanvien.Activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.ducluanxutrieu.quanlynhanvien.Adapter.MessageAdapter;
import com.ducluanxutrieu.quanlynhanvien.Models.Friend;
import com.ducluanxutrieu.quanlynhanvien.Models.MessageItem;
import com.ducluanxutrieu.quanlynhanvien.MyTask;
import com.ducluanxutrieu.quanlynhanvien.R;
import com.ducluanxutrieu.quanlynhanvien.Models.TokenUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.List;

public class ChatsActivity extends AppCompatActivity {
    RecyclerView mChatRecyclerView;
    EditText mInputChatEditText;
    ImageButton mSendImageButton;
    MessageAdapter mMessageAdapter;
    List<MessageItem> mMessageItems;

    //Firebase
    FirebaseDatabase mMessageFireDatabase;
    DatabaseReference mMessageReferenceSend;
    DatabaseReference mMessageReferenceReceive;
    ChildEventListener mMessageChildEvenListener;
    FirebaseMessaging mFirebaseMessage;
    FirebaseAuth mMessageFireAuth;
    FirebaseUser mUserFire;
    FirebaseFunctions mFunctions;

    Friend friend;
    String rootName;

    private static String token;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats);
        mapping();
        mMessageItems = new ArrayList<>();

        Intent intent = getIntent();
        friend = (Friend) intent.getSerializableExtra("friend");
        setTitle(friend.getName());
        mMessageFireDatabase = FirebaseDatabase.getInstance();
        mMessageFireAuth = FirebaseAuth.getInstance();
        mFirebaseMessage = FirebaseMessaging.getInstance();
        mFunctions = FirebaseFunctions.getInstance();
        mUserFire = mMessageFireAuth.getCurrentUser();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mChatRecyclerView.setLayoutManager(layoutManager);
        if (mMessageFireAuth.getCurrentUser() != null){
            rootName = mMessageFireAuth.getCurrentUser().getDisplayName();
        }

        mMessageAdapter = new MessageAdapter(mMessageItems, rootName);
        mChatRecyclerView.setAdapter(mMessageAdapter);

        mMessageReferenceSend = mMessageFireDatabase.getReference().child("message" + "/" + mUserFire.getEmail().replace(".", "") + "/" + friend.getEmail().replace(".", ""));
        mMessageReferenceReceive = mMessageFireDatabase.getReference().child("message" + "/" + friend.getEmail().replace(".", "") + "/" + mUserFire.getEmail().replace(".", ""));
        final DatabaseReference referenceSend = mMessageFireDatabase.getReference().child("friend_ship/" + "/" + mUserFire.getEmail().replace(".", "") + "/" + friend.getEmail().replace(".", ""));
        final DatabaseReference referenceReceive = mMessageFireDatabase.getReference().child("friend_ship" + "/" + friend.getEmail().replace(".", "") + "/" + mUserFire.getEmail().replace(".", ""));

        attachDatabaseReadListener();

        mInputChatEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() > 0){
                    mSendImageButton.setEnabled(true);
                }else {
                    mSendImageButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mSendImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MessageItem messageItem = new MessageItem(mInputChatEditText.getText().toString(), mUserFire.getDisplayName(), "a");
                String mPushKey = mMessageReferenceSend.push().getKey();
                mMessageReferenceSend.child(mPushKey).setValue(messageItem);
                mMessageReferenceReceive.child(mPushKey).setValue(messageItem);
                Friend root = new Friend(mUserFire.getDisplayName(), mUserFire.getEmail(), mUserFire.getUid(), mInputChatEditText.getText().toString());

                friend.setRecentMessage(mInputChatEditText.getText().toString());
                referenceSend.setValue(friend);
                referenceReceive.setValue(root);

                pushNoti();
                mInputChatEditText.setText("");
            }
        });

        getToken();
    }

    private void pushNoti() {
        MyTask task = new MyTask(ChatsActivity.this);

        final String BASE_URL = "https://quan-ly-nhan-vien.firebaseapp.com/api";
        //final String BASE_URL = "http://localhost:5000/api";
        String name = rootName;
        String message = mInputChatEditText.getText().toString();
        String to = token;
        //{"data":{"title":"luanxx","message":"cac","bac":"cac"},"to":"ejgJWQ"}
        String query = "{\"data\":{\"title\":\""+name +"\",\"message\":\""+ message + "\"},\"to\":\""+to+"\"}";
        Log.i("QUERY", query);
        task.execute(BASE_URL, query);
    }

    private void getToken() {
        runtimeEnableAutoInit();
        String email =  friend.getEmail().replace(".", "");
        String pathChild = "token/" + email;
        DatabaseReference getTokenReference = mMessageFireDatabase.getReference().child(pathChild);
        getTokenReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                TokenUser tokenUser = dataSnapshot.getValue(TokenUser.class);
                if (tokenUser == null){
                    token = null;
                }else {
                    token = tokenUser.getTokenU();
                    Log.i("kiemtra1", token);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void runtimeEnableAutoInit() {
        // [START fcm_runtime_enable_auto_init]
        FirebaseMessaging.getInstance().setAutoInitEnabled(true);
        // [END fcm_runtime_enable_auto_init]
    }

    private void attachDatabaseReadListener() {
        if (mMessageChildEvenListener == null){
            mMessageChildEvenListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    MessageItem messageItem = dataSnapshot.getValue(MessageItem.class);
                    mMessageItems.add(messageItem);
                    mMessageAdapter.notifyItemChanged(mMessageItems.size());
                    mChatRecyclerView.scrollToPosition(mMessageAdapter.getItemCount() - 1);
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
            mMessageReferenceSend.addChildEventListener(mMessageChildEvenListener);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mMessageReferenceSend.removeEventListener(mMessageChildEvenListener);
        mMessageChildEvenListener = null;
        mMessageItems.clear();
    }

    private void mapping(){
        mChatRecyclerView = findViewById(R.id.chats_recycler_view);
        mInputChatEditText = findViewById(R.id.input_chats_edit_text);
        mSendImageButton = findViewById(R.id.send_message_image_button);
    }
}
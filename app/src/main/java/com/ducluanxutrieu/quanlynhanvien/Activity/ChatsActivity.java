package com.ducluanxutrieu.quanlynhanvien.Activity;

import android.accounts.Account;
import android.accounts.AccountManager;
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
import com.ducluanxutrieu.quanlynhanvien.Item.Friend;
import com.ducluanxutrieu.quanlynhanvien.Item.MessageItem;
import com.ducluanxutrieu.quanlynhanvien.R;
import com.ducluanxutrieu.quanlynhanvien.Item.TokenUser;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import java.util.ArrayList;
import java.util.List;

public class ChatsActivity extends AppCompatActivity {
    RecyclerView mChatRecyclerView;
    EditText mInputChatEditText;
    ImageButton mSendImageButton;
    MessageAdapter mMessageAdapter;
    List<MessageItem> mMessageItems;

    FirebaseDatabase mMessageFireDatabase;
    DatabaseReference mMessageReferenceSend;
    DatabaseReference mMessageReferenceReceive;
    ChildEventListener mMessageChildEvenListener;
    FirebaseMessaging mFirebaseMessage;
    FirebaseAuth mMessageFireAuth;
    FirebaseUser mUserFire;
    Friend friend;
    String rootName;

    private static String token;
    public static String TAG = ".ChatsActivity";
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

                mInputChatEditText.setText("");
                mChatRecyclerView.scrollToPosition(mMessageAdapter.getItemCount());
            }
        });

        getToken();
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

    public String getAccount() {
        // This call requires the Android GET_ACCOUNTS permission
        Account[] accounts = AccountManager.get(this /* activity */).
                getAccountsByType("com.google");
        if (accounts.length == 0) {
            return null;
        }
        return accounts[0].name;
    }

    public void getAuthToken() {
        // [START fcm_get_token]
        String accountName = getAccount();

        // Initialize the scope using the client ID you got from the Console.
        final String scope = "audience:server:client_id:"
                + "1262xxx48712-9qs6n32447mcj9dirtnkyrejt82saa52.apps.googleusercontent.com";

        String idToken = null;
        try {
            idToken = GoogleAuthUtil.getToken(this, accountName, scope);
        } catch (Exception e) {
            Log.w(TAG, "Exception while getting idToken: " + e);
        }
        // [END fcm_get_token]
    }

    public void sendUpstream(String message) {

        final String SENDER_ID = token;
        final int messageId = 0; // Increment for each
        // [START fcm_send_upstream]
        FirebaseMessaging fm = FirebaseMessaging.getInstance();
        fm.send(new RemoteMessage.Builder("615792692592")
                .setMessageId(Integer.toString(messageId))
                .addData("my_message", message)
                .addData("to",token)
                .build());
        // [END fcm_send_upstream]
    }
    private void attachDatabaseReadListener() {
        if (mMessageChildEvenListener == null){
            mMessageChildEvenListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    MessageItem messageItem = dataSnapshot.getValue(MessageItem.class);
                    mMessageItems.add(messageItem);
                    mMessageAdapter.notifyDataSetChanged();
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


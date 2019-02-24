package com.ducluanxutrieu.quanlynhanvien;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.ducluanxutrieu.quanlynhanvien.Adapter.MessageAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class ChatsActivity extends AppCompatActivity {
    RecyclerView mChatRecyclerView;
    EditText mInputChatEditText;
    ImageButton mSendImageButton;
    ActionBar mActionBar;
    MessageAdapter mMessageAdapter;
    List<MessageItem> mMessageItems;
    String uidChatWith = "";
    String nameChatWith = "";

    FirebaseDatabase mMessageFireDatabase;
    DatabaseReference mMessageReferenceSend;
    DatabaseReference mMessageReferenceReceive;
    ChildEventListener mMessageChildEvenListener;
    FirebaseAuth mMessageFireAuth;
    FirebaseUser mUserFire;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats);
        mapping();
//        mActionBar.setHomeButtonEnabled(true);
        mMessageItems = new ArrayList<>();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mChatRecyclerView.setLayoutManager(layoutManager);
        mMessageAdapter = new MessageAdapter(mMessageItems, this);
        mChatRecyclerView.setAdapter(mMessageAdapter);

        Intent intent = getIntent();
        uidChatWith = intent.getStringExtra("uid");
        nameChatWith = intent.getStringExtra("name");
        setTitle(nameChatWith);

        mMessageFireDatabase = FirebaseDatabase.getInstance();
        mMessageFireAuth = FirebaseAuth.getInstance();
        mUserFire = mMessageFireAuth.getCurrentUser();
        mMessageReferenceSend = mMessageFireDatabase.getReference().child("message" + "/" + mUserFire.getUid() + "/" + uidChatWith);
        mMessageReferenceReceive = mMessageFireDatabase.getReference().child("message" + "/" + uidChatWith + "/" + mUserFire.getUid());
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
            }
        });
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

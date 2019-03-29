package com.ducluanxutrieu.quanlynhanvien.Activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.ducluanxutrieu.quanlynhanvien.Adapter.MessageAdapter;
import com.ducluanxutrieu.quanlynhanvien.Models.MessageItem;
import com.ducluanxutrieu.quanlynhanvien.R;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.FirebaseFunctionsException;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatsActivity extends AppCompatActivity {
    //recycler
    LinearLayoutManager layoutManager;
    RecyclerView mChatRecyclerView;
    Query query;

    EditText mInputChatEditText;
    ImageButton mSendImageButton;
    MessageAdapter mMessageAdapter;
    List<MessageItem> mMessageItems;

    //Firebase
    FirebaseDatabase mMessageFireDatabase;
    FirebaseFunctions mFunctions;
    FirebaseMessaging mFirebaseMessage;
    FirebaseAuth mMessageFireAuth;
    FirebaseUser mUserFire;

    String rootName;
    String uidFriend;
    String nameFriend;


    //loadmore
    ProgressBar loadmoreProgress;
    int limitToLoad = 50;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats);
        mapping();
        mMessageItems = new ArrayList<>();


        mFunctions = FirebaseFunctions.getInstance();
        mMessageFireDatabase = FirebaseDatabase.getInstance();
        mMessageFireAuth = FirebaseAuth.getInstance();
        mFirebaseMessage = FirebaseMessaging.getInstance();
        mUserFire = mMessageFireAuth.getCurrentUser();

        if (mMessageFireAuth.getCurrentUser() != null){
            rootName = mMessageFireAuth.getCurrentUser().getDisplayName();
        }

        Intent intent = getIntent();
        uidFriend = intent.getStringExtra("uid");
        nameFriend = intent.getStringExtra("name");
        setTitle(nameFriend);


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
                //pushNoti();
                addMessage(mInputChatEditText.getText().toString()).addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Exception e = task.getException();
                            if (e instanceof FirebaseFunctionsException) {
                                FirebaseFunctionsException ffe = (FirebaseFunctionsException) e;
//                                FirebaseFunctionsException.Code code = ffe.getCode();
//                                Object details = ffe.getDetails();
                            }

                            // ...
                        }

                        // ...
                    }
                });
                mInputChatEditText.setText("");
            }
        });
    }
    private Task<String> addMessage(String text) {
        // Create the arguments to the callable function.
        Map<String, String> data = new HashMap<>();
        data.put("text", text);
        data.put("timeStamp", getTimeNow());
        data.put("to", uidFriend);
        //data.put("push", true);

        return mFunctions
                .getHttpsCallable("addMessage")
                .call(data)
                .continueWith(new Continuation<HttpsCallableResult, String>() {
                    @Override
                    public String then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        // This continuation runs on either success or failure, but if the task
                        // has failed then getResult() will throw an Exception which will be
                        // propagated down.
                        return (String) task.getResult().getData();
                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mMessageItems.clear();
    }

    private void mapping(){
        mChatRecyclerView = findViewById(R.id.chats_recycler_view);
        mInputChatEditText = findViewById(R.id.input_chats_edit_text);
        mSendImageButton = findViewById(R.id.send_message_image_button);
        loadmoreProgress = findViewById(R.id.loadmore_progress);
    }

    private String getTimeNow() {
        String time;
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        time = hour + ":" + minute;
        return time;
    }

    @Override
    protected void onStart() {
        super.onStart();
        query = FirebaseDatabase.getInstance().getReference().child("messages" + "/" + mUserFire.getUid() + "/" + uidFriend).limitToLast(limitToLoad);
        FirebaseRecyclerOptions.Builder<MessageItem> itemBuilder = new FirebaseRecyclerOptions.Builder<>();
        itemBuilder.setQuery(query, MessageItem.class);
        FirebaseRecyclerOptions<MessageItem> options = itemBuilder.build();

        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mChatRecyclerView.setLayoutManager(layoutManager);
        mMessageAdapter = new MessageAdapter(options);
        mMessageAdapter.startListening();
        mChatRecyclerView.setAdapter(mMessageAdapter);

        mMessageAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                layoutManager.smoothScrollToPosition(mChatRecyclerView, null, mMessageAdapter.getItemCount() -1);
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        mMessageAdapter.stopListening();
    }
}
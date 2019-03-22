package com.ducluanxutrieu.quanlynhanvien.Activity;

import android.content.Intent;
import android.support.annotation.NonNull;
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
import android.widget.Toast;

import com.ducluanxutrieu.quanlynhanvien.Adapter.MessageAdapter;
import com.ducluanxutrieu.quanlynhanvien.Models.Friend;
import com.ducluanxutrieu.quanlynhanvien.Models.MessageItem;
import com.ducluanxutrieu.quanlynhanvien.MyTask;
import com.ducluanxutrieu.quanlynhanvien.R;
import com.ducluanxutrieu.quanlynhanvien.Models.TokenUser;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.FirebaseFunctionsException;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatsActivity extends AppCompatActivity {
    RecyclerView mChatRecyclerView;
    EditText mInputChatEditText;
    ImageButton mSendImageButton;
    MessageAdapter mMessageAdapter;
    List<MessageItem> mMessageItems;

    //Firebase
    FirebaseDatabase mMessageFireDatabase;
    //DatabaseReference mMessageReferenceSend;
    //DatabaseReference mMessageReferenceReceive;
    FirebaseFunctions mFunctions;
    //ChildEventListener mMessageChildEvenListener;
    FirebaseMessaging mFirebaseMessage;
    FirebaseAuth mMessageFireAuth;
    FirebaseUser mUserFire;

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

        mFunctions = FirebaseFunctions.getInstance();
        mMessageFireDatabase = FirebaseDatabase.getInstance();
        mMessageFireAuth = FirebaseAuth.getInstance();
        mFirebaseMessage = FirebaseMessaging.getInstance();
        mUserFire = mMessageFireAuth.getCurrentUser();


        if (mMessageFireAuth.getCurrentUser() != null){
            rootName = mMessageFireAuth.getCurrentUser().getDisplayName();
        }

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
                                FirebaseFunctionsException.Code code = ffe.getCode();
                                Object details = ffe.getDetails();
                            }

                            // ...
                        }

                        // ...
                    }
                });
                mInputChatEditText.setText("");
            }
        });

        getToken();
    }

    private Task<String> addMessage(String text) {
        // Create the arguments to the callable function.
        Map<String, Object> data = new HashMap<>();
        data.put("text", text);
        data.put("to", friend.getUid());
        data.put("push", true);

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


    private void pushNoti() {
        MyTask task = new MyTask(ChatsActivity.this);

        final String BASE_URL = "https://quan-ly-nhan-vien.firebaseapp.com/api";
        //final String BASE_URL = "http://localhost:5000/api";
        String name = rootName;
        String message = mInputChatEditText.getText().toString();
        String to = token;
        String query = "{\"data\":{\"title\":\""+name +"\",\"message\":\""+ message + "\"},\"to\":\""+to+"\"}";
        Log.i("QUERY", query);
        task.execute(BASE_URL, query);
    }

    private void getToken() {
        runtimeEnableAutoInit();
        String email =  friend.getUid();
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        mMessageReferenceSend.removeEventListener(mMessageChildEvenListener);
        //mMessageChildEvenListener = null;
        mMessageItems.clear();
    }

    private void mapping(){
        mChatRecyclerView = findViewById(R.id.chats_recycler_view);
        mInputChatEditText = findViewById(R.id.input_chats_edit_text);
        mSendImageButton = findViewById(R.id.send_message_image_button);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Query query = FirebaseDatabase.getInstance().getReference().child("messages" + "/" + mUserFire.getUid() + "/" + friend.getUid()).limitToLast(50);
        FirebaseRecyclerOptions.Builder<MessageItem> itemBuilder = new FirebaseRecyclerOptions.Builder<>();
        itemBuilder.setQuery(query, MessageItem.class);
        FirebaseRecyclerOptions<MessageItem> options = itemBuilder.build();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mChatRecyclerView.setLayoutManager(layoutManager);
        mMessageAdapter = new MessageAdapter(options);
        mMessageAdapter.startListening();
        mChatRecyclerView.setAdapter(mMessageAdapter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mMessageAdapter.stopListening();
    }
}
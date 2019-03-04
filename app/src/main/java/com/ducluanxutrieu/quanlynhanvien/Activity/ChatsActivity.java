package com.ducluanxutrieu.quanlynhanvien.Activity;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.os.AsyncTask;
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
import android.widget.Toast;

import com.ducluanxutrieu.quanlynhanvien.Adapter.MessageAdapter;
import com.ducluanxutrieu.quanlynhanvien.Item.ChatID;
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

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
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

    FirebaseDatabase mMessageFireDatabase;
    DatabaseReference mMessageReferenceSend;
    DatabaseReference mMessageReferenceReceive;
    ChildEventListener mMessageChildEvenListener;
    FirebaseMessaging mFirebaseMessage;
    FirebaseAuth mMessageFireAuth;
    FirebaseUser mUserFire;
    Friend friend;
    String rootName;
    String chatIDWith;

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

        //findChatID();

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
                pushNoti();
                mInputChatEditText.setText("");
            }
        });

        getToken();
    }

     private void pushNoti() {
        AsyncTask<String, Void ,String> task = new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... strings) {
                String BASE_URL = strings[0];
                String query = strings[1];
                StringBuilder result = new StringBuilder();
                try {
                    URL url = new URL(BASE_URL);
                    HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
                    httpCon.setDoOutput(true);
                    httpCon.setDoInput(true);
                    httpCon.setRequestMethod("POST");
                    httpCon.setRequestProperty("Accept", "application/json");
                    httpCon.setRequestProperty("Content-Type", "application/json");

                    OutputStream os = httpCon.getOutputStream();
                    OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");
                    osw.write(query);
                    osw.flush();
                    osw.close();
                    os.close();  //don't forget to close the OutputStream
                    httpCon.connect();

                    //read the inputstream and print it
                    InputStream inputStream = httpCon.getInputStream();
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    String line = "";

                    //send message result
                    while((line = bufferedReader.readLine()) != null) {
                        result.append(line);
                    }
                    bufferedReader.close();
                    inputStreamReader.close();
                    inputStream.close();
                    System.out.println(result.toString());
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return result.toString();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                Toast.makeText(ChatsActivity.this, s, Toast.LENGTH_SHORT).show();
            }
        };

        final String BASE_URL = "https://quan-ly-nhan-vien.firebaseapp.com/api";
        String name = rootName;
        String message = mInputChatEditText.getText().toString();
        String to = token;
        //{"data":{"title":"luanxx","message":"cac"},"to":"ejgJWQ"}
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
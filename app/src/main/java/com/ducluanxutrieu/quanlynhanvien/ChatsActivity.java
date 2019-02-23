package com.ducluanxutrieu.quanlynhanvien;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.widget.EditText;
import android.widget.ImageButton;

public class ChatsActivity extends AppCompatActivity {
    RecyclerView mChatRecyclerView;
    EditText mInputChatEditText;
    ImageButton mSendImageButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats);
    }

    private void mapping(){
        mChatRecyclerView = findViewById(R.id.chats_recycler_view);
        mInputChatEditText = findViewById(R.id.input_chats_edit_text);
        mSendImageButton = findViewById(R.id.send_message_image_button);
    }
}

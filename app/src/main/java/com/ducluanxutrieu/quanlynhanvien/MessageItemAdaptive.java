package com.ducluanxutrieu.quanlynhanvien;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

public class MessageItemAdaptive extends ArrayAdapter<MessageItem> {
    private ImageView photoMessage;
    private TextView nameMessage;
    private TextView textMessage;


    public MessageItemAdaptive(Context context, int resource, List<MessageItem> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null){
            convertView = ((Activity)getContext()).getLayoutInflater().inflate(R.layout.item_message, parent, false);
        }

        photoMessage = convertView.findViewById(R.id.photoImageView);
        nameMessage = convertView.findViewById(R.id.nameTextView);
        textMessage = convertView.findViewById(R.id.messageTextView);

        MessageItem messageItem =getItem(position);
        boolean isPhoto = (messageItem.getPhotoUrl() != null);
        if (isPhoto){
            photoMessage.setVisibility(View.VISIBLE);
            textMessage.setVisibility(View.GONE);
            Glide.with(photoMessage.getContext())
                    .load(messageItem.getPhotoUrl())
                    .into(photoMessage);
        }else {
            textMessage.setVisibility(View.VISIBLE);
            photoMessage.setVisibility(View.GONE);
            textMessage.setText(messageItem.getText());
        }

        nameMessage.setText(messageItem.getName());

        return convertView;
    }
}

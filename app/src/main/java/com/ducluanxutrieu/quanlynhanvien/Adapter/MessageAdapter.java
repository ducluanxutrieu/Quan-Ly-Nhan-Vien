package com.ducluanxutrieu.quanlynhanvien.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ducluanxutrieu.quanlynhanvien.MessageItem;
import com.ducluanxutrieu.quanlynhanvien.R;

import java.util.List;


public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    List<MessageItem> messageItems;
    Context context;

    final private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater =LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.item_message, viewGroup, false);

        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        if (viewHolder instanceof ItemViewHolder){
            ItemViewHolder itemViewHolder = (ItemViewHolder) viewHolder;
            itemViewHolder.contentItemMessage.setText(messageItems.get(i).getText());
            itemViewHolder.nameItemMessage.setText(messageItems.get(i).getName());
            itemViewHolder.avatarItemMessage.setImageResource(messageItems.get(i).getPhotoUrl());
            viewHolder.itemView.setOnClickListener(mOnClickListener);
        }
    }

    @Override
    public int getItemCount() {
        return messageItems.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        ImageView avatarItemMessage;
        TextView nameItemMessage, contentItemMessage;
        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            avatarItemMessage = itemView.findViewById(R.id.avatar_item_message);
            nameItemMessage = itemView.findViewById(R.id.name_item_message);
            contentItemMessage = itemView.findViewById(R.id.content_item_message);
        }
    }
}

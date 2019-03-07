package com.ducluanxutrieu.quanlynhanvien.Adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ducluanxutrieu.quanlynhanvien.Models.MessageItem;
import com.ducluanxutrieu.quanlynhanvien.R;

import java.util.List;


public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ItemViewHolder> {
    private static final int RIGHT_MSG = 0;
    private static final int LEFT_MSG = 1;
    private List<MessageItem> messageItems;
    private String nameUser;

    public MessageAdapter(List<MessageItem> messageItems, String nameUser) {
        this.messageItems = messageItems;
        this.nameUser = nameUser;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater =LayoutInflater.from(viewGroup.getContext());
        View view;
        if (viewType == RIGHT_MSG){
            view = inflater.inflate(R.layout.item_message_right, viewGroup, false);
        }else {
            view = inflater.inflate(R.layout.item_message_left, viewGroup, false);
        }
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder itemViewHolder, int i) {
        itemViewHolder.contentItemMessage.setText(messageItems.get(itemViewHolder.getAdapterPosition()).getText());
        itemViewHolder.nameItemMessage.setText(messageItems.get(itemViewHolder.getAdapterPosition()).getName());
    }

    @Override
    public long getItemId(int position) {

        return super.getItemId(position);
    }

    @Override
    public int getItemViewType(int position) {
        MessageItem messageItem = messageItems.get(position);

        if (messageItem.getName().equals(nameUser)){
            return RIGHT_MSG;
        }else {
            return LEFT_MSG;
        }
    }
    @Override
    public int getItemCount() {
        return messageItems.size();
    }


    class ItemViewHolder extends RecyclerView.ViewHolder{
        TextView nameItemMessage, contentItemMessage;

        ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            nameItemMessage = itemView.findViewById(R.id.name_item_message);
            contentItemMessage = itemView.findViewById(R.id.content_item_message);
        }

    }

}

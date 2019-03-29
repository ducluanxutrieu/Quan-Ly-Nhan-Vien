package com.ducluanxutrieu.quanlynhanvien.Adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ducluanxutrieu.quanlynhanvien.Models.MessageItem;
import com.ducluanxutrieu.quanlynhanvien.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;

import java.util.List;


public class MessageAdapter extends FirebaseRecyclerAdapter<MessageItem, MessageAdapter.ItemViewHolder> {
    private static final int RIGHT_MSG = 0;
    private static final int LEFT_MSG = 1;
    private static final String nameUser = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
    private View view;
    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public MessageAdapter(@NonNull FirebaseRecyclerOptions<MessageItem> options) {
        super(options);
    }


    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater =LayoutInflater.from(viewGroup.getContext());

        if (viewType == RIGHT_MSG){
            view = inflater.inflate(R.layout.item_message_right, viewGroup, false);
        }else {
            view = inflater.inflate(R.layout.item_message_left, viewGroup, false);
        }
        return new ItemViewHolder(view);
    }

    @Override
    public void onError(@NonNull DatabaseError error) {
        super.onError(error);
        Toast.makeText(view.getContext(), "Error send message", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onBindViewHolder(@NonNull ItemViewHolder holder, int position, @NonNull MessageItem model) {
        holder.contentItemMessage.setText(model.getText());
        //holder.nameItemMessage.setText(model.getName());
        holder.timeStampMessage.setText(model.getTimeStamp());
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemViewType(int position) {
        if (getItem(position).getName().equals(nameUser)){
            return RIGHT_MSG;
        }else {
            return LEFT_MSG;
        }
    }


    class ItemViewHolder extends RecyclerView.ViewHolder{
        TextView nameItemMessage, contentItemMessage, timeStampMessage;

        ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            //nameItemMessage = itemView.findViewById(R.id.sender_text_view);
            contentItemMessage = itemView.findViewById(R.id.message_text_view);
            timeStampMessage = itemView.findViewById(R.id.timestamp_text_view);
        }
    }
}
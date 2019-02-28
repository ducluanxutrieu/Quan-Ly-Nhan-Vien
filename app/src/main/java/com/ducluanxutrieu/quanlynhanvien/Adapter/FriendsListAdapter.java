package com.ducluanxutrieu.quanlynhanvien.Adapter;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ducluanxutrieu.quanlynhanvien.Activity.ChatsActivity;
import com.ducluanxutrieu.quanlynhanvien.Item.Friend;
import com.ducluanxutrieu.quanlynhanvien.R;

import java.util.List;

public class FriendsListAdapter extends RecyclerView.Adapter<FriendsListAdapter.ItemViewHolder>{
    List<Friend> friendList;
    Context context;


    public FriendsListAdapter(List<Friend> friendList, Context context) {
        this.friendList = friendList;
        this.context = context;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.friend_item, viewGroup, false);

        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ItemViewHolder itemViewHolder, int i) {
        itemViewHolder.nameMessage.setText(friendList.get(i).getName());
        itemViewHolder.recentMessage.setText(friendList.get(i).getRecentMessage());
        itemViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, ChatsActivity.class);
                intent.putExtra("friend", friendList.get(itemViewHolder.getAdapterPosition()));
                ActivityOptions options =
                        ActivityOptions.makeCustomAnimation(context, R.anim.fui_slide_in_right, R.anim.fui_slide_out_left);
                context.startActivity(intent, options.toBundle());
            }
        });
    }

    @Override
    public int getItemCount() {
        return friendList.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView nameMessage;
        TextView recentMessage;
        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            nameMessage = itemView.findViewById(R.id.friend_name);
            recentMessage = itemView.findViewById(R.id.recent_chat);
        }
    }
}

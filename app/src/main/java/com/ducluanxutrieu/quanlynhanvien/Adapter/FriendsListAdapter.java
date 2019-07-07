package com.ducluanxutrieu.quanlynhanvien.Adapter;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ducluanxutrieu.quanlynhanvien.Activity.ChatsActivity;
import com.ducluanxutrieu.quanlynhanvien.Models.Friend;
import com.ducluanxutrieu.quanlynhanvien.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class FriendsListAdapter extends FirebaseRecyclerAdapter<Friend, FriendsListAdapter.ItemViewHolder> {

    public FriendsListAdapter(@NonNull FirebaseRecyclerOptions<Friend> options) {
        super(options);
    }


    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.item_friend, viewGroup, false);

        return new ItemViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull ItemViewHolder holder, int position, @NonNull final Friend model) {
        holder.setNameMessage(model.getName());
        holder.setRecentMessage(model.getRecentMessage());
        holder.setAvatar();
        Glide.with(holder.itemView.getContext()).load(model.getAvatarUrl()).apply(RequestOptions.circleCropTransform()).into(holder.avatar);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, ChatsActivity.class);
                intent.putExtra("friend", model);
                ActivityOptions options =
                        ActivityOptions.makeCustomAnimation(context, R.anim.fui_slide_in_right, R.anim.fui_slide_out_left);
                context.startActivity(intent, options.toBundle());
            }
        });
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView nameMessage;
        TextView recentMessage;
        ImageView avatar;
        View view;
        ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
        }

        void setNameMessage(String name) {
            nameMessage = view.findViewById(R.id.friend_name);
            nameMessage.setText(name);
        }

        void setRecentMessage(String recent) {
            recentMessage = view.findViewById(R.id.recent_chat);
            recentMessage.setText(recent);
        }

        void setAvatar() {
            avatar = view.findViewById(R.id.avatar);
        }
    }
}

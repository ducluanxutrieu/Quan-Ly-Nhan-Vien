package com.ducluanxutrieu.quanlynhanvien.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ducluanxutrieu.quanlynhanvien.R;
import com.ducluanxutrieu.quanlynhanvien.Users;

import java.util.ArrayList;
import java.util.List;

public class StaffListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Users> usersList;
    public Context context;
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
/*            Bundle bundle = new Bundle();
            bundle.putString("test", "ádđ ");
            ChatsFragment chatsFragment = new ChatsFragment();
            chatsFragment.setArguments(bundle);
            mainActivity.getSupportFragmentManager().beginTransaction().commit();*/
        }
    };

    public StaffListAdapter(ArrayList<Users> usersList, Context context){
        this.usersList = usersList;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.staff_item, viewGroup, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        ItemViewHolder itemViewHolder = (ItemViewHolder) viewHolder;
        itemViewHolder.name.setText(usersList.get(i).getName());
        itemViewHolder.recentChat.setText(usersList.get(i).getRecentChat());
        viewHolder.itemView.setOnClickListener(onClickListener);
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView name, recentChat;
        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            recentChat = itemView.findViewById(R.id.recent_chats);
            name = itemView.findViewById(R.id.staff_name);
        }
    }
}

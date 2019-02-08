package com.ducluanxutrieu.quanlynhanvien.Admin;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ducluanxutrieu.quanlynhanvien.R;
import com.ducluanxutrieu.quanlynhanvien.Staff.Staff;

import java.util.ArrayList;
import java.util.List;

public class StaffListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Staff> staffList;
    private Context context;

    public StaffListAdapter(RecyclerView recyclerView, ArrayList<Staff> staffList, Context context){
        this.staffList = staffList;
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
        itemViewHolder.avatar.setImageResource(staffList.get(i).getAvatar());
        itemViewHolder.name.setText(staffList.get(i).getName());
    }

    @Override
    public int getItemCount() {
        return staffList.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        ImageView avatar;
        TextView name, chatContent;
        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.avatar);
            chatContent = itemView.findViewById(R.id.content_chats);
            name = itemView.findViewById(R.id.name);
        }
    }
}

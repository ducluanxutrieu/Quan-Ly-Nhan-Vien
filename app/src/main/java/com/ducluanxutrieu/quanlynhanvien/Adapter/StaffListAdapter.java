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

import com.ducluanxutrieu.quanlynhanvien.Activity.UserInfoActivity;
import com.ducluanxutrieu.quanlynhanvien.R;
import com.ducluanxutrieu.quanlynhanvien.Item.Users;

import java.util.ArrayList;
import java.util.List;

public class StaffListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Users> usersList;
    public Context context;

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
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int i) {
        final ItemViewHolder itemViewHolder = (ItemViewHolder) viewHolder;
        itemViewHolder.name.setText(usersList.get(i).getName());
        itemViewHolder.possition.setText(usersList.get(i).getPosition());
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, UserInfoActivity.class);
                intent.putExtra("user", usersList.get(itemViewHolder.getAdapterPosition()));
                ActivityOptions options =
                        ActivityOptions.makeCustomAnimation(context, R.anim.fui_slide_in_right, R.anim.fui_slide_out_left);
                context.startActivity(intent, options.toBundle());
            }
        });
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView name, possition;
        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            possition = itemView.findViewById(R.id.position_staff);
            name = itemView.findViewById(R.id.staff_name);
        }
    }
}

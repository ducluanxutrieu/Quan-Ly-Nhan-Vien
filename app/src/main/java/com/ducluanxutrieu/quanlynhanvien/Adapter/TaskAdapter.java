package com.ducluanxutrieu.quanlynhanvien.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ducluanxutrieu.quanlynhanvien.Activity.TaskDetailActivity;
import com.ducluanxutrieu.quanlynhanvien.Models.Tasks;
import com.ducluanxutrieu.quanlynhanvien.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class TaskAdapter extends FirebaseRecyclerAdapter<Tasks, TaskAdapter.ItemViewHolder> {
    private Context context;

    public TaskAdapter(@NonNull FirebaseRecyclerOptions<Tasks> options) {
        super(options);
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_task, viewGroup, false);
        context = view.getContext();
        return new ItemViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull ItemViewHolder holder, int position, @NonNull final Tasks model) {
        final int i = position;
        holder.taskContent.setText(model.getTaskContent());
        holder.taskTitle.setText(model.getTaskTitle());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, TaskDetailActivity.class);
                intent.putExtra("task", model);
                intent.putExtra("signal", "edit");
                intent.putExtra("key", getRef(i).getKey());
                v.getContext().startActivity(intent);
            }
        });
    }

    class ItemViewHolder extends RecyclerView.ViewHolder{
        private TextView taskContent;
        private TextView taskTitle;
        ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            taskContent = itemView.findViewById(R.id.task_content);
            taskTitle = itemView.findViewById(R.id.task_title);
        }
    }
}

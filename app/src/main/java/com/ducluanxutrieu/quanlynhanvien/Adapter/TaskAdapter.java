package com.ducluanxutrieu.quanlynhanvien.Adapter;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ducluanxutrieu.quanlynhanvien.Activity.TaskDetailActivity;
import com.ducluanxutrieu.quanlynhanvien.Models.Task;
import com.ducluanxutrieu.quanlynhanvien.R;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ItemViewHolder> {
    private List<Task> taskList;
    private Context context;

    public TaskAdapter() {
    }

    public TaskAdapter(List<Task> taskList, Context context) {
        this.taskList = taskList;
        this.context = context;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.task_item, viewGroup, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ItemViewHolder itemViewHolder, int i) {
        final int position = itemViewHolder.getAdapterPosition();
        itemViewHolder.taskContent.setText(taskList.get(position).getTaskContent());
        itemViewHolder.taskTitle.setText(taskList.get(position).getTaskTitle());
        itemViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, TaskDetailActivity.class);
                intent.putExtra("task", taskList.get(position));
                intent.putExtra("signal", "edit");
                intent.putExtra("key", taskList.get(position).getKeyTask());
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return taskList.size();
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

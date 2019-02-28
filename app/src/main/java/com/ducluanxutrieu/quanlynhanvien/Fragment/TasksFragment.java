package com.ducluanxutrieu.quanlynhanvien.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ducluanxutrieu.quanlynhanvien.Adapter.TaskAdapter;
import com.ducluanxutrieu.quanlynhanvien.Item.Task;
import com.ducluanxutrieu.quanlynhanvien.R;

import java.util.ArrayList;
import java.util.List;

public class TasksFragment extends Fragment {
    RecyclerView mRecyclerViewTask;
    TaskAdapter mTaskAdapter;
    List<Task> taskList;
    FloatingActionButton fab;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tasks_fragment, container, false);

        taskList = new ArrayList<>();
        mRecyclerViewTask = view.findViewById(R.id.recycler_view_tasks_list);
        fab = view.findViewById(R.id.fab_add_task);

        mTaskAdapter = new TaskAdapter(taskList, view.getContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext(), LinearLayoutManager.VERTICAL, false);
        mRecyclerViewTask.setLayoutManager(layoutManager);
        mRecyclerViewTask.setAdapter(mTaskAdapter);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return view;
    }
}

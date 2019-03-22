package com.ducluanxutrieu.quanlynhanvien.Fragment;

import android.content.Context;
import android.content.Intent;
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

import com.ducluanxutrieu.quanlynhanvien.Activity.TaskDetailActivity;
import com.ducluanxutrieu.quanlynhanvien.Adapter.TaskAdapter;
import com.ducluanxutrieu.quanlynhanvien.Models.Task;
import com.ducluanxutrieu.quanlynhanvien.R;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.List;

public class TasksFragment extends Fragment {
    RecyclerView mRecyclerViewTask;
    TaskAdapter mTaskAdapter;
    List<Task> taskList;
    FloatingActionButton fab;
    FirebaseAuth mFirebaseAuth;
    FirebaseUser mFirebaseUser;
    String rooUid;
    Context context;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tasks, container, false);
        context = view.getContext();
        taskList = new ArrayList<>();
        mRecyclerViewTask = view.findViewById(R.id.recycler_view_tasks_list);

        fab = view.findViewById(R.id.fab_add_task);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), TaskDetailActivity.class);
                intent.putExtra("signal", "add");
                startActivity(intent);
            }
        });
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if (mFirebaseUser != null){
            rooUid = mFirebaseUser.getUid();
        }

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Query query = FirebaseDatabase.getInstance().getReference().child("task/" + rooUid);
        FirebaseRecyclerOptions.Builder<Task> builder = new FirebaseRecyclerOptions.Builder<>();
        builder.setQuery(query, Task.class);
        FirebaseRecyclerOptions<Task> options = builder.build();
        mTaskAdapter = new TaskAdapter(options);
        mTaskAdapter.startListening();
        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        mRecyclerViewTask.setLayoutManager(layoutManager);
        mRecyclerViewTask.setAdapter(mTaskAdapter);
    }

    @Override
    public void onStop() {
        super.onStop();
        mTaskAdapter.stopListening();
    }
}

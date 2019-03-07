package com.ducluanxutrieu.quanlynhanvien.Fragment;

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
import android.widget.Toast;

import com.ducluanxutrieu.quanlynhanvien.Activity.TaskDetailActivity;
import com.ducluanxutrieu.quanlynhanvien.Adapter.TaskAdapter;
import com.ducluanxutrieu.quanlynhanvien.Models.Task;
import com.ducluanxutrieu.quanlynhanvien.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class TasksFragment extends Fragment {
    RecyclerView mRecyclerViewTask;
    TaskAdapter mTaskAdapter;
    List<Task> taskList;
    FloatingActionButton fab;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    FirebaseAuth mFirebaseAuth;
    FirebaseUser mFirebaseUser;
    ChildEventListener mChildEventListener;
    String rootEmail;
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
                Intent intent = new Intent(v.getContext(), TaskDetailActivity.class);
                intent.putExtra("signal", "add");
                startActivity(intent);
            }
        });

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if (mFirebaseUser != null){
            rootEmail = mFirebaseUser.getEmail().replace(".", "");
        }

        mDatabaseReference = mFirebaseDatabase.getReference().child("task/" + rootEmail);
        onSyncTask();
        return view;
    }

    public void addNewTask(String title, String content){
        Task task = new Task(title, content);
        mDatabaseReference.child(task.getTaskTitle()).setValue(task)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull com.google.android.gms.tasks.Task<Void> task) {
                        if (task.isComplete()){
                            Toast.makeText(getContext(), getString(R.string.add_new_task_succesful), Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(getContext(), getString(R.string.have_a_problem_to_add_new_task), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void onSyncTask(){
        if (mChildEventListener == null){
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    Task task = dataSnapshot.getValue(Task.class);
                    if (task != null){
                        task.setKeyTask(dataSnapshot.getKey());
                        taskList.add(task);
                        //taskList.get(taskList.size() - 1).setKeyTask(dataSnapshot.getKey());
                        mTaskAdapter.notifyItemChanged(taskList.size());
                    }
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };
            mDatabaseReference.addChildEventListener(mChildEventListener);
        }
    }
}

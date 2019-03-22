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

import com.ducluanxutrieu.quanlynhanvien.Activity.EditUserActivity;
import com.ducluanxutrieu.quanlynhanvien.Adapter.StaffListAdapter;
import com.ducluanxutrieu.quanlynhanvien.Interface.TransferSignal;
import com.ducluanxutrieu.quanlynhanvien.R;
import com.ducluanxutrieu.quanlynhanvien.Models.Users;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.List;


public class StaffListFragment extends Fragment {
    RecyclerView mRecyclerViewStaff;
    StaffListAdapter mStaffListAdapter;
    List<Users> usersList;
    TransferSignal mTransferSignal;
    private FloatingActionButton fab;

    public StaffListFragment(){}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View viewInflate = inflater.inflate(R.layout.fragment_staff_list, container, false);
        mapping(viewInflate);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), EditUserActivity.class);
                intent.putExtra("signal", "create");
                startActivity(intent);
            }
        });

        return viewInflate;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        usersList = new ArrayList<>();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mTransferSignal = (TransferSignal) context;
    }

    private void mapping(View view){
        mRecyclerViewStaff = view.findViewById(R.id.recycler_view_staff_list);
        fab = view.findViewById(R.id.fab);
    }

    @Override
    public void onStart() {
        super.onStart();

        Query query = FirebaseDatabase.getInstance().getReference().child("users").limitToLast(50);
        FirebaseRecyclerOptions.Builder<Users> usersBuilder = new FirebaseRecyclerOptions.Builder<>();
        usersBuilder.setQuery(query, Users.class);

        FirebaseRecyclerOptions<Users> options = usersBuilder.build();
        mStaffListAdapter = new StaffListAdapter(options);

        mStaffListAdapter.startListening();

        mRecyclerViewStaff.hasFixedSize();
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerViewStaff.setLayoutManager(layoutManager);
        mRecyclerViewStaff.setAdapter(mStaffListAdapter);
    }

    @Override
    public void onStop() {
        super.onStop();
        mStaffListAdapter.stopListening();
    }
}

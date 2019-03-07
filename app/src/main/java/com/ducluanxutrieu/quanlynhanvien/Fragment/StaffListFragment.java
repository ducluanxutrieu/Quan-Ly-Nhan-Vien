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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;


public class StaffListFragment extends Fragment {
    RecyclerView mRecyclerViewStaff;
    StaffListAdapter mStaffListAdapter;
    List<Users> usersList;
    TransferSignal mTransferSignal;
    private FloatingActionButton fab;

    FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mUsersReference;
    private ChildEventListener mChildEvenListener;

    public StaffListFragment(){}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View viewInflate = inflater.inflate(R.layout.staff_list_fragment, container, false);
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

        mRecyclerViewStaff.hasFixedSize();
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerViewStaff.setLayoutManager(layoutManager);
        mStaffListAdapter = new StaffListAdapter((ArrayList<Users>) usersList, getContext());
        mRecyclerViewStaff.setAdapter(mStaffListAdapter);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mUsersReference = mFirebaseDatabase.getReference().child("user");
        attachDatabaseReadListener();
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

    private void attachDatabaseReadListener() {
        if (mChildEvenListener == null){
            mChildEvenListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    Users users = dataSnapshot.getValue(Users.class);
                    usersList.add(users);
                    mStaffListAdapter.notifyDataSetChanged();
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
            mUsersReference.addChildEventListener(mChildEvenListener);
        }
    }

    private void detachDatabaseReadListener() {
        if (mChildEvenListener != null){
            mUsersReference.removeEventListener(mChildEvenListener);
            mChildEvenListener = null;
        }
    }

}

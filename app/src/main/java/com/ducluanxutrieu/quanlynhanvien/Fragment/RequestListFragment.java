package com.ducluanxutrieu.quanlynhanvien.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ducluanxutrieu.quanlynhanvien.Adapter.RequestAdapter;
import com.ducluanxutrieu.quanlynhanvien.Item.RequestItem;
import com.ducluanxutrieu.quanlynhanvien.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class RequestListFragment extends Fragment {
    RequestAdapter mRequestAdapter;
    RecyclerView mRecyclerView;
    List<RequestItem> requestItemList;
    private ChildEventListener mChildEvenListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.request_list_fragment, container, false);

        requestItemList = new ArrayList<>();
        mRecyclerView = rootView.findViewById(R.id.recycler_view_request_list);
        mRequestAdapter = new RequestAdapter(rootView.getContext(), requestItemList);
        mRecyclerView.setAdapter(mRequestAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FirebaseDatabase mFireDatabase = FirebaseDatabase.getInstance();
        DatabaseReference mReference = mFireDatabase.getReference().child("request_from_staff");

        if (mChildEvenListener == null){
            mChildEvenListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    RequestItem requestItem = dataSnapshot.getValue(RequestItem.class);
                    if (requestItem != null){
                        requestItem.setRequestKey(dataSnapshot.getKey());
                        requestItemList.add(requestItem);
                        mRequestAdapter.notifyItemChanged(requestItemList.size());
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
            mReference.addChildEventListener(mChildEvenListener);
        }

    }
}

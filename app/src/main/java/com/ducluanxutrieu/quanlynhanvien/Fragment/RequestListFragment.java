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
import com.ducluanxutrieu.quanlynhanvien.Models.RequestItem;
import com.ducluanxutrieu.quanlynhanvien.R;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.List;

public class RequestListFragment extends Fragment {
    RequestAdapter mRequestAdapter;
    RecyclerView mRecyclerView;
    List<RequestItem> requestItemList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_request_list, container, false);

        requestItemList = new ArrayList<>();
        mRecyclerView = rootView.findViewById(R.id.recycler_view_request_list);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        Query query = FirebaseDatabase.getInstance().getReference().child("request_from_staff").limitToLast(50);
        FirebaseRecyclerOptions.Builder<RequestItem> itemBuilder = new FirebaseRecyclerOptions.Builder<>();
        itemBuilder.setQuery(query, RequestItem.class);
        FirebaseRecyclerOptions<RequestItem> options = itemBuilder.build();

        mRequestAdapter = new RequestAdapter(options);
        mRequestAdapter.startListening();
        mRecyclerView.setAdapter(mRequestAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.smoothScrollToPosition(0);
    }

    @Override
    public void onStop() {
        super.onStop();
        mRequestAdapter.stopListening();
    }
}

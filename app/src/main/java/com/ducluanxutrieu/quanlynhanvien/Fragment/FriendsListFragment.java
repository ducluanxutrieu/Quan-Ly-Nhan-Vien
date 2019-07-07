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

import com.ducluanxutrieu.quanlynhanvien.Adapter.FriendsListAdapter;
import com.ducluanxutrieu.quanlynhanvien.Dialog.AddNewFriend;
import com.ducluanxutrieu.quanlynhanvien.Models.Friend;
import com.ducluanxutrieu.quanlynhanvien.R;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.List;

public class FriendsListFragment extends Fragment {
    RecyclerView mRecyclerViewFriends;
    FloatingActionButton mFAB;
    FirebaseAuth mFirebaseAuth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    //ChildEventListener mChildEvenListener;

    List<Friend> friendList;
    FriendsListAdapter mFriendsAdapter;
    String rootUid;

    public FriendsListFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_friends_list, container, false);

        mRecyclerViewFriends = rootView.findViewById(R.id.recycler_view_friends_list);
        mFAB = rootView.findViewById(R.id.fab_add_friend);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();

        rootUid = mFirebaseAuth.getUid();

        mDatabaseReference = mFirebaseDatabase.getReference().child("friend_ship/" + rootUid);

        friendList = new ArrayList<>();


        mFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddNewFriend addNewFriend = new AddNewFriend();
                addNewFriend.show(getFragmentManager(), "friend");
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("friend_ship/" + rootUid)
                .limitToLast(50);
        FirebaseRecyclerOptions.Builder<Friend> itemBuilder = new FirebaseRecyclerOptions.Builder<>();
        itemBuilder.setQuery(query, Friend.class);
        FirebaseRecyclerOptions<Friend> options = itemBuilder.build();

        //mFriendsAdapter = new FriendsListAdapter(friendList, view.getContext());
        mFriendsAdapter = new FriendsListAdapter(options);
        mFriendsAdapter.startListening();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerViewFriends.setLayoutManager(layoutManager);
        mRecyclerViewFriends.setAdapter(mFriendsAdapter);
    }

    @Override
    public void onStop() {
        super.onStop();
        mFriendsAdapter.stopListening();
    }
}

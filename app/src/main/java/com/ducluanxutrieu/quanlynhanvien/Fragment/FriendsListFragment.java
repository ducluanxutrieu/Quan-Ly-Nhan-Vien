package com.ducluanxutrieu.quanlynhanvien.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ducluanxutrieu.quanlynhanvien.Adapter.FriendsListAdapter;
import com.ducluanxutrieu.quanlynhanvien.Dialog.AddNewFriend;
import com.ducluanxutrieu.quanlynhanvien.Item.Friend;
import com.ducluanxutrieu.quanlynhanvien.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class FriendsListFragment extends Fragment {
    RecyclerView mRecyclerViewFriends;
    FloatingActionButton mFAB;
    FirebaseAuth mFirebaseAuth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    ChildEventListener mChildEvenListener;

    List<Friend> friendList;
    FriendsListAdapter mFriendsAdapter;


    public FriendsListFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.friends_list_fragment, container, false);

        mRecyclerViewFriends = rootView.findViewById(R.id.recycler_view_friends_list);
        mFAB = rootView.findViewById(R.id.fab_add_friend);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        friendList = new ArrayList<>();
        mFriendsAdapter = new FriendsListAdapter(friendList, view.getContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerViewFriends.setLayoutManager(layoutManager);
        mRecyclerViewFriends.setAdapter(mFriendsAdapter);
        mFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddNewFriend addNewFriend = new AddNewFriend();
                addNewFriend.show(getFragmentManager(), "friend");
            }
        });

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
        String rootEmail = mFirebaseAuth.getCurrentUser().getEmail().replace(".", "");
        mDatabaseReference = mFirebaseDatabase.getReference().child("friend_ship/" + rootEmail);
        attachDatabaseReadListener();
    }

    private void attachDatabaseReadListener() {
        if (mChildEvenListener == null){
            mChildEvenListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    Friend friend = dataSnapshot.getValue(Friend.class);
                    friendList.add(friend);
                    Log.i("kiemtra", friendList.get(friendList.size() - 1).toString() + "");
                    mFriendsAdapter.notifyDataSetChanged();
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
            mDatabaseReference.addChildEventListener(mChildEvenListener);
        }
    }
}

package com.ducluanxutrieu.quanlynhanvien;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FriendsListFragment extends Fragment {
    RecyclerView mRecyclerViewFriends;
    FloatingActionButton mFAB;

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
}

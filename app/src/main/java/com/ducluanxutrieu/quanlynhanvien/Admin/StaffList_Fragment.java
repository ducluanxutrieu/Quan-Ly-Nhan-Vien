package com.ducluanxutrieu.quanlynhanvien.Admin;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ducluanxutrieu.quanlynhanvien.R;


public class StaffList_Fragment extends Fragment {
    RecyclerView mRecyclerViewStaff;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.staff_list_fragment, container, false);
        return view;
    }

    private void mapping(View view){
        mRecyclerViewStaff = view.findViewById(R.id.recycler_view_staff_list);
    }
}

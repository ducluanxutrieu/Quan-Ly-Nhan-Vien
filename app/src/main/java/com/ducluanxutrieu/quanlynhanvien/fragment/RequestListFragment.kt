package com.ducluanxutrieu.quanlynhanvien.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.ducluanxutrieu.quanlynhanvien.adapter.RequestAdapter
import com.ducluanxutrieu.quanlynhanvien.models.RequestItem
import com.ducluanxutrieu.quanlynhanvien.R
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.FirebaseDatabase

import java.util.ArrayList

class RequestListFragment : Fragment() {
    private lateinit var mRequestAdapter: RequestAdapter
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var requestItemList: List<RequestItem>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_request_list, container, false)

        requestItemList = ArrayList()
        mRecyclerView = rootView.findViewById(R.id.recycler_view_request_list)
        return rootView
    }

    override fun onStart() {
        super.onStart()

        val query = FirebaseDatabase.getInstance().reference.child("request_from_staff").limitToLast(50)
        val itemBuilder = FirebaseRecyclerOptions.Builder<RequestItem>()
        itemBuilder.setQuery(query, RequestItem::class.java)
        val options = itemBuilder.build()

        mRequestAdapter = RequestAdapter(options)
        mRequestAdapter.startListening()
        mRecyclerView.adapter = mRequestAdapter
        val layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        mRecyclerView.layoutManager = layoutManager
        mRecyclerView.smoothScrollToPosition(0)
    }

    override fun onStop() {
        super.onStop()
        mRequestAdapter.stopListening()
    }
}

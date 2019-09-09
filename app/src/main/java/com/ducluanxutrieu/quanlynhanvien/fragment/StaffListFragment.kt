package com.ducluanxutrieu.quanlynhanvien.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.ducluanxutrieu.quanlynhanvien.activity.EditUserActivity
import com.ducluanxutrieu.quanlynhanvien.adapter.StaffListAdapter
import com.ducluanxutrieu.quanlynhanvien.interfaces.TransferSignal
import com.ducluanxutrieu.quanlynhanvien.R
import com.ducluanxutrieu.quanlynhanvien.models.Staff
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.FirebaseDatabase

import java.util.ArrayList


class StaffListFragment : Fragment() {
    private lateinit var mRecyclerViewStaff: RecyclerView
    private lateinit var mStaffListAdapter: StaffListAdapter
    private lateinit var staffList: List<Staff>
    private lateinit var mTransferSignal: TransferSignal
    private var fab: FloatingActionButton? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val viewInflate = inflater.inflate(R.layout.fragment_staff_list, container, false)
        mapping(viewInflate)

        fab!!.setOnClickListener { view ->
            val intent = Intent(view.context, EditUserActivity::class.java)
            intent.putExtra("signal", "createUser")
            startActivity(intent)
        }

        return viewInflate
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        staffList = ArrayList()
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mTransferSignal = context as TransferSignal
    }

    private fun mapping(view: View) {
        mRecyclerViewStaff = view.findViewById(R.id.recycler_view_staff_list)
        fab = view.findViewById(R.id.fab)
    }

    override fun onStart() {
        super.onStart()

        val query = FirebaseDatabase.getInstance().reference.child("users").limitToLast(50)
        val usersBuilder = FirebaseRecyclerOptions.Builder<Staff>()
        usersBuilder.setQuery(query, Staff::class.java)

        val options = usersBuilder.build()
        mStaffListAdapter = StaffListAdapter(options)

        mStaffListAdapter.startListening()

        mRecyclerViewStaff.hasFixedSize()
        val layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        mRecyclerViewStaff.layoutManager = layoutManager
        mRecyclerViewStaff.adapter = mStaffListAdapter
    }

    override fun onStop() {
        super.onStop()
        mStaffListAdapter.stopListening()
    }
}

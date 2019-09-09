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

import com.ducluanxutrieu.quanlynhanvien.activity.TaskDetailActivity
import com.ducluanxutrieu.quanlynhanvien.adapter.TaskAdapter
import com.ducluanxutrieu.quanlynhanvien.models.Tasks
import com.ducluanxutrieu.quanlynhanvien.R
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase

import java.util.ArrayList

class TasksFragment : Fragment() {
    private lateinit var mRecyclerViewTask: RecyclerView
    private lateinit var mTaskAdapter: TaskAdapter
    private lateinit var taskList: List<Tasks>
    private lateinit var fab: FloatingActionButton
    private lateinit var mFirebaseAuth: FirebaseAuth
    private var mFirebaseUser: FirebaseUser? = null
    private lateinit var rooUid: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_tasks, container, false)
        taskList = ArrayList()
        mRecyclerViewTask = view.findViewById(R.id.recycler_view_tasks_list)

        fab = view.findViewById(R.id.fab_add_task)
        fab.setOnClickListener { v ->
            val intent = Intent(v.context, TaskDetailActivity::class.java)
            intent.putExtra("signal", "add")
            startActivity(intent)
        }
        mFirebaseAuth = FirebaseAuth.getInstance()
        mFirebaseUser = mFirebaseAuth.currentUser
        if (mFirebaseUser != null) {
            rooUid = mFirebaseUser!!.uid
        }

        return view
    }

    override fun onStart() {
        super.onStart()
        val query = FirebaseDatabase.getInstance().reference.child("task/$rooUid")
        val builder = FirebaseRecyclerOptions.Builder<Tasks>()
        builder.setQuery(query, Tasks::class.java)
        val options = builder.build()
        mTaskAdapter = TaskAdapter(options)
        mTaskAdapter.startListening()
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        mRecyclerViewTask.layoutManager = layoutManager
        mRecyclerViewTask.adapter = mTaskAdapter
    }

    override fun onStop() {
        super.onStop()
        mTaskAdapter.stopListening()
    }
}

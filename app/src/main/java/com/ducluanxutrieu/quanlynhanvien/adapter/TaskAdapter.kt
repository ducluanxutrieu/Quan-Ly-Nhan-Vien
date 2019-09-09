package com.ducluanxutrieu.quanlynhanvien.adapter

import android.content.Context
import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.ducluanxutrieu.quanlynhanvien.activity.TaskDetailActivity
import com.ducluanxutrieu.quanlynhanvien.models.Tasks
import com.ducluanxutrieu.quanlynhanvien.R
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions

class TaskAdapter(options: FirebaseRecyclerOptions<Tasks>) : FirebaseRecyclerAdapter<Tasks, TaskAdapter.ItemViewHolder>(options) {
    private var context: Context? = null

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ItemViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_task, viewGroup, false)
        context = view.context
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int, model: Tasks) {
        holder.taskContent.text = model.taskContent
        holder.taskTitle.text = model.taskTitle
        holder.itemView.setOnClickListener { v ->
            val intent = Intent(context, TaskDetailActivity::class.java)
            intent.putExtra("task", model)
            intent.putExtra("signal", "edit")
            intent.putExtra("key", getRef(position).key)
            v.context.startActivity(intent)
        }
    }

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val taskContent: TextView = itemView.findViewById(R.id.task_content)
        val taskTitle: TextView = itemView.findViewById(R.id.task_title)
    }
}

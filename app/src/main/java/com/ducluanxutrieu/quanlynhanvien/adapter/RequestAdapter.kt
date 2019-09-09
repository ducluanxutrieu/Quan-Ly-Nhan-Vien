package com.ducluanxutrieu.quanlynhanvien.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ducluanxutrieu.quanlynhanvien.R
import com.ducluanxutrieu.quanlynhanvien.activity.RequestDetailActivity
import com.ducluanxutrieu.quanlynhanvien.models.RequestItem
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions


class RequestAdapter(options: FirebaseRecyclerOptions<RequestItem>) : FirebaseRecyclerAdapter<RequestItem, RequestAdapter.ItemViewHolder>(options) {
    private var rootView: View? = null

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ItemViewHolder {
        val layoutInflater = LayoutInflater.from(viewGroup.context)
        rootView = layoutInflater.inflate(R.layout.item_request, viewGroup, false)

        return ItemViewHolder(rootView!!)
    }


    override fun onBindViewHolder(holder: ItemViewHolder, position: Int, model: RequestItem) {
        val context = holder.itemView.context

        holder.name.text = model.name
        holder.content.text = model.text
        holder.date.text = model.startDate
        holder.time.text = model.timeRequest
        holder.itemView.setOnClickListener {
            //                getSnapshots().getSnapshot()
            model.requestKey = getRef(position).key
            val intent = Intent(context, RequestDetailActivity::class.java)
            intent.putExtra("request", model)
            context.startActivity(intent)
        }

        if (model.isAccept) {
            holder.accept.text = holder.itemView.context.getString(R.string.accepted)
            holder.accept.visibility = View.VISIBLE
        } else {
            holder.accept.text = context.getString(R.string.denied)
            holder.accept.visibility = View.VISIBLE
        }
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var name: TextView = itemView.findViewById(R.id.text_name_request)
        var content: TextView = itemView.findViewById(R.id.text_content_request)
        var date: TextView = itemView.findViewById(R.id.text_date_request)
        var time: TextView = itemView.findViewById(R.id.text_time_request)
        var accept: TextView = itemView.findViewById(R.id.text_accept_request)
    }
}

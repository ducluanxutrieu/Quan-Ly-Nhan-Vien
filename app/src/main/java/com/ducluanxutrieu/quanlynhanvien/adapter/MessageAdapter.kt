package com.ducluanxutrieu.quanlynhanvien.adapter

import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast

import com.ducluanxutrieu.quanlynhanvien.models.MessageItem
import com.ducluanxutrieu.quanlynhanvien.R
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseError


class MessageAdapter
/**
 * Initialize a [RecyclerView.Adapter] that listens to a Firebase query. See
 * [FirebaseRecyclerOptions] for configuration options.
 *
 * @param options
 */
(options: FirebaseRecyclerOptions<MessageItem>) : FirebaseRecyclerAdapter<MessageItem, MessageAdapter.ItemViewHolder>(options) {

    private val TAG = "tagMessageAdapter"
    private var view: View? = null


    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(viewGroup.context)

        if (viewType == RIGHT_MSG) {
            view = inflater.inflate(R.layout.item_message_right, viewGroup, false)
        } else {
            view = inflater.inflate(R.layout.item_message_left, viewGroup, false)
        }
        return ItemViewHolder(view!!)
    }

    override fun onError(error: DatabaseError) {
        super.onError(error)
        Toast.makeText(view!!.context, "Error send message", Toast.LENGTH_SHORT).show()
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int, model: MessageItem) {
        holder.contentItemMessage.text = model.text
        //holder.nameItemMessage.setText(model.getName());
        holder.timeStampMessage.text = model.timeStamp
    }

    override fun getItemViewType(position: Int): Int {
        Log.i(TAG, getItem(position).toString())
        return if (getItem(position).name == nameUser) {
            RIGHT_MSG
        } else {
            LEFT_MSG
        }
    }


    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var contentItemMessage: TextView = itemView.findViewById(R.id.message_text_view)
        var timeStampMessage: TextView = itemView.findViewById(R.id.timestamp_text_view)
    }

    companion object {
        private const val RIGHT_MSG = 0
        private const val LEFT_MSG = 1
        private val nameUser = FirebaseAuth.getInstance().currentUser!!.displayName
    }
}
package com.ducluanxutrieu.quanlynhanvien.adapter

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.ducluanxutrieu.quanlynhanvien.activity.ChatsActivity
import com.ducluanxutrieu.quanlynhanvien.models.Friend
import com.ducluanxutrieu.quanlynhanvien.R
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class FriendsListAdapter(options: FirebaseRecyclerOptions<Friend>) : FirebaseRecyclerAdapter<Friend, FriendsListAdapter.ItemViewHolder>(options) {
    private var rootView: View? = null

    val context: Context
        get() {
            while (rootView!!.context == null) {
                try {
                    Thread.sleep(100)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }

            }
            return rootView!!.context
        }


    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(viewGroup.context)
        rootView = inflater.inflate(R.layout.item_friend, viewGroup, false)

        return ItemViewHolder(rootView!!)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int, model: Friend) {
        holder.setNameMessage(model.name)
        holder.setRecentMessage(model.recentMessage)
        holder.setAvatar()
        Glide.with(holder.itemView.context).load(model.avatarUrl).apply(RequestOptions.circleCropTransform()).into(holder.avatar)

        holder.itemView.setOnClickListener { v ->
            val context = v.context
            val intent = Intent(context, ChatsActivity::class.java)
            intent.putExtra("friend", model)
            intent.putExtra("uid", model.uid)
            intent.putExtra("name", model.name)
            val options = ActivityOptions.makeCustomAnimation(context, R.anim.fui_slide_in_right, R.anim.fui_slide_out_left)
            context.startActivity(intent, options.toBundle())
        }
    }

    fun restoreItem(friend: Friend, key: String) {
        val reference = FirebaseDatabase.getInstance().reference
        reference.child("friend_ship/" + FirebaseAuth.getInstance().uid + "/" + key).setValue(friend)
    }

    inner class ItemViewHolder(var view: View) : RecyclerView.ViewHolder(view) {
        private lateinit var nameMessage: TextView
        private lateinit var recentMessage: TextView
        lateinit var avatar: ImageView

        fun setNameMessage(name: String?) {
            nameMessage = view.findViewById(R.id.friend_name)
            nameMessage.text = name
        }

        fun setRecentMessage(recent: String?) {
            recentMessage = view.findViewById(R.id.recent_chat)
            recentMessage.text = recent
        }

        fun setAvatar() {
            avatar = view.findViewById(R.id.avatar)
        }
    }
}

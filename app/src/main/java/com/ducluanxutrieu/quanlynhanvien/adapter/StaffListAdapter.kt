package com.ducluanxutrieu.quanlynhanvien.adapter


import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.ducluanxutrieu.quanlynhanvien.R
import com.ducluanxutrieu.quanlynhanvien.activity.UserInfoActivity
import com.ducluanxutrieu.quanlynhanvien.models.Staff
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.functions.FirebaseFunctions
import java.util.*

class StaffListAdapter(options: FirebaseRecyclerOptions<Staff>) : FirebaseRecyclerAdapter<Staff, StaffListAdapter.ItemViewHolder>(options) {
    private var rootView: View? = null


    //Firebase
    private var mFunction: FirebaseFunctions? = null


    val context: Context
        get() = rootView!!.context

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(viewGroup.context)
        rootView = inflater.inflate(R.layout.item_staff, viewGroup, false)

        mFunction = FirebaseFunctions.getInstance()

        return ItemViewHolder(rootView!!)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int, model: Staff) {
        holder.name.text = model.name
        holder.position.text = model.position
        Glide.with(rootView!!).load(model.avatarUrl).apply(RequestOptions.circleCropTransform()).into(holder.avatar)
        holder.itemView.setOnClickListener { v ->
            val context = v.context
            val intent = Intent(context, UserInfoActivity::class.java)
            intent.putExtra("user", model)
            intent.putExtra("isAdmin", true)
            val options = ActivityOptions.makeCustomAnimation(context, R.anim.fui_slide_in_right, R.anim.fui_slide_out_left)
            context.startActivity(intent, options.toBundle())
        }

        holder.itemView.setOnLongClickListener { v ->
            val builder = AlertDialog.Builder(v.context)
            builder.setTitle(rootView!!.context.getString(R.string.delete_this_account))
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setMessage(rootView!!.context.getString(R.string.are_you_sure_delete_this_account))
                    .setPositiveButton("OK") { dialog, which ->
                        //deleteUserFromAuth(model.getUid());
                        //deleteUserFromDatabase(model.getEmail());
                        val map = HashMap<String, Any>()
                        map["uid"] = model.uid!!
                        deleteUser(map).addOnSuccessListener { s -> Toast.makeText(v.context, s, Toast.LENGTH_SHORT).show() }.addOnFailureListener { e -> Toast.makeText(v.context, e.message, Toast.LENGTH_SHORT).show() }
                    }
                    .setNegativeButton(rootView!!.context.getString(R.string.cancel), null)
            builder.create().show()
            true
        }
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var name: TextView = itemView.findViewById(R.id.staff_name)
        var position: TextView = itemView.findViewById(R.id.position_staff)
        var avatar: ImageView = itemView.findViewById(R.id.avatar)

    }

    private fun deleteUser(map: HashMap<String, Any>): Task<String> {
        return mFunction!!.getHttpsCallable("deleteUser")
                .call(map)
                .continueWith { task -> task.result!!.toString() }
    }

    companion object {

        private val TAG = ".StaffList"
    }
}

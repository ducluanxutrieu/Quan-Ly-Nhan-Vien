package com.ducluanxutrieu.quanlynhanvien.Adapter;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ducluanxutrieu.quanlynhanvien.Activity.UserInfoActivity;
import com.ducluanxutrieu.quanlynhanvien.Models.Users;
import com.ducluanxutrieu.quanlynhanvien.MyTask;
import com.ducluanxutrieu.quanlynhanvien.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class StaffListAdapter extends FirebaseRecyclerAdapter<Users, StaffListAdapter.ItemViewHolder> {
    private View rootView;

    public StaffListAdapter(@NonNull FirebaseRecyclerOptions<Users> options) {
        super(options);
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        rootView = inflater.inflate(R.layout.item_staff, viewGroup, false);
        return new ItemViewHolder(rootView);
    }

    @Override
    protected void onBindViewHolder(@NonNull final ItemViewHolder holder, final int position, @NonNull final Users model) {
        holder.name.setText(model.getName());
        holder.position.setText(model.getPosition());
        Glide.with(rootView).load(model.getAvatarUrl()).apply(RequestOptions.circleCropTransform()).into(holder.avatar);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, UserInfoActivity.class);
                intent.putExtra("user", model);
                intent.putExtra("isAdmin", true);
                ActivityOptions options =
                        ActivityOptions.makeCustomAnimation(context, R.anim.fui_slide_in_right, R.anim.fui_slide_out_left);
                context.startActivity(intent, options.toBundle());
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle(rootView.getContext().getString(R.string.delete_this_account))
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setMessage(rootView.getContext().getString(R.string.are_you_sure_delete_this_account))
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteUserFromAuth(model.getUid());
                                deleteUserFromDatabase(model.getEmail());
                            }
                        })
                        .setNegativeButton(rootView.getContext().getString(R.string.cancel), null);
                builder.create();
                return true;
            }
        });
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView name, position;
        ImageView avatar;
        ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            position = itemView.findViewById(R.id.position_staff);
            name = itemView.findViewById(R.id.staff_name);
            avatar = itemView.findViewById(R.id.avatar);
        }
    }

    private void deleteUserFromAuth(String uid){
        final String BASE_URL = "https://quan-ly-nhan-vien.firebaseapp.com/delete";
        String TAG = ".EditUserActivity";
        Log.i(TAG, BASE_URL);
        //final String BASE_URL = "http://10.0.3.2:5000/create";
        Map<String, String> map = new HashMap<>();
        map.put("uid", uid);
        JSONObject jsonObject = new JSONObject(map);
        String query = jsonObject.toString();
        Log.i("QUERY", query);
        new MyTask(rootView.getContext()).execute(BASE_URL, query);
    }

    private void deleteUserFromDatabase(String email){
        email = email.replace(".", "");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("user/" + email);
        reference.removeValue();
    }
}

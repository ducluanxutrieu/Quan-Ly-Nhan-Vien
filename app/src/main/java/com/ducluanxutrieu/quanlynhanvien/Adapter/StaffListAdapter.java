package com.ducluanxutrieu.quanlynhanvien.Adapter;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ducluanxutrieu.quanlynhanvien.Activity.UserInfoActivity;
import com.ducluanxutrieu.quanlynhanvien.MyTask;
import com.ducluanxutrieu.quanlynhanvien.R;
import com.ducluanxutrieu.quanlynhanvien.Models.Users;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StaffListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Users> usersList;
    public Context context;

    public StaffListAdapter(ArrayList<Users> usersList, Context context){
        this.usersList = usersList;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.staff_item, viewGroup, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        final ItemViewHolder itemViewHolder = (ItemViewHolder) viewHolder;
        final int pos = itemViewHolder.getAdapterPosition();
        itemViewHolder.name.setText(usersList.get(i).getName());
        itemViewHolder.possition.setText(usersList.get(i).getPosition());
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, UserInfoActivity.class);
                intent.putExtra("user", usersList.get(itemViewHolder.getAdapterPosition()));
                ActivityOptions options =
                        ActivityOptions.makeCustomAnimation(context, R.anim.fui_slide_in_right, R.anim.fui_slide_out_left);
                context.startActivity(intent, options.toBundle());
            }
        });

        viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("Delete this account!")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setMessage("Are you sure delete this account, can not restore when you choose OK")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteUserFromAuth(usersList.get(pos).getUid());
                                deleteUserFromDatabase(usersList.get(pos).getEmail());
                            }
                        })
                        .setNegativeButton(context.getString(R.string.cancel), null);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView name, possition;
        ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            possition = itemView.findViewById(R.id.position_staff);
            name = itemView.findViewById(R.id.staff_name);
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
        new MyTask(context).execute(BASE_URL, query);
    }

    private void deleteUserFromDatabase(String email){
        email = email.replace(".", "");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("user/" + email);
        reference.removeValue();
    }
}

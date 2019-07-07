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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ducluanxutrieu.quanlynhanvien.Activity.UserInfoActivity;
import com.ducluanxutrieu.quanlynhanvien.Models.Staff;
import com.ducluanxutrieu.quanlynhanvien.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;


import java.util.HashMap;

public class StaffListAdapter extends FirebaseRecyclerAdapter<Staff, StaffListAdapter.ItemViewHolder> {
    private View rootView;


    //Firebase
    private FirebaseFunctions mFunction;

    private final static String TAG = ".StaffList";
    public StaffListAdapter(@NonNull FirebaseRecyclerOptions<Staff> options) {
        super(options);
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        rootView = inflater.inflate(R.layout.item_staff, viewGroup, false);

        mFunction = FirebaseFunctions.getInstance();

        return new ItemViewHolder(rootView);
    }

    @Override
    protected void onBindViewHolder(@NonNull final ItemViewHolder holder, final int position, @NonNull final Staff model) {
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
            public boolean onLongClick(final View v) {
                Log.i(TAG, "ahihi");
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle(rootView.getContext().getString(R.string.delete_this_account))
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setMessage(rootView.getContext().getString(R.string.are_you_sure_delete_this_account))
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //deleteUserFromAuth(model.getUid());
                                //deleteUserFromDatabase(model.getEmail());
                                HashMap<String, Object> map = new HashMap<>();
                                map.put("uid", model.getUid());
                                deleteUser(map).addOnSuccessListener(new OnSuccessListener<String>() {
                                    @Override
                                    public void onSuccess(String s) {
                                        Toast.makeText(v.getContext(), s, Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(v.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }
                        })
                        .setNegativeButton(rootView.getContext().getString(R.string.cancel), null);
                builder.create().show();
                return true;
            }
        });
    }


    public Context getContext() {
        return rootView.getContext();
    }

//    public View getView() {
//        return rootView;
//    }

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

    private Task<String> deleteUser(HashMap<String, Object> map){
        return mFunction.getHttpsCallable("deleteUser")
                .call(map)
                .continueWith(new Continuation<HttpsCallableResult, String>() {
                    @Override
                    public String then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        return task.getResult().toString();
                    }
                });
    }
}

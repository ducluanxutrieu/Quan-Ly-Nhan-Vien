package com.ducluanxutrieu.quanlynhanvien.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ducluanxutrieu.quanlynhanvien.Models.MessageItem;
import com.ducluanxutrieu.quanlynhanvien.Models.RequestItem;
import com.ducluanxutrieu.quanlynhanvien.Models.TokenUser;
import com.ducluanxutrieu.quanlynhanvien.MyTask;
import com.ducluanxutrieu.quanlynhanvien.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class RequestAdapter extends FirebaseRecyclerAdapter<RequestItem, RequestAdapter.ItemViewHolder> {
    private View rootView;

    public RequestAdapter(@NonNull FirebaseRecyclerOptions<RequestItem> options) {
        super(options);
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        rootView = layoutInflater.inflate(R.layout.item_request, viewGroup, false);

        return new ItemViewHolder(rootView);
    }


    @Override
    protected void onBindViewHolder(@NonNull final ItemViewHolder holder, final int position, @NonNull final RequestItem model) {
        final Context context = holder.itemView.getContext();

        holder.name.setText(model.getName());
        holder.content.setText(model.getContent());
        holder.date.setText(model.getDate());
        holder.time.setText(model.getTime());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                builder.setTitle("Accept off day")
                        .setNegativeButton("Deny", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                model.setAccept(false);
                                setAccept(model);
                                holder.accept.setText(context.getString(R.string.denied));
                                holder.accept.setVisibility(View.VISIBLE);
                            }
                        })
                        .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                model.setAccept(true);
                                setAccept(model);
                                holder.accept.setText(context.getString(R.string.accepted));
                                holder.accept.setVisibility(View.VISIBLE);
                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        if (model.isAccept()){
            holder.accept.setText(holder.itemView.getContext().getString(R.string.accepted));
            holder.accept.setVisibility(View.VISIBLE);
        }else {
            holder.accept.setText(context.getString(R.string.denied));
            holder.accept.setVisibility(View.VISIBLE);
        }
    }

    private void setAccept(RequestItem requestItem){
        DatabaseReference referenceRequest = FirebaseDatabase.getInstance().getReference().child("request_from_staff/" + "/" + requestItem.getRequestKey());
        DatabaseReference referenceDayOff = FirebaseDatabase.getInstance().getReference().child("day_off/" + requestItem.getUid() + "/" + requestItem.getRequestKey());
        DatabaseReference sendMessageToStaff = FirebaseDatabase.getInstance().getReference().child("message/admin@gmailcom/" + requestItem.getUid());
        DatabaseReference receiveMessageToStaff = FirebaseDatabase.getInstance().getReference().child("message/" + requestItem.getUid() + "/admin@gmailcom");
        referenceRequest.setValue(requestItem);
        referenceDayOff.setValue(requestItem);

        String isAccept;
        if (requestItem.isAccept()) {
            isAccept = "You can off day " + requestItem.getDate();
        }else {
            isAccept = "You can not off day " + requestItem.getDate();
        }
        MessageItem messageItem = new MessageItem(isAccept, "Admin",null);
        sendMessageToStaff.push().setValue(messageItem);
        receiveMessageToStaff.push().setValue(messageItem);

        pushNotification(isAccept, requestItem.getUid());
    }

    private void pushNotification(final String message, String email) {
        final MyTask task = new MyTask(rootView.getContext());

        final String BASE_URL = "https://quan-ly-nhan-vien.firebaseapp.com/api";
        final String name = "Admin";
        //get Token
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("token/" + email.replace(".", ""));
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                TokenUser tokenUser = dataSnapshot.getValue(TokenUser.class);
                String to = tokenUser.getTokenU();
                String query = "{\"data\":{\"title\":\""+name +"\",\"message\":\""+ message + "\"},\"to\":\""+to+"\"}";
                Log.i("QUERY", query);
                task.execute(BASE_URL, query);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView name, content, date, time, accept;
        ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.text_name_request);
            content = itemView.findViewById(R.id.text_content_request);
            date = itemView.findViewById(R.id.text_date_request);
            time = itemView.findViewById(R.id.text_time_request);
            accept = itemView.findViewById(R.id.text_accept_request);
        }
    }
}

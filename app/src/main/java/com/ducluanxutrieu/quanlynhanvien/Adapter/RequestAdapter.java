package com.ducluanxutrieu.quanlynhanvien.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.ItemViewHolder>{
    private Context context;
    private List<RequestItem> requestItemList;

    public RequestAdapter() {}

    public RequestAdapter(Context context, List<RequestItem> requestItemList) {
        this.context = context;
        this.requestItemList = requestItemList;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View rootView = layoutInflater.inflate(R.layout.request_item, viewGroup, false);

        return new ItemViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ItemViewHolder itemViewHolder, int i) {
        final int position = itemViewHolder.getAdapterPosition();
        itemViewHolder.name.setText(requestItemList.get(position).getName());
        itemViewHolder.content.setText(requestItemList.get(position).getContent());
        itemViewHolder.date.setText(requestItemList.get(position).getDate());
        itemViewHolder.time.setText(requestItemList.get(position).getTime());
        itemViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Accept off day")
                        .setNegativeButton("Deny", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                requestItemList.get(position).setAccept(false);
                                setAccept(requestItemList.get(position));
                                itemViewHolder.accept.setText(context.getString(R.string.denied));
                                itemViewHolder.accept.setVisibility(View.VISIBLE);
                            }
                        })
                        .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                requestItemList.get(position).setAccept(true);
                                setAccept(requestItemList.get(position));
                                itemViewHolder.accept.setText(context.getString(R.string.accepted));
                                itemViewHolder.accept.setVisibility(View.VISIBLE);
                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        if (requestItemList.get(position).isAccept()){
            itemViewHolder.accept.setText(context.getString(R.string.accepted));
            itemViewHolder.accept.setVisibility(View.VISIBLE);
        }else {
            itemViewHolder.accept.setText(context.getString(R.string.denied));
            itemViewHolder.accept.setVisibility(View.VISIBLE);
        }
    }

    private void setAccept(RequestItem requestItem){
        DatabaseReference referenceRequest = FirebaseDatabase.getInstance().getReference().child("request_from_staff/" + "/" + requestItem.getRequestKey());
        DatabaseReference referenceDayOff = FirebaseDatabase.getInstance().getReference().child("day_off/" + requestItem.getEmail().replace(".", "") + "/" + requestItem.getRequestKey());
        DatabaseReference sendMessageToStaff = FirebaseDatabase.getInstance().getReference().child("message/admin@gmailcom/" + requestItem.getEmail().replace(".", ""));
        DatabaseReference receiveMessageToStaff = FirebaseDatabase.getInstance().getReference().child("message/" + requestItem.getEmail().replace(".", "") + "/admin@gmailcom");
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

        pushNotification(isAccept, requestItem.getEmail());
    }

    private void pushNotification(final String message, String email) {
        final MyTask task = new MyTask(context);

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

    @Override
    public int getItemCount() {
        return requestItemList.size();
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

package com.ducluanxutrieu.quanlynhanvien.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ducluanxutrieu.quanlynhanvien.Item.RequestItem;
import com.ducluanxutrieu.quanlynhanvien.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.ItemViewHolder>{
    private Context context;
    private List<RequestItem> requestItemList;

    public RequestAdapter() {
    }

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
                            }
                        })
                        .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                requestItemList.get(position).setAccept(true);
                                setAccept(requestItemList.get(position));
                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
    }

    private void setAccept(RequestItem requestItem){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("request_from_staff/" + "/" + requestItem.getRequestKey());
        reference.setValue(requestItem);
    }

    @Override
    public int getItemCount() {
        return requestItemList.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView name, content, date, time;
        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.text_name_request);
            content = itemView.findViewById(R.id.text_content_request);
            date = itemView.findViewById(R.id.text_date_request);
            time = itemView.findViewById(R.id.text_time_request);
        }
    }
}

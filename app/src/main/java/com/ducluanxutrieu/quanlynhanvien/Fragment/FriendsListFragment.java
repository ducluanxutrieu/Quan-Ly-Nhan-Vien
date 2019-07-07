package com.ducluanxutrieu.quanlynhanvien.Fragment;

import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ducluanxutrieu.quanlynhanvien.Activity.MainActivity;
import com.ducluanxutrieu.quanlynhanvien.Adapter.FriendsListAdapter;
import com.ducluanxutrieu.quanlynhanvien.Dialog.AddNewFriend;
import com.ducluanxutrieu.quanlynhanvien.Models.Friend;
import com.ducluanxutrieu.quanlynhanvien.R;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FriendsListFragment extends Fragment {
    RecyclerView mRecyclerViewFriends;
    FloatingActionButton mFAB;
    FirebaseAuth mFirebaseAuth;

    //Firebase
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    FirebaseFunctions mFunctions;

    List<Friend> friendList;
    FriendsListAdapter mFriendsAdapter;
    String rootUid;
    boolean userAlready = false;

    public FriendsListFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_friends_list, container, false);

        mRecyclerViewFriends = rootView.findViewById(R.id.recycler_view_friends_list);
        mFAB = rootView.findViewById(R.id.fab_add_friend);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //getInstance
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFunctions = FirebaseFunctions.getInstance();

        rootUid = mFirebaseAuth.getUid();

        mDatabaseReference = mFirebaseDatabase.getReference().child("friend_ship/" + rootUid);

        friendList = new ArrayList<>();


        mFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddNewFriend addNewFriend = new AddNewFriend();
                addNewFriend.show(getFragmentManager(), "friend");
            }
        });
    }


    public void getEmailFriend(String email){
        if (checkFriendAlreadyExist(email)){
            Toast.makeText(getContext(), "This friend already exist!", Toast.LENGTH_SHORT).show();
        }else {
            addFriend(email).addOnSuccessListener(new OnSuccessListener<String>() {
                @Override
                public void onSuccess(String s) {
                    Toast.makeText(getContext(), s, Toast.LENGTH_SHORT).show();
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private Task<String> addFriend(String text) {
        // Create the arguments to the callable function.
        Map<String, Object> data = new HashMap<>();
        data.put("email", text);

        return mFunctions
                .getHttpsCallable("addFriend")
                .call(data)
                .continueWith(new Continuation<HttpsCallableResult, String>() {
                    @Override
                    public String then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        // This continuation runs on either success or failure, but if the task
                        // has failed then getResult() will throw an Exception which will be
                        // propagated down.
                        return (String) task.getResult().getData();
                    }
                });
    }

    private boolean checkFriendAlreadyExist(String s1) {

        try {
            mDatabaseReference.child(s1).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Friend friend = dataSnapshot.getValue(Friend.class);

                    //userAlready = false if friend == null
                    userAlready = friend != null;
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return userAlready;
    }

    @Override
    public void onStart() {
        super.onStart();
        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("friend_ship/" + rootUid)
                .limitToLast(50);
        FirebaseRecyclerOptions.Builder<Friend> itemBuilder = new FirebaseRecyclerOptions.Builder<>();
        itemBuilder.setQuery(query, Friend.class);
        FirebaseRecyclerOptions<Friend> options = itemBuilder.build();

        //mFriendsAdapter = new FriendsListAdapter(friendList, view.getContext());
        mFriendsAdapter = new FriendsListAdapter(options);
        mFriendsAdapter.startListening();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerViewFriends.setLayoutManager(layoutManager);
        mRecyclerViewFriends.setAdapter(mFriendsAdapter);

        //Enable swipe left or right to delete an item
        enableSwipe();

        MainActivity.mCatView.dismiss();

    }

    @Override
    public void onStop() {
        super.onStop();
        mFriendsAdapter.stopListening();
    }

    private void enableSwipe() {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                final int position = viewHolder.getAdapterPosition();
                if (i ==  ItemTouchHelper.LEFT || i == ItemTouchHelper.RIGHT) {
                final Friend friend = mFriendsAdapter.getItem(position);
                final String key = mFriendsAdapter.getRef(position).getKey();
                mFriendsAdapter.getRef(position).removeValue();
                    Snackbar snackbar = (Snackbar) Snackbar.make(getView(), "Removed a friend.", Snackbar.LENGTH_LONG);
                    snackbar.setAction("UNDO", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mFriendsAdapter.restoreItem(friend, key);
                        }
                    });
                    snackbar.setActionTextColor(Color.YELLOW);
                    snackbar.show();
                }
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                getDefaultUIUtil().onDraw(c, recyclerView,
                        viewHolder.itemView, dX, dY,
                        actionState, isCurrentlyActive);
            }

        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerViewFriends);
    }
}

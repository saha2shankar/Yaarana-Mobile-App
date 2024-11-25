package com.yaarana.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yaarana.Activities.ChatActivity;
import com.yaarana.Models.User;
import com.yaarana.R;
import com.yaarana.databinding.RowConversationBinding;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UsersViewHolder> {
    private Context context;
    private ArrayList<User> users;
    private DateFormat dateFormat;

    public UsersAdapter(Context context, ArrayList<User> users) {
        this.context = context;
        this.users = users;
        this.dateFormat = DateFormat.getDateTimeInstance(); // Initialize date format
    }

    @NonNull
    @Override
    public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_conversation, parent, false);
        return new UsersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UsersViewHolder holder, int position) {
        User user = users.get(position);
        String senderId = FirebaseAuth.getInstance().getUid();
        String senderRoom = senderId + user.getUid();
        FirebaseDatabase.getInstance().getReference()
                .child("chats")
                .child(senderRoom)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            String lastMsg = snapshot.child("lastMsg").getValue(String.class);
                            Long time = snapshot.child("lastMsgTime").getValue(Long.class);
                            if (lastMsg != null && time != null) {
                                // Truncate lastMsg if it exceeds 40 characters
                                if (lastMsg.length() > 40) {
                                    lastMsg = lastMsg.substring(0, 30) + "...";
                                }
                                holder.binding.lastsms.setText(lastMsg);
                                holder.binding.smstime.setText(formatTime(time));
                            }
                        } else {
                            holder.binding.lastsms.setText("Tap to Chat");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle error
                    }
                });
        holder.binding.username.setText(user.getName());
        Glide.with(context)
                .load(user.getProfileImage())
                .placeholder(R.drawable.avatar)
                .into(holder.binding.imageView);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("name", user.getName());
                intent.putExtra("image", user.getProfileImage());
                intent.putExtra("uid", user.getUid());
                intent.putExtra("token", user.getToken());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class UsersViewHolder extends RecyclerView.ViewHolder {
        RowConversationBinding binding;

        public UsersViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = RowConversationBinding.bind(itemView);
        }
    }

    private String formatTime(Long time) {
        return dateFormat.format(new Date(time));
    }
}

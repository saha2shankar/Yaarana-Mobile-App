package com.yaarana.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.yaarana.Models.Message;
import com.yaarana.R;
import com.yaarana.databinding.ItemRecivedBinding;
import com.yaarana.databinding.ItemSendBinding;
import com.github.pgreze.reactions.Reaction;
import com.github.pgreze.reactions.ReactionView;
import com.github.pgreze.reactions.ReactionPopup;
import com.github.pgreze.reactions.ReactionsConfigBuilder;
import com.github.pgreze.reactions.ReactionsConfig;

import java.util.ArrayList;

public class MessagesAdapter extends RecyclerView.Adapter {

    Context context;
    ArrayList<Message> messages;
    final int ITEM_SEND = 1;
    final int ITEM_RECEIVE = 2;
    String senderRoom;
    String receiverRoom;
    public MessagesAdapter(Context context, ArrayList<Message> messages,String senderRoom,String receiverRoom){
        this.context = context;
        this.messages = messages;
        this.senderRoom =senderRoom;
        this.receiverRoom = receiverRoom;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        if(viewType == ITEM_SEND){
            View view = LayoutInflater.from(context).inflate(R.layout.item_send, parent, false);
            return new SendViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_recived, parent, false);
            return new ReceiverViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messages.get(position);
        if(FirebaseAuth.getInstance().getUid().equals(message.getSenderId())){
            return ITEM_SEND;
        } else {
            return ITEM_RECEIVE;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position){
        Message message = messages.get(position);
        int reactions[] =new int[]{
                R.drawable.happy,
                R.drawable.like,
                R.drawable.love,
                R.drawable.party,
                R.drawable.emoji,
                R.drawable.sad,
                R.drawable.angry,
                R.drawable.dislike
        };
        ReactionsConfig config = new ReactionsConfigBuilder(context)
                .withReactions(reactions)
                .build();
        ReactionPopup popup = new ReactionPopup(context, config, (pos) -> {
            if(pos <0)
                return false;
            if(holder.getClass() ==SendViewHolder.class)
            {
                SendViewHolder viewHolder = (SendViewHolder) holder;
                viewHolder.binding.feeling.setImageResource(reactions[pos]);
                viewHolder.binding.feeling.setVisibility(View.VISIBLE);
            }else {
                ReceiverViewHolder viewHolder = (ReceiverViewHolder) holder;
                viewHolder.binding.feeling.setImageResource(reactions[pos]);
                viewHolder.binding.feeling.setVisibility(View.VISIBLE);
            }
            message.setFeeling(pos);
        FirebaseDatabase.getInstance().getReference()
                .child("chats")
                .child(senderRoom)
                .child("messages")
                .child(message.getMessageId()).setValue(message);
            FirebaseDatabase.getInstance().getReference()
                    .child("chats")
                    .child(receiverRoom)
                    .child("messages")
                    .child(message.getMessageId()).setValue(message);

            return true; // true is closing popup, false is requesting a new selection
        });

        if(holder instanceof SendViewHolder){
            SendViewHolder viewHolder = (SendViewHolder) holder;
            if (message.getMessage().equals("photo")){
                viewHolder.binding.image.setVisibility(View.VISIBLE);
                viewHolder.binding.message.setVisibility(View.GONE);
                Glide.with(context)
                        .load(message.getImageUrl())
                        .placeholder(R.drawable.image)
                        .into(viewHolder.binding.image);
            }

            viewHolder.binding.message.setText(message.getMessage());
            if (message.getFeeling() >=0) {
                viewHolder.binding.feeling.setImageResource(reactions[(int) message.getFeeling()]);
                viewHolder.binding.feeling.setVisibility(View.VISIBLE);
            } else {
                viewHolder.binding.feeling.setVisibility(View.GONE);
            }
            viewHolder.binding.message.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return popup.onTouch(v, event);
                }
            });
        } else if(holder instanceof ReceiverViewHolder){
            ReceiverViewHolder viewHolder = (ReceiverViewHolder) holder;
            if (message.getMessage().equals("photo")){
                viewHolder.binding.image.setVisibility(View.VISIBLE);
                viewHolder.binding.message.setVisibility(View.GONE);
                Glide.with(context)
                        .load(message.getImageUrl())
                        .placeholder(R.drawable.image)
                        .into(viewHolder.binding.image);
            }
            viewHolder.binding.message.setText(message.getMessage());
            if (message.getFeeling() >=0) {

                viewHolder.binding.feeling.setImageResource(reactions[(int) message.getFeeling()]);
                viewHolder.binding.feeling.setVisibility(View.VISIBLE);
            } else {
                viewHolder.binding.feeling.setVisibility(View.GONE);
            }
            viewHolder.binding.message.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return popup.onTouch(v, event);
                }
            });
            viewHolder.binding.image.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return popup.onTouch(v, event);
                }
            });

        }
    }

    @Override
    public int getItemCount(){
        return messages.size();
    }

    public class SendViewHolder extends RecyclerView.ViewHolder{
        ItemSendBinding binding;
        public SendViewHolder(@NonNull View itemView){
            super(itemView);
            binding = ItemSendBinding.bind(itemView);
        }
    }

    public class ReceiverViewHolder extends RecyclerView.ViewHolder{
        ItemRecivedBinding binding;
        public ReceiverViewHolder(@NonNull View itemView){
            super(itemView);
            binding = ItemRecivedBinding.bind(itemView);
        }
    }
}

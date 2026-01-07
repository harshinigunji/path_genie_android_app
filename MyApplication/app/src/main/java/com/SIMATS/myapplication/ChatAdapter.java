package com.SIMATS.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private List<ChatMessage> chatMessages;

    public ChatAdapter(List<ChatMessage> chatMessages) {
        this.chatMessages = chatMessages;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_message, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        ChatMessage message = chatMessages.get(position);

        if (message.isSentByMe()) {
            holder.userLayout.setVisibility(View.VISIBLE);
            holder.botLayout.setVisibility(View.GONE);
            holder.userMessage.setText(message.getMessage());
        } else {
            holder.userLayout.setVisibility(View.GONE);
            holder.botLayout.setVisibility(View.VISIBLE);
            holder.botMessage.setText(message.getMessage());
        }

        holder.timestamp.setText(message.getTimestamp());
        holder.timestamp.setVisibility(View.VISIBLE);

        // Show date header only for the first message for now (simple logic)
        if (position == 0) {
            holder.dateHeader.setVisibility(View.VISIBLE);
        } else {
            holder.dateHeader.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder {
        LinearLayout userLayout, botLayout;
        TextView userMessage, botMessage, timestamp, dateHeader;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            userLayout = itemView.findViewById(R.id.userLayout);
            botLayout = itemView.findViewById(R.id.botLayout);
            userMessage = itemView.findViewById(R.id.userMessage);
            botMessage = itemView.findViewById(R.id.botMessage);
            timestamp = itemView.findViewById(R.id.timestamp);
            dateHeader = itemView.findViewById(R.id.dateHeader);
        }
    }
}

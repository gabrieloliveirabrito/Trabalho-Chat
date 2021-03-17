package com.gobrito.trabalho_chat.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.gobrito.trabalho_chat.AppController;
import com.gobrito.trabalho_chat.Models.MensagensDTO;
import com.gobrito.trabalho_chat.R;
import com.gobrito.trabalho_chat.ViewHolders.ReceivedMessageHolder;
import com.gobrito.trabalho_chat.ViewHolders.SentMessageHolder;

import java.util.List;

public class MessageListAdapter extends RecyclerView.Adapter {
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;

    private final Context context;
    private final List<MensagensDTO> messageList;
    private RecyclerView.ViewHolder lastViewHolder;

    public MessageListAdapter(Context context, List<MensagensDTO> messageList) {
        this.context = context;
        this.messageList = messageList;
        lastViewHolder = null;
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    @Override
    public int getItemViewType(int position) {
        MensagensDTO message = (MensagensDTO) messageList.get(position);
        int lastUserId = AppController.getLastUserId();

        return message.getUsers_id() == lastUserId ? VIEW_TYPE_MESSAGE_SENT : VIEW_TYPE_MESSAGE_RECEIVED;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.message_me, parent, false);
            return new SentMessageHolder(view);
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.message_other, parent, false);
            return new ReceivedMessageHolder(view);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        MensagensDTO message = (MensagensDTO) messageList.get(position);

//        int lastPosition = position - 1;
//        if(lastViewHolder != null && position > 0 && messageList.get(lastPosition).getUsers_id() == message.getUsers_id()) {
//            int lastViewType = getItemViewType(lastPosition);
//            if (lastViewType == VIEW_TYPE_MESSAGE_SENT)
//                ((SentMessageHolder) lastViewHolder).hideSentAt();
//            else if (lastViewType == VIEW_TYPE_MESSAGE_RECEIVED)
//                ((ReceivedMessageHolder) lastViewHolder).hideSentAt();
//        }
//        lastViewHolder = holder;


        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT:
                ((SentMessageHolder) holder).bind(message);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((ReceivedMessageHolder) holder).bind(message);
        }
    }
}
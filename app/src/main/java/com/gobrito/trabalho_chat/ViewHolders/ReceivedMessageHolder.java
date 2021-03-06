package com.gobrito.trabalho_chat.ViewHolders;

import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.gobrito.trabalho_chat.Models.MensagensDTO;
import com.gobrito.trabalho_chat.R;

import java.text.DateFormat;

public class ReceivedMessageHolder extends RecyclerView.ViewHolder {
    private View itemView;
    private TextView lblMessage, lblChatSentAt, lblName, lblAvatar;

    public ReceivedMessageHolder(View itemView) {
        super(itemView);

        this.itemView = itemView;
        lblMessage = itemView.findViewById(R.id.lblMessage);
        lblChatSentAt = itemView.findViewById(R.id.lblChatSentAt);
        lblName = itemView.findViewById(R.id.lblUserName);
        lblAvatar = itemView.findViewById(R.id.lblAvatar);
    }

    public void bind(MensagensDTO message) {
        lblMessage.setText(message.getMensagem());
        lblChatSentAt.setText(itemView.getContext().getString(R.string.chat_bubble_sentat, DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(message.getSentAt())));
        lblName.setText(message.getName());
        lblAvatar.setText(message.getName().substring(0, 2));
    }
}
package com.gobrito.trabalho_chat.ViewHolders;

import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.gobrito.trabalho_chat.Models.MensagensDTO;
import com.gobrito.trabalho_chat.R;

import java.text.DateFormat;

public class SentMessageHolder extends RecyclerView.ViewHolder {
    private TextView lblMessage, lblChatSentAt, lblAvatar;

    public SentMessageHolder(View itemView) {
        super(itemView);

        lblMessage =  itemView.findViewById(R.id.lblMessage);
        lblChatSentAt =  itemView.findViewById(R.id.lblChatSentAt);
        lblAvatar = itemView.findViewById(R.id.lblAvatar);
    }

    public void bind(MensagensDTO message) {
        lblMessage.setText(message.getMensagem());
        lblChatSentAt.setText(itemView.getContext().getString(R.string.chat_bubble_sentat, DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(message.getSentAt())));
        lblAvatar.setText(message.getName().substring(0, 2));
    }
}
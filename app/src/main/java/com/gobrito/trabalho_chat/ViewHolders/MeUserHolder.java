package com.gobrito.trabalho_chat.ViewHolders;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.gobrito.trabalho_chat.Models.Users;
import com.gobrito.trabalho_chat.R;

public class MeUserHolder extends RecyclerView.ViewHolder {
    private TextView lblUserName, lblEmail;

    public MeUserHolder(View itemView) {
        super(itemView);

        lblUserName =  itemView.findViewById(R.id.lblUserName);
        lblEmail =  itemView.findViewById(R.id.lblUserEmail);
    }

    public void bind(Users user) {
        lblUserName.setText(user.getName());
        lblEmail.setText(user.getEmail());
    }
}
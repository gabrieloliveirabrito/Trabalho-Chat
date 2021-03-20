package com.gobrito.trabalho_chat.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.gobrito.trabalho_chat.AppController;
import com.gobrito.trabalho_chat.Models.Users;
import com.gobrito.trabalho_chat.R;
import com.gobrito.trabalho_chat.ViewHolders.MeUserHolder;
import com.gobrito.trabalho_chat.ViewHolders.OtherUserHolder;

import java.util.List;

public class UserListAdapter extends RecyclerView.Adapter {
    private static final int VIEW_TYPE_USER_ME = 1;
    private static final int VIEW_TYPE_USER_OTHER = 2;

    private final Context context;
    private final List<Users> userList;
    private RecyclerView.ViewHolder lastViewHolder;

    public UserListAdapter(Context context, List<Users> userList) {
        this.context = context;
        this.userList = userList;
        lastViewHolder = null;
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    @Override
    public int getItemViewType(int position) {
        Users user = userList.get(position);
        int lastUserId = AppController.getPreferences().getLastUserId();

        return user.getId() == lastUserId ? VIEW_TYPE_USER_ME : VIEW_TYPE_USER_OTHER;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        if (viewType == VIEW_TYPE_USER_ME) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.user_me, parent, false);
            return new MeUserHolder(view);
        } else if (viewType == VIEW_TYPE_USER_OTHER) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.user_other, parent, false);
            return new OtherUserHolder(view);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Users user = userList.get(position);

        switch (holder.getItemViewType()) {
            case VIEW_TYPE_USER_ME:
                ((MeUserHolder) holder).bind(user);
                break;
            case VIEW_TYPE_USER_OTHER:
                ((OtherUserHolder) holder).bind(user);
        }
    }
}
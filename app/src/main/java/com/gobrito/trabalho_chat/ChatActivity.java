package com.gobrito.trabalho_chat;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.gobrito.trabalho_chat.Models.Users;

public class ChatActivity extends AppCompatActivity {
    private Users usuario;

    public ChatActivity(Users usuario) {
        super();
        this.usuario = usuario;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
    }
}
package com.gobrito.trabalho_chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Application;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.gobrito.trabalho_chat.Adapters.MessageListAdapter;
import com.gobrito.trabalho_chat.Models.MensagensDTO;
import com.gobrito.trabalho_chat.Models.Users;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    private Users usuario;
    private List<MensagensDTO> mensagens;

    private RecyclerView recyclerChat;
    private MessageListAdapter recyclerAdapter;
    private TextView lblChatWelcome, lblChatLoading;
    private EditText txtMessage;
    private Button btnSend;
    private Handler handler;
    private int segundos = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        usuario = AppController.getUsuarioAtual();
        setContentView(R.layout.activity_chat);

        mensagens = new LinkedList<>();

        lblChatWelcome = findViewById(R.id.lblChatWelcome);
        lblChatWelcome.setText("Seja bem-vindo " + usuario.getName());

        lblChatLoading = findViewById(R.id.lblChatLoading);
        lblChatLoading.setText("Atualizando em 5s");

        recyclerChat = findViewById(R.id.recycler_chat);
        recyclerAdapter = new MessageListAdapter(this, mensagens);
        recyclerChat.setAdapter(recyclerAdapter);
        recyclerChat.setLayoutManager(new LinearLayoutManager(this));

        txtMessage = findViewById(R.id.txtChatMessage);
        btnSend = findViewById(R.id.btnSendChatMessage);

        btnSend.setOnClickListener(v -> btnSend_OnClick());

        handler = new Handler();
        Runnable messageThread = new Runnable() {
            @Override
            public void run() {
                if (segundos > 0) {
                    runOnUiThread(() -> lblChatLoading.setText(String.format("Atualizando em %d", segundos--) + "s"));
                    handler.postDelayed(this, 1000);
                } else {
                    runOnUiThread(() -> lblChatLoading.setText("Carregando mensagens..."));
                    AppController.sendMessagesRequest(response -> {
                        mensagens.clear();
                        mensagens.addAll(Arrays.asList(response));

                        runOnUiThread(() -> {
                            boolean canScroll = recyclerChat.canScrollVertically(1);

                            recyclerAdapter.notifyDataSetChanged();

                            if (!canScroll)
                                recyclerChat.scrollToPosition(mensagens.size() - 1);

                            segundos = 5;
                            handler.postDelayed(this, 1000);
                        });
                    }, error -> {
                        Toast.makeText(ChatActivity.this, "Houve um erro ao recuperar as mensagens!!", Toast.LENGTH_SHORT).show();

                        segundos = 5;
                        handler.postDelayed(this, 1000);
                    });
                }
            }
        };
        handler.post(messageThread);
    }

    @Override
    public void onBackPressed() {
        this.moveTaskToBack(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.chat_context_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_chat_logout:
               AppController.setUsuarioAtual(null);
               AppController.saveLastUserId(-1);
               finish();
                break;
            case R.id.menu_chat_exit:
                System.exit(0);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    void btnSend_OnClick() {
        String mensagem = txtMessage.getText().toString();
        if(!mensagem.isEmpty()) {
            txtMessage.setText("");

            AppController.sendChatMessage(mensagem, response -> {
                Toast.makeText(ChatActivity.this, "Mensagem enviada", Toast.LENGTH_SHORT).show();
            }, error -> {
                error.printStackTrace();
                Toast.makeText(this, "Houve um erro ao enviar a mensagem!!", Toast.LENGTH_SHORT).show();
            });
        }
    }
}
package com.gobrito.trabalho_chat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.gobrito.trabalho_chat.Adapters.MessageListAdapter;
import com.gobrito.trabalho_chat.Models.MensagensDTO;
import com.gobrito.trabalho_chat.Models.Users;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    private Users usuario;
    private List<MensagensDTO> mensagens;
    private boolean from = true;

    private RecyclerView recyclerChat;
    private MessageListAdapter recyclerAdapter;
    private TextView lblChatWelcome;
    private EditText txtMessage;
    private Button btnSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        usuario = AppController.getUsuarioAtual();
        setContentView(R.layout.activity_chat);

        mensagens = new LinkedList<>();

        lblChatWelcome = findViewById(R.id.lblChatWelcome);
        lblChatWelcome.setText("Seja bem-vindo " + usuario.getName());

        recyclerChat = findViewById(R.id.recycler_chat);
        recyclerAdapter = new MessageListAdapter(this, mensagens);
        recyclerChat.setAdapter(recyclerAdapter);
        recyclerChat.setLayoutManager(new LinearLayoutManager(this));

        txtMessage = findViewById(R.id.txtChatMessage);
        btnSend = findViewById(R.id.btnSendChatMessage);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mensagem = txtMessage.getText().toString();
                if(!mensagem.isEmpty()) {
//                    AppController.sendChatMessage(mensagem, response -> {
//
//                    }, null);

                    if(mensagens.size() % 5 == 0)
                        from = !from;

                    MensagensDTO mensagemDTO = new MensagensDTO();
                    mensagemDTO.setId(0);
                    mensagemDTO.setMensagem(mensagem);
                    mensagemDTO.setSentAt(Calendar.getInstance().getTime());
                    mensagemDTO.setUsers_id(usuario.getId() + (from ? 1 : 0));
                    mensagemDTO.setName("Gabriel Oliveira");

                    mensagens.add(mensagemDTO);
                    recyclerAdapter.notifyItemInserted(mensagens.size() - 1);
                }
            }
        });

       for(int i = 0; i < 20; i++)
           btnSend.performClick();
    }

    @Override
    public void onBackPressed() {

    }
}
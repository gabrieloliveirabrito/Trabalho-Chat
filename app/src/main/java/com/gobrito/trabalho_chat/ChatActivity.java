package com.gobrito.trabalho_chat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gobrito.trabalho_chat.Adapters.MessageListAdapter;
import com.gobrito.trabalho_chat.Models.MensagensDTO;
import com.gobrito.trabalho_chat.Models.Users;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Arrays;
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
    private FloatingActionButton fabChat;
    private Handler handler;
    private Runnable messageThread;
    private int segundos = 0;
    private int msgCount = 0;
    private boolean cancelRun = false;

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

        fabChat = findViewById(R.id.fab_chat);
        fabChat.setOnClickListener((v) -> recyclerChat.scrollToPosition(mensagens.size() - 1));

        recyclerChat = findViewById(R.id.recycler_chat);
        recyclerAdapter = new MessageListAdapter(this, mensagens);
        recyclerChat.setAdapter(recyclerAdapter);
        recyclerChat.setLayoutManager(new LinearLayoutManager(this));
        recyclerChat.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                fabChat.setVisibility(recyclerChat.canScrollVertically(1) ? View.VISIBLE : View.GONE);
            }
        });
        recyclerChat.setHasFixedSize(true);
        recyclerChat.setItemViewCacheSize(20);
        recyclerChat.setDrawingCacheEnabled(true);
        recyclerChat.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        txtMessage = findViewById(R.id.txtChatMessage);
        btnSend = findViewById(R.id.btnSendChatMessage);

        txtMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                btnSend.setEnabled(!s.toString().isEmpty());
            }
        });
        btnSend.setOnClickListener(v -> btnSend_OnClick());

        handler = new Handler();
        messageThread = new Runnable() {
            @Override
            public void run() {
                if (cancelRun) return;

                if (segundos > 0) {
                    runOnUiThread(() -> lblChatLoading.setText(String.format("Atualizando em %d", segundos--) + "s"));
                    handler.postDelayed(this, 1000);
                } else {
                    runOnUiThread(() -> lblChatLoading.setText("Carregando mensagens..."));
                    AppController.sendMessagesRequest(response -> {
                        msgCount = mensagens.size();
                        if (msgCount != response.length) {
                            List toAdd = Arrays.asList(response).subList(msgCount == 0 ? 0 : msgCount, response.length);
                            mensagens.addAll(toAdd);
                        }

                        runOnUiThread(() -> {
                            if (msgCount != mensagens.size()) {
                                boolean canScroll = recyclerChat.canScrollVertically(1);
                                recyclerAdapter.notifyItemRangeChanged(msgCount - 1, response.length - msgCount);

                                if (!canScroll)
                                    recyclerChat.scrollToPosition(mensagens.size() - 1);
                            }

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
    protected void onDestroy() {
        super.onDestroy();
        cancelRun = true;
        handler.removeCallbacks(this.messageThread);
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
        switch (item.getItemId()) {
            case R.id.menu_chat_logout: {
                AppController.setUsuarioAtual(null);
                AppController.saveLastUserId(-1);

                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("LOGOUT", true);
                startActivity(intent);
                finish();
            }
            break;
            case R.id.menu_chat_exit: {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
            break;
        }
        return super.onOptionsItemSelected(item);
    }

    void btnSend_OnClick() {
        String mensagem = txtMessage.getText().toString();
        if (!mensagem.isEmpty()) {
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
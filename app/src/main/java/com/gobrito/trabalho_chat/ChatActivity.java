package com.gobrito.trabalho_chat;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
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
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gobrito.trabalho_chat.Adapters.MessageListAdapter;
import com.gobrito.trabalho_chat.Models.MensagensDTO;
import com.gobrito.trabalho_chat.Models.Users;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class ChatActivity extends AppCompatActivity {
    private Users usuario;
    private List<MensagensDTO> mensagens;
    private List<Users> usuarios;
    private Preferences preferences;

    private RecyclerView recyclerChat;
    private MessageListAdapter recyclerAdapter;
    private TextView lblChatWelcome, lblChatLoading;
    private EditText txtMessage;
    private Button btnSend;
    private FloatingActionButton fabChat;
    private Toolbar chatToolbar;
    private Handler handler;
    private Runnable messageThread;
    private Resources resources;
    private int segundos = 0;
    private int msgCount = 0;
    private boolean cancelRun = false;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppController.changeLocale(this);

        usuario = AppController.getUsuarioAtual();
        preferences = AppController.getPreferences();

        setContentView(R.layout.activity_chat);
        resources = getResources();

        chatToolbar = findViewById(R.id.chat_appbar);
        setSupportActionBar(chatToolbar);
        chatToolbar.setSubtitle(R.string.loading);

        mensagens = new LinkedList<>();
        usuarios = new LinkedList<>();

        lblChatWelcome = findViewById(R.id.lblChatWelcome);
        lblChatWelcome.setText(resources.getString(R.string.chat_welcome, usuario.getName()));

        lblChatLoading = findViewById(R.id.lblChatLoading);
        lblChatLoading.setText(resources.getString(R.string.chat_messages_updating, 5));

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
        messageThread = () -> messageThreadWork();
        handler.post(messageThread);

        invalidateOptionsMenu();
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

        menu.findItem(R.id.menu_chat_night).setIcon(AppController.getDarkMode(this) ? R.drawable.ic_baseline_brightness_7_24 : R.drawable.ic_baseline_brightness_3_24);
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_chat_logout: {
                AppController.setUsuarioAtual(null);
                AppController.getPreferences().saveLastUserId(-1);

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
            case R.id.menu_chat_night: {
                if (!AppController.toggleDarkMode(this, false)) {
                    preferences.setDarkMode(false);
                    Toast.makeText(this, R.string.app_nightmode_failed, Toast.LENGTH_SHORT).show();
                }
            }
            break;
            case R.id.menu_chat_users: {
                Intent intent = new Intent(this, UsersActivity.class);
                startActivity(intent);
            }
            break;
            case R.id.menu_chat_language_br:
                AppController.getPreferences().setLocale("pt-BR");
                reloadActivity();
                break;
            case R.id.menu_chat_language_en:
                AppController.getPreferences().setLocale("en");
                reloadActivity();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void reloadActivity() {
        finish();
        startActivity(getIntent());
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    void changeLocale(String locale) {
        Configuration conf = resources.getConfiguration();
        conf.setLocale(new Locale(locale));
        conf.locale = new Locale(locale);
        Locale.setDefault(conf.locale);

        resources.updateConfiguration(conf, resources.getDisplayMetrics());
    }

    void messageThreadWork() {
        if (cancelRun) return;

        if (segundos > 0) {
            runOnUiThread(() -> lblChatLoading.setText(resources.getString(R.string.chat_messages_updating, segundos--)));
            handler.postDelayed(messageThread, 1000);
        } else {
            runOnUiThread(() -> lblChatLoading.setText(R.string.loading_chat_messages));
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

                    msgCount = mensagens.size();
                    chatToolbar.setSubtitle(resources.getString(R.string.chat_subtitle, msgCount));

                    segundos = 5;
                    handler.postDelayed(messageThread, 1000);
                });
            }, error -> {
                Toast.makeText(ChatActivity.this, R.string.chat_messages_error, Toast.LENGTH_SHORT).show();

                segundos = 5;
                handler.postDelayed(messageThread, 1000);
            });
        }
    }

    void btnSend_OnClick() {
        String mensagem = txtMessage.getText().toString();
        if (!mensagem.isEmpty()) {
            txtMessage.setText("");

            AppController.sendChatMessage(mensagem, response -> {
                Toast.makeText(ChatActivity.this, R.string.chat_message_sent, Toast.LENGTH_SHORT).show();
            }, error -> {
                error.printStackTrace();
                Toast.makeText(ChatActivity.this, R.string.chat_message_error, Toast.LENGTH_SHORT).show();
            });
        }
    }
}
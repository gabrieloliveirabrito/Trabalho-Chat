package com.gobrito.trabalho_chat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.gobrito.trabalho_chat.Models.ClienteLogin;
import com.gobrito.trabalho_chat.Models.Users;

public class MainActivity extends AppCompatActivity {
    EditText txtNome, txtEmail;
    Button btnEntrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtNome = findViewById(R.id.txtNome);
        txtEmail = findViewById(R.id.txtEmail);
        btnEntrar = findViewById(R.id.btnEntrar);

        ClienteLogin lastLogin = AppController.getLastClienteLogin();
        txtNome.setText(lastLogin.getNome());
        txtEmail.setText(lastLogin.getEmail());

        btnEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClienteLogin model = new ClienteLogin();
                model.setNome(txtNome.getText().toString());
                model.setEmail(txtEmail.getText().toString());

                AppController.sendUserRegister(model, response -> {
                    AppController.saveLastUserId(response.getId());
                    AppController.setUsuarioAtual(response);

                    goToChatActivity();
                }, error -> {
                    error.printStackTrace();
                    String message = "unknown";

                    if (error.networkResponse != null) {
                        switch (error.networkResponse.statusCode) {
                            case 401:
                                message = "Falha ao registrar seu acesso, permissão negada!!";
                                break;
                            case 403:
                                message = "Acesso negado!";
                                break;
                            case 404:
                                message = "Não encontrado!";
                                break;
                        }
                    } else message = error.getMessage();

                    Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
                });
            }
        });

        int lastUser = AppController.getLastUserId();
        if (lastUser == -1) {
            txtNome.setEnabled(true);
            txtEmail.setEnabled(true);
            btnEntrar.setEnabled(true);
        } else {
            AppController.sendGetUserInfo(lastUser, new Response.Listener<Users>() {
                @Override
                public void onResponse(Users response) {
                    Toast.makeText(MainActivity.this, "Seja bem-vindo " + response.getName(), Toast.LENGTH_SHORT).show();
                    AppController.setUsuarioAtual(response);

                    goToChatActivity();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    txtNome.setEnabled(true);
                    txtEmail.setEnabled(true);
                    btnEntrar.setEnabled(true);
                }
            });
        }
    }

    void goToChatActivity() {
        runOnUiThread(() -> {
            Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
            startActivity(intent);
        });
    }
}
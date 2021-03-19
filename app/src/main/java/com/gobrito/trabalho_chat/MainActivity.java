package com.gobrito.trabalho_chat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.gobrito.trabalho_chat.Models.ClienteLogin;
import com.gobrito.trabalho_chat.Models.Users;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class MainActivity extends AppCompatActivity {
    TextInputEditText txtNome, txtEmail;
    MaterialCheckBox cbAutoLogin;
    Button btnEntrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtNome = findViewById(R.id.txtNome);
        txtEmail = findViewById(R.id.txtEmail);
        cbAutoLogin = findViewById(R.id.cbAutoLogin);
        btnEntrar = findViewById(R.id.btnEntrar);

        ClienteLogin lastLogin = AppController.getLastClienteLogin();
        txtNome.setText(lastLogin.getNome());
        txtEmail.setText(lastLogin.getEmail());
        cbAutoLogin.setChecked(AppController.getAutoLogin());

        btnEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClienteLogin model = new ClienteLogin();
                model.setNome(txtNome.getText().toString());
                model.setEmail(txtEmail.getText().toString());

                AppController.sendUserRegister(model, response -> {
                    AppController.saveLastUserId(response.getId());
                    AppController.setUsuarioAtual(response);
                    AppController.setAutoLogin(cbAutoLogin.isChecked());
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
                    Toast.makeText(MainActivity.this.getApplicationContext(), "Seja bem-vindo " + response.getName(), Toast.LENGTH_LONG).show();
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
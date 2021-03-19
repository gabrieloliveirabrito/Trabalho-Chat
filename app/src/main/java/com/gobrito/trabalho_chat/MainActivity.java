package com.gobrito.trabalho_chat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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

        if(getIntent().getBooleanExtra("LOGOUT", false))
            setFieldsStatus(true);

        btnEntrar.setOnClickListener(v -> btnEntrar_OnClick());

        if(AppController.getAutoLogin()) {
            int lastUser = AppController.getLastUserId();
            if (lastUser == -1) {
                setFieldsStatus(true);
            } else {
                setFieldsStatus(false);
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
                        setFieldsStatus(true);
                    }
                });
            }
        } else {
            setFieldsStatus(true);
        }
    }

    void setFieldsStatus(boolean enabled) {
        txtNome.setEnabled(enabled);
        txtEmail.setEnabled(enabled);
        btnEntrar.setEnabled(enabled);
        cbAutoLogin.setEnabled(enabled);
    }

    void goToChatActivity() {
        runOnUiThread(() -> {
            Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
            startActivity(intent);
        });
    }

    void btnEntrar_OnClick() {
        String nome = txtNome.getText().toString();
        String email = txtEmail.getText().toString();

        if(nome.isEmpty())
            new AlertDialog.Builder(this).setTitle("AVISO").setMessage("Você deixou o nome do usuário em branco!").setIcon(R.drawable.ic_baseline_warning_24).show();
        else if(email.isEmpty())
            new AlertDialog.Builder(this).setTitle("AVISO").setMessage("Você deixou o e-mail em branco!").setIcon(R.drawable.ic_baseline_warning_24).show();
        else {
            setFieldsStatus(false);

            ClienteLogin model = new ClienteLogin();
            model.setNome(nome);
            model.setEmail(email);

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
                setFieldsStatus(true);
            });
        }
    }
}
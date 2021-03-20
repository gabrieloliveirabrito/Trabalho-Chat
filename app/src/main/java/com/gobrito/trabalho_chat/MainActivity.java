package com.gobrito.trabalho_chat;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.gobrito.trabalho_chat.Models.ClienteLogin;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputEditText;

public class MainActivity extends AppCompatActivity {
    static boolean loadDarkMode = true;

    TextInputEditText txtNome, txtEmail;
    MaterialCheckBox cbAutoLogin;
    Button btnEntrar;
    Resources resources;
    Preferences preferences;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            AppController.changeLocale(this);
            setContentView(R.layout.activity_main);
            resources = getResources();

            txtNome = findViewById(R.id.txtNome);
            txtEmail = findViewById(R.id.txtEmail);
            cbAutoLogin = findViewById(R.id.cbAutoLogin);
            btnEntrar = findViewById(R.id.btnEntrar);
            preferences = AppController.getPreferences();

            if (loadDarkMode && preferences.getDarkMode() && (resources.getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_NO) {
                loadDarkMode = false;
                if (!AppController.toggleDarkMode(this, false)) {
                    preferences.setDarkMode(false);
                    Toast.makeText(this, R.string.app_nightmode_failed, Toast.LENGTH_SHORT).show();
                }
                recreate();
            } else {
                ClienteLogin lastLogin = preferences.getLastClienteLogin();
                txtNome.setText(lastLogin.getNome());
                txtEmail.setText(lastLogin.getEmail());
                cbAutoLogin.setChecked(preferences.getAutoLogin());

                if (getIntent().getBooleanExtra("LOGOUT", false))
                    setFieldsStatus(true);

                btnEntrar.setOnClickListener(v -> btnEntrar_OnClick());

                if (preferences.getAutoLogin()) {
                    int lastUser = preferences.getLastUserId();
                    if (lastUser == -1) {
                        setFieldsStatus(true);
                    } else {
                        setFieldsStatus(false);
                        AppController.sendGetUserInfo(lastUser, response -> {
                            Toast.makeText(MainActivity.this.getApplicationContext(), resources.getString(R.string.welcome_user, response.getName()), Toast.LENGTH_LONG).show();
                            AppController.setUsuarioAtual(response);

                            goToChatActivity();
                        }, error -> setFieldsStatus(true));
                    }
                } else {
                    setFieldsStatus(true);
                }
            }
    }

    @Override
    public void applyOverrideConfiguration(Configuration overrideConfiguration) {
        if (overrideConfiguration != null) {
            int uiMode = overrideConfiguration.uiMode;
            overrideConfiguration.setTo(getBaseContext().getResources().getConfiguration());
            overrideConfiguration.uiMode = uiMode;
        }
        super.applyOverrideConfiguration(overrideConfiguration);
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

        if (nome.isEmpty())
            new AlertDialog.Builder(this).setTitle(R.string.warning).setMessage(R.string.main_name_empty).setIcon(R.drawable.ic_baseline_warning_24).show();
        else if (email.isEmpty())
            new AlertDialog.Builder(this).setTitle(R.string.warning).setMessage(R.string.main_email_empty).setIcon(R.drawable.ic_baseline_warning_24).show();
        else {
            setFieldsStatus(false);

            ClienteLogin model = new ClienteLogin();
            model.setNome(nome);
            model.setEmail(email);

            AppController.sendUserRegister(model, response -> {
                preferences.saveLastUserId(response.getId());
                AppController.setUsuarioAtual(response);
                preferences.setAutoLogin(cbAutoLogin.isChecked());
                goToChatActivity();
            }, error -> {
                error.printStackTrace();
                String message = "unknown";

                if (error.networkResponse != null) {
                    switch (error.networkResponse.statusCode) {
                        case 401:
                            message = getString(R.string.main_login_unauthorized);
                            break;
                        case 403:
                            message = getString(R.string.main_login_forbidden);
                            break;
                        case 404:
                            message = getString(R.string.main_login_notfound);
                            break;
                    }
                } else message = error.getMessage();

                Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
                setFieldsStatus(true);
            });
        }
    }
}
package com.gobrito.trabalho_chat;

import android.content.Context;
import android.content.SharedPreferences;

import com.gobrito.trabalho_chat.Models.ClienteLogin;
import com.gobrito.trabalho_chat.Models.Users;

public class Preferences {
    private SharedPreferences sharedPreferences;

    Preferences(Context context) {
        sharedPreferences = context.getSharedPreferences(Constants.APP_PREFID, Context.MODE_PRIVATE);
    }

    public void setLastUser(ClienteLogin login) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("nome", login.getNome());
        editor.putString("email", login.getEmail());
        editor.apply();
    }

    public boolean getAutoLogin() {
        return sharedPreferences.getBoolean("auto_login", false);
    }

    public void setAutoLogin(boolean autoLogin) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("auto_login", autoLogin);
        editor.apply();
    }

    public void saveLastUserId(int id) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("usuario", id);
        editor.apply();
    }

    public int getLastUserId() {
        return sharedPreferences.getInt("usuario", -1);
    }

    public  ClienteLogin getLastClienteLogin() {
        ClienteLogin login = new ClienteLogin();
        login.setNome(sharedPreferences.getString("nome", ""));
        login.setEmail(sharedPreferences.getString("email", ""));

        return login;
    }

    public boolean getDarkMode() {
        return sharedPreferences.getBoolean("dark_mode", false);
    }

    public void setDarkMode(boolean darkMode) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("dark_mode", darkMode);
        editor.apply();
    }

    public String getLocale() {
        return sharedPreferences.getString("locale", "pt-BR");
    }

    public void setLocale(String locale) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("locale", locale);
        editor.apply();
    }
}

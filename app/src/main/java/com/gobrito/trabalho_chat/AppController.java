package com.gobrito.trabalho_chat;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.Nullable;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.gobrito.trabalho_chat.Models.ClienteLogin;
import com.gobrito.trabalho_chat.Models.Users;
import com.google.gson.Gson;

import org.json.JSONObject;

public class AppController extends Application {
    private static RequestQueue queue;
    private static Gson gsonInstance;
    private static SharedPreferences sharedPreferences;

    @Override
    public void onCreate() {
        super.onCreate();
        queue = Volley.newRequestQueue(getApplicationContext());
        gsonInstance = new Gson();
        sharedPreferences = getSharedPreferences(Constants.APP_PREFID, Context.MODE_PRIVATE);
    }

    public static Gson getGson() {
        return gsonInstance;
    }

    public static void saveLastUserId(int id) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("usuario", id);
        editor.apply();
    }

    public static int getLastUserId() {
        return sharedPreferences.getInt("usuario", -1);
    }

    public static ClienteLogin getLastClienteLogin() {
        ClienteLogin login = new ClienteLogin();
        login.setNome(sharedPreferences.getString("nome", ""));
        login.setEmail(sharedPreferences.getString("email", ""));

        return login;
    }

    public static void sendUserRegister(ClienteLogin data, Response.Listener<Users> listener, @Nullable Response.ErrorListener errorListener) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("nome", data.getNome());
        editor.putString("email", data.getEmail());
        editor.apply();

        GsonRequest request = new GsonRequest<Users>(Request.Method.POST, Constants.APP_URL + "chat/registrar", data, Users.class, null, listener, errorListener);

        queue.add(request);
    }

    public static void sendGetUserInfo(int id, Response.Listener<Users> listener, @Nullable Response.ErrorListener errorListener) {
        GsonRequest request = new GsonRequest<Users>(Request.Method.GET, Constants.APP_URL + "chat/registrar/" + Integer.toString(id), null, Users.class, null, listener, errorListener);

        queue.add(request);
    }
}

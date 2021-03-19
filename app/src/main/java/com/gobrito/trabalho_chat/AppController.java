package com.gobrito.trabalho_chat;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.gobrito.trabalho_chat.Models.ClienteLogin;
import com.gobrito.trabalho_chat.Models.MensagemEnvioDTO;
import com.gobrito.trabalho_chat.Models.MensagensDTO;
import com.gobrito.trabalho_chat.Models.Users;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;

import java.util.Date;


public class AppController extends Application {
    private static RequestQueue queue;
    private static Gson gsonInstance;
    private static SharedPreferences sharedPreferences;
    private static Users usuarioAtual;

    public static Gson getGson() {
        return gsonInstance;
    }

    public static Users getUsuarioAtual() {
        return usuarioAtual;
    }

    public static boolean getAutoLogin() {
        return sharedPreferences.getBoolean("auto_login", false);
    }

    public static void setAutoLogin(boolean autoLogin) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("auto_login", autoLogin);
        editor.apply();
    }

    public static void setUsuarioAtual(Users usuarioAtual) {
        AppController.usuarioAtual = usuarioAtual;
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

        GsonRequest request = new GsonRequest<>(Request.Method.POST, Constants.APP_URL + "chat/registrar", data, Users.class, null, listener, errorListener);
        queue.add(request);
    }

    public static void sendGetUserInfo(int id, Response.Listener<Users> listener, @Nullable Response.ErrorListener errorListener) {
        GsonRequest request = new GsonRequest<>(Request.Method.GET, Constants.APP_URL + "chat/registrar/" + Integer.toString(id), null, Users.class, null, listener, errorListener);
        queue.add(request);
    }

    public static void sendChatMessage(String message, Response.Listener<MensagemEnvioDTO> listener, @Nullable Response.ErrorListener errorListener) {
        MensagemEnvioDTO mensagem = new MensagemEnvioDTO();
        mensagem.setMensagem(message);
        mensagem.setId(usuarioAtual.getId());

        GsonRequest request = new GsonRequest<>(Request.Method.POST, Constants.APP_URL + "chat/enviar", mensagem, MensagemEnvioDTO.class, null, listener, errorListener);
        queue.add(request);
    }

    public static void sendMessagesRequest(Response.Listener<MensagensDTO[]> listener, Response.ErrorListener errorListener) {
        GsonRequest request = new GsonRequest(Request.Method.GET, Constants.APP_URL + "chat/mensagens", null, MensagensDTO[].class,null, listener, errorListener);
        queue.add(request);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        queue = Volley.newRequestQueue(getApplicationContext());
        gsonInstance = new GsonBuilder().registerTypeAdapter(Date.class, (JsonDeserializer<Date>) (json, typeOfT, context) -> new Date(json.getAsJsonPrimitive().getAsLong()))
                .registerTypeAdapter(Date.class, (JsonSerializer<Date>) (date, type, jsonSerializationContext) -> new JsonPrimitive(date.getTime()))
                .create();
        sharedPreferences = getSharedPreferences(Constants.APP_PREFID, Context.MODE_PRIVATE);
        usuarioAtual = null;
    }
}

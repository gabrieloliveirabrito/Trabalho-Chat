package com.gobrito.trabalho_chat;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.util.DisplayMetrics;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatDelegate;

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
import java.util.Locale;


public class AppController extends Application {
    private static RequestQueue queue;
    private static Gson gsonInstance;
    private static Users usuarioAtual;
    private static Preferences preferences;

    public static Gson getGson() {
        return gsonInstance;
    }

    public static Users getUsuarioAtual() {
        return usuarioAtual;
    }

    public static void setUsuarioAtual(Users usuarioAtual) {
        AppController.usuarioAtual = usuarioAtual;
    }

    public static Preferences getPreferences() {
        return preferences;
    }

    public static void toggleDarkMode(Context context) {
        toggleDarkMode(context,true);
    }

    public static boolean toggleDarkMode(Context context, boolean apply) {
        Resources resources = context.getResources();
        int darkModeFlag = resources.getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;

        switch (darkModeFlag) {
            case Configuration.UI_MODE_NIGHT_YES:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

                if (apply)
                    preferences.setDarkMode(false);
                return true;
            case Configuration.UI_MODE_NIGHT_NO:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

                if (apply)
                    preferences.setDarkMode(true);
                return true;
            case Configuration.UI_MODE_NIGHT_UNDEFINED:
                Toast.makeText(context, R.string.app_nightmode_unsupported, Toast.LENGTH_SHORT).show();
                return false;
            default:
                Toast.makeText(context, R.string.app_nightmode_unknown, Toast.LENGTH_SHORT).show();
                return false;
        }
    }

    public static boolean getDarkMode(Context context) {
        Resources resources = context.getResources();
        return (resources.getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static void changeLocale(Context context) {
        changeLocale(context, preferences.getLocale(), false);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static void changeLocale(Context context, String locale) {
        changeLocale(context, locale, true);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static void changeLocale(Context context, String locale, boolean apply) {
        Resources resources = context.getResources();

        DisplayMetrics dm = resources.getDisplayMetrics();
        Configuration conf = new Configuration();

        int countrySep = locale.indexOf("-");
        Locale loc;
        if(countrySep == -1)
            loc = new Locale(locale);
        else {
            String language = locale.substring(0, countrySep);
            String country = locale.substring(countrySep + 1);
            loc = new Locale(language, country);
        }
        Locale.setDefault(loc);

        conf.setLocale(loc);
        resources.updateConfiguration(conf, dm);

        if (apply)
            preferences.setLocale(locale);
    }

    public static void sendUserRegister(ClienteLogin data, Response.Listener<Users> listener, @Nullable Response.ErrorListener errorListener) {
        preferences.setLastUser(data);

        GsonRequest request = new GsonRequest(Request.Method.POST, Constants.APP_URL + "chat/registrar", data, ClienteLogin.class, Users.class, listener, errorListener);
        queue.add(request);
    }

    public static void sendGetUserInfo(int id, Response.Listener<Users> listener, @Nullable Response.ErrorListener errorListener) {
        GsonRequest request = new GsonRequest(Request.Method.GET, Constants.APP_URL + "chat/registrar/" + Integer.toString(id), Users.class, listener, errorListener);
        queue.add(request);
    }

    public static void sendChatMessage(String message, Response.Listener<MensagemEnvioDTO> listener, @Nullable Response.ErrorListener errorListener) {
        MensagemEnvioDTO mensagem = new MensagemEnvioDTO();
        mensagem.setMensagem(message);
        mensagem.setId(usuarioAtual.getId());

        GsonRequest request = new GsonRequest(Request.Method.POST, Constants.APP_URL + "chat/enviar", mensagem, MensagemEnvioDTO.class, listener, errorListener);
        queue.add(request);
    }

    public static void sendMessagesRequest(Response.Listener<MensagensDTO[]> listener, Response.ErrorListener errorListener) {
        GsonRequest request = new GsonRequest(Request.Method.GET, Constants.APP_URL + "chat/mensagens", MensagensDTO[].class, listener, errorListener);
        queue.add(request);
    }

    public static void sendUsersRequest(Response.Listener<Users[]> listener, Response.ErrorListener errorListener) {
        GsonRequest request = new GsonRequest(Request.Method.GET, Constants.APP_URL + "chat/contatos", Users[].class, listener, errorListener);
        queue.add(request);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Context context = getApplicationContext();

        queue = Volley.newRequestQueue(context);
        gsonInstance = new GsonBuilder().registerTypeAdapter(Date.class, (JsonDeserializer<Date>) (json, typeOfT, ctx) -> new Date(json.getAsJsonPrimitive().getAsLong()))
                .registerTypeAdapter(Date.class, (JsonSerializer<Date>) (date, type, jsonSerializationContext) -> new JsonPrimitive(date.getTime()))
                .create();
        preferences = new Preferences(context);
        usuarioAtual = null;
    }
}

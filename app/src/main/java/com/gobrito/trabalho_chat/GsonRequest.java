package com.gobrito.trabalho_chat;

import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.JsonSyntaxException;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class GsonRequest<TIn, TOut> extends Request<TOut> {
    private final Class<TIn> dataInClass;
    private final Class<TOut> dataOutClass;
    private final Map<String, String> headers;
    private final Response.Listener<TOut> listener;
    private final TIn dataIn;

    public GsonRequest(int method, String url, TIn dataIn, Class<TIn> dataInClass, Class<TOut> dataOutClass, Map<String, String> headers,
                       Response.Listener<TOut> listener, Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        this.dataIn = dataIn;
        this.dataInClass = dataInClass;
        this.dataOutClass = dataOutClass;
        this.headers = headers;
        this.listener = listener;
    }

    public GsonRequest(int method, String url, TIn dataIn, Class<TIn> dataInClass, Class<TOut> dataOutClass,
                       Response.Listener<TOut> listener, Response.ErrorListener errorListener) {
        this(method, url, dataIn, dataInClass, dataOutClass, null, listener, errorListener);
    }

    public GsonRequest(int method, String url, Class<TOut> dataOutClass, Map<String, String> headers,
                        Response.Listener<TOut> listener, Response.ErrorListener errorListener) {
        this(method, url, null, null, dataOutClass, headers, listener, errorListener);
    }

    public GsonRequest(int method, String url, Class<TOut> dataOutClass,
                       Response.Listener<TOut> listener, Response.ErrorListener errorListener) {
        this(method, url, null, null, dataOutClass, null, listener, errorListener);
    }

    public GsonRequest(int method, String url, TIn dataIn, Class<TIn> dataInClass, Map<String, String> headers,
                       Response.Listener<TOut> listener, Response.ErrorListener errorListener) {
        this(method, url, dataIn, dataInClass, null, headers, listener, errorListener);
    }

    public GsonRequest(int method, String url, TIn dataIn, Class<TIn> dataInClass,
                       Response.Listener<TOut> listener, Response.ErrorListener errorListener) {
        this(method, url, dataIn, dataInClass, null, null, listener, errorListener);
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headersToSent = new HashMap<String, String>(headers != null ? headers : super.getHeaders());
        headersToSent.put("content-type", "application/json");
        headersToSent.put("charset", "utf-8");

        return headersToSent;
    }

    @Override
    protected void deliverResponse(TOut response) {
        listener.onResponse(response);
    }

    @Override
    public byte[] getBody() {
        if(dataIn != null) {
            String json = AppController.getGson().toJson(dataIn, dataInClass);
            return json.getBytes();
        } else return null;
    }

    @Override
    protected Response<TOut> parseNetworkResponse(NetworkResponse response) {
        try {
            if(dataOutClass != null) {
                String json = new String(response.data, "UTF-8");
                return Response.success(
                        AppController.getGson().fromJson(json, dataOutClass),
                        HttpHeaderParser.parseCacheHeaders(response));
            } else {
                return Response.success(null, HttpHeaderParser.parseCacheHeaders(response));
            }
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JsonSyntaxException e) {
            return Response.error(new ParseError(e));
        }
    }
}
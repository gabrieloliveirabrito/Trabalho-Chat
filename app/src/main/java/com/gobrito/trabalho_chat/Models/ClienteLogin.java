package com.gobrito.trabalho_chat.Models;

import org.json.JSONObject;

public class ClienteLogin extends JSONObject {
    private String nome, email;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

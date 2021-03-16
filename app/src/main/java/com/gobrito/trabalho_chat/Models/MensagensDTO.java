package com.gobrito.trabalho_chat.Models;

import java.util.Date;

public class MensagensDTO {
    private int Id;
    private String mensagem, name;
    private Date sentAt;
    private int users_id;

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getSentAt() {
        return sentAt;
    }

    public void setSentAt(Date sentAt) {
        this.sentAt = sentAt;
    }

    public int getUsers_id() {
        return users_id;
    }

    public void setUsers_id(int users_id) {
        this.users_id = users_id;
    }
}

package com.gobrito.trabalho_chat.Models;

import java.util.Date;

public class MensagensDTO {
    private int Id;
    private String message, name;
    private Date sentat;
    private int users_id;

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getMensagem() {
        return message;
    }

    public void setMensagem(String mensagem) {
        this.message = mensagem;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getSentAt() {
        return sentat;
    }

    public void setSentAt(Date sentAt) {
        this.sentat = sentAt;
    }

    public int getUsers_id() {
        return users_id;
    }

    public void setUsers_id(int users_id) {
        this.users_id = users_id;
    }
}

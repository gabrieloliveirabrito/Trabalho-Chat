package com.gobrito.trabalho_chat.Models;

public class MensagemEnvioDTO {
    private int id;
    private String mensagem;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }
}

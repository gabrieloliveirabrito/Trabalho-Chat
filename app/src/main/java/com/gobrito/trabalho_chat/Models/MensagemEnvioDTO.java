package com.gobrito.trabalho_chat.Models;

public class MensagemEnvioDTO {
    private int Id;
    private String mensagem;

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
}

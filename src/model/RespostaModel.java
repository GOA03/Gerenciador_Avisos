package model;

import java.util.List;

public class RespostaModel {

    private String operacao;
    private String ra;
    private String senha;
    private String nome;
    private int status;
    private String token; 
    private String msg;
    private List<UsuarioModel> usuarios;

    public String getMsg() {
        return msg;
    }
    public void setMsg(String msg) {
        this.msg = msg;
    }
    public String getToken() {
        return token;
    }
    public void setToken(String token) { 
        this.token = token;
    }
    public int getStatus() {
        return status;
    }
    public void setStatus(int status) {
        this.status = status;
    }
    public String getOperacao() {
        return operacao;
    }
    public void setOperacao(String operacao) {
        this.operacao = operacao;
    }
    public String getRa() {
        return ra;
    }
    public void setRa(String ra) { 
        this.ra = ra;
    }
    public String getSenha() {
        return senha;
    }
    public void setSenha(String senha) {
        this.senha = senha;
    }
    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }

    public List<UsuarioModel> getUsuarios() {
        return usuarios; // Getter para a lista de usuários
    }

    public void setUsuarios(List<UsuarioModel> usuarios) {
        this.usuarios = usuarios; // Setter para a lista de usuários
    }
}
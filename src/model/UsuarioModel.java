package model;

public class UsuarioModel {

    private String ra; 
    private String token;
    private String senha;
    private String nome;
    private String operacao;
    private boolean admin;
    
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
    
    public String getOperacao() {
        return operacao;
    }
    public void setOperacao(String operacao) {
        this.operacao = operacao;
    }
    
    public void setToken(String token) {
    	this.token = token;
    }
    
    public String getToken() {
		return token;
	}
    
    public boolean isAdmin() {
    	return admin;
    }
    public void setAdmin(boolean admin) {
    	this.admin = admin;
    }
    
	@Override
    public String toString() {
        return "UsuarioModel [ra=" + ra + ", senha=" + senha + ", nome=" + nome + ", operacao=" + operacao + "]";
    }
}
package model;

public class UsuarioModel {

    private String ra; 
    private String token;
    private String senha;
    private String nome;
    private String operacao;
    private boolean logado;
    
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
    
    public boolean isLogado() {
		return logado;
	}
    public void setLogado(boolean logado) {
		this.logado = logado;
	}
	@Override
	public String toString() {
		return "UsuarioModel [ra=" + ra + ", token=" + token + ", senha=" + senha + ", nome=" + nome + ", operacao="
				+ operacao + ", logado=" + logado + "]";
	}
	
	
}
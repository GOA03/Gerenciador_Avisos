package enums;

public enum LoginEnum {
    SUCESSO(200, "Login bem-sucedido."),
    ERRO_USUARIO_E_SENHA(401, "Credenciais incorretas."),
    ERRO_JSON(401, "Não foi possível ler o json recebido."),
    ERRO_VALIDACAO(401, "Os campos recebidos não são válidos."),
    ERRO_BANCO(401, "O servidor nao conseguiu conectar com o banco de dados.");

    private final int valor;
    private final String descricao;

    LoginEnum(int valor, String descricao) {
        this.valor = valor;
        this.descricao = descricao;
    }

    public int getValor() {
        return valor;
    }

    public String getDescricao() {
        return descricao;
    }
}
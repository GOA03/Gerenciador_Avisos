package enums;

public enum RaEnum {

    JA_CADASTRADO(1),
    CARACTERES_INVALIDOS(2),
    SUCESSO(3),
    ERRO(4),
    NAO_ENCONTRADO(5);
    
    private final int valor;

    // Construtor
    RaEnum(int valor) {
        this.valor = valor;
    }

    // Getter para acessar o valor
    public int getValor() {
        return valor;
    }
}

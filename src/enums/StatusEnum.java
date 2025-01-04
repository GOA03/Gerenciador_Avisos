package enums;

public enum StatusEnum {
	SUCESSO(200),
	LOGIN_INCORRETO(401),
    LOGOUT(204);
	
	private final int valor;
	
	StatusEnum(int valor) {
		this.valor = valor;
	}

	public int getValor() {
		return valor;
	}
}
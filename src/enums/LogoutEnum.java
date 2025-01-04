package enums;

public enum LogoutEnum {
	SUCESSO(200),
	ERRO_LOGOUT(401);

	private final int valor;
	
	LogoutEnum(int valor) {
		this.valor = valor;
	}

	public int getValor() {
		return valor;
	}
}

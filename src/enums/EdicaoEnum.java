package enums;

public enum EdicaoEnum {
	SUCESSO(1),
	ERRO(2);
	
private final int valor;
	
	EdicaoEnum(int valor) {
		this.valor = valor;
	}

	public int getValor() {
		return valor;
	}
}
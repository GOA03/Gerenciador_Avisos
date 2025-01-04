package enums;

public enum CadastroEnum {
	
	SUCESSO(201),
	ERRO(401),
	RA_CADASTRADO(422); 
	
	private final int valor;
	
	CadastroEnum(int valor) {
		this.valor = valor;
	}

	public int getValor() {
		return valor;
	}
}
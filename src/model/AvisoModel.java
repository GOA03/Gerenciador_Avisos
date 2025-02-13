package model;

public class AvisoModel {
	
    private int id;
    private CategoriaModel categoria;
    private String titulo;
    private String descricao;
    
	public AvisoModel() {}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public CategoriaModel getCategoria() {
		return categoria;
	}

	public void setCategoria(CategoriaModel categoria) {
		this.categoria = categoria;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Override
	public String toString() {
		return "AvisoModel [id=" + id + ", categoria=" + categoria + ", titulo=" + titulo + ", descricao=" + descricao
				+ "]";
	}
}
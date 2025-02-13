package model;

public class CategoriaModel {

    private int id; // Identificador único da categoria
    private String nome; // Nome da categoria

    // Construtor padrão
    public CategoriaModel() {
    }

    // Construtor com parâmetros
    public CategoriaModel(int id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    // Getter e Setter para o campo id
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    // Getter e Setter para o campo nome
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    @Override
    public String toString() {
        return "CategoriaModel [id=" + id + ", nome=" + nome + "]";
    }
}
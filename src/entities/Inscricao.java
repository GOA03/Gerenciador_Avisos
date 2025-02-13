package entities;

public class Inscricao {
    private int id;
    private String raUsuario; // Referência ao RA do usuário
    private int idCategoria; // Referência à categoria

    public Inscricao() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRaUsuario() {
        return raUsuario;
    }

    public void setRaUsuario(String raUsuario) {
        this.raUsuario = raUsuario;
    }

    public int getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(int idCategoria) {
        this.idCategoria = idCategoria;
    }
}
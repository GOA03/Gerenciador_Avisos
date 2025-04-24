package model;

import java.util.List;

public class RespostaModel {

    private String operacao;
    private String ra;
    private String senha; 
    private String nome;
    private int status;
    private String token; 
    private String msg;
    private UsuarioModel usuario;
    private List<UsuarioModel> usuarios;
    private List<CategoriaModel> categorias;
    private CategoriaModel categoria;
    private List<AvisoModel> avisos;
    private AvisoModel aviso;
    private int idCategoria;
    private int categoriaId;
    private List<Integer> idsCategorias;
    private String nomeCategoria;
    private int id;

    // Getters e Setters
    public String getOperacao() { return operacao; }
    public void setOperacao(String operacao) { this.operacao = operacao; }

    public String getRa() { return ra; }
    public void setRa(String ra) { this.ra = ra; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getMsg() { return msg; }
    public void setMsg(String msg) { this.msg = msg; }

    public UsuarioModel getUsuario() { return usuario; }
    public void setUsuario(UsuarioModel usuario) { this.usuario = usuario; }

    public List<UsuarioModel> getUsuarios() { return usuarios; }
    public void setUsuarios(List<UsuarioModel> usuarios) { this.usuarios = usuarios; }

    public List<CategoriaModel> getCategorias() { return categorias; }
    public void setCategorias(List<CategoriaModel> categorias) { this.categorias = categorias; }

    public CategoriaModel getCategoria() { return categoria; }
    public void setCategoria(CategoriaModel categoria) { this.categoria = categoria; }

    public List<AvisoModel> getAvisos() { return avisos; }
    public void setAvisos(List<AvisoModel> avisos) { this.avisos = avisos; }
    
    public AvisoModel getAviso() { return aviso; }
    public void setAviso(AvisoModel aviso) { this.aviso = aviso; }

    public int getIdCategoria() { return idCategoria; }
    public void setIdCategoria(int idCategoria) { this.idCategoria = idCategoria; }
    
    public int getCategoriaId() { return categoriaId; }
    public void setCategoriaId(int categoriaId) { this.categoriaId = categoriaId; }
    
    public List<Integer> getIdsCategorias() { return idsCategorias; }
    public void setIdsCategorias(List<Integer> idsCategorias) { this.idsCategorias = idsCategorias; }
    
    public String getNomeCategoria() { return nomeCategoria; }
    public void setNomeCategoria(String nomeCategoria) { this.nomeCategoria = nomeCategoria; }
    
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("RespostaModel [");

        if (operacao != null) sb.append("operacao=").append(operacao).append(", ");
        if (ra != null) sb.append("ra=").append(ra).append(", ");
        if (senha != null) sb.append("senha=").append(senha).append(", ");
        if (nome != null) sb.append("nome=").append(nome).append(", ");
        if (status != 0) sb.append("status=").append(status).append(", ");
        if (token != null) sb.append("token=").append(token).append(", ");
        if (msg != null) sb.append("msg=").append(msg).append(", ");
        if (usuario != null) sb.append("usuario=").append(usuario).append(", ");
        if (usuarios != null) sb.append("usuarios=").append(usuarios).append(", ");
        if (categorias != null) sb.append("categorias=").append(categorias).append(", ");
        if (categoria != null) sb.append("categoria=").append(categoria).append(", ");
        if (idsCategorias != null) sb.append("idsCategorias=").append(idsCategorias).append(", ");
        if (idCategoria >= 0) sb.append("idCategoria=").append(idCategoria).append(", "); // Modificação aqui
        if (avisos != null) sb.append("avisos=").append(avisos).append(", ");
        if (aviso != null) sb.append("aviso=").append(aviso).append(", ");
        if (id != 0) sb.append("id=").append(id).append(", ");

        // Remove the last comma and space, if present
        int length = sb.length();
        if (length > 2) {
            sb.setLength(length - 2);  // Removes the trailing ", "
        }

        sb.append("]");
        return sb.toString();
    }
}
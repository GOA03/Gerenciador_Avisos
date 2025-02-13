package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import entities.Inscricao;
import model.CategoriaModel;

public class InscricaoDAO {
    private Connection conn;

    public InscricaoDAO(Connection conn) {
        this.conn = conn;
    }

    public int cadastrar(Inscricao inscricao) throws SQLException {
        String sql = "INSERT INTO inscricao (raUsuario, idCategoria) VALUES (?, ?)";
        try (PreparedStatement st = conn.prepareStatement(sql)) {
            st.setString(1, inscricao.getRaUsuario());
            st.setInt(2, inscricao.getIdCategoria());
            return st.executeUpdate();
        }
    }

    public List<Inscricao> buscarTodos() throws SQLException {
        String sql = "SELECT * FROM inscricao";
        List<Inscricao> listaInscricoes = new ArrayList<>();
        try (PreparedStatement st = conn.prepareStatement(sql);
             ResultSet rs = st.executeQuery()) {
            while (rs.next()) {
                Inscricao inscricao = new Inscricao();
                inscricao.setId(rs.getInt("id"));
                inscricao.setRaUsuario(rs.getString("raUsuario"));
                inscricao.setIdCategoria(rs.getInt("idCategoria"));
                listaInscricoes.add(inscricao);
            }
        }
        return listaInscricoes;
    }
    
    public List<Integer> buscarCategoriasPorRA(String ra) throws SQLException {
        String sql = "SELECT idCategoria FROM inscricao WHERE raUsuario = ?";
        List<Integer> listaCategorias = new ArrayList<>();
        try (PreparedStatement st = conn.prepareStatement(sql)) {
            st.setString(1, ra);
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    listaCategorias.add(rs.getInt("idCategoria"));
                }
            }
        }
        return listaCategorias;
    }

    public int remover(int id) throws SQLException {
        String sql = "DELETE FROM inscricao WHERE id = ?";
        try (PreparedStatement st = conn.prepareStatement(sql)) {
            st.setInt(1, id);
            return st.executeUpdate();
        }
    }

	public boolean usuarioJaInscrito(String ra, int idCategoria) {
        String sql = "SELECT COUNT(*) FROM inscricao WHERE raUsuario = ? AND idCategoria = ?";
        try (PreparedStatement st = conn.prepareStatement(sql)) {
            st.setString(1, ra);
            st.setInt(2, idCategoria);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0; // Retorna true se o usuário já estiver inscrito
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Retorna false se ocorrer um erro ou se não estiver inscrito
    }
}
package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.AvisoModel;
import model.CategoriaModel;

public class AvisoDAO {

    private Connection conn;

    public AvisoDAO(Connection conn) {
        this.conn = conn;
    }

    // Método para adicionar um novo aviso
    public void adicionarAviso(AvisoModel aviso) throws SQLException {
        String query = "INSERT INTO aviso (idCategoria, titulo, descricao) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, aviso.getCategoria().getId());
            stmt.setString(2, aviso.getTitulo());
            stmt.setString(3, aviso.getDescricao());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Erro ao adicionar o aviso", e);
        }
    }

    // Método para buscar uma categoria pelo ID
    public CategoriaModel getCategoriaPorId(int id) throws SQLException {
        CategoriaModel categoria = null;
        String query = "SELECT id, nome FROM categorias WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                categoria = new CategoriaModel();
                categoria.setId(rs.getInt("id"));
                categoria.setNome(rs.getString("nome"));
            }
        } catch (SQLException e) {
            throw new SQLException("Erro ao buscar categoria pelo ID", e);
        }

        return categoria;
    }

    // Método para listar todos os avisos
    public List<AvisoModel> listarAvisos() throws SQLException {
        List<AvisoModel> avisos = new ArrayList<>();
        String query = "SELECT a.id, a.titulo, a.descricao, a.idCategoria, c.nome AS nomeCategoria " +
                       "FROM aviso a " +
                       "JOIN categorias c ON a.idCategoria = c.id";

        try (PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                CategoriaModel categoria = new CategoriaModel();
                categoria.setId(rs.getInt("idCategoria"));
                categoria.setNome(rs.getString("nomeCategoria"));

                AvisoModel aviso = new AvisoModel();
                aviso.setId(rs.getInt("id"));
                aviso.setTitulo(rs.getString("titulo"));
                aviso.setDescricao(rs.getString("descricao"));
                aviso.setCategoria(categoria);
                avisos.add(aviso);
            }
        } catch (SQLException e) {
            throw new SQLException("Erro ao listar avisos", e);
        }

        return avisos;
    }

    // Método para listar avisos por categoria
    public List<AvisoModel> listarAvisosPorCategoria(int categoriaId) throws SQLException {
        List<AvisoModel> avisos = new ArrayList<>();
        String query = "SELECT a.id, a.titulo, a.descricao, a.idCategoria, c.nome FROM aviso a JOIN categorias c ON a.idCategoria = c.id WHERE a.idCategoria = ?";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, categoriaId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                CategoriaModel categoria = new CategoriaModel();
                categoria.setId(rs.getInt("idCategoria"));
                categoria.setNome(rs.getString("nome"));

                AvisoModel aviso = new AvisoModel();
                aviso.setId(rs.getInt("id"));
                aviso.setTitulo(rs.getString("titulo"));
                aviso.setDescricao(rs.getString("descricao"));
                aviso.setCategoria(categoria);
                avisos.add(aviso);
            }
        } catch (SQLException e) {
            throw new SQLException("Erro ao listar avisos por categoria", e);
        }

        return avisos;
    }

    // Método para localizar um aviso pelo ID
    public AvisoModel getAvisoPorId(int id) throws SQLException {
        AvisoModel aviso = null;
        String query = "SELECT a.id, a.titulo, a.descricao, a.idCategoria, c.nome FROM aviso a JOIN categorias c ON a.idCategoria = c.id WHERE a.id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                CategoriaModel categoria = new CategoriaModel();
                categoria.setId(rs.getInt("idCategoria"));
                categoria.setNome(rs.getString("nome"));

                aviso = new AvisoModel();
                aviso.setId(rs.getInt("id"));
                aviso.setTitulo(rs.getString("titulo"));
                aviso.setDescricao(rs.getString("descricao"));
                aviso.setCategoria(categoria);
            }
        } catch (SQLException e) {
            throw new SQLException("Erro ao buscar aviso pelo ID", e);
        }

        return aviso;
    }

    // Método para atualizar um aviso
    public void atualizarAviso(AvisoModel aviso) throws SQLException {
        String query = "UPDATE aviso SET idCategoria = ?, titulo = ?, descricao = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, aviso.getCategoria().getId());
            stmt.setString(2, aviso.getTitulo());
            stmt.setString(3, aviso.getDescricao());
            stmt.setInt(4, aviso.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Erro ao atualizar o aviso", e);
        }
    }

    // Método para excluir um aviso
    public void excluirAviso(int id) throws SQLException {
        String query = "DELETE FROM aviso WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Erro ao excluir o aviso", e);
        }
    }
}

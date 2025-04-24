package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.CategoriaModel;

public class CategoriaDAO {

    private Connection conn;

    public CategoriaDAO(Connection conn) {
        this.conn = conn;
    }

    // Método para adicionar uma nova categoria
    public void adicionarCategoria(CategoriaModel categoria) throws SQLException {
        String query = "INSERT INTO categorias (nome) VALUES (?)";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, categoria.getNome());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Erro ao adicionar a categoria", e);
        }
    }

    // Método para listar todas as categorias
    public List<CategoriaModel> listarCategorias() throws SQLException {
        List<CategoriaModel> categorias = new ArrayList<>();
        String query = "SELECT id, nome FROM categorias";

        try (PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                CategoriaModel categoria = new CategoriaModel();
                categoria.setId(rs.getInt("id"));
                categoria.setNome(rs.getString("nome").toUpperCase());
                categorias.add(categoria);
            }
        } catch (SQLException e) {
            throw new SQLException("Erro ao listar categorias", e);
        }

        return categorias;
    }

    // Método para localizar uma categoria pelo ID
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
    
    public CategoriaModel getCategoriaPorNome(String nome) throws SQLException {
        CategoriaModel categoria = null;
        String query = "SELECT id, nome FROM categorias WHERE nome = ?";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, nome);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                categoria = new CategoriaModel();
                categoria.setId(rs.getInt("id"));
                categoria.setNome(rs.getString("nome"));
            }
        } catch (SQLException e) {
            throw new SQLException("Erro ao buscar categoria pelo nome", e);
        }

        return categoria;
    }

    // Método para atualizar uma categoria
    public void atualizarCategoria(CategoriaModel categoria) throws SQLException {
        String query = "UPDATE categorias SET nome = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, categoria.getNome());
            stmt.setInt(2, categoria.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Erro ao atualizar a categoria", e);
        }
    }

    // Método para remover uma categoria
    public void removerCategoria(int id) throws SQLException {
        String query = "DELETE FROM categorias WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Erro ao remover a categoria", e);
        }
    }
}
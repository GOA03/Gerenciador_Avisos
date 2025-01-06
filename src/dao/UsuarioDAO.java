package dao;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.UsuarioModel;

public class UsuarioDAO {

	private Connection conn;

	public UsuarioDAO(Connection conn) {
		this.conn = conn;
	}
	
	// Método para listar todos os usuários com tratamento de exceções
	public List<UsuarioModel> listarUsuarios() {
	    List<UsuarioModel> usuarios = new ArrayList<>();
	    String query = "SELECT ra, senha, nome FROM usuario"; // Consulta para obter todos os usuários

	    try (PreparedStatement stmt = conn.prepareStatement(query);
	         ResultSet rs = stmt.executeQuery()) {

	        while (rs.next()) {
	            try {
	                UsuarioModel usuario = new UsuarioModel();
	                usuario.setRa(rs.getString("ra")); // Obtém o RA do usuário
	                usuario.setSenha(rs.getString("senha")); // Obtém a senha do usuário
	                usuario.setNome(rs.getString("nome")); // Obtém o nome do usuário
	                usuarios.add(usuario); // Adiciona o usuário à lista
	            } catch (SQLException e) {
	                System.err.println("Erro ao processar um registro do ResultSet: " + e.getMessage());
	            } catch (Exception e) {
	                System.err.println("Erro inesperado ao processar um registro: " + e.getMessage());
	            }
	        }

	    } catch (SQLException e) {
	        System.err.println("Erro ao executar a consulta SQL: " + e.getMessage());
	    } catch (NullPointerException e) {
	        System.err.println("Erro: Conexão com o banco de dados não inicializada. " + e.getMessage());
	    } catch (Exception e) {
	        System.err.println("Erro inesperado ao listar usuários: " + e.getMessage());
	    }

	    return usuarios; // Retorna a lista de usuários (possivelmente vazia)
	}


	// Método para adicionar um usuário no banco
    public void adicionarUsuario(UsuarioModel usuario) throws SQLException, IOException {
        
        PreparedStatement st = null;
        String query = "INSERT INTO usuario (ra, senha, nome) VALUES (?, ?, ?)";
        
        try {
            st = conn.prepareStatement(query); // Usando a conexão passada no construtor
            
            st.setString(1, usuario.getRa()); // Setando o usuario
            st.setString(2, usuario.getSenha()); // Setando a senha
            st.setString(3, usuario.getNome()); // Setando o nome

            st.executeUpdate(); // Executando a inserção

        } catch (SQLException e) {
            throw new SQLException("Erro ao adicionar o usuário", e);
        } finally {
            BancoDados.finalizarStatement(st);
        }
    }

    // Método para remover um usuário
    public void removerUsuario(UsuarioModel usuario) throws SQLException, IOException {
        String query = "DELETE FROM usuario WHERE ra = ?";
        Connection conn = null; // Declaração da conexão fora do bloco try

        try {
            conn = BancoDados.conectar(); // Usando a conexão do BancoDados
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, usuario.getRa());
            stmt.executeUpdate(); // Executando a remoção
        } catch (SQLException e) {
            throw new SQLException("Erro ao remover o usuário", e);
        } finally {
            if (conn != null) {
                BancoDados.desconectar(); // Fecha a conexão no bloco finally
            }
        }
    }

	// Método para atualizar as informações de um usuário
	public void atualizarUsuario(UsuarioModel usuario) throws SQLException, IOException {
		String query = "UPDATE usuario SET senha = ?, nome = ? WHERE ra = ?";

		try (Connection conn = BancoDados.conectar(); // Usando a conexão do BancoDados
				PreparedStatement stmt = conn.prepareStatement(query)) {

			stmt.setString(1, usuario.getSenha()); // Setando a nova senha
			stmt.setString(2, usuario.getNome()); // Setando o novo nome
			stmt.setString(3, usuario.getRa()); // Setando o novo nome RA

			stmt.executeUpdate(); // Executando a atualização

		} catch (SQLException e) {
			throw new SQLException("Erro ao atualizar o usuário", e);
		}
	}

	public Boolean loginUsuario(String ra, String senha) {
	    String query = "SELECT * FROM usuario WHERE ra = ? AND senha = ?";
	    if (conn == null) {
	        // Se a conexão é nula, exiba uma mensagem de erro e retorne false
	        System.err.println("Erro: Conexão com o banco de dados não foi estabelecida.");
	        return false;
	    }
	    
	    try (PreparedStatement stmt = conn.prepareStatement(query)) {
	        stmt.setString(1, ra);
	        stmt.setString(2, senha);
	        ResultSet rs = stmt.executeQuery();
	        return rs.next(); // Retorna true se houver um registro correspondente
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false; // Retorna false em caso de erro
	    }
	}

	public Connection getConn() {
		return conn;
	}

	public String getRA(String ra) throws SQLException {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement("SELECT * FROM usuario WHERE ra = ?");
			st.setString(1, ra);
			rs = st.executeQuery();
			if (rs.next()) {
				String token = rs.getString("ra");
				return token;
			} else {
				System.out.println("Ra Não Encontrado: " + ra);
				return null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		} finally {
			BancoDados.finalizarStatement(st);
			BancoDados.desconectar();
		}
	}

	public Boolean validarRa(String ra) throws SQLException {

		PreparedStatement st = null;
		ResultSet rs = null;

		try {

			st = conn.prepareStatement("SELECT * FROM usuario WHERE ra = ?");
			st.setString(1, ra);
			rs = st.executeQuery();

			if (rs.next()) {
				return false;
			} else {
				return true;
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
			
		} finally {
			
			BancoDados.finalizarStatement(st);
			BancoDados.desconectar();
		}
	}
}
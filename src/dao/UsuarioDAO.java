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

	public UsuarioModel getUsuarioPorRa(String ra) throws SQLException {
		UsuarioModel usuario = null;
		String query = "SELECT ra, senha, nome, logado FROM usuario WHERE ra = ?";

		try (PreparedStatement stmt = conn.prepareStatement(query)) {
			stmt.setString(1, ra);
			ResultSet rs = stmt.executeQuery();

			if (rs.next()) {
				usuario = new UsuarioModel();
				usuario.setRa(rs.getString("ra"));
				usuario.setSenha(rs.getString("senha"));
				usuario.setNome(rs.getString("nome"));
				usuario.setLogado(rs.getBoolean("logado"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new SQLException("Erro ao buscar usuário pelo RA: " + e.getMessage());
		}

		return usuario;
	}

	public List<UsuarioModel> listarUsuarios() {
		List<UsuarioModel> usuarios = new ArrayList<>();
		String query = "SELECT ra, senha, nome FROM usuario";

		try (PreparedStatement stmt = conn.prepareStatement(query); ResultSet rs = stmt.executeQuery()) {

			while (rs.next()) {
				try {
					UsuarioModel usuario = new UsuarioModel();
					usuario.setRa(rs.getString("ra"));
					usuario.setSenha(rs.getString("senha"));
					usuario.setNome(rs.getString("nome"));
					usuarios.add(usuario);
				} catch (SQLException e) {
					System.err.println("Erro ao processar registro: " + e.getMessage());
				}
			}
		} catch (SQLException e) {
			System.err.println("Erro ao listar usuários: " + e.getMessage());
		}

		return usuarios;
	}

	public void adicionarUsuario(UsuarioModel usuario) throws SQLException {
	    String query = "INSERT INTO usuario (ra, senha, nome, logado) VALUES (?, ?, ?, ?)";
	    try (PreparedStatement st = conn.prepareStatement(query)) {
	        st.setString(1, usuario.getRa());
	        st.setString(2, usuario.getSenha());
	        st.setString(3, usuario.getNome());
	        st.setBoolean(4, false);
	        st.executeUpdate();
	    } catch (SQLException e) {
	        System.err.println("Erro ao adicionar o usuário: " + e.getMessage());
	        throw new SQLException("Erro ao adicionar o usuário", e);
	    }
	}

	public void removerUsuario(UsuarioModel usuario) throws SQLException, IOException {
		String query = "DELETE FROM usuario WHERE ra = ?";

		try (PreparedStatement stmt = conn.prepareStatement(query)) {
			stmt.setString(1, usuario.getRa());
			stmt.executeUpdate();
		} catch (SQLException e) {
			throw new SQLException("Erro ao remover o usuário", e);
		}
	}

	public void atualizarUsuario(UsuarioModel usuario) throws SQLException, IOException {
		String query = "UPDATE usuario SET senha = ?, nome = ? WHERE ra = ?";

		try (PreparedStatement stmt = conn.prepareStatement(query)) {
			stmt.setString(1, usuario.getSenha());
			stmt.setString(2, usuario.getNome());
			stmt.setString(3, usuario.getRa());
			stmt.executeUpdate();
		} catch (SQLException e) {
			throw new SQLException("Erro ao atualizar o usuário", e);
		}
	}

	public Boolean loginUsuario(String ra, String senha) {
		String query = "SELECT * FROM usuario WHERE ra = ? AND senha = ?";
		if (conn == null) {
			System.err.println("Erro: Conexão com o banco de dados não foi estabelecida.");
			return false;
		}

		try (PreparedStatement stmt = conn.prepareStatement(query)) {
			stmt.setString(1, ra);
			stmt.setString(2, senha);
			ResultSet rs = stmt.executeQuery();
			return rs.next();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public Boolean verificarLogado(String ra) {
		String query = "SELECT logado FROM usuario WHERE ra = ?";

		try (PreparedStatement stmt = conn.prepareStatement(query)) {
			stmt.setString(1, ra);
			ResultSet rs = stmt.executeQuery();

			if (rs.next()) {
				return rs.getBoolean("logado");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return false; // Retorna falso caso não encontre o usuário ou em caso de erro
	}

	public String getRA(String ra) throws SQLException {
		
		String query = "SELECT ra FROM usuario WHERE ra = ?";
		
		try (PreparedStatement stmt = conn.prepareStatement(query)) {
			stmt.setString(1, ra);
			ResultSet rs = stmt.executeQuery();
			
			if (rs.next()) {
				return rs.getString("ra");
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
			throw e; // Propaga a exceção
		}
		return null;
	}

	public Boolean validarRa(String ra) throws SQLException {
	    String query = "SELECT ra FROM usuario WHERE ra = ?";
	    try (PreparedStatement stmt = conn.prepareStatement(query)) {
	        stmt.setString(1, ra);
	        ResultSet rs = stmt.executeQuery();
	        return !rs.next(); // Retorna true se o RA não existe
	    } catch (SQLException e) {
	        e.printStackTrace();
	        throw e; // Propaga a exceção
	    }
	}
	
	public void atualizarStatusLogin(String ra, boolean logado) throws SQLException {
	    String query = "UPDATE usuario SET logado = ? WHERE ra = ?";
	    try (PreparedStatement stmt = conn.prepareStatement(query)) {
	        stmt.setBoolean(1, logado);
	        stmt.setString(2, ra);
	        stmt.executeUpdate();
	    } catch (SQLException e) {
	        throw new SQLException("Erro ao atualizar o status de login do usuário", e);
	    }
	}
	
	public void deslogarTodosUsuarios() throws SQLException {
	    String query = "UPDATE usuario SET logado = false"; // Atualiza todos os usuários para deslogados
	    try (PreparedStatement stmt = conn.prepareStatement(query)) {
	        stmt.executeUpdate(); // Executa a atualização
	    } catch (SQLException e) {
	        throw new SQLException("Erro ao deslogar todos os usuários", e);
	    }
	}
	
	public List<UsuarioModel> listarLogados() throws SQLException {
	    List<UsuarioModel> usuariosLogados = new ArrayList<>();
	    String query = "SELECT ra, senha, nome FROM usuario WHERE logado = true";
	    
	    try (PreparedStatement stmt = conn.prepareStatement(query);
	         ResultSet rs = stmt.executeQuery()) {
	        while (rs.next()) {
	            UsuarioModel usuario = new UsuarioModel();
	            usuario.setRa(rs.getString("ra"));
	            usuario.setSenha(rs.getString("senha"));
	            usuario.setNome(rs.getString("nome"));
	            usuariosLogados.add(usuario);
	        }
	    } catch (SQLException e) {
	        throw new SQLException("Erro ao listar usuários logados", e);
	    }
	    
	    return usuariosLogados; // Retorna a lista de usuários logados
	}
}

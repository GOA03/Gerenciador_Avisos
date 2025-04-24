package controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.swing.JOptionPane;

import dao.BancoDados;
import dao.UsuarioDAO;
import enums.LoginEnum;
import model.UsuarioModel;

public class LoginController {

	public LoginEnum validarLogin(UsuarioModel usuario) throws IOException {
		if (usuario.getRa() == null || usuario.getSenha() == null) {
			return LoginEnum.ERRO_VALIDACAO;
		}

		try {

			Boolean isCorrectLogin = validarDados(usuario);

			if (isCorrectLogin) {

				if (verificarLogado(usuario.getRa())) {
					return LoginEnum.ERRO_USUARIO_LOGADO;
				}

				return LoginEnum.SUCESSO;

			} else {
				return LoginEnum.ERRO_USUARIO_E_SENHA;
			}

		} catch (SQLException e) {
			e.printStackTrace();
			return LoginEnum.ERRO_BANCO;
		} catch (IOException e) {
			e.printStackTrace();
			return LoginEnum.ERRO_JSON;
		} catch (Exception e) {
			e.printStackTrace();
			return LoginEnum.ERRO_VALIDACAO;
		}
	}

	private Boolean verificarLogado(String ra) throws SQLException, IOException {
		Connection conn = null;
		try {
			conn = BancoDados.conectar();
			UsuarioDAO usuarioDAO = new UsuarioDAO(conn);
			return usuarioDAO.verificarLogado(ra);
		} finally {
			if (conn != null) {
				BancoDados.desconectar();
			}
		}
	}

	public Boolean validarDados(UsuarioModel usuario) throws IOException, SQLException {
		Connection conn = null;
		try {
			conn = BancoDados.conectar();

			if (conn == null) {

				JOptionPane.showMessageDialog(null, "Conexao com o banco de dados falhou.", "Erro com o Banco de Dados",
						JOptionPane.ERROR_MESSAGE);
				 return false; 
			}

			String ra = usuario.getRa();
			String senha = usuario.getSenha();

			return new UsuarioDAO(conn).loginUsuario(ra, senha);
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (conn != null) {
				BancoDados.desconectar();
			}
		}
	}

	public String getRa(String ra) throws SQLException, IOException {
		Connection conn = null;
		try {
			conn = BancoDados.conectar();

			if (conn == null) {
				JOptionPane.showMessageDialog(null, "Conexao com o banco de dados falhou.", "Erro com o Banco de Dados",
						JOptionPane.ERROR_MESSAGE);
				return null;
			}

			String token = new UsuarioDAO(conn).getRA(ra);

			if (token == null) {
				System.out.println("TOKEN NULO");
			}

			return token;
		} finally {
			if (conn != null) {
				BancoDados.desconectar();
			}
		}
	}
}

package controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import dao.BancoDados;
import dao.UsuarioDAO;
import enums.LoginEnum;
import model.UsuarioModel;

public class LoginController {
    
	public LoginEnum validarLogin(UsuarioModel usuario) throws IOException {
	    // Verifica se os dados do usuário são válidos
	    if (usuario.getRa() == null || usuario.getSenha() == null) {
	        return LoginEnum.ERRO_VALIDACAO; // Campos inválidos
	    }

	    try {
	        Boolean isCorrectLogin = validarDados(usuario);
	        
	        // Se os dados estão corretos, retorna sucesso
	        if (isCorrectLogin) {
	            return LoginEnum.SUCESSO;
	        } else {
	            return LoginEnum.ERRO_USUARIO_E_SENHA; // Credenciais incorretas
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	        return LoginEnum.ERRO_JSON; // Erro ao processar o JSON
	    }
	}
    
    public Boolean validarDados(UsuarioModel usuario) throws IOException {
        try {
            Connection conn = BancoDados.conectar();
            String ra = usuario.getRa();
            String senha = usuario.getSenha();
            Boolean response = new UsuarioDAO(conn).loginUsuario(ra, senha);
            BancoDados.desconectar();
            return response;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String getRa(String ra) throws SQLException, IOException {
        Connection conn = BancoDados.conectar();
        String token = new UsuarioDAO(conn).getRA(ra); 
        if (token == null) {
            System.out.println("TOKEN NULO");
        }
        BancoDados.desconectar();
        
        return token;
    }
}
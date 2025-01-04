package dao;

import java.io.*;
import java.sql.*;
import java.util.Properties;

import javax.swing.JOptionPane;

public class BancoDados {

	private static Connection conn = null;
	
	public static Connection conectar() throws SQLException, IOException {
	    try {
	        if (conn == null) {
	            Properties props = carregarPropriedades();
	            String url = props.getProperty("dburl");
	            conn = DriverManager.getConnection(url, props);
	        }
	        return conn;
	    } catch (SQLException e) {
	        // Captura a exceção de comunicação
	        if (e instanceof com.mysql.cj.jdbc.exceptions.CommunicationsException) {
	            JOptionPane.showMessageDialog(null, "Erro de comunicação com o servidor MySQL. Verifique se o servidor está em execução e a configuração da conexão.");
	        } else {
	            String errorMessage = "Erro ao conectar ao banco de dados: " + e.getMessage();
	            JOptionPane.showMessageDialog(null, errorMessage, "Erro de Conexão", JOptionPane.ERROR_MESSAGE);
	        }
	        // Evita que a pilha de erro padrão seja exibida
	        System.err.println("Ocorreu um erro ao tentar conectar ao banco de dados. Por favor, verifique a conexão.");
	        return null; // Retorna null ou lance uma exceção personalizada, se necessário
	    }
	}
	
	public static Connection desconectar() throws SQLException{
		
		if (conn != null) {
			
			conn.close();
			conn = null;
		}
		
		return conn;
	}
	
	private static Properties carregarPropriedades() throws IOException{
		
		FileInputStream propriedadesBanco = new FileInputStream("database.properties");
		
		Properties props = new Properties();
		props.load(propriedadesBanco);
		
		return props;
	}
	
	public static void finalizarStatement(Statement st) throws SQLException{
		
		if (st != null) {
			
			st.close();
		}
	}
	
	public static void finalizarResultSet(ResultSet rs) throws SQLException{
		
		if (rs != null) {
			
			rs.close();
		}
	}
}

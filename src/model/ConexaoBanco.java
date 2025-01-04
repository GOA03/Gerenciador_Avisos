package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ConexaoBanco {
    
    private static Connection conexao;
    
    // Método para inicializar a conexão com o banco
    private static void conectarBanco() {
        try {
            if (conexao == null || conexao.isClosed()) {
                Class.forName("org.sqlite.JDBC");
                conexao = DriverManager.getConnection("jdbc:sqlite:banco.sqlite");
            }
        } catch (ClassNotFoundException e) {
            System.out.println("Classe JDBC do SQLite não encontrada.");
        } catch (SQLException e) {
            System.out.println("Erro ao conectar com o banco de dados: " + e.getMessage());
        }
    }
    
    // Método para fechar a conexão com o banco
    @SuppressWarnings("unused")
	private static void desconectarBanco() {
        if (conexao != null) {
            try {
                if (!conexao.isClosed()) {
                    conexao.close();
                }
            } catch (SQLException e) {
                System.out.println("Não foi possível fechar a conexão com o banco");
            }
        }
    }

    // Método público para obter a conexão
    public static Connection getConexao() throws SQLException {
        if (conexao == null || conexao.isClosed()) {
            conectarBanco();  // Estabelece a conexão caso ainda não tenha sido feita
        }
        return conexao;
    }

    // Método para limpar (excluir) a tabela 'usuario'
    public static void limparUsuarios() throws SQLException {
        Statement stm = null;
        try {
            conexao = getConexao();
            stm = conexao.createStatement();
            stm.executeUpdate("DROP TABLE IF EXISTS usuario");
        } finally {
            if (stm != null) {
                stm.close();
            }
        }
    }

    // Método para criar e inicializar o banco de dados
    public static void iniciarBD() {
        Statement stm = null;
        try {
            conexao = getConexao();
            stm = conexao.createStatement();

            // Exclui a tabela se ela existir e cria a nova tabela
            stm.executeUpdate("DROP TABLE IF EXISTS usuario");
            stm.executeUpdate("CREATE TABLE usuario (id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
                    + "ra INTEGER NOT NULL, "
                    + "senha TEXT NOT NULL, "
                    + "nome TEXT NOT NULL)");

            // Insere um usuário padrão
            stm.executeUpdate("INSERT INTO usuario (ra, senha, nome) VALUES ('admin', 'admin', 'admin')");

            System.out.println("Banco iniciado com sucesso!");
        } catch (SQLException e) {
            System.err.println("Não foi possível abrir a conexão com o banco: " + e.getMessage());
        } finally {
            if (stm != null) {
                try {
                    stm.close();
                } catch (SQLException e) {
                    System.out.println("Erro ao fechar Statement: " + e.getMessage());
                }
            }
        }
    }

    // Método para executar uma consulta de atualização no banco
    public static void alterarBD(String query) throws SQLException {
        Statement stm = null;
        try {
            conexao = getConexao();
            stm = conexao.createStatement();
            stm.executeUpdate(query);
        } finally {
            if (stm != null) {
                stm.close();
            }
        }
    }
}

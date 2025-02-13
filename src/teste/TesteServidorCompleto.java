package teste;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class TesteServidorCompleto {
    private static final String HOST = "127.0.0.1"; // Endereço IP do servidor
    private static final int PORT = 3; // Porta do servidor

    public static void main(String[] args) {
        try (Socket socket = new Socket(HOST, PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // Executa todos os testes
            executarTestes(out, in);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void executarTestes(PrintWriter out, BufferedReader in) {
        System.out.println("Iniciando testes no servidor...");

        // Testes de erro global
        System.out.println("\nTestes de Erro Global:");
        enviarMensagem(out, in, "{\"status\": 401, \"mensagem\": \"Não foi possível processar a requisição.\"}");
        enviarMensagem(out, in, "{\"status\": 401, \"mensagem\": \"Operação não encontrada.\"}");
        enviarMensagem(out, in, "{\"status\": 401, \"operacao\": \"login\", \"mensagem\": \"Falha ao processar requisição, ocorreu um erro interno no servidor.\"}");
        enviarMensagem(out, in, "{\"status\": 401, \"operacao\": \"login\", \"mensagem\": \"Acesso não autorizado.\"}");

        // Testes de login
        System.out.println("\nTestes de Login:");
        enviarMensagem(out, in, "{\"operacao\":\"login\", \"ra\":\"1234567\", \"senha\":\"senha123\"}"); // Login Válido
        enviarMensagem(out, in, "{\"operacao\":\"login\", \"ra\":\"12345\", \"senha\":\"senha123\"}"); // RA Inválido
        enviarMensagem(out, in, "{\"operacao\":\"login\", \"ra\":\"1234567\", \"senha\":\"senhaErrada\"}"); // Senha Inválida
        enviarMensagem(out, in, "{\"operacao\":\"login\", \"ra\":\"\", \"senha\":\"\"}"); // Campos Vazios
        enviarMensagem(out, in, "{\"operacao\":\"login\", \"ra\":\"1234567\", \"senha\":\"senha123\""); // JSON Malformado
        enviarMensagem(out, in, "{\"operacao\":\"login\", \"ra\":1234567, \"senha\":\"senha123\"}"); // RA como Número
        enviarMensagem(out, in, "{\"operacao\":\"login\", \"ra\":\"!@#$%^&*()\", \"senha\":\"senha123\"}"); // RA com Caracteres Especiais
        enviarMensagem(out, in, "{\"operacao\":\"login\", \"ra\":\"12345678901234567\", \"senha\":\"senha123\"}"); // RA Muito Longo
        enviarMensagem(out, in, "{\"operacao\":\"login\", \"ra\":\"1234567\", \"senha\":\"senha12345678901234567890\"}"); // Senha Muito Longa
        enviarMensagem(out, in, "{\"operacao\":\"login\", \"ra\":\"1234567\", \"senha\":\"<script>alert('XSS')</script>\"}"); // Injeção de Código

        // Testes de logout
        System.out.println("\nTestes de Logout:");
        enviarMensagem(out, in, "{\"operacao\":\"logout\", \"token\":\"1234567\"}"); // Logout Válido

        // Testes de cadastro de usuário
        System.out.println("\nTestes de Cadastro de Usuário:");
        enviarMensagem(out, in, "{\"operacao\":\"cadastrarUsuario\", \"ra\":\"1234567\", \"senha\":\"senha123\", \"nome\":\"FULANO DA SILVA\"}"); // Cadastro Válido
        enviarMensagem(out, in, "{\"operacao\":\"cadastrarUsuario\", \"ra\":\"12345\", \"senha\":\"senha123\", \"nome\":\"FULANO DA SILVA\"}"); // RA Inválido
        enviarMensagem(out, in, "{\"operacao\":\"cadastrarUsuario\", \"ra\":\"1234567\", \"senha\":\"\", \"nome\":\"FULANO DA SILVA\"}"); // Senha Vazia
        enviarMensagem(out, in, "{\"operacao\":\"cadastrarUsuario\", \"ra\":\"1234567\", \"senha\":\"senha123\", \"nome\":\"\"}"); // Nome Vazio
        enviarMensagem(out, in, "{\"operacao\":\"cadastrarUsuario\", \"ra\":\"1234567\", \"senha\":\"senha123\", \"nome\":\"FULANO DA SILVA\"}"); // Usuário Já Cadastrado

        // Testes de listar usuários
        System.out.println("\nTestes de Listar Usuários:");
        enviarMensagem(out, in, "{\"operacao\":\"listarUsuarios\", \"token\":\"1234567\"}"); // Listar Usuários

        // Testes de localizar usuário
        System.out.println("\nTestes de Localizar Usuário:");
        enviarMensagem(out, in, "{\"operacao\":\"localizarUsuario\", \"token\":\"1234567\", \"ra\":\"1234567\"}"); // Localizar Usuário Válido
        enviarMensagem(out, in, "{\"operacao\":\"localizarUsuario\", \"token\":\"1234567\", \"ra\":\"7654321\"}"); // Usuário Não Encontrado

        // Testes de excluir usuário
        System.out.println("\nTestes de Excluir Usuário:");
        enviarMensagem(out, in, "{\"operacao\":\"excluirUsuario\", \"token\":\"1234567\", \"ra\":\"1234567\"}"); // Excluir Usuário Válido
        enviarMensagem(out, in, "{\"operacao\":\"excluirUsuario\", \"token\":\"1234567\", \"ra\":\"7654321\"}"); // Usuário Não Encontrado

        // Testes de editar usuário
        System.out.println("\nTestes de Editar Usuário:");
        enviarMensagem(out, in, "{\"operacao\":\"editarUsuario\", \"token\":\"1234567\", \"usuario\":{\"ra\":\"1234567\", \"senha\":\"novaSenha\", \"nome\":\"FULANO DA SILVA\"}}"); // Editar Usuário Válido
        enviarMensagem(out, in, "{\"operacao\":\"editarUsuario\", \"token\":\"1234567\", \"usuario\":{\"ra\":\"7654321\", \"senha\":\"novaSenha\", \"nome\":\"FULANO DA SILVA\"}}"); // Usuário Não Encontrado

        // Testes de salvar categoria
        System.out.println("\nTestes de Salvar Categoria:");
        enviarMensagem(out, in, "{\"operacao\":\"salvarCategoria\", \"token\":\"1234567\", \"categoria\":{\"id\":0, \"nome\":\"Nova Categoria\"}}"); // Salvar Categoria Válida
        enviarMensagem(out, in, "{\"operacao\":\"salvarCategoria\", \"token\":\"1234567\", \"categoria\":{\"id\":1, \"nome\":\"\"}}"); // Nome da Categoria Vazio

        // Testes de listar categorias
        System.out.println("\nTestes de Listar Categorias:");
        enviarMensagem(out, in, "{\"operacao\":\"listarCategorias\", \"token\":\"1234567\"}"); // Listar Categorias

        // Testes de localizar categoria
        System.out.println("\nTestes de Localizar Categoria:");
        enviarMensagem(out, in, "{\"operacao\":\"localizarCategoria\", \"token\":\"1234567\", \"id\":1}"); // Localizar Categoria Válida
        enviarMensagem(out, in, "{\"operacao\":\"localizarCategoria\", \"token\":\"1234567\", \"id\":999}"); // Categoria Não Encontrada

        // Testes de excluir categoria
        System.out.println("\nTestes de Excluir Categoria:");
        enviarMensagem(out, in, "{\"operacao\":\"excluirCategoria\", \"token\":\"1234567\", \"id\":1}"); // Excluir Categoria Válida
        enviarMensagem(out, in, "{\"operacao\":\"excluirCategoria\", \"token\":\"1234567\", \"id\":999}"); // Categoria Não Encontrada

        System.out.println("Todos os testes concluídos.");
    }

    private static void enviarMensagem(PrintWriter out, BufferedReader in, String mensagem) {
        out.println(mensagem);
        System.out.println("Enviado: " + mensagem);
        try {
            String resposta = in.readLine();
            System.out.println("Resposta do servidor: " + resposta);
        } catch (IOException e) {
            System.err.println("Erro ao ler a resposta do servidor: " + e.getMessage());
        }
    }
}
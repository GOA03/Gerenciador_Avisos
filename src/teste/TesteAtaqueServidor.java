package teste;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class TesteAtaqueServidor {
    private static final String HOST = "127.0.0.1"; // Endereço IP do servidor
    private static final int PORT = 3; // Porta do servidor

    public static void main(String[] args) {
        try (Socket socket = new Socket(HOST, PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // Executa testes de ataque em todas as operações
            executarTestesOperacoes(out, in);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void executarTestesOperacoes(PrintWriter out, BufferedReader in) {
        System.out.println("Iniciando testes de todas as operações no servidor...");

        // Testes de erro global
        System.out.println("\nTestes de Erro Global:");
        enviarMensagem(out, in, "{\"status\": 401, \"mensagem\": \"Não foi possível processar a requisição.\"}");
        enviarMensagem(out, in, "{\"status\": 401, \"mensagem\": \"Operação não encontrada.\"}");
        enviarMensagem(out, in, "{\"status\": 401, \"operacao\": \"login\", \"mensagem\": \"Falha ao processar requisição, ocorreu um erro interno no servidor.\"}");
        enviarMensagem(out, in, "{\"status\": 401, \"operacao\": \"login\", \"mensagem\": \"Acesso não autorizado.\"}");

        // Testes de LOGIN
        System.out.println("\nTestes de LOGIN:");
        enviarMensagem(out, in, "{\"operacao\":\"login\", \"ra\":\"1234567\", \"senha\":\"senha123\"}"); // Login Válido
        enviarMensagem(out, in, "{\"operacao\":\"login\", \"ra\":\"12345\", \"senha\":\"senha123\"}"); // RA Inválido
        enviarMensagem(out, in, "{\"operacao\":\"login\", \"ra\":\"1234567\", \"senha\":\"senhaErrada\"}"); // Senha Inválida
        enviarMensagem(out, in, "{\"operacao\":\"login\", \"ra\":\"\", \"senha\":\"\"}"); // Campos Vazios
        enviarMensagem(out, in, "{\"operacao\":\"login\", \"ra\":1234567, \"senha\":\"senha123\"}"); // RA como Número
        enviarMensagem(out, in, "{\"operacao\":\"login\", \"ra\":\"!@#$%^&*()\", \"senha\":\"senha123\"}"); // RA com Caracteres Especiais
        enviarMensagem(out, in, "{\"operacao\":\"login\", \"ra\":\"12345678901234567\", \"senha\":\"senha123\"}"); // RA Muito Longo
        enviarMensagem(out, in, "{\"operacao\":\"login\", \"ra\":\"1234567\", \"senha\":\"<script>alert('XSS')</script>\"}"); // Injeção de Código

        // Testes de LOGOUT
        System.out.println("\nTestes de LOGOUT:");
        enviarMensagem(out, in, "{\"operacao\":\"logout\", \"token\":\"ra\"}"); // Logout Válido
        enviarMensagem(out, in, "{\"operacao\":\"logout\", \"token\":null}"); // Token Nulo
        enviarMensagem(out, in, "{\"operacao\":\"logout\", \"token\":\"\"}"); // Token Vazio

        // Testes de CADASTRAR USUÁRIO
        System.out.println("\nTestes de CADASTRAR USUÁRIO:");
        enviarMensagem(out, in, "{\"operacao\":\"cadastrarUsuario\", \"ra\":\"1234567\", \"senha\":\"senha123\", \"nome\":\"FULANO DA SILVA\"}"); // Cadastro Válido
        enviarMensagem(out, in, "{\"operacao\":\"cadastrarUsuario\", \"ra\":\"12345\", \"senha\":\"senha123\", \"nome\":\"FULANO DA SILVA\"}"); // RA Inválido
        enviarMensagem(out, in, "{\"operacao\":\"cadastrarUsuario\", \"ra\":\"1234567\", \"senha\":\"\", \"nome\":\"FULANO DA SILVA\"}"); // Senha Vazia
        enviarMensagem(out, in, "{\"operacao\":\"cadastrarUsuario\", \"ra\":\"1234567\", \"senha\":\"senha123\", \"nome\":\"\"}"); // Nome Vazio
        enviarMensagem(out, in, "{\"operacao\":\"cadastrarUsuario\", \"ra\":\"1234567\", \"senha\":\"senha123\", \"nome\":\"FULANO DA SILVA\"}"); // Usuário Já Cadastrado

        // Testes de LISTAR USUÁRIOS
        System.out.println("\nTestes de LISTAR USUÁRIOS:");
        enviarMensagem(out, in, "{\"operacao\":\"listarUsuarios\", \"token\":\"ra\"}"); // Listar Usuários
        enviarMensagem(out, in, "{\"operacao\":\"listarUsuarios\", \"token\":null}"); // Token Nulo
        enviarMensagem(out, in, "{\"operacao\":\"listarUsuarios\", \"token\":\"\"}"); // Token Vazio

        // Testes de LOCALIZAR USUÁRIO
        System.out.println("\nTestes de LOCALIZAR USUÁRIO:");
        enviarMensagem(out, in, "{\"operacao\":\"localizarUsuario\", \"token\":\"ra\", \"ra\":\"1234567\"}"); // Localizar Usuário Válido
        enviarMensagem(out, in, "{\"operacao\":\"localizarUsuario\", \"token\":\"ra\", \"ra\":\"7654321\"}"); // Usuário Não Encontrado
        enviarMensagem(out, in, "{\"operacao\":\"localizarUsuario\", \"token\":null, \"ra\":\"1234567\"}"); // Token Nulo
        enviarMensagem(out, in, "{\"operacao\":\"localizarUsuario\", \"token\":\"\", \"ra\":\"1234567\"}"); // Token Vazio

        // Testes de EXCLUIR USUÁRIO
        System.out.println("\nTestes de EXCLUIR USUÁRIO:");
        enviarMensagem(out, in, "{\"operacao\":\"excluirUsuario\", \"token\":\"ra\", \"ra\":\"1234567\"}"); // Excluir Usuário Válido
        enviarMensagem(out, in, "{\"operacao\":\"excluirUsuario\", \"token\":\"ra\", \"ra\":\"7654321\"}"); // Usuário Não Encontrado
        enviarMensagem(out, in, "{\"operacao\":\"excluirUsuario\", \"token\":null, \"ra\":\"1234567\"}"); // Token Nulo
        enviarMensagem(out, in, "{\"operacao\":\"excluirUsuario\", \"token\":\"\", \"ra\":\"1234567\"}"); // Token Vazio

        // Testes de EDITAR USUÁRIO
        System.out.println("\nTestes de EDITAR USUÁRIO:");
        enviarMensagem(out, in, "{\"operacao\":\"editarUsuario\", \"token\":\"ra\", \"usuario\":{\"ra\":\"1234567\", \"senha\":\"novaSenha\", \"nome\":\"FULANO DA SILVA\"}}"); // Editar Usuário Válido
        enviarMensagem(out, in, "{\"operacao\":\"editarUsuario\", \"token\":\"ra\", \"usuario\":{\"ra\":\"7654321\", \"senha\":\"novaSenha\", \"nome\":\"FULANO DA SILVA\"}}"); // Usuário Não Encontrado
        enviarMensagem(out, in, "{\"operacao\":\"editarUsuario\", \"token\":null, \"usuario\":{\"ra\":\"1234567\", \"senha\":\"novaSenha\", \"nome\":\"FULANO DA SILVA\"}}"); // Token Nulo
        enviarMensagem(out, in, "{\"operacao\":\"editarUsuario\", \"token\":\"\", \"usuario\":{\"ra\":\"1234567\", \"senha\":\"novaSenha\", \"nome\":\"FULANO DA SILVA\"}}"); // Token Vazio

        // Testes de SALVAR CATEGORIA
        System.out.println("\nTestes de SALVAR CATEGORIA:");
        enviarMensagem(out, in, "{\"operacao\":\"salvarCategoria\", \"token\":\"ra\", \"categoria\":{\"id\":0, \"nome\":\"Nova Categoria\"}}"); // Salvar Categoria Válida
        enviarMensagem(out, in, "{\"operacao\":\"salvarCategoria\", \"token\":\"ra\", \"categoria\":{\"id\":1, \"nome\":\"\"}}"); // Nome da Categoria Vazio

        // Testes de LISTAR CATEGORIAS
        System.out.println("\nTestes de LISTAR CATEGORIAS:");
        enviarMensagem(out, in, "{\"operacao\":\"listarCategorias\", \"token\":\"ra\"}"); // Listar Categorias
        enviarMensagem(out, in, "{\"operacao\":\"listarCategorias\", \"token\":null}"); // Token Nulo
        enviarMensagem(out, in, "{\"operacao\":\"listarCategorias\", \"token\":\"\"}"); // Token Vazio

        // Testes de LOCALIZAR CATEGORIA
        System.out.println("\nTestes de LOCALIZAR CATEGORIA:");
        enviarMensagem(out, in, "{\"operacao\":\"localizarCategoria\", \"token\":\"ra\", \"id\":1}"); // Localizar Categoria Válida
        enviarMensagem(out, in, "{\"operacao\":\"localizarCategoria\", \"token\":\"ra\", \"id\":999}"); // Categoria Não Encontrada

        // Testes de EXCLUIR CATEGORIA
        System.out.println("\nTestes de EXCLUIR CATEGORIA:");
        enviarMensagem(out, in, "{\"operacao\":\"excluirCategoria\", \"token\":\"ra\", \"id\":1}"); // Excluir Categoria Válida
        enviarMensagem(out, in, "{\"operacao\":\"excluirCategoria\", \"token\":\"ra\", \"id\":999}"); // Categoria Não Encontrada

        System.out.println("Testes de todas as operações concluídos.");
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
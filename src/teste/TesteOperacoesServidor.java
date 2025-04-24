package teste;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class TesteOperacoesServidor {
    private static final String HOST = "127.0.0.1"; // Endereço IP do servidor
    private static final int PORT = 3; // Porta do servidor

    public static void main(String[] args) {
        try (Socket socket = new Socket(HOST, PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // Testes com RA 1234567
            System.out.println("Iniciando testes com RA 1234567...");
            executarTestes(out, in, "1234567");

            // Testes com RA 2376342
            System.out.println("Iniciando testes com RA 2376342...");
            executarTestes(out, in, "2376342");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void executarTestes(PrintWriter out, BufferedReader in, String ra) throws IOException {
        // Testes de Erro Global
        enviarMensagem(out, in, "{\"status\": 401, \"mensagem\": \"Não foi possível processar a requisição.\"}");
        enviarMensagem(out, in, "{\"status\": 401, \"mensagem\": \"Operação não encontrada.\"}");
        enviarMensagem(out, in, "{\"status\": 401, \"operacao\": \"login\", \"mensagem\": \"Falha ao processar requisição, ocorreu um erro interno no servidor.\"}");
        enviarMensagem(out, in, "{\"status\": 401, \"operacao\": \"login\", \"mensagem\": \"Acesso não autorizado.\"}");

        // Testes de Login
        enviarMensagem(out, in, "{\"operacao\":\"login\", \"ra\":\"" + ra + "\", \"senha\":\"senha123\"}"); // Login Válido
        enviarMensagem(out, in, "{\"operacao\":\"login\", \"ra\":\"" + ra + "\", \"senha\":\"\"}"); // Senha Vazia
        enviarMensagem(out, in, "{\"operacao\":\"login\", \"ra\":\"\", \"senha\":\"senha123\"}"); // RA Vazio
        enviarMensagem(out, in, "{\"operacao\":\"login\", \"ra\":\"" + ra + "\", \"senha\":\"senhaErrada\"}"); // Senha Inválida
        enviarMensagem(out, in, "{\"operacao\":\"login\", \"ra\":1234567, \"senha\":\"senha123\"}"); // RA como Número
        enviarMensagem(out, in, "{\"operacao\":\"login\", \"ra\":\"!@#$%^&*()\", \"senha\":\"senha123\"}"); // RA com Caracteres Especiais
        enviarMensagem(out, in, "{\"operacao\":\"login\", \"ra\":\"" + ra + "\", \"senha\":123456}"); // Senha como Número
        enviarMensagem(out, in, "{\"operacao\":\"login\", \"ra\":\"" + ra + "\", \"senha\":null}"); // Senha Nula

        // Testes de Logout
        enviarMensagem(out, in, "{\"operacao\":\"logout\", \"token\":\"" + ra + "\"}"); // Logout Válido
        enviarMensagem(out, in, "{\"operacao\":\"logout\", \"token\":null}"); // Token Nulo

        // Testes de Cadastrar Usuário
        enviarMensagem(out, in, "{\"operacao\":\"cadastrarUsuario\", \"ra\":\"" + ra + "\", \"senha\":\"senha123\", \"nome\":\"FULANO DA SILVA\"}"); // Cadastro Válido
        enviarMensagem(out, in, "{\"operacao\":\"cadastrarUsuario\", \"ra\":\"" + ra + "\", \"senha\":\"\", \"nome\":\"FULANO DA SILVA\"}"); // Senha Vazia
        enviarMensagem(out, in, "{\"operacao\":\"cadastrarUsuario\", \"ra\":\"\", \"senha\":\"senha123\", \"nome\":\"\"}"); // Campos Vazios
        enviarMensagem(out, in, "{\"operacao\":\"cadastrarUsuario\", \"ra\":\"" + ra + "\", \"senha\":\"senha123\", \"nome\":\"\"}"); // Nome Vazio
        enviarMensagem(out, in, "{\"operacao\":\"cadastrarUsuario\", \"ra\":\"" + ra + "\", \"senha\":\"senha123\", \"nome\":12345}"); // Nome como Número
        enviarMensagem(out, in, "{\"operacao\":\"cadastrarUsuario\", \"ra\":\"" + ra + "\", \"senha\":null, \"nome\":\"FULANO DA SILVA\"}"); // Senha Nula

        // Testes de Listar Usuários
        enviarMensagem(out, in, "{\"operacao\":\"listarUsuarios\", \"token\":\"" + ra + "\"}"); // Listar Usuários

        // Testes de Localizar Usuário
        enviarMensagem(out, in, "{\"operacao\":\"localizarUsuario\", \"token\":\"" + ra + "\", \"ra\":\"" + ra + "\"}"); // Localizar Usuário Válido
        enviarMensagem(out, in, "{\"operacao\":\"localizarUsuario\", \"token\":\"" + ra + "\", \"ra\":\"7654321\"}"); // Usuário Não Encontrado
        enviarMensagem(out, in, "{\"operacao\":\"localizarUsuario\", \"token\":\"" + ra + "\", \"ra\":null}"); // RA Nulo

        // Testes de Excluir Usuário
        enviarMensagem(out, in, "{\"operacao\":\"excluirUsuario\", \"token\":\"" + ra + "\", \"ra\":\"" + ra + "\"}"); // Excluir Usuário Válido
        enviarMensagem(out, in, "{\"operacao\":\"excluirUsuario\", \"token\":\"" + ra + "\", \"ra\":\"7654321\"}"); // Usuário Não Encontrado

        // Testes de Editar Usuário
        enviarMensagem(out, in, "{\"operacao\":\"editarUsuario\", \"token\":\"" + ra + "\", \"usuario\":{\"ra\":\"" + ra + "\", \"senha\":\"novaSenha\", \"nome\":\"FULANO DA SILVA\"}}"); // Editar Usuário Válido
        enviarMensagem(out, in, "{\"operacao\":\"editarUsuario\", \"token\":\"" + ra + "\", \"usuario\":{\"ra\":\"" + ra + "\", \"senha\":\"\", \"nome\":\"FULANO DA SILVA\"}}"); // Senha Vazia
        enviarMensagem(out, in, "{\"operacao\":\"editarUsuario\", \"token\":\"" + ra + "\", \"usuario\":{\"ra\":\"\", \"senha\":\"novaSenha\", \"nome\":\"FULANO DA SILVA\"}}"); // RA Vazio

        // Testes de Salvar Categoria
        enviarMensagem(out, in, "{\"operacao\":\"salvarCategoria\", \"token\":\"" + ra + "\", \"categoria\":{\"id\":0, \"nome\":\"Nova Categoria\"}}"); // Salvar Categoria Válida
        enviarMensagem(out, in, "{\"operacao\":\"salvarCategoria\", \"token\":\"" + ra + "\", \"categoria\":{\"id\":0, \"nome\":\"\"}}"); // Nome Vazio
        enviarMensagem(out, in, "{\"operacao\":\"salvarCategoria\", \"token\":\"" + ra + "\", \"categoria\":{\"id\":0, \"nome\":12345}}"); // Nome como Número

        // Testes de Listar Categorias
        enviarMensagem(out, in, "{\"operacao\":\"listarCategorias\", \"token\":\"" + ra + "\"}"); // Listar Categorias

        // Testes de Localizar Categoria
        enviarMensagem(out, in, "{\"operacao\":\"localizarCategoria\", \"token\":\"" + ra + "\", \"id\":1}"); // Localizar Categoria Válida
        enviarMensagem(out, in, "{\"operacao\":\"localizarCategoria\", \"token\":\"" + ra + "\", \"id\":null}"); // ID Nulo

        // Testes de Excluir Categoria
        enviarMensagem(out, in, "{\"operacao\":\"excluirCategoria\", \"token\":\"" + ra + "\", \"id\":1}"); // Excluir Categoria Válida
        enviarMensagem(out, in, "{\"operacao\":\"excluirCategoria\", \"token\":\"" + ra + "\", \"id\":null}"); // ID Nulo

        // Testes de Listar Avisos
        enviarMensagem(out, in, "{\"operacao\":\"listarAvisos\", \"token\":\"" + ra + "\", \"categoria\":0}"); // Listar Todos os Avisos
        enviarMensagem(out, in, "{\"operacao\":\"listarAvisos\", \"token\":\"" + ra + "\", \"categoria\":1}"); // Listar Avisos por Categoria
        enviarMensagem(out, in, "{\"operacao\":\"listarAvisos\", \"token\":\"" + ra + "\", \"categoria\":null}"); // Categoria Nula

        // Testes de Localizar Aviso
        enviarMensagem(out, in, "{\"operacao\":\"localizarAviso\", \"token\":\"" + ra + "\", \"id\":1}"); // Localizar Aviso Válido
        enviarMensagem(out, in, "{\"operacao\":\"localizarAviso\", \"token\":\"" + ra + "\", \"id\":null}"); // ID Nulo
        enviarMensagem(out, in, "{\"operacao\":\"localizarAviso\", \"token\":\"" + ra + "\", \"id\":999}"); // Aviso Não Encontrado

        // Testes de Excluir Aviso
        enviarMensagem(out, in, "{\"operacao\":\"excluirAviso\", \"token\":\"" + ra + "\", \"id\":1}"); // Excluir Aviso Válido
        enviarMensagem(out, in, "{\"operacao\":\"excluirAviso\", \"token\":\"" + ra + "\", \"id\":null}"); // ID Nulo
        enviarMensagem(out, in, "{\"operacao\":\"excluirAviso\", \"token\":\"" + ra + "\", \"id\":999}"); // Aviso Não Encontrado

        // Testes de Cadastrar Usuário em Categoria de Avisos
        enviarMensagem(out, in, "{\"operacao\":\"cadastrarUsuarioCategoria\", \"token\":\"" + ra + "\", \"ra\":\"" + ra + "\", \"categoria\":1}"); // Cadastro Válido
        enviarMensagem(out, in, "{\"operacao\":\"cadastrarUsuarioCategoria\", \"token\":\"" + ra + "\", \"ra\":\"\", \"categoria\":1}"); // RA Vazio
        enviarMensagem(out, in, "{\"operacao\":\"cadastrarUsuarioCategoria\", \"token\":\"" + ra + "\", \"ra\":\"" + ra + "\", \"categoria\":null}"); // Categoria Nula

        // Testes de Listar Categorias que o Usuário se Cadastrou
        enviarMensagem(out, in, "{\"operacao\":\"listarUsuarioCategorias\", \"token\":\"" + ra + "\", \"ra\":\"" + ra + "\"}"); // Listar Categorias Válido
        enviarMensagem(out, in, "{\"operacao\":\"listarUsuarioCategorias\", \"token\":\"" + ra + "\", \"ra\":\"\"}"); // RA Vazio
    }

    private static void enviarMensagem(PrintWriter out, BufferedReader in, String mensagem) throws IOException {
        out.println(mensagem);
        System.out.println("Enviado: " + mensagem);
        String resposta = in.readLine();
        System.out.println("Resposta do servidor: " + resposta);
    }
}
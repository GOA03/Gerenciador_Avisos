package teste;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class TesteProtocoloMensagens {
    private static final String HOST = "127.0.0.1"; // Endereço IP do servidor
    private static final int PORT = 3; // Porta do servidor

    public static void main(String[] args) {
        try (Socket socket = new Socket(HOST, PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // Testes de erro global
            System.out.println("Iniciando testes de erro global...");
            testarErroGlobal(out, in);

            // Testes de login
            System.out.println("Iniciando testes de login...");
            testarLogin(out, in);

            // Testes de logout
            System.out.println("Iniciando testes de logout...");
            testarLogout(out, in);

            // Testes de cadastro de usuário
            System.out.println("Iniciando testes de cadastro de usuário...");
            testarCadastroUsuario(out, in);

            // Testes de listar usuários
            System.out.println("Iniciando testes de listar usuários...");
            testarListarUsuarios(out, in);

            // Testes de localizar usuário
            System.out.println("Iniciando testes de localizar usuário...");
            testarLocalizarUsuario(out, in);

            // Testes de excluir usuário
            System.out.println("Iniciando testes de excluir usuário...");
            testarExcluirUsuario(out, in);

            // Testes de editar usuário
            System.out.println("Iniciando testes de editar usuário...");
            testarEditarUsuario(out, in);

            // Testes de salvar categoria
            System.out.println("Iniciando testes de salvar categoria...");
            testarSalvarCategoria(out, in);

            // Testes de listar categorias
            System.out.println("Iniciando testes de listar categorias...");
            testarListarCategorias(out, in);

            // Testes de localizar categoria
            System.out.println("Iniciando testes de localizar categoria...");
            testarLocalizarCategoria(out, in);

            // Testes de excluir categoria
            System.out.println("Iniciando testes de excluir categoria...");
            testarExcluirCategoria(out, in);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void testarErroGlobal(PrintWriter out, BufferedReader in) throws IOException {
        // ERRO GLOBAL: Mensagem de erro genérica
        enviarMensagem(out, in, "{\"status\": 401, \"mensagem\": \"Não foi possível processar a requisição.\"}");
        enviarMensagem(out, in, "{\"status\": 401, \"mensagem\": \"Operação não encontrada.\"}");
        enviarMensagem(out, in, "{\"status\": 401, \"operacao\": \"login\", \"mensagem\": \"Falha ao processar requisição, ocorreu um erro interno no servidor.\"}");
        enviarMensagem(out, in, "{\"status\": 401, \"operacao\": \"login\", \"mensagem\": \"Acesso não autorizado.\"}");
    }

    private static void testarLogin(PrintWriter out, BufferedReader in) throws IOException {
        // LOGIN: Testes com RA 1234567 (admin)
        enviarMensagem(out, in, "{\"operacao\":\"login\", \"ra\":\"1234567\", \"senha\":\"senha123\"}"); // Login Válido
        enviarMensagem(out, in, "{\"operacao\":\"login\", \"ra\":\"1234567\", \"senha\":\"senhaErrada\"}"); // Senha Inválida
        enviarMensagem(out, in, "{\"operacao\":\"login\", \"ra\":\"\", \"senha\":\"\"}"); // Campos Vazios
        enviarMensagem(out, in, "{\"operacao\":\"login\", \"ra\":\"1234567\", \"senha\":null}"); // Senha Nula

        // LOGIN: Testes com RA 7654321 (comum)
        enviarMensagem(out, in, "{\"operacao\":\"login\", \"ra\":\"7654321\", \"senha\":\"senha123\"}"); // Login Válido
        enviarMensagem(out, in, "{\"operacao\":\"login\", \"ra\":\"7654321\", \"senha\":\"senhaErrada\"}"); // Senha Inválida
        enviarMensagem(out, in, "{\"operacao\":\"login\", \"ra\":\"\", \"senha\":\"\"}"); // Campos Vazios
        enviarMensagem(out, in, "{\"operacao\":\"login\", \"ra\":\"7654321\", \"senha\":null}"); // Senha Nula
    }

    private static void testarLogout(PrintWriter out, BufferedReader in) throws IOException {
        // LOGOUT: Testes com RA 1234567 (admin)
        enviarMensagem(out, in, "{\"operacao\":\"logout\", \"token\":\"1234567\"}"); // Logout Válido
        enviarMensagem(out, in, "{\"operacao\":\"logout\", \"token\":null}"); // Token Nulo
        enviarMensagem(out, in, "{\"operacao\":\"logout\", \"token\":\"\"}"); // Token Vazio

        // LOGOUT: Testes com RA 7654321 (comum)
        enviarMensagem(out, in, "{\"operacao\":\"logout\", \"token\":\"7654321\"}"); // Logout Válido
        enviarMensagem(out, in, "{\"operacao\":\"logout\", \"token\":null}"); // Token Nulo
        enviarMensagem(out, in, "{\"operacao\":\"logout\", \"token\":\"\"}"); // Token Vazio
    }

    private static void testarCadastroUsuario(PrintWriter out, BufferedReader in) throws IOException {
        // CADASTRAR USUÁRIO: Testes com RA 1234567 (admin)
        enviarMensagem(out, in, "{\"operacao\":\"cadastrarUsuario\", \"ra\":\"1234567\", \"senha\":\"senha123\", \"nome\":\"FULANO DA SILVA\"}"); // Cadastro Válido
        enviarMensagem(out, in, "{\"operacao\":\"cadastrarUsuario\", \"ra\":\"1234567\", \"senha\":\"\", \"nome\":\"FULANO DA SILVA\"}"); // Senha Vazia
        enviarMensagem(out, in, "{\"operacao\":\"cadastrarUsuario\", \"ra\":\"1234567\", \"senha\":\"senha123\", \"nome\":\"\"}"); // Nome Vazio
        enviarMensagem(out, in, "{\"operacao\":\"cadastrarUsuario\", \"ra\":\"1234567\", \"senha\":\"senha123\", \"nome\":\"FULANO DA SILVA\"}"); // Usuário Já Cadastrado

        // CADASTRAR USUÁRIO: Testes com RA 7654321 (comum)
        enviarMensagem(out, in, "{\"operacao\":\"cadastrarUsuario\", \"ra\":\"7654321\", \"senha\":\"senha123\", \"nome\":\"FULANO DA SILVA\"}"); // Cadastro Válido
        enviarMensagem(out, in, "{\"operacao\":\"cadastrarUsuario\", \"ra\":\"7654321\", \"senha\":\"\", \"nome\":\"FULANO DA SILVA\"}"); // Senha Vazia
        enviarMensagem(out, in, "{\"operacao\":\"cadastrarUsuario\", \"ra\":\"7654321\", \"senha\":\"senha123\", \"nome\":\"\"}"); // Nome Vazio
        enviarMensagem(out, in, "{\"operacao\":\"cadastrarUsuario\", \"ra\":\"7654321\", \"senha\":\"senha123\", \"nome\":\"FULANO DA SILVA\"}"); // Usuário Já Cadastrado
    }

    private static void testarListarUsuarios(PrintWriter out, BufferedReader in) throws IOException {
        // LISTAR USUÁRIOS: Testes com RA 1234567 (admin)
        enviarMensagem(out, in, "{\"operacao\":\"listarUsuarios\", \"token\":\"1234567\"}"); // Listar Usuários
        enviarMensagem(out, in, "{\"operacao\":\"listarUsuarios\", \"token\":null}"); // Token Nulo
        enviarMensagem(out, in, "{\"operacao\":\"listarUsuarios\", \"token\":\"\"}"); // Token Vazio

        // LISTAR USUÁRIOS: Testes com RA 7654321 (comum)
        enviarMensagem(out, in, "{\"operacao\":\"listarUsuarios\", \"token\":\"7654321\"}"); // Listar Usuários
        enviarMensagem(out, in, "{\"operacao\":\"listarUsuarios\", \"token\":null}"); // Token Nulo
        enviarMensagem(out, in, "{\"operacao\":\"listarUsuarios\", \"token\":\"\"}"); // Token Vazio
    }

    private static void testarLocalizarUsuario(PrintWriter out, BufferedReader in) throws IOException {
        // LOCALIZAR USUÁRIO: Testes com RA 1234567 (admin)
        enviarMensagem(out, in, "{\"operacao\":\"localizarUsuario\", \"token\":\"1234567\", \"ra\":\"1234567\"}"); // Localizar Usuário Válido
        enviarMensagem(out, in, "{\"operacao\":\"localizarUsuario\", \"token\":\"1234567\", \"ra\":\"7654321\"}"); // Usuário Não Encontrado
        enviarMensagem(out, in, "{\"operacao\":\"localizarUsuario\", \"token\":null, \"ra\":\"1234567\"}"); // Token Nulo
        enviarMensagem(out, in, "{\"operacao\":\"localizarUsuario\", \"token\":\"\", \"ra\":\"1234567\"}"); // Token Vazio

        // LOCALIZAR USUÁRIO: Testes com RA 7654321 (comum)
        enviarMensagem(out, in, "{\"operacao\":\"localizarUsuario\", \"token\":\"7654321\", \"ra\":\"7654321\"}"); // Localizar Usuário Válido
        enviarMensagem(out, in, "{\"operacao\":\"localizarUsuario\", \"token\":\"7654321\", \"ra\":\"1234567\"}"); // Usuário Não Encontrado
        enviarMensagem(out, in, "{\"operacao\":\"localizarUsuario\", \"token\":null, \"ra\":\"7654321\"}"); // Token Nulo
        enviarMensagem(out, in, "{\"operacao\":\"localizarUsuario\", \"token\":\"\", \"ra\":\"7654321\"}"); // Token Vazio
    }

    private static void testarExcluirUsuario(PrintWriter out, BufferedReader in) throws IOException {
        // EXCLUIR USUÁRIO: Testes com RA 1234567 (admin)
        enviarMensagem(out, in, "{\"operacao\":\"excluirUsuario\", \"token\":\"1234567\", \"ra\":\"1234567\"}"); // Excluir Usuário Válido
        enviarMensagem(out, in, "{\"operacao\":\"excluirUsuario\", \"token\":\"1234567\", \"ra\":\"7654321\"}"); // Usuário Não Encontrado
        enviarMensagem(out, in, "{\"operacao\":\"excluirUsuario\", \"token\":null, \"ra\":\"1234567\"}"); // Token Nulo
        enviarMensagem(out, in, "{\"operacao\":\"excluirUsuario\", \"token\":\"\", \"ra\":\"1234567\"}"); // Token Vazio

        // EXCLUIR USUÁRIO: Testes com RA 7654321 (comum)
        enviarMensagem(out, in, "{\"operacao\":\"excluirUsuario\", \"token\":\"7654321\", \"ra\":\"7654321\"}"); // Excluir Usuário Válido
        enviarMensagem(out, in, "{\"operacao\":\"excluirUsuario\", \"token\":\"7654321\", \"ra\":\"1234567\"}"); // Usuário Não Encontrado
        enviarMensagem(out, in, "{\"operacao\":\"excluirUsuario\", \"token\":null, \"ra\":\"7654321\"}"); // Token Nulo
        enviarMensagem(out, in, "{\"operacao\":\"excluirUsuario\", \"token\":\"\", \"ra\":\"7654321\"}"); // Token Vazio
    }

    private static void testarEditarUsuario(PrintWriter out, BufferedReader in) throws IOException {
        // EDITAR USUÁRIO: Testes com RA 1234567 (admin)
        enviarMensagem(out, in, "{\"operacao\":\"editarUsuario\", \"token\":\"1234567\", \"usuario\":{\"ra\":\"1234567\", \"senha\":\"novaSenha\", \"nome\":\"FULANO DA SILVA\"}}"); // Editar Usuário Válido
        enviarMensagem(out, in, "{\"operacao\":\"editarUsuario\", \"token\":\"1234567\", \"usuario\":{\"ra\":\"7654321\", \"senha\":\"novaSenha\", \"nome\":\"FULANO DA SILVA\"}}"); // Usuário Não Encontrado
        enviarMensagem(out, in, "{\"operacao\":\"editarUsuario\", \"token\":null, \"usuario\":{\"ra\":\"1234567\", \"senha\":\"novaSenha\", \"nome\":\"FULANO DA SILVA\"}}"); // Token Nulo
        enviarMensagem(out, in, "{\"operacao\":\"editarUsuario\", \"token\":\"\", \"usuario\":{\"ra\":\"1234567\", \"senha\":\"novaSenha\", \"nome\":\"FULANO DA SILVA\"}}"); // Token Vazio

        // EDITAR USUÁRIO: Testes com RA 7654321 (comum)
        enviarMensagem(out, in, "{\"operacao\":\"editarUsuario\", \"token\":\"7654321\", \"usuario\":{\"ra\":\"7654321\", \"senha\":\"novaSenha\", \"nome\":\"FULANO DA SILVA\"}}"); // Editar Usuário Válido
        enviarMensagem(out, in, "{\"operacao\":\"editarUsuario\", \"token\":\"7654321\", \"usuario\":{\"ra\":\"1234567\", \"senha\":\"novaSenha\", \"nome\":\"FULANO DA SILVA\"}}"); // Usuário Não Encontrado
        enviarMensagem(out, in, "{\"operacao\":\"editarUsuario\", \"token\":null, \"usuario\":{\"ra\":\"7654321\", \"senha\":\"novaSenha\", \"nome\":\"FULANO DA SILVA\"}}"); // Token Nulo
        enviarMensagem(out, in, "{\"operacao\":\"editarUsuario\", \"token\":\"\", \"usuario\":{\"ra\":\"7654321\", \"senha\":\"novaSenha\", \"nome\":\"FULANO DA SILVA\"}}"); // Token Vazio
    }

    private static void testarSalvarCategoria(PrintWriter out, BufferedReader in) throws IOException {
        // SALVAR CATEGORIA: Testes com RA 1234567 (admin)
        enviarMensagem(out, in, "{\"operacao\":\"salvarCategoria\", \"token\":\"1234567\", \"categoria\":{\"id\":0, \"nome\":\"Nova Categoria\"}}"); // Salvar Categoria Válida
        enviarMensagem(out, in, "{\"operacao\":\"salvarCategoria\", \"token\":\"1234567\", \"categoria\":{\"id\":1, \"nome\":\"\"}}"); // Nome da Categoria Vazio

        // SALVAR CATEGORIA: Testes com RA 7654321 (comum)
        enviarMensagem(out, in, "{\"operacao\":\"salvarCategoria\", \"token\":\"7654321\", \"categoria\":{\"id\":0, \"nome\":\"Nova Categoria\"}}"); // Salvar Categoria Válida
        enviarMensagem(out, in, "{\"operacao\":\"salvarCategoria\", \"token\":\"7654321\", \"categoria\":{\"id\":1, \"nome\":\"\"}}"); // Nome da Categoria Vazio
    }

    private static void testarListarCategorias(PrintWriter out, BufferedReader in) throws IOException {
        // LISTAR CATEGORIAS: Testes com RA 1234567 (admin)
        enviarMensagem(out, in, "{\"operacao\":\"listarCategorias\", \"token\":\"1234567\"}"); // Listar Categorias
        enviarMensagem(out, in, "{\"operacao\":\"listarCategorias\", \"token\":null}"); // Token Nulo
        enviarMensagem(out, in, "{\"operacao\":\"listarCategorias\", \"token\":\"\"}"); // Token Vazio

        // LISTAR CATEGORIAS: Testes com RA 7654321 (comum)
        enviarMensagem(out, in, "{\"operacao\":\"listarCategorias\", \"token\":\"7654321\"}"); // Listar Categorias
        enviarMensagem(out, in, "{\"operacao\":\"listarCategorias\", \"token\":null}"); // Token Nulo
        enviarMensagem(out, in, "{\"operacao\":\"listarCategorias\", \"token\":\"\"}"); // Token Vazio
    }

    private static void testarLocalizarCategoria(PrintWriter out, BufferedReader in) throws IOException {
        // LOCALIZAR CATEGORIA: Testes com RA 1234567 (admin)
        enviarMensagem(out, in, "{\"operacao\":\"localizarCategoria\", \"token\":\"1234567\", \"id\":1}"); // Localizar Categoria Válida
        enviarMensagem(out, in, "{\"operacao\":\"localizarCategoria\", \"token\":\"1234567\", \"id\":999}"); // Categoria Não Encontrada

        // LOCALIZAR CATEGORIA: Testes com RA 7654321 (comum)
        enviarMensagem(out, in, "{\"operacao\":\"localizarCategoria\", \"token\":\"7654321\", \"id\":1}"); // Localizar Categoria Válida
        enviarMensagem(out, in, "{\"operacao\":\"localizarCategoria\", \"token\":\"7654321\", \"id\":999}"); // Categoria Não Encontrada
    }

    private static void testarExcluirCategoria(PrintWriter out, BufferedReader in) throws IOException {
        // EXCLUIR CATEGORIA: Testes com RA 1234567 (admin)
        enviarMensagem(out, in, "{\"operacao\":\"excluirCategoria\", \"token\":\"1234567\", \"id\":1}"); // Excluir Categoria Válida
        enviarMensagem(out, in, "{\"operacao\":\"excluirCategoria\", \"token\":\"1234567\", \"id\":999}"); // Categoria Não Encontrada

        // EXCLUIR CATEGORIA: Testes com RA 7654321 (comum)
        enviarMensagem(out, in, "{\"operacao\":\"excluirCategoria\", \"token\":\"7654321\", \"id\":1}"); // Excluir Categoria Válida
        enviarMensagem(out, in, "{\"operacao\":\"excluirCategoria\", \"token\":\"7654321\", \"id\":999}"); // Categoria Não Encontrada
    }

    private static void enviarMensagem(PrintWriter out, BufferedReader in, String mensagem) throws IOException {
        out.println(mensagem);
        System.out.println("Enviado: " + mensagem);
        String resposta = in.readLine();
        System.out.println("Resposta do servidor: " + resposta);
    }
}
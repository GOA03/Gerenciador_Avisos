package teste;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class TesteLoginServidor {
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
        // Cenários de teste
        System.out.println("Iniciando testes de login...");

        // Cenário 1: Login Válido
        enviarMensagem(out, in, "{\"operacao\":\"login\", \"ra\":\"1234567\", \"senha\":\"senha123\"}");

        // Cenário 2: RA Inválido
        enviarMensagem(out, in, "{\"operacao\":\"login\", \"ra\":\"12345\", \"senha\":\"senha123\"}");

        // Cenário 3: Senha Inválida
        enviarMensagem(out, in, "{\"operacao\":\"login\", \"ra\":\"1234567\", \"senha\":\"senhaErrada\"}");

        // Cenário 4: Campos Vazios
        enviarMensagem(out, in, "{\"operacao\":\"login\", \"ra\":\"\", \"senha\":\"\"}");

        // Cenário 5: JSON Malformado
        enviarMensagem(out, in, "{\"operacao\":\"login\", \"ra\":\"1234567\", \"senha\":\"senha123\""); // Falta chave de fechamento

        // Cenário 6: RA como Número
        enviarMensagem(out, in, "{\"operacao\":\"login\", \"ra\":1234567, \"senha\":\"senha123\"}");

        // Cenário 7: RA com Caracteres Especiais
        enviarMensagem(out, in, "{\"operacao\":\"login\", \"ra\":\"!@#$%^&*()\", \"senha\":\"senha123\"}");

        // Cenário 8: RA Muito Longo
        enviarMensagem(out, in, "{\"operacao\":\"login\", \"ra\":\"12345678901234567\", \"senha\":\"senha123\"}");

        // Cenário 9: Senha Muito Longa
        enviarMensagem(out, in, "{\"operacao\":\"login\", \"ra\":\"1234567\", \"senha\":\"senha12345678901234567890\"}");

        // Cenário 10: Injeção de Código
        enviarMensagem(out, in, "{\"operacao\":\"login\", \"ra\":\"1234567\", \"senha\":\"<script>alert('XSS')</script>\"}");

        // Cenário 11: RA com Acentuação
        enviarMensagem(out, in, "{\"operacao\":\"login\", \"ra\":\"1234567\", \"senha\":\"senha123\"}");

        // Cenário 12: RA Nulo
        enviarMensagem(out, in, "{\"operacao\":\"login\", \"ra\":null, \"senha\":\"senha123\"}");

        // Cenário 13: Senha Nula
        enviarMensagem(out, in, "{\"operacao\":\"login\", \"ra\":\"1234567\", \"senha\":null}");

        // Cenário 14: RA com Espaços
        enviarMensagem(out, in, "{\"operacao\":\"login\", \"ra\":\"   \", \"senha\":\"senha123\"}");

        // Cenário 15: Senha com Espaços
        enviarMensagem(out, in, "{\"operacao\":\"login\", \"ra\":\"1234567\", \"senha\":\"   \"}");

        // Cenário 16: RA com Caracteres Não Alfanuméricos
        enviarMensagem(out, in, "{\"operacao\":\"login\", \"ra\":\"1234567!@#\", \"senha\":\"senha123\"}");

        // Cenário 17: RA com Caracteres Unicode
        enviarMensagem(out, in, "{\"operacao\":\"login\", \"ra\":\"1234567\", \"senha\":\"senha\uD83D\uDE00\"}");

        // Cenário 18: RA com Comprimento Exato
        enviarMensagem(out, in, "{\"operacao\":\"login\", \"ra\":\"1234567\", \"senha\":\"senha123\"}");

        // Cenário 19: Senha com Comprimento Exato
        enviarMensagem(out, in, "{\"operacao\":\"login\", \"ra\":\"1234567\", \"senha\":\"senha123\"}");

        // Cenário 20: Operação Desconhecida
        enviarMensagem(out, in, "{\"operacao\":\"loginInvalido\", \"ra\":\"1234567\", \"senha\":\"senha123\"}");

        System.out.println("Testes de login concluídos.");
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
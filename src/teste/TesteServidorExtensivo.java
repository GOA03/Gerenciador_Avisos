package teste;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class TesteServidorExtensivo {
    private static final String HOST = "127.0.0.1"; // IP do servidor
    private static final int PORT = 8080; // Porta do servidor

    public static void main(String[] args) {
        List<String> testes = gerarCenariosDeTeste();

        try (Socket socket = new Socket(HOST, PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            for (int i = 0; i < testes.size(); i++) {
                String mensagem = testes.get(i);
                System.out.println("Teste " + (i + 1) + ": " + mensagem);
                out.println(mensagem);

                // Lê a resposta do servidor
                String resposta = in.readLine();
                System.out.println("Resposta do servidor: " + resposta);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static List<String> gerarCenariosDeTeste() {
        List<String> testes = new ArrayList<>();

        // Gerar 500 testes para cada categoria principal
        testes.addAll(gerarTestesLogin());
        testes.addAll(gerarTestesCadastro());
        testes.addAll(gerarTestesLogout());
        testes.addAll(gerarTestesExtremos());

        return testes;
    }

    private static List<String> gerarTestesLogin() {
        List<String> testes = new ArrayList<>();

        for (int i = 1; i <= 500; i++) {
            // Cenários válidos
            testes.add(String.format("{\"operacao\":\"login\", \"ra\":\"123456%d\", \"senha\":\"senha%d\"}", i, i));

            // Campos inválidos
            testes.add("{\"operacao\":\"login\", \"ra\":\"\", \"senha\":\"senha123\"}");
            testes.add("{\"operacao\":\"login\", \"ra\":\"123456\", \"senha\":\"\"}");
            testes.add("{\"operacao\":\"login\", \"ra\":null, \"senha\":null}");

            // Dados malformados e entradas inesperadas
            testes.add("{\"operacao\":\"login\", \"ra\":\"" + gerarStringAleatoria(i) + "\", \"senha\":\"senha" + i + "\"}");
            testes.add("{\"operacao\":\"login\", \"ra\":\"123456\", \"senha\":\"<script>alert('XSS')</script>\"}");
            testes.add("{\"operacao\":\"login\", \"ra\":123456, \"senha\":true}");
        }

        return testes;
    }

    private static List<String> gerarTestesCadastro() {
        List<String> testes = new ArrayList<>();

        for (int i = 1; i <= 500; i++) {
            // Cenários válidos
            testes.add(String.format("{\"operacao\":\"cadastrarUsuario\", \"ra\":\"123456%d\", \"senha\":\"senha%d\", \"nome\":\"Usuario%d\"}", i, i, i));

            // Campos inválidos
            testes.add("{\"operacao\":\"cadastrarUsuario\", \"ra\":null, \"senha\":\"senha123\", \"nome\":\"Usuario\"}");
            testes.add("{\"operacao\":\"cadastrarUsuario\", \"ra\":\"\", \"senha\":\"senha123\", \"nome\":\"\"}");

            // Dados malformados
            testes.add("{\"operacao\":\"cadastrarUsuario\", \"ra\":\"123456\", \"senha\":\"<script>malicious()</script>\", \"nome\":\"" + gerarStringAleatoria(30) + "\"}");
        }

        return testes;
    }

    private static List<String> gerarTestesLogout() {
        List<String> testes = new ArrayList<>();

        for (int i = 1; i <= 500; i++) {
            // Cenário válido
            testes.add("{\"operacao\":\"logout\"}");

            // Campos inesperados e dados maliciosos
            testes.add("{\"operacao\":\"logout\", \"ra\":\"123456\"}");
            testes.add("{\"operacao\":\"logout\", \"campoExtra\":\"valorInvalido\"}");
            testes.add("{\"operacao\":\"logout\", \"ra\":123456, \"extra\":true}");
        }

        return testes;
    }

    private static List<String> gerarTestesExtremos() {
        List<String> testes = new ArrayList<>();

        for (int i = 1; i <= 500; i++) {
            // JSONs incompletos ou malformados
            testes.add("{\"operacao\":\"login\", \"ra\":\"123456\""); // Falta chave de fechamento
            testes.add("Texto puro sem formato JSON");
            testes.add("");

            // Dados extremamente grandes
            testes.add("{\"operacao\":\"login\", \"ra\":\"123456\", \"senha\":\"" + gerarStringLonga(10000) + "\"}");
            testes.add("{\"operacao\":\"cadastrarUsuario\", \"ra\":\"" + gerarStringLonga(1000) + "\", \"senha\":\"senha123\", \"nome\":\"UsuarioGrande\"}");

            // Dados inesperados
            testes.add("{\"operacao\":\"desconhecida" + i + "\", \"campo\":\"" + gerarStringAleatoria(20) + "\"}");
            testes.add("{\"operacao\":\"\", \"valor\":false}");
        }

        return testes;
    }

    private static String gerarStringAleatoria(int tamanho) {
        String caracteres = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()";
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < tamanho; i++) {
            int indice = (int) (Math.random() * caracteres.length());
            builder.append(caracteres.charAt(indice));
        }
        return builder.toString();
    }

    private static String gerarStringLonga(int tamanho) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < tamanho; i++) {
            builder.append("A");
        }
        return builder.toString();
    }
}

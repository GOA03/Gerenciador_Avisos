package teste;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class TesteServidorProtocolo {
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

        // Adiciona cenários para cada tipo de operação
        testes.addAll(gerarTestesLogin());
        testes.addAll(gerarTestesLogout());
        testes.addAll(gerarTestesCadastroUsuario());
        testes.addAll(gerarTestesErrosGlobais());

        return testes;
    }

    private static List<String> gerarTestesLogin() {
        List<String> testes = new ArrayList<>();

        for (int i = 1; i <= 1250; i++) {
            // Cenários válidos
            testes.add(String.format("{\"operacao\":\"login\", \"ra\":123456%d, \"senha\":\"senha%d\"}", i, i));

            // JSONs inválidos ou malformados
            testes.add("{\"operacao\":\"login\", \"ra\":null, \"senha\":null}");
            testes.add("{\"operacao\":\"login\", \"ra\":\"\"}");
            testes.add("{\"operacao\":\"login\", \"ra\":1234567, \"senha\":[]}");

            // Dados inesperados
            testes.add("{\"operacao\":\"login\", \"ra\":123456, \"senha\":\"<script>alert('XSS')</script>\"}");
            testes.add("{\"operacao\":\"login\", \"ra\":\"não-numérico\", \"senha\":\"123\"}");
            testes.add("{\"operacao\":\"login\", \"ra\":123456, \"senha\":null}");

            // Testes extremos
            testes.add("{\"operacao\":\"login\", \"ra\":123456, \"senha\":\"" + gerarStringLonga(10000) + "\"}");
            testes.add("{\"operacao\":\"login\"}");
        }

        return testes;
    }

    private static List<String> gerarTestesLogout() {
        List<String> testes = new ArrayList<>();

        for (int i = 1; i <= 1250; i++) {
            // Cenários válidos
            testes.add("{\"operacao\":\"logout\", \"token\":\"" + i + "\"}");

            // JSONs inválidos ou incompletos
            testes.add("{\"operacao\":\"logout\", \"token\":null}");
            testes.add("{\"operacao\":\"logout\"}");
            testes.add("{\"operacao\":\"logout\", \"token\":123}");

            // Dados maliciosos
            testes.add("{\"operacao\":\"logout\", \"token\":\"<script>alert('hack')</script>\"}");

            // Dados inesperados
            testes.add("{\"operacao\":\"logout\", \"campoExtra\":\"valor\"}");
        }

        return testes;
    }

    private static List<String> gerarTestesCadastroUsuario() {
        List<String> testes = new ArrayList<>();

        for (int i = 1; i <= 1250; i++) {
            // Cenários válidos
            testes.add(String.format("{\"operacao\":\"cadastrarUsuario\", \"ra\":123456%d, \"senha\":\"senha%d\", \"nome\":\"Usuario%d\"}", i, i, i));

            // JSONs inválidos ou incompletos
            testes.add("{\"operacao\":\"cadastrarUsuario\", \"ra\":123456, \"senha\":\"\", \"nome\":\"Fulano\"}");
            testes.add("{\"operacao\":\"cadastrarUsuario\", \"ra\":null, \"senha\":\"senha123\", \"nome\":\"Fulano\"}");

            // Dados maliciosos
            testes.add("{\"operacao\":\"cadastrarUsuario\", \"ra\":123456, \"senha\":\"<script>malicious()</script>\", \"nome\":\"Fulano\"}");

            // Testes extremos
            testes.add("{\"operacao\":\"cadastrarUsuario\", \"ra\":123456, \"senha\":\"senha123\", \"nome\":\"" + gerarStringLonga(5000) + "\"}");
        }

        return testes;
    }

    private static List<String> gerarTestesErrosGlobais() {
        List<String> testes = new ArrayList<>();

        for (int i = 1; i <= 1250; i++) {
            // Operações inexistentes
            testes.add("{\"operacao\":\"naoExistente" + i + "\"}");
            testes.add("{\"operacao\":\"\", \"campo\":\"valor\"}");

            // JSONs malformados
            testes.add("{\"operacao\":\"");

            // Dados extremos
            testes.add("{\"operacao\":\"" + gerarStringLonga(2000) + "\"}");
        }

        return testes;
    }

    private static String gerarStringLonga(int tamanho) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < tamanho; i++) {
            builder.append("A");
        }
        return builder.toString();
    }
}

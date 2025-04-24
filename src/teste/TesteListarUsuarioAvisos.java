package teste;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class TesteListarUsuarioAvisos {
    private static final String HOST = "127.0.0.1"; // Endereço IP do servidor
    private static final int PORT = 8080; // Porta do servidor

    public static void main(String[] args) {
        try (Socket socket = new Socket(HOST, PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // Enviar uma mensagem de login com RA 2376342
//            String mensagemLogin = "{\"operacao\":\"login\", \"ra\":\"2376342\", \"senha\":\"testeteste\"}";
//            enviarMensagem(out, in, mensagemLogin);

            // Enviar uma mensagem para listar avisos do usuário
            String mensagemListarAvisos = "{\"operacao\":\"listarUsuarioAvisos\", \"ra\":\"2376342\", \"token\":\"seu_token_aqui\"}";
            enviarMensagem(out, in, mensagemListarAvisos);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void enviarMensagem(PrintWriter out, BufferedReader in, String mensagem) throws IOException {
        out.println(mensagem);
        System.out.println("Enviado: " + mensagem);
        String resposta = in.readLine();
        System.out.println("Resposta do servidor: " + resposta);
    }
}
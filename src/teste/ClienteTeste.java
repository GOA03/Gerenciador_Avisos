package teste;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClienteTeste {
    private static final String HOST = "127.0.0.1"; // Endereço IP do servidor
    private static final int PORT = 3; // Porta do servidor

    public static void main(String[] args) {
        try (Socket socket = new Socket(HOST, PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // Teste 1: Enviar uma mensagem válida
            String mensagemValida = "{\"operacao\":\"teste\"}";
            enviarMensagem(out, in, mensagemValida);

            // Teste 2: Enviar uma mensagem inválida (não JSON)
            String mensagemInvalida = "a";
            enviarMensagem(out, in, mensagemInvalida);

            // Teste 3: Enviar um JSON sem a chave "operacao"
            String mensagemSemChave = "{}";
            enviarMensagem(out, in, mensagemSemChave);

            // Teste 4: Enviar um JSON com chave "operacao" mas valor nulo
            String mensagemValorNulo = "{\"operacao\":null}";
            enviarMensagem(out, in, mensagemValorNulo);

            // Teste 5: Enviar um JSON com chave "operacao" vazia
            String mensagemVazia = "{\"operacao\":\"\"}";
            enviarMensagem(out, in, mensagemVazia);

            // Teste 6: Enviar um JSON malformado
            String mensagemMalformada = "{\"operacao\":\"teste\""; // Falta o fechamento
            enviarMensagem(out, in, mensagemMalformada);

            // Teste 7: Enviar um JSON com caracteres especiais
            String mensagemCaracteresEspeciais = "{\"operacao\":\"teste!@#$%^&*()\"}";
            enviarMensagem(out, in, mensagemCaracteresEspeciais);

            // Teste 8: Enviar um JSON com um número como operação
            String mensagemNumero = "{\"operacao\":12345}";
            enviarMensagem(out, in, mensagemNumero);

            // Teste 9: Enviar um JSON com um array como operação
            String mensagemArray = "{\"operacao\":[\"op1\", \"op2\"]}";
            enviarMensagem(out, in, mensagemArray);

            // Teste 10: Enviar um JSON com um objeto como operação
            String mensagemObjeto = "{\"operacao\":{\"subOperacao\":\"teste\"}}";
            enviarMensagem(out, in, mensagemObjeto);

            // Teste 11: Enviar um JSON com um número negativo
            String mensagemNumeroNegativo = "{\"operacao\":-12345}";
            enviarMensagem(out, in, mensagemNumeroNegativo);

            // Teste 12: Enviar um JSON com um número decimal
            String mensagemDecimal = "{\"operacao\":123.45}";
            enviarMensagem(out, in, mensagemDecimal);

            // Teste 13: Enviar um JSON com um array vazio
            String mensagemArrayVazio = "{\"operacao\":[]}";
            enviarMensagem(out, in, mensagemArrayVazio);

            // Teste 14: Enviar um JSON com um objeto vazio
            String mensagemObjetoVazio = "{\"operacao\":{}}";
            enviarMensagem(out, in, mensagemObjetoVazio);

            // Teste 15: Enviar um JSON com uma string muito longa
            String mensagemLonga = "{\"operacao\":\"" + "a".repeat(10000) + "\"}";
            enviarMensagem(out, in, mensagemLonga);

            // Teste 16: Enviar um JSON com caracteres Unicode
            String mensagemUnicode = "{\"operacao\":\"Teste com Unicode: \uD83D\uDE00\"}";
            enviarMensagem(out, in, mensagemUnicode);

            // Teste 17: Enviar um JSON com uma chave não esperada
            String mensagemChaveInesperada = "{\"outraChave\":\"valor\"}";
            enviarMensagem(out, in, mensagemChaveInesperada);

            // Teste 18: Enviar um JSON com um valor booleano
            String mensagemBooleano = "{\"operacao\":true}";
            enviarMensagem(out, in, mensagemBooleano);

            // Teste 19: Enviar um JSON com um valor de data (string)
            String mensagemData = "{\"operacao\":\"2023-10-01T12 :00:00\"}";
            enviarMensagem(out, in, mensagemData);

            // Teste 20: Enviar um JSON com um valor de data inválido
            String mensagemDataInvalida = "{\"operacao\":\"2023-13-01\"}";
            enviarMensagem(out, in, mensagemDataInvalida);

            // Teste 21: Enviar um JSON com um valor de hora inválido
            String mensagemHoraInvalida = "{\"operacao\":\"25:00:00\"}";
            enviarMensagem(out, in, mensagemHoraInvalida);

            // Teste 22: Enviar um JSON com um valor de operação muito grande
            String mensagemOperacaoGrande = "{\"operacao\":\"" + "op".repeat(1000) + "\"}";
            enviarMensagem(out, in, mensagemOperacaoGrande);

            // Teste 23: Enviar um JSON com um valor de operação que é um array de objetos
            String mensagemArrayObjetos = "{\"operacao\":[{\"subOperacao\":\"op1\"}, {\"subOperacao\":\"op2\"}]}";
            enviarMensagem(out, in, mensagemArrayObjetos);

            // Teste 24: Enviar um JSON com um valor de operação que é um objeto aninhado
            String mensagemObjetoAninhado = "{\"operacao\":{\"nivel1\":{\"nivel2\":\"teste\"}}}";
            enviarMensagem(out, in, mensagemObjetoAninhado);

            // Teste 25: Enviar um JSON com um valor de operação que é um número muito grande
            String mensagemNumeroGrande = "{\"operacao\":9223372036854775807}"; // Long.MAX_VALUE
            enviarMensagem(out, in, mensagemNumeroGrande);

            // Teste 26: Enviar um JSON com um valor de operação que é um número muito pequeno
            String mensagemNumeroPequeno = "{\"operacao\":-9223372036854775808}"; // Long.MIN_VALUE
            enviarMensagem(out, in, mensagemNumeroPequeno);

            // Teste 27: Enviar um JSON com um valor de operação que é um número fora do intervalo
            String mensagemNumeroForaDoIntervalo = "{\"operacao\":1.7976931348623157E+308}"; // Double.MAX_VALUE
            enviarMensagem(out, in, mensagemNumeroForaDoIntervalo);

            // Teste 28: Enviar um JSON com um valor de operação que é um número negativo fora do intervalo
            String mensagemNumeroNegativoForaDoIntervalo = "{\"operacao\":-1.7976931348623157E+308}"; // Double.MIN_VALUE
            enviarMensagem(out, in, mensagemNumeroNegativoForaDoIntervalo);

            // Teste 29: Enviar um JSON com um valor de operação que é um array de números
            String mensagemArrayNumeros = "{\"operacao\":[1, 2, 3, 4, 5]}";
            enviarMensagem(out, in, mensagemArrayNumeros);

            // Teste 30: Enviar um JSON com um valor de operação que é um array de strings
            String mensagemArrayStrings = "{\"operacao\":[\"string1\", \"string2\", \"string3\"]}";
            enviarMensagem(out, in, mensagemArrayStrings);

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
package teste;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClienteTesteCadastro {
    private static final String HOST = "127.0.0.1"; // Endereço IP do servidor
    private static final int PORT = 3; // Porta do servidor

    public static void main(String[] args) {
        try (Socket socket = new Socket(HOST, PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // Teste 1: Enviar um JSON válido para cadastro
            String mensagemValida = "{\"operacao\":\"cadastrarUsuario\", \"ra\":\"1234567\", \"senha\":\"senha123\", \"nome\":\"Usuario Teste\"}";
            enviarMensagem(out, in, mensagemValida);

            // Teste 2: Enviar um JSON com RA inválido (menos de 7 dígitos)
            String mensagemRaInvalido = "{\"operacao\":\"cadastrarUsuario\", \"ra\":\"12345\", \"senha\":\"senha123\", \"nome\":\"Usuario Teste\"}";
            enviarMensagem(out, in, mensagemRaInvalido);

            // Teste 3: Enviar um JSON com senha inválida (menos de 8 caracteres)
            String mensagemSenhaInvalida = "{\"operacao\":\"cadastrarUsuario\", \"ra\":\"1234567\", \"senha\":\"123\", \"nome\":\"Usuario Teste\"}";
            enviarMensagem(out, in, mensagemSenhaInvalida);

            // Teste 4: Enviar um JSON com campos vazios
            String mensagemCamposVazios = "{\"operacao\":\"cadastrarUsuario\", \"ra\":\"\", \"senha\":\"\", \"nome\":\"\"}";
            enviarMensagem(out, in, mensagemCamposVazios);

            // Teste 5: Enviar um JSON com RA já cadastrado
            String mensagemRaJaCadastrado = "{\"operacao\":\"cadastrarUsuario\", \"ra\":\"admin\", \"senha\":\"senha123\", \"nome\":\"Usuario Admin\"}";
            enviarMensagem(out, in, mensagemRaJaCadastrado);

            // Teste 6: Enviar um JSON malformado
            String mensagemMalformada = "{\"operacao\":\"cadastrarUsuario\", \"ra\":\"1234567\", \"senha\":\"senha123\""; // Falta o fechamento
            enviarMensagem(out, in, mensagemMalformada);

            // Teste 7: Enviar um JSON com caracteres especiais no nome
            String mensagemCaracteresEspeciais = "{\"operacao\":\"cadastrarUsuario\", \"ra\":\"1234567\", \"senha\":\"senha123\", \"nome\":\"Usuario!@#$%^&*()\"}";
            enviarMensagem(out, in, mensagemCaracteresEspeciais);

            // Teste 8: Enviar um JSON com RA nulo
            String mensagemRaNulo = "{\"operacao\":\"cadastrarUsuario\", \"ra\":null, \"senha\":\"senha123\", \"nome\":\"Usuario Teste\"}";
            enviarMensagem(out, in, mensagemRaNulo);

            // Teste 9: Enviar um JSON com senha nula
            String mensagemSenhaNula = "{\"operacao\":\"cadastrarUsuario\", \"ra\":\"1234567\", \"senha\":null, \"nome\":\"Usuario Teste\"}";
            enviarMensagem(out, in, mensagemSenhaNula);

            // Teste 10: Enviar um JSON sem a chave "operacao"
            String mensagemSemOperacao = "{\"ra\":\"1234567\", \"senha\":\"senha123\", \"nome\":\"Usuario Teste\"}";
            enviarMensagem(out, in, mensagemSemOperacao);

            // Teste 11: Enviar um JSON com RA muito longo (mais de 7 dígitos)
            String mensagemRaMuitoLongo = "{\"operacao\":\"cadastrarUsuario\", \"ra\":\"12345678901234567\", \"senha\":\"senha123\", \"nome\":\"Usuario Teste\"}";
            enviarMensagem(out, in, mensagemRaMuitoLongo);

            // Teste 12: Enviar um JSON com senha muito longa (mais de 20 caracteres)
            String mensagemSenhaMuitoLonga = "{\"operacao\":\"cadastrarUsuario\", \"ra\":\"1234567\", \"senha\":\"" + "a".repeat(21) + "\", \"nome\":\"Usuario Teste\"}";
            enviarMensagem(out, in, mensagemSenhaMuitoLonga);

            // Teste 13: Enviar um JSON com nome muito longo (mais de 50 caracteres)
            String mensagemNomeMuitoLongo = "{\"operacao\":\"cadastrarUsuario\", \"ra\":\"1234567\", \"senha\":\"senha123\", \"nome\":\"" + "A".repeat(51) + "\"}";
            enviarMensagem(out, in, mensagemNomeMuitoLongo);

            // Teste 14: Enviar um JSON com caracteres Unicode no nome
            String mensagemUnicode = "{\"operacao\":\"cadastrarUsuario\", \"ra\":\"1234567\", \"senha\":\"senha123\", \"nome\":\"Usuario com Unicode: \uD83D\uDE00\"}";
            enviarMensagem(out, in, mensagemUnicode);

            // Teste 15: Enviar um JSON com um número como RA
            String mensagemRaNumero = "{\"operacao\":\"cadastrarUsuario\", \"ra\":1234567, \"senha\":\"senha123\", \"nome\":\"Usuario Teste\"}";
            enviarMensagem(out, in, mensagemRaNumero);

            // Teste 16: Enviar um JSON com um objeto como RA
            String mensagemRaObjeto = "{\"operacao\":\"cadastrarUsuario\", \"ra\":{\"id\":1}, \"senha\":\"senha123\", \"nome\":\"Usuario Teste\"}";
            enviarMensagem(out, in, mensagemRaObjeto);

            // Teste 17: Enviar um JSON com um array como RA
            String mensagemRaArray = "{\"operacao\":\"cadastrarUsuario\", \"ra\":[\"1234567\"], \"senha\":\"senha123\", \"nome\":\"Usuario Teste\"}";
            enviarMensagem(out, in, mensagemRaArray);

            // Teste 18: Enviar um JSON com um valor booleano como RA
            String mensagemRaBooleano = "{\"operacao\":\"cadastrarUsuario\", \"ra\":true, \"senha\":\"senha123\", \"nome\":\"Usuario Teste\"}";
            enviarMensagem(out, in, mensagemRaBooleano);

            // Teste 19: Enviar um JSON com um valor de data como RA
            String mensagemRaData = "{\"operacao\":\"cadastrarUsuario\", \"ra\":\"2023-10-01\", \"senha\":\"senha123\", \"nome\":\"Usuario Teste\"}";
            enviarMensagem(out, in, mensagemRaData);

            // Teste 20: Enviar um JSON com um valor de operação muito grande
            String mensagemOperacaoGrande = "{\"operacao\":\"" + "op".repeat(1000) + "\", \"ra\":\"1234567\", \"senha\":\"senha123\", \"nome\":\"Usuario Teste\"}";
            enviarMensagem(out, in, mensagemOperacaoGrande);

            // Teste 21: Enviar um JSON com um valor de operação que é um objeto aninhado
            String mensagemObjetoAninhado = "{\"operacao\":{\"nivel1\":{\"nivel2\":\"teste\"}}, \"ra\":\"1234567\", \"senha\":\"senha123\", \"nome\":\"Usuario Teste\"}";
            enviarMensagem(out, in, mensagemObjetoAninhado);

            // Teste 22: Enviar um JSON com um valor de operação que é um array de objetos
            String mensagemArrayObjetos = "{\"operacao\":[{\"subOperacao\":\"op1\"}, {\"subOperacao\":\"op2\"}], \"ra\":\"1234567\", \"senha\":\"senha123\", \"nome\":\"Usuario Teste\"}";
            enviarMensagem(out, in, mensagemArrayObjetos);

            // Teste 23: Enviar um JSON com um valor de operação que é um número muito grande
            String mensagemNumeroGrande = "{\"operacao\":9223372036854775807, \"ra\":\"1234567\", \"senha\":\"senha123\", \"nome\":\"Usuario Teste\"}";
            enviarMensagem(out, in, mensagemNumeroGrande);

            // Teste 24: Enviar um JSON com um valor de operação que é um número muito pequeno
            String mensagemNumeroPequeno = "{\"operacao\":-9223372036854775808, \"ra\":\"1234567\", \"senha\":\"senha123\", \"nome\":\"Usuario Teste\"}";
            enviarMensagem(out, in, mensagemNumeroPequeno);

            // Teste 25: Enviar um JSON com um valor de operação que é um número fora do intervalo
            String mensagemNumeroForaDoIntervalo = "{\"operacao\":1.7976931348623157E+308, \"ra\":\"1234567\", \"senha\":\"senha123\", \"nome\":\"Usuario Teste\"}";
            enviarMensagem(out, in, mensagemNumeroForaDoIntervalo);

            // Teste 26: Enviar um JSON com um valor de operação que é um número negativo fora do intervalo
            String mensagemNumeroNegativoForaDoIntervalo = "{\"operacao\":-1.7976931348623157E+308, \"ra\":\"1234567 \", \"senha\":\"senha123\", \"nome\":\"Usuario Teste\"}";
            enviarMensagem(out, in, mensagemNumeroNegativoForaDoIntervalo);

            // Teste 27: Enviar um JSON com um valor de operação que é um array de números
            String mensagemArrayNumeros = "{\"operacao\":[1, 2, 3, 4, 5], \"ra\":\"1234567\", \"senha\":\"senha123\", \"nome\":\"Usuario Teste\"}";
            enviarMensagem(out, in, mensagemArrayNumeros);

            // Teste 28: Enviar um JSON com um valor de operação que é um array de strings
            String mensagemArrayStrings = "{\"operacao\":[\"string1\", \"string2\", \"string3\"], \"ra\":\"1234567\", \"senha\":\"senha123\", \"nome\":\"Usuario Teste\"}";
            enviarMensagem(out, in, mensagemArrayStrings);

            // Teste 29: Enviar um JSON com um valor de operação que é um array vazio
            String mensagemArrayVazio = "{\"operacao\":[], \"ra\":\"1234567\", \"senha\":\"senha123\", \"nome\":\"Usuario Teste\"}";
            enviarMensagem(out, in, mensagemArrayVazio);

            // Teste 30: Enviar um JSON com um valor de operação que é um objeto vazio
            String mensagemObjetoVazio = "{\"operacao\":{}, \"ra\":\"1234567\", \"senha\":\"senha123\", \"nome\":\"Usuario Teste\"}";
            enviarMensagem(out, in, mensagemObjetoVazio);

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
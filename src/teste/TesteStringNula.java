package teste;

public class TesteStringNula {

    public static void main(String[] args) {
        // Simula o erro passando uma string nula
        try {
            processarString(null); // Passa null para o método
        } catch (NullPointerException e) {
            // Captura e imprime a mensagem de erro
            System.err.println("Erro capturado: " + e.getMessage());
        }
    }

    private static void processarString(String s) {
        // Tenta acessar o método length() em uma string que pode ser nula
        System.out.println("O comprimento da string é: " + s.length()); // Isso causará NullPointerException
    }
}
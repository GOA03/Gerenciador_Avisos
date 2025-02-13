package teste;

public class ServidorTeste {
    public static void main(String[] args) {
        try {
            // Não inicia o servidor, apenas simula que ele não está disponível
            System.out.println("Servidor não iniciado. O cliente deve tentar se conectar e falhar.");
            // Aguarda indefinidamente para simular um servidor que não responde
            Thread.sleep(Long.MAX_VALUE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
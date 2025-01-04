package model;

import java.io.*;
import java.net.*;
import java.text.ParseException;
import java.util.List;

import javax.swing.JOptionPane;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import controller.JSONController;
import view.AdminMainView;
import view.AvisosView;
import view.CadastroView;
import view.GerenciadorUsuariosView;
import view.LoginView;
import view.MainView;

public class ClienteModel {

    private Socket socketEcho;
    private PrintWriter saida;
    private BufferedReader entrada;
    private Thread threadEscuta;
    private JSONController jsonController;
    private String token;
    private AvisosView avisosView;
    private LoginView loginView;
    private CadastroView cadastroView;
    private MainView telaPrincipal;
	private AdminMainView telaAdmin;

    // Método para conectar ao servidor
    public synchronized void conectar(String ip, int porta) throws IOException {
        socketEcho = new Socket(ip, porta);
        saida = new PrintWriter(socketEcho.getOutputStream(), true);
        entrada = new BufferedReader(new InputStreamReader(socketEcho.getInputStream()));
        jsonController = new JSONController();

        threadEscuta = new Thread(() -> {
            System.out.println("Escutando mensagens do servidor...");
            try {
                String msg;
                while ((msg = entrada.readLine()) != null) {
                    System.out.println("SERVIDOR -> CLIENTE: " + msg);
                    RespostaModel resposta = jsonController.changeResponseToJson(msg);

                    if (resposta == null) {
                        System.out.println("Resposta nula recebida do servidor.");
                        continue;
                    }

                    int status = resposta.getStatus();
                    String mensagem = resposta.getMsg();
                    String token = resposta.getToken();
                    String operacao = resposta.getOperacao();
                    List<UsuarioModel> usuarios = resposta.getUsuarios();

                    if (operacao == null) {
                        tratarOperacaoNula(status, token, mensagem);
                    } else {
                        tratarOperacao(operacao, status, mensagem, token, usuarios);
                    }
                }
            } catch (IOException e) {
                tratarIOException(e);
            }
        });
        threadEscuta.start();
    }

    // Lida com operações nulas
    private void tratarOperacaoNula(int status, String token, String mensagem) {
        if (status == 200 && token != null) {
            JOptionPane.showMessageDialog(null, "Login realizado com sucesso!");
            tratarLogin(status, mensagem, token);
        } else if (status == 200) {
            JOptionPane.showMessageDialog(null, "Logout realizado com sucesso!");
            new LoginView(this).setVisible(true);
        } else {
            JOptionPane.showMessageDialog(null, mensagem);
        }
    }

    // Lida com as operações recebidas do servidor
    private void tratarOperacao(String operacao, int status, String mensagem, String token, List<UsuarioModel> usuarios) {
        switch (operacao) {
            case "login":
                tratarLogin(status, mensagem, token);
                break;
            case "cadastrarUsuario":
                tratarCadastro(status, mensagem);
                break;
            case "logout":
                tratarLogout(status, mensagem, token);
                break;
            case "listarUsuarios":
            	tratarListaUsuario(usuarios);
                break;
            case "excluirUsuarios":
            	//tratarExcluirUsuario();
                break;
            default:
                JOptionPane.showMessageDialog(null, mensagem + ": " + operacao);
                System.out.println("Operação inválida ou não reconhecida.");
                break;
        }
    }

	// Lida com login
    private void tratarLogin(int status, String mensagem, String token) {
    	// Verifica se o token é nulo
        if (token == null) {
            JOptionPane.showMessageDialog(loginView, "Token não recebido ou nulo.", "Erro", JOptionPane.ERROR_MESSAGE);
            return; // Sai do método se o token for nulo
        }
        
        if (status == 200) {
            if ("2376342".equals(token)) {
                loginView.dispose();
                AdminMainView telaAdimin = new AdminMainView(ClienteModel.this, token);
                telaAdimin.setVisible(true);
                this.telaAdmin = telaAdimin.getEssaTela();
            } else {
            	abrirTelaPrincipal(token);
            }
        } else {
            tratarErro(loginView, mensagem, "Erro de Login");
        }
    }

    // Lida com cadastro
    private void tratarCadastro(int status, String mensagem) {
        if (status == 201 || status == 200) {
            JOptionPane.showMessageDialog(null, "Cadastro realizado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            fecharTelaCadastro();
            new LoginView(this).setVisible(true);
        } else if (status == 404 || status == 422 || status == 401) {
            tratarErro(null, mensagem, "Erro de Cadastro");
        }
    }

    // Lida com logout
    private void tratarLogout(int status, String mensagem, String token) {
        if (status == 200) {
        	System.out.println(avisosView);
            if (avisosView != null) {
                avisosView.dispose();
            }
            JOptionPane.showMessageDialog(null, "Logout realizado com sucesso!");
            new LoginView(this).setVisible(true);
        } else {
            tratarErro(loginView, mensagem, "Erro de Logout");
        }
    }
    
    private void tratarListaUsuario(List<UsuarioModel> usuarios) {
    	
    	if (telaAdmin.getToken() != null) {
			
    		String token = telaAdmin.getToken();
    		GerenciadorUsuariosView gerenciadorUsuario = new GerenciadorUsuariosView(this, token);
        	gerenciadorUsuario.setTelaAdmin(telaAdmin);
        	gerenciadorUsuario.atualizarTabelaUsuarios(usuarios);
            gerenciadorUsuario.setVisible(true);
		} else { System.out.println("Sem tela de Admin"); }
    }

    // Lida com erros exibindo mensagens apropriadas
    private void tratarErro(Object parent, String mensagem, String titulo) {
        if (mensagem == null || mensagem.trim().isEmpty()) {
            mensagem = "Erro desconhecido. Sem mensagem disponível.";
        }
        JOptionPane.showMessageDialog((parent instanceof java.awt.Component ? (java.awt.Component) parent : null), mensagem, titulo, JOptionPane.ERROR_MESSAGE);
    }

    // Lida com exceções de IO
    private void tratarIOException(IOException e) {
        if (e.getMessage() != null && e.getMessage().contains("Connection reset")) {
            JOptionPane.showMessageDialog(null, "O servidor foi fechado.", "Servidor Fechado", JOptionPane.WARNING_MESSAGE);
            System.exit(0);
        } else {
            e.printStackTrace();
        }
    }

    public void abrirTelaPrincipal(String token) {
        if (loginView != null) {
            loginView.dispose();
            telaPrincipal = new MainView(this, token);
            telaPrincipal.setVisible(true);
        }
    }

    public AvisosView getAvisosView() {
        return avisosView;
    }

    public void fecharTelaCadastro() {
        if (cadastroView != null) {
            cadastroView.dispose();
        }
    }

    public void setAvisosView(AvisosView avisosView) {
        this.avisosView = avisosView;
    }

    public synchronized void enviarMensagem(String mensagem) throws IOException {
        saida.println(mensagem);
    }

    public synchronized String receberResposta() throws IOException {
        return entrada.readLine();
    }

    public JSONObject receberRespostaJSON() throws IOException, ParseException, org.json.simple.parser.ParseException {
        String resposta = receberResposta();
        JSONParser parser = new JSONParser();
        return (JSONObject) parser.parse(resposta);
    }

    public synchronized void fecharConexao() throws IOException {
        if (saida != null) saida.close();
        if (entrada != null) entrada.close();
        if (socketEcho != null) socketEcho.close();
    }

    public void enviarMensagem(JSONObject msg) {
        System.out.println("CLIENTE -> SERVIDOR: " + msg.toString());
        saida.println(msg.toString());
    }

    public void logarAvisosView(String token) {
        this.setToken(token);
        if (loginView != null) {
            loginView.dispose();
        }
        avisosView = new AvisosView(this, token);
        avisosView.setVisible(true);
    }

    public void setLoginView(LoginView loginView) {
        this.loginView = loginView;
    }

    public void setCadastroView(CadastroView cadastroView) {
        this.cadastroView = cadastroView;
    }

    public String getToken() {
        return token;
    }

    private void setToken(String token) {
        this.token = token;
    }
}

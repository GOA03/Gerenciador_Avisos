package view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.json.simple.JSONObject;

import controller.JSONController;
import model.ClienteModel;
import model.UsuarioModel;

public class UsuarioUpdateView extends JFrame {

	private static final long serialVersionUID = 1L;
	private JTextField nomeField;
	private JTextField raField;
	private JTextField senhaField;
	private JButton btnSalvar;
	private JButton btnVoltar;
	private ClienteModel cliente;
	private String token;
	private String nome;
	private String senha;

	public UsuarioUpdateView(ClienteModel cliente, String token, UsuarioInfoView telaInfoUsuario, String nome,
			String senha) {

		this.cliente = cliente;
		this.token = token;
		this.nome = nome;
		this.senha = senha;

		setTitle("Atualizar Informações do Usuário");
		setSize(400, 250);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null); // Centraliza a janela

		JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
		mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));

		formPanel.add(new JLabel("RA:"));
		raField = new JTextField(token);
		raField.setEditable(false);
		formPanel.add(raField);

		formPanel.add(new JLabel("Nome:"));
		nomeField = new JTextField(this.nome);
		formPanel.add(nomeField);

		formPanel.add(new JLabel("Senha:"));
		senhaField = new JTextField(this.senha);
		formPanel.add(senhaField);

		mainPanel.add(formPanel, BorderLayout.CENTER);

		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

		btnSalvar = new JButton("Salvar");
		btnSalvar.addActionListener(e -> salvarInformacoes());
		buttonPanel.add(btnSalvar);

		btnVoltar = new JButton("Voltar");
		btnVoltar.addActionListener(e -> voltarInfo());
		buttonPanel.add(btnVoltar);

		mainPanel.add(buttonPanel, BorderLayout.SOUTH);

		getContentPane().add(mainPanel);
		
		addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                cliente.deslogarUsuario(); // Desloga o usuário ao fechar a janela
                System.exit(0); // Encerra a aplicação
            }
        });
	}

	private void voltarInfo() {
		dispose();
		UsuarioModel usuario = new UsuarioModel();
		usuario.setOperacao("localizarUsuario");
		usuario.setToken(token); // Usando o token do cliente
		usuario.setRa(token); // Usando o token do cliente

		// Converte o usuário para JSON
		JSONController jsonController = new JSONController();
		JSONObject res = jsonController.changeToJSON(usuario);

		// Envia a requisição ao servidor
		cliente.enviarMensagem(res);
	}

	private void salvarInformacoes() {
		// Obtém o RA do usuário (mantido como não editável)
		String ra = raField.getText();

		// Atualiza o nome e a senha com os valores dos campos de texto
		String nome = nomeField.getText().trim();
		String senha = new String(senhaField.getText()).trim();

		// Verifica se os campos obrigatórios estão preenchidos
		if (nome.isEmpty() || senha.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Por favor, preencha todos os campos!", "Erro",
					JOptionPane.ERROR_MESSAGE);
			return; // Sai do método se os campos não estiverem preenchidos
		}

		// Cria um objeto UsuarioModel para enviar ao JSON Controller
		UsuarioModel usuario = new UsuarioModel();
		usuario.setRa(ra); // Define o RA do usuário
		usuario.setNome(nome); // Define o Nome do usuário
		usuario.setSenha(senha); // Define a Senha do usuário
		usuario.setToken(token);

		// Converte o usuário para JSON
		JSONController jsonController = new JSONController();
		JSONObject res = jsonController.changeUserUpdateToJSON(usuario);

		// Envia a requisição ao servidor
		if (this.cliente != null) {
			this.cliente.enviarMensagem(res);
		}

		voltarInfo();
		dispose();
	}
}

package view;

import javax.swing.*;
import org.json.simple.JSONObject;
import controller.JSONController;
import model.ClienteModel;
import model.UsuarioModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CadastroView extends JFrame {

	private static final long serialVersionUID = 1L;
	private JTextField nomeField, raField;
	private JPasswordField passwordField;
	private JButton cadastrarButton, voltarButton;
	private ClienteModel cliente;

	public CadastroView(ClienteModel cliente) {

		this.cliente = cliente;
		setTitle("Cadastro");
		setSize(400, 213);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);

		JPanel panel = new JPanel();
		getContentPane().add(panel);

		JLabel lblNome = new JLabel("Nome:");
		nomeField = new JTextField();
		nomeField.setPreferredSize(new Dimension(150, 25)); // Ajusta o tamanho do campo

		JLabel lblRa = new JLabel("RA:");
		raField = new JTextField();
		raField.setPreferredSize(new Dimension(150, 25)); // Ajusta o tamanho do campo

		JLabel lblSenha = new JLabel("Senha:");
		passwordField = new JPasswordField();
		passwordField.setPreferredSize(new Dimension(150, 25)); // Ajusta o tamanho do campo

		cadastrarButton = new JButton("Cadastrar");

		// Adicionando ActionListener para o botão Cadastrar
		cadastrarButton.addActionListener(new ActionListener() {
			@SuppressWarnings("deprecation")
			public void actionPerformed(ActionEvent e) {
				// Cria um novo objeto UsuarioModel e preenche com os dados do formulário
				UsuarioModel usuario = new UsuarioModel();
				usuario.setOperacao("cadastrarUsuario");
				usuario.setRa(raField.getText()); // Alterado para String
				usuario.setSenha(passwordField.getText().toUpperCase());
				usuario.setNome(nomeField.getText().toUpperCase());

				// Converte o usuário para JSON
				JSONController cadastroUsuarioController = new JSONController();
				JSONObject res = cadastroUsuarioController.changeToJSON(usuario);

				// Processa a resposta do servidor
				processarCadastro(res);
			}
		});

		voltarButton = new JButton("Voltar");

		// Usando GroupLayout para garantir compatibilidade com WindowBuilder
		GroupLayout layout = new GroupLayout(panel);
		panel.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(lblNome)
						.addComponent(lblRa).addComponent(lblSenha))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(nomeField)
						.addComponent(raField).addComponent(passwordField).addGroup(layout.createSequentialGroup()
								.addComponent(cadastrarButton).addComponent(voltarButton))));

		layout.setVerticalGroup(layout.createSequentialGroup().addGap(20) // Espaço do topo
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(lblNome)
						.addComponent(nomeField))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(lblRa)
						.addComponent(raField))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(lblSenha)
						.addComponent(passwordField))
				.addGap(20) // Espaço entre os campos e os botões
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(cadastrarButton)
						.addComponent(voltarButton))
				.addGap(20) // Espaço do fundo
		);

		// Adicionar ActionListener para o botão Voltar
		voltarButton.addActionListener(e -> {
			this.dispose(); // Fecha a tela de cadastro
			new LoginView(this.cliente).setVisible(true); // Abre a tela de login
		});
	}

	protected void processarCadastro(JSONObject res) {
		if (this.cliente == null) {
			System.out.println("O cliente está nulo, inicie o cliente e o servidor");
		} else {
			this.cliente.setCadastroView(this);
			this.cliente.enviarMensagem(res);
		}
	}

	public String getNome() {
		return nomeField.getText();
	}

	public String getRa() {
		return raField.getText();
	}

	public String getPassword() {
		return new String(passwordField.getPassword());
	}

	public void adicionarActionListenerCadastrar(ActionListener listener) {
		cadastrarButton.addActionListener(listener);
	}
}
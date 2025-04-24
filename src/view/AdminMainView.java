package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import org.json.simple.JSONObject;

import controller.JSONController;
import model.ClienteModel;
import model.RespostaModel;
import model.UsuarioModel;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AdminMainView extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private ClienteModel cliente;
	private String token;
	private AdminMainView essaTela;

	public AdminMainView(ClienteModel cliente, String token) {
		this.cliente = cliente;
		this.token = token;
		this.essaTela = this;

		setTitle("Tela Principal - Admin");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(455, 340);
		setLocationRelativeTo(null); // Centraliza a janela
		contentPane = new JPanel();
		contentPane.setBackground(new Color(234, 234, 234));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel lblTitulo = new JLabel("Bem-vindo ao Painel de Administração");
		lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
		lblTitulo.setFont(new Font("Poppins", Font.PLAIN, 20));
		lblTitulo.setBounds(50, 25, 340, 30);
		contentPane.add(lblTitulo);

		JButton btnGerenciarUsuarios = new JButton("Gerenciar Usuários");
		btnGerenciarUsuarios.setFont(new Font("Poppins", Font.PLAIN, 15));
		btnGerenciarUsuarios.setBounds(120, 80, 200, 30);
		contentPane.add(btnGerenciarUsuarios);

		JButton btnGerenciarAvisos = new JButton("Gerenciar Avisos");
		btnGerenciarAvisos.setFont(new Font("Poppins", Font.PLAIN, 15));
		btnGerenciarAvisos.setBounds(120, 135, 200, 30);
		contentPane.add(btnGerenciarAvisos);

		JButton btnGerenciarCategorias = new JButton("Gerenciar Categorias");
		btnGerenciarCategorias.setFont(new Font("Poppins", Font.PLAIN, 15));
		btnGerenciarCategorias.setBounds(120, 190, 200, 30);
		contentPane.add(btnGerenciarCategorias);

		JButton btnLogout = new JButton("Logout");
		btnLogout.setFont(new Font("Poppins", Font.PLAIN, 15));
		btnLogout.setBounds(120, 245, 200, 30);
		contentPane.add(btnLogout);
		
		addWindowListener(new java.awt.event.WindowAdapter() {
		    @Override
		    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
		        cliente.deslogarUsuario(); // Desloga o usuário ao fechar a janela
		        System.exit(0); // Encerra a aplicação
		    }
		});

		// Adicionando ActionListeners
		btnGerenciarUsuarios.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				setVisible(false);

				UsuarioModel usuario = new UsuarioModel();
				usuario.setOperacao("listarUsuarios");
				usuario.setToken(token); // Usando o token do cliente

				// Converte o usuário para JSON
				JSONController jsonController = new JSONController();
				JSONObject res = jsonController.changeToJSON(usuario);

				// Envia a requisição ao servidor
				cliente.enviarMensagem(res);
			}
		});

		btnGerenciarAvisos.addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent e) {
        
		        // Cria um objeto UsuarioModel para listar os avisos
		        RespostaModel resp = new RespostaModel();
		        resp.setOperacao("listarAvisos"); // Define a operação
		        resp.setToken(token); // Usando o token do cliente
		        resp.setIdCategoria(0); // Define a categoria como 0 para listar todos os avisos

		        // Converte o usuário para JSON
		        JSONController jsonController = new JSONController();
		        JSONObject res = jsonController.changeToJSONCategoria(resp);

		        // Envia a requisição ao servidor
		        cliente.enviarMensagem(res);
		    }
		});

		btnGerenciarCategorias.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				UsuarioModel usuario = new UsuarioModel();
				usuario.setOperacao("listarCategorias");
				usuario.setToken(token); // Usando o token do cliente

				// Converte o usuário para JSON
				JSONController jsonController = new JSONController();
				JSONObject res = jsonController.changeToJSON(usuario);

				// Envia a requisição ao servidor
				cliente.enviarMensagem(res);
			}
		});

		btnLogout.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				UsuarioModel usuario = new UsuarioModel();
				usuario.setOperacao("logout");
				usuario.setToken(token);

				JSONController jsonController = new JSONController();
				JSONObject res = jsonController.changeToJSON(usuario);

				setVisible(false);
				if (cliente != null) {
					cliente.setTelaAdmin(null);
					cliente.enviarMensagem(res);
				}
			}
		});
	}

	public ClienteModel getCliente() {
		return cliente;
	}

	public String getToken() {
		return token;
	}

	public AdminMainView getEssaTela() {
		return essaTela;
	}

}
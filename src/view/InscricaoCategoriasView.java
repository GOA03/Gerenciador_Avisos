package view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import org.json.simple.JSONObject;

import controller.JSONController;
import model.CategoriaModel;
import model.ClienteModel;
import model.RespostaModel;

public class InscricaoCategoriasView extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JButton btnCadastrar;
	private JButton btnVoltar;
	private ClienteModel cliente;
	private List<CategoriaModel> categoriasDisponiveis; // Lista de categorias disponíveis
	private JPanel panelCategorias; // Painel para exibir as categorias

	public InscricaoCategoriasView(ClienteModel cliente, List<CategoriaModel> categorias) {

		this.cliente = cliente;
		this.categoriasDisponiveis = categorias;
		carregarComponentes();
	}

	private void carregarComponentes() {

		setTitle("Cadastro de Categorias");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(600, 400);
		setLocationRelativeTo(null); // Centraliza a janela

		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
		contentPane.setLayout(new BorderLayout(10, 10));
		setContentPane(contentPane);

		// Cabeçalho
		JLabel lblTitulo = new JLabel("Categorias Disponíveis para Cadastro", SwingConstants.CENTER);
		lblTitulo.setFont(new Font("Poppins", Font.BOLD, 18));
		contentPane.add(lblTitulo, BorderLayout.NORTH);

		// Painel para exibir as categorias
		panelCategorias = new JPanel();
		panelCategorias.setLayout(new BoxLayout(panelCategorias, BoxLayout.Y_AXIS));
		JScrollPane scrollPane = new JScrollPane(panelCategorias);
		contentPane.add(scrollPane, BorderLayout.CENTER);

		// Adiciona as categorias ao painel
		for (CategoriaModel categoria : categoriasDisponiveis) {
			JCheckBox checkBox = new JCheckBox(categoria.getNome());
			checkBox.setFont(new Font("Poppins", Font.PLAIN, 14));
			panelCategorias.add(checkBox);
			panelCategorias.add(Box.createVerticalStrut(5)); // Espaçamento entre checkboxes
		}

		// Painel de botões
		JPanel panelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
		btnCadastrar = new JButton("Cadastrar Selecionadas");
		btnVoltar = new JButton("Voltar");
		panelBotoes.add(btnCadastrar);
		panelBotoes.add(btnVoltar);
		contentPane.add(panelBotoes, BorderLayout.SOUTH);

		// Adiciona ActionListeners
		btnCadastrar.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				cadastrarCategoriasSelecionadas();
			}
		});

		btnVoltar.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose(); // Fecha a tela atual
				// Retorna à tela anterior (pode ser a tela principal ou outra)
				// Aqui você pode implementar a lógica para voltar à tela anterior
			}
		});
		
		addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                cliente.deslogarUsuario(); // Desloga o usuário ao fechar a janela
                System.exit(0); // Encerra a aplicação
            }
        });
	}

	private void cadastrarCategoriasSelecionadas() {
		cliente.setInscricaoCategoriasView(this);
	    for (Component component : panelCategorias.getComponents()) {
	        if (component instanceof JCheckBox) {
	            JCheckBox checkBox = (JCheckBox) component;
	            if (checkBox.isSelected()) {
	                for (CategoriaModel categoriaModel : categoriasDisponiveis) {
	                    if (checkBox.getText().equals(categoriaModel.getNome())) {
	                        // Cria um objeto RespostaModel para enviar ao servidor
	                        RespostaModel resposta = new RespostaModel();
	                        resposta.setOperacao("cadastrarUsuarioCategoria"); // Define a operação
	                        resposta.setToken(cliente.getToken()); // Usando o token do cliente
	                        resposta.setRa(cliente.getToken()); // Método para definir o RA no RespostaModel
	                        resposta.setCategoriaId(categoriaModel.getId());

	                        // Converte a resposta para JSON
	                        JSONController jsonController = new JSONController();
	                        JSONObject json = jsonController.changeToJSONIncreverseCategoria(resposta);

	                        // Envia a mensagem ao cliente
	                        cliente.enviarMensagem(json);
	                    }
	                }
	            }
	        }
	    }
	}
}
package view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import org.json.simple.JSONObject;

import controller.JSONController;
import model.CategoriaModel;
import model.ClienteModel;
import model.RespostaModel;
import model.UsuarioModel;

public class GerenciadorCategoriasView extends JFrame {

	private static final long serialVersionUID = 1L;

	// Componentes da interface
	private JPanel contentPane;
	private JTable tableCategorias;
	private DefaultTableModel tableModel;
	private JTextField txtNomeCategoria;
	private JButton btnAdicionar, btnEditar, btnRemover, btnVoltar, btnAtualizar;

	// Atributos do modelo
	private AdminMainView telaAdmin;
	private ClienteModel cliente;
	private String token;
	private List<CategoriaModel> categorias;

	public GerenciadorCategoriasView(String token, ClienteModel cliente, List<CategoriaModel> categorias) {
		this.token = token;
		this.cliente = cliente;
		this.categorias = categorias;
		inicializarComponentes();
	}

	private void inicializarComponentes() {
		setTitle("Gerenciador de Categorias");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(600, 450);
		setLocationRelativeTo(null);

		contentPane = new JPanel(new BorderLayout(10, 10));
		contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
		setContentPane(contentPane);

		// Configuração do título
		JLabel lblTitulo = new JLabel("Gerenciamento de Categorias", SwingConstants.CENTER);
		lblTitulo.setFont(new Font("Poppins", Font.BOLD, 16));
		contentPane.add(lblTitulo, BorderLayout.NORTH);

		// Configuração da tabela
		String[] colunas = { "ID", "Nome da Categoria" }; // Adicionando a coluna ID
		tableModel = new DefaultTableModel(colunas, 0);
		tableCategorias = new JTable(tableModel);
		contentPane.add(new JScrollPane(tableCategorias), BorderLayout.CENTER);

		// Painel inferior para os controles
		JPanel panelInferior = new JPanel(new BorderLayout(10, 10));
		contentPane.add(panelInferior, BorderLayout.SOUTH);

		// Painel de formulário
		JPanel panelFormulario = new JPanel(new GridLayout(2, 2, 10, 10));
		JLabel lblNomeCategoria = new JLabel("Nome da Categoria:");
		lblNomeCategoria.setFont(new Font("Poppins", Font.PLAIN, 12));
		panelFormulario.add(lblNomeCategoria);

		txtNomeCategoria = new JTextField();
		txtNomeCategoria.setFont(new Font("Poppins", Font.PLAIN, 12));
		panelFormulario.add(txtNomeCategoria);

		btnAdicionar = criarBotao("Adicionar");
		panelFormulario.add(btnAdicionar);

		btnEditar = criarBotao("Editar");
		panelFormulario.add(btnEditar);

		panelInferior.add(panelFormulario, BorderLayout.NORTH);

		// Painel para botões adicionais
		JPanel panelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
		btnRemover = criarBotao("Remover");
		panelBotoes.add(btnRemover);

		btnAtualizar = criarBotao("Listar Categorias");
		panelBotoes.add(btnAtualizar);

		btnVoltar = criarBotao("Voltar");
		panelBotoes.add(btnVoltar);

		panelInferior.add(panelBotoes, BorderLayout.CENTER);
		
		addWindowListener(new java.awt.event.WindowAdapter() {
		    @Override
		    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
		        cliente.deslogarUsuario(); // Desloga o usuário ao fechar a janela
		        System.exit(0); // Encerra a aplicação
		    }
		});

		// Adiciona eventos aos botões
		configurarEventos();
		atualizarTabelaCategorias(categorias);
	}

	private JButton criarBotao(String texto) {
		JButton botao = new JButton(texto);
		botao.setFont(new Font("Poppins", Font.PLAIN, 12));
		return botao;
	}

	private void configurarEventos() {
		btnAdicionar.addActionListener(e -> {
			String nomeCategoria = txtNomeCategoria.getText().trim();
			if (!nomeCategoria.isEmpty()) {
				adicionarCategoria(nomeCategoria);
			} else {
				mostrarMensagem("Preencha o nome da categoria!", "Erro", JOptionPane.ERROR_MESSAGE);
			}
		});

		btnEditar.addActionListener(e -> {
			int selectedRow = tableCategorias.getSelectedRow();
			if (selectedRow != -1) {
				String nomeCategoria = txtNomeCategoria.getText().trim();
				if (!nomeCategoria.isEmpty()) {
					editarCategoria(selectedRow, nomeCategoria);
				} else {
					mostrarMensagem("Preencha o nome da categoria!", " Erro", JOptionPane.ERROR_MESSAGE);
				}
			} else {
				mostrarMensagem("Selecione uma categoria para editar.", "Aviso", JOptionPane.WARNING_MESSAGE);
			}
		});

		btnVoltar.addActionListener(e -> {
			dispose();
			this.cliente.setGerenciadorCategoria(null);
			if (telaAdmin != null) {
				telaAdmin.setVisible(true);
			}
		});

		btnAtualizar.addActionListener(e -> pedirAtualizacaoCategorias());

		tableCategorias.getSelectionModel().addListSelectionListener(e -> {
			int selectedRow = tableCategorias.getSelectedRow();
			if (selectedRow != -1) {
				txtNomeCategoria.setText((String) tableModel.getValueAt(selectedRow, 1)); // Ajustado para pegar o nome
																							// da categoria
			}
		});

		btnRemover.addActionListener(e -> {
			int selectedRow = tableCategorias.getSelectedRow();
			if (selectedRow != -1) {
				// Adiciona a confirmação antes de remover
				int confirm = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja remover esta categoria?",
						"Confirmação de Exclusão", JOptionPane.YES_NO_OPTION);
				if (confirm == JOptionPane.YES_OPTION) {
					// Obtém o ID da categoria a ser removida
					int idCategoria = (int) tableModel.getValueAt(selectedRow, 0);

					localizarCategoria(idCategoria);
				}
			} else {
				mostrarMensagem("Selecione uma categoria para remover.", "Aviso", JOptionPane.WARNING_MESSAGE);
			}
		});
	}

	public void atualizarTabelaCategorias(List<CategoriaModel> categorias) {
		tableModel.setRowCount(0); // Limpa a tabela antes de adicionar novos dados
		if (categorias != null) {
			for (CategoriaModel categoria : categorias) {
				// Adiciona o ID e o nome da categoria à tabela
				tableModel.addRow(new Object[] { categoria.getId(), categoria.getNome() });
			}
		} else {
			// Adiciona uma linha com a mensagem de que não há categorias cadastradas
			tableModel.addRow(new Object[] { "Não há categorias cadastradas" });
		}
	}

	public void pedirAtualizacaoCategorias() {
		UsuarioModel usuario = new UsuarioModel();
		usuario.setOperacao("listarCategorias");
		usuario.setToken(token); // Usando o token do cliente

		// Converte o usuário para JSON
		JSONController jsonController = new JSONController();
		JSONObject res = jsonController.changeToJSON(usuario);

		// Envia a requisição ao servidor
		cliente.enviarMensagem(res);
	}

	private void localizarCategoria(int idCategoria) {

		RespostaModel resposta = new RespostaModel();
		resposta.setIdCategoria(idCategoria);
		resposta.setOperacao("localizarCategoria");
		resposta.setToken(token); // Usando o token do cliente

		// Converte o usuário para JSON
		JSONController jsonController = new JSONController();
		JSONObject res = jsonController.changeToJSON(resposta);

		// Envia a requisição ao servidor
		cliente.enviarMensagem(res);
	}

	private void adicionarCategoria(String nomeCategoria) {

		// Cria um objeto RespostaModel para enviar ao cliente
		RespostaModel resposta = new RespostaModel();
		CategoriaModel categoria = new CategoriaModel();

		resposta.setOperacao("salvarCategoria"); // Define a operação
		resposta.setToken(token); // Usando o token do cliente
		categoria.setId(0); // ID 0 para indicar um novo registro
		categoria.setNome(nomeCategoria); // Define o nome da categoria
		resposta.setCategoria(categoria); // Adiciona a categoria ao objeto resposta

		// Converte a resposta para JSON
		JSONController jsonController = new JSONController();
		JSONObject json = jsonController.changeToSalvarCategoriaJSON(resposta);

		// Envia a mensagem ao cliente
		cliente.enviarMensagem(json);
	}

	private void editarCategoria(int rowIndex, String nomeCategoria) {
		
		// Obtém o ID da categoria a partir da linha selecionada
		int idCategoria = (int) tableModel.getValueAt(rowIndex, 0); // Primeira coluna contém o ID

		// Cria um objeto RespostaModel para enviar ao cliente
		RespostaModel resposta = new RespostaModel();
		CategoriaModel categoria = new CategoriaModel();

		resposta.setOperacao("salvarCategoria"); // Define a operação
		resposta.setToken(token); // Usando o token do cliente
		categoria.setId(idCategoria); // ID 0 para indicar um novo registro
		categoria.setNome(nomeCategoria); // Define o nome da categoria
		resposta.setCategoria(categoria); // Adiciona a categoria ao objeto resposta

		// Converte a resposta para JSON
		JSONController jsonController = new JSONController();
		JSONObject json = jsonController.changeToSalvarCategoriaJSON(resposta);

		// Envia a mensagem ao cliente
		cliente.enviarMensagem(json);
	}

	public void excluirCategoria(CategoriaModel categoria) {

		// Cria um objeto RespostaModel para enviar ao cliente
		RespostaModel resposta = new RespostaModel();
		resposta.setOperacao("excluirCategoria"); // Define a operação
		resposta.setToken(token); // Usando o token do cliente
		categoria.setNome(null);
		resposta.setCategoria(categoria); // Adiciona o ID da categoria
		

		// Converte a resposta para JSON
		JSONController jsonController = new JSONController();
		JSONObject json = jsonController.changeToExcluirCategoriaJSON(resposta);

		// Envia a mensagem ao cliente
		cliente.enviarMensagem(json);
	}

	private void mostrarMensagem(String mensagem, String titulo, int tipo) {
		JOptionPane.showMessageDialog(this, mensagem, titulo, tipo);
	}

	public AdminMainView getTelaAdmin() {
		return telaAdmin;
	}

	public void setTelaAdmin(AdminMainView telaAdmin) {
		this.telaAdmin = telaAdmin;
	}
}
package view;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import org.json.simple.JSONObject;

import controller.JSONController;
import model.AvisoModel;
import model.CategoriaModel;
import model.ClienteModel;
import model.RespostaModel;

public class GerenciadorAvisosView extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTable tableMensagens;
    private DefaultTableModel tableModel;
    private JTextField txtTitulo;
    private JTextArea txtConteudo;
    private JButton btnAdicionar;
    private JButton btnEditar;
    private JButton btnRemover;
    private JButton btnVoltar;
    private ClienteModel cliente;
    private String token;
    protected Window telaAdmin;

    // JComboBox for categories
    private JComboBox<CategoriaModel> comboBoxCategorias;

    public GerenciadorAvisosView(ClienteModel cliente, String token, List<CategoriaModel> categorias) {
        this.cliente = cliente;
        this.token = token;

        setTitle("CRUD de Mensagens - Admin");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 500);
        setLocationRelativeTo(null); // Centraliza a janela

        contentPane = new JPanel(new GridBagLayout());
        contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        setContentPane(contentPane);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Título
        JLabel lblTituloJanela = new JLabel("Gerenciamento de Mensagens", SwingConstants.CENTER);
        lblTituloJanela.setFont(new Font("Poppins", Font.BOLD, 18));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 4;
        contentPane.add(lblTituloJanela, gbc);

        // Tabela para listar mensagens
        String[] colunas = {"ID", "Título", "Conteúdo", "Categoria"};
        tableModel = new DefaultTableModel(colunas, 0);
        tableMensagens = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(tableMensagens);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 0.5;
        contentPane.add(scrollPane, gbc);

        // Campos de entrada
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 0;
        gbc.gridwidth = 1;

        JLabel lblTitulo = new JLabel("Título:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        contentPane.add(lblTitulo, gbc);

        txtTitulo = new JTextField();
        gbc.gridx = 1;
        gbc.gridwidth = 3;
        contentPane.add(txtTitulo, gbc);

        JLabel lblConteudo = new JLabel("Conteúdo:");
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        contentPane.add(lblConteudo, gbc);

        txtConteudo = new JTextArea(3, 20);
        txtConteudo.setLineWrap(true);
        txtConteudo.setWrapStyleWord(true);
        JScrollPane scrollConteudo = new JScrollPane(txtConteudo);
        gbc.gridx = 1;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.BOTH;
        contentPane.add(scrollConteudo, gbc);

        // Categoria
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 0;
        gbc.gridwidth = 1;

        JLabel lblCategoriaLabel = new JLabel("Categoria:");
        gbc.gridx = 0;
        gbc.gridy = 4;
        contentPane.add(lblCategoriaLabel, gbc);

        comboBoxCategorias = new JComboBox<>();
        for (CategoriaModel categoria : categorias) {
            comboBoxCategorias.addItem(categoria); // Adiciona a categoria diretamente
        }
        gbc.gridx = 1;
        gbc.gridwidth = 3;
        contentPane.add(comboBoxCategorias, gbc);

        // Botões
        gbc.gridwidth = 1;
        gbc.gridy = 5;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;

        btnAdicionar = new JButton("Adicionar");
        gbc.gridx = 0;
        contentPane.add(btnAdicionar, gbc);

        btnEditar = new JButton("Editar");
        gbc.gridx = 1;
        contentPane.add(btnEditar, gbc);

        btnRemover = new JButton("Remover");
        gbc.gridx = 2;
        contentPane.add(btnRemover, gbc);

        btnVoltar = new JButton("Voltar");
        gbc.gridx = 3;
        contentPane.add(btnVoltar, gbc);

        // Fechar janela e deslogar
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                cliente.deslogarUsuario();
                System.exit(0);
            }
        });

        adicionarEventos();
    }

    private void adicionarEventos() {
        btnAdicionar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String titulo = txtTitulo.getText().trim();
                String conteudo = txtConteudo.getText().trim();
                CategoriaModel categoriaSelecionada = (CategoriaModel) comboBoxCategorias.getSelectedItem();

                if (!titulo.isEmpty() && !conteudo.isEmpty() && categoriaSelecionada != null) {
                    salvarAviso(titulo, conteudo, categoriaSelecionada);
                } else {
                    JOptionPane.showMessageDialog(null, "Preencha todos os campos!", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnEditar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = tableMensagens.getSelectedRow();
                if (selectedRow != -1) {
                    String titulo = txtTitulo.getText().trim();
                    String conteudo = txtConteudo.getText().trim();
                    CategoriaModel categoriaSelecionada = (CategoriaModel) comboBoxCategorias.getSelectedItem();
                    if (!titulo.isEmpty() && !conteudo.isEmpty() && categoriaSelecionada != null) {
                        editarAviso(selectedRow, titulo, conteudo, categoriaSelecionada);
                    } else {
                        JOptionPane.showMessageDialog(null, "Preencha todos os campos!", "Erro", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Selecione uma mensagem para editar.", "Aviso", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        btnRemover.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = tableMensagens.getSelectedRow();
                if (selectedRow != -1) {
                    removerAviso(selectedRow);
                } else {
                    JOptionPane.showMessageDialog(null, "Selecione uma mensagem para remover.", "Aviso", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        btnVoltar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                
                if (telaAdmin != null) {
                	dispose(); // Fecha a tela de gerenciamento de avisos
                    cliente.setGerenciadorAviso(null);
                	telaAdmin.setVisible(true); // Retorna à tela principal do admin
                } else {
                	JOptionPane.showMessageDialog(null, "Tela Admin Nula", "View nula", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        // Adicionando o ListSelectionListener para a tabela
        tableMensagens.getSelectionModel().addListSelectionListener(e -> {
            int selectedRow = tableMensagens.getSelectedRow();
            if (selectedRow != -1) {
                // Obtém os dados da linha selecionada
                String titulo = (String) tableModel.getValueAt(selectedRow, 1); // Título do aviso
                String conteudo = (String) tableModel.getValueAt(selectedRow, 2); // Conteúdo do aviso
                // Atualiza os campos de texto com os dados do aviso selecionado
                txtTitulo.setText(titulo);
                txtConteudo.setText(conteudo);
            }
        });
    }

    private void salvarAviso(String titulo, String conteudo, CategoriaModel categoria) {
        // Cria um objeto AvisoModel para enviar ao servidor
        AvisoModel aviso = new AvisoModel();
        aviso.setTitulo(titulo);
        aviso.setDescricao(conteudo);
        aviso.setId(0); // ID 0 para indicar um novo registro (INSERT)
        aviso.setCategoria(categoria); // Define a categoria do aviso

        // Cria um objeto RespostaModel para enviar ao cliente
        RespostaModel resposta = new RespostaModel();
        resposta.setOperacao("salvarAviso"); // Define a operação
        resposta.setToken(token); // Usando o token do cliente
        resposta.setAviso(aviso); // Adiciona o aviso ao objeto resposta

        // Criação do JSON a ser enviado ao servidor usando changeToJSON
        JSONController jsonController = new JSONController();
        JSONObject json = jsonController.changeToJSON(resposta);

        // Envia a mensagem ao cliente
        cliente.enviarMensagem(json);
    }

    private void editarAviso(int row, String titulo, String conteudo, CategoriaModel categoria) {
        int id = (int) tableModel.getValueAt(row, 0); // Obtém o ID do aviso selecionado
        AvisoModel aviso = new AvisoModel();
        aviso.setId(id);
        aviso.setTitulo(titulo);
        aviso.setDescricao(conteudo);
        aviso.setCategoria(categoria); // Define a categoria do aviso

        // Cria um objeto RespostaModel para enviar ao cliente
        RespostaModel resposta = new RespostaModel();
        resposta.setOperacao("salvarAviso"); // Define a operação
        resposta.setToken(token); // Usando o token do cliente
        resposta.setAviso(aviso); // Adiciona o aviso ao objeto resposta

        // Converte a resposta para JSON
        JSONController jsonController = new JSONController();
        JSONObject res = jsonController.changeToJSON(resposta);

        // Envia a mensagem ao cliente
        cliente.enviarMensagem(res);
    }

    private void removerAviso(int row) {
        int id = (int) tableModel.getValueAt(row, 0); // Obtém o ID do aviso a ser removido

        // Cria um objeto RespostaModel para enviar ao cliente
        RespostaModel resposta = new RespostaModel();
        resposta.setOperacao("excluirAviso"); // Define a operação
        resposta.setToken(token); // Usando o token do cliente
        resposta.setIdCategoria(id); // Adiciona o ID do aviso ao objeto resposta

        // Converte a resposta para JSON
        JSONController jsonController = new JSONController();
        JSONObject res = jsonController.changeToJSON(resposta);

        // Envia a mensagem ao cliente
        cliente.enviarMensagem(res);
    }
    
    public void pedirAtualizacaoAvisos() {
        // Cria um objeto RespostaModel para enviar ao cliente
        RespostaModel resposta = new RespostaModel();
        resposta.setOperacao("listarAvisos"); // Define a operação
        resposta.setToken(token); // Usando o token do cliente

        // Converte a resposta para JSON
        JSONController jsonController = new JSONController();
        JSONObject json = jsonController.changeToJSON(resposta);

        // Envia a mensagem ao cliente
        cliente.enviarMensagem(json);
    }

    public void atualizarTabelaMensagens(List<AvisoModel> avisos) {
        tableModel.setRowCount(0); // Limpa a tabela antes de adicionar novos dados
        if (avisos != null) {
            for (AvisoModel aviso : avisos) {
                // Adiciona o ID, título, conteúdo e categoria do aviso à tabela
                String categoriaFormatada = aviso.getCategoria() != null ? 
                    aviso.getCategoria().getId() + " - " + aviso.getCategoria().getNome() : "Sem Categoria";
                tableModel.addRow(new Object[]{aviso.getId(), aviso.getTitulo(), aviso.getDescricao(), categoriaFormatada});
            }
        } else {
            // Adiciona uma linha com a mensagem de que não há avisos cadastrados
            tableModel.addRow(new Object[]{"Não há avisos cadastrados"});
        }
    }

    public Window getTelaAdmin() {
        return telaAdmin;
    }

    public void setTelaAdmin(Window telaAdmin) {
        this.telaAdmin = telaAdmin;
    }

    public ClienteModel getCliente() {
        return cliente;
    }

    public String getToken() {
        return token;
    }
}
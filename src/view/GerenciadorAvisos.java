package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import model.ClienteModel;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GerenciadorAvisos extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTable tableMensagens;
    private DefaultTableModel tableModel;
    private JTextField txtTitulo;
    private JTextArea txtConteudo;
    private JButton btnAdicionar;
    private JButton btnEditar;
    private JButton btnRemover;
    private JButton btnVoltar; // Alterado para "Voltar"
    private ClienteModel cliente;
    private String token;
	protected Window telaAdmin;

    public GerenciadorAvisos(ClienteModel cliente, String token) {
        this.cliente = cliente;
        this.token = token;
        setTitle("CRUD de Mensagens - Admin");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 600, 500);
        setLocationRelativeTo(null); // Centraliza a janela

        contentPane = new JPanel();
        contentPane.setBackground(new Color(240, 240, 240));
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        JLabel lblTituloJanela = new JLabel("Gerenciamento de Mensagens");
        lblTituloJanela.setHorizontalAlignment(SwingConstants.CENTER);
        lblTituloJanela.setFont(new Font("Poppins", Font.BOLD, 16));
        lblTituloJanela.setBounds(150, 10, 300, 25);
        contentPane.add(lblTituloJanela);

        // Tabela para listar as mensagens
        String[] colunas = {"ID", "Título", "Conteúdo"};
        tableModel = new DefaultTableModel(colunas, 0);
        tableMensagens = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(tableMensagens);
        scrollPane.setBounds(20, 50, 540, 200);
        contentPane.add(scrollPane);

        // Campos de entrada para título e conteúdo da mensagem
        JLabel lblTitulo = new JLabel("Título:");
        lblTitulo.setFont(new Font("Poppins", Font.PLAIN, 12));
        lblTitulo.setBounds(20, 270, 50, 20);
        contentPane.add(lblTitulo);

        txtTitulo = new JTextField();
        txtTitulo.setFont(new Font("Poppins", Font.PLAIN, 12));
        txtTitulo.setBounds(80, 270, 200, 25);
        contentPane.add(txtTitulo);

        JLabel lblConteudo = new JLabel("Conteúdo:");
        lblConteudo.setFont(new Font("Poppins", Font.PLAIN, 12));
        lblConteudo.setBounds(20, 300, 60, 20);
        contentPane.add(lblConteudo);

        txtConteudo = new JTextArea();
        txtConteudo.setFont(new Font("Poppins", Font.PLAIN, 12));
        txtConteudo.setLineWrap(true);
        txtConteudo.setWrapStyleWord(true);
        JScrollPane scrollConteudo = new JScrollPane(txtConteudo);
        scrollConteudo.setBounds(80, 300, 480, 100);
        contentPane.add(scrollConteudo);

        // Botões de CRUD
        btnAdicionar = new JButton("Adicionar");
        btnAdicionar.setFont(new Font("Poppins", Font.PLAIN, 12));
        btnAdicionar.setBounds(80, 420, 100, 25);
        contentPane.add(btnAdicionar);

        btnEditar = new JButton("Editar");
        btnEditar.setFont(new Font("Poppins", Font.PLAIN, 12));
        btnEditar.setBounds(190, 420, 100, 25);
        contentPane.add(btnEditar);

        btnRemover = new JButton("Remover");
        btnRemover.setFont(new Font("Poppins", Font.PLAIN, 12));
        btnRemover.setBounds(300, 420, 100, 25);
        contentPane.add(btnRemover);

        // Botão de Voltar
        btnVoltar = new JButton("Voltar");
        btnVoltar.setFont(new Font("Poppins", Font.PLAIN, 12));
        btnVoltar.setBounds(410, 420, 100, 25);
        contentPane.add(btnVoltar);

        adicionarEventos();
    }

    private void adicionarEventos() {
        btnAdicionar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String titulo = txtTitulo.getText().trim();
                String conteudo = txtConteudo.getText().trim();

                if (!titulo.isEmpty() && !conteudo.isEmpty()) {
                    adicionarMensagem(titulo, conteudo);
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
                    if (!titulo.isEmpty() && !conteudo.isEmpty()) {
                        editarMensagem(selectedRow, titulo, conteudo);
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
                    removerMensagem(selectedRow);
                } else {
                    JOptionPane.showMessageDialog(null, "Selecione uma mensagem para remover.", "Aviso", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        btnVoltar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // Fecha a tela de gerenciamento de avisos
                telaAdmin.setVisible(true); // Retorna à tela principal do admin
            }
        });
    }

    private void adicionarMensagem(String titulo, String conteudo) {
        // Simula a adição de uma nova mensagem (ID seria gerado pelo servidor no futuro)
        int id = tableModel.getRowCount() + 1;
        tableModel.addRow(new Object[]{id, titulo, conteudo});
        limparCampos();
    }

    private void editarMensagem(int row, String titulo, String conteudo) {
        tableModel.setValueAt(titulo, row, 1);
        tableModel.setValueAt(conteudo, row, 2);
        limparCampos();
    }

    private void removerMensagem(int row) {
        tableModel.removeRow(row);
    }

    private void limparCampos() {
        txtTitulo.setText("");
        txtConteudo.setText("");
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
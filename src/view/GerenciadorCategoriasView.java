package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GerenciadorCategoriasView extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTable tableCategorias;
    private DefaultTableModel tableModel;
    private JTextField txtNomeCategoria;
    private JButton btnAdicionar;
    private JButton btnEditar;
    private JButton btnRemover;
    private JButton btnVoltar;
    private AdminMainView telaAdmin;

    public GerenciadorCategoriasView() {
        setTitle("Gerenciador de Categorias");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 500);
        setLocationRelativeTo(null); // Centraliza a janela

        contentPane = new JPanel();
        contentPane.setBackground(new Color(240, 240, 240));
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        JLabel lblTitulo = new JLabel("Gerenciamento de Categorias");
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Poppins", Font.BOLD, 16));
        lblTitulo.setBounds(150, 10, 300, 25);
        contentPane.add(lblTitulo);

        // Tabela para listar as categorias
        String[] colunas = {"Nome da Categoria"};
        tableModel = new DefaultTableModel(colunas, 0);
        tableCategorias = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(tableCategorias);
        scrollPane.setBounds(20, 50, 540, 200);
        contentPane.add(scrollPane);

        // Campo de entrada para Nome da Categoria
        JLabel lblNomeCategoria = new JLabel("Nome da Categoria:");
        lblNomeCategoria.setFont(new Font("Poppins", Font.PLAIN, 12));
        lblNomeCategoria.setBounds(20, 270, 120, 20);
        contentPane.add(lblNomeCategoria);

        txtNomeCategoria = new JTextField();
        txtNomeCategoria.setFont(new Font("Poppins", Font.PLAIN, 12));
        txtNomeCategoria.setBounds(150, 270, 200, 25);
        contentPane.add(txtNomeCategoria);

        // Botões de CRUD
        btnAdicionar = new JButton("Adicionar");
        btnAdicionar.setFont(new Font("Poppins", Font.PLAIN, 12));
        btnAdicionar.setBounds(150, 310, 100, 25);
        contentPane.add(btnAdicionar);

        btnEditar = new JButton("Editar");
        btnEditar.setFont(new Font("Poppins", Font.PLAIN, 12));
        btnEditar.setBounds(260, 310, 100, 25);
        contentPane.add(btnEditar);

        btnRemover = new JButton("Remover");
        btnRemover.setFont(new Font("Poppins", Font.PLAIN, 12));
        btnRemover.setBounds(370, 310, 100, 25);
        contentPane.add(btnRemover);

        // Botão de Voltar
        btnVoltar = new JButton("Voltar");
        btnVoltar.setFont(new Font("Poppins", Font.PLAIN, 12));
        btnVoltar.setBounds(480, 310, 100, 25);
        contentPane.add(btnVoltar);

        adicionarEventos();
    }

    private void adicionarEventos() {
        btnAdicionar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nomeCategoria = txtNomeCategoria.getText().trim();
                if (!nomeCategoria.isEmpty()) {
                    adicionarCategoria(nomeCategoria);
                } else {
                    JOptionPane.showMessageDialog(null, "Preencha o nome da categoria!", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnEditar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = tableCategorias.getSelectedRow();
                if (selectedRow != -1) {
                    String nomeCategoria = txtNomeCategoria.getText().trim();
                    if (!nomeCategoria.isEmpty()) {
                        editarCategoria(selectedRow, nomeCategoria);
                    } else {
                        JOptionPane.showMessageDialog(null, "Preencha o nome da categoria!", "Erro", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Selecione uma categoria para editar.", "Aviso", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        btnRemover.addActionListener(new ActionListener() {
            @ Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = tableCategorias.getSelectedRow();
                if (selectedRow != -1) {
                    removerCategoria(selectedRow);
                } else {
                    JOptionPane.showMessageDialog(null, "Selecione uma categoria para remover.", "Aviso", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        btnVoltar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               dispose(); // Fecha a tela de gerenciamento de categorias
               telaAdmin.setVisible(true); // Retorna à tela principal do admin
            }
        });

        tableCategorias.getSelectionModel().addListSelectionListener(e -> {
            int selectedRow = tableCategorias.getSelectedRow();
            if (selectedRow != -1) {
                String nomeCategoria = (String) tableModel.getValueAt(selectedRow, 0);
                txtNomeCategoria.setText(nomeCategoria);
            }
        });
    }

    private void adicionarCategoria(String nomeCategoria) {
        tableModel.addRow(new Object[]{nomeCategoria});
        txtNomeCategoria.setText(""); // Limpa o campo de entrada
    }

    private void editarCategoria(int rowIndex, String nomeCategoria) {
        tableModel.setValueAt(nomeCategoria, rowIndex, 0);
        txtNomeCategoria.setText(""); // Limpa o campo de entrada
    }

    private void removerCategoria(int rowIndex) {
        tableModel.removeRow(rowIndex);
        txtNomeCategoria.setText(""); // Limpa o campo de entrada
    }

	public AdminMainView getTelaAdmin() {
		return telaAdmin;
	}

	public void setTelaAdmin(AdminMainView telaAdmin) {
		this.telaAdmin = telaAdmin;
	}
}
package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import org.json.simple.JSONObject;

import controller.JSONController;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import model.ClienteModel;
import model.UsuarioModel;

public class GerenciadorUsuariosView extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTable tableUsuarios;
    private DefaultTableModel tableModel;
    private JTextField txtRa;
    private JTextField txtNome;
    private JPasswordField txtSenha;
    private JButton btnAdicionar;
    private JButton btnEditar;
    private JButton btnExcluir;
    private JButton btnVoltar;
    private AdminMainView telaAdmin;
    private ClienteModel cliente;
    private String token;

    public GerenciadorUsuariosView(ClienteModel cliente, String token) {
        this.cliente = cliente; // Recebe o cliente
        this.token = token;
        setTitle("Gerenciador de Usuários");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 386);
        setLocationRelativeTo(null); // Centraliza a janela

        contentPane = new JPanel();
        contentPane.setBackground(new Color(240, 240, 240));
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        JLabel lblTitulo = new JLabel("Gerenciamento de Usuários");
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Poppins", Font.BOLD, 16));
        lblTitulo.setBounds(150, 10, 300, 25);
        contentPane.add(lblTitulo);

        // Tabela para listar os usuários
        String[] colunas = {"RA", "Nome", "Senha"};
        tableModel = new DefaultTableModel(colunas, 0);
        tableUsuarios = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(tableUsuarios);
        scrollPane.setBounds(20, 50, 556, 200);
        contentPane.add(scrollPane);

        // Campos de entrada para RA, Nome e Senha
        JLabel lblRa = new JLabel("RA:");
        lblRa.setFont(new Font("Poppins", Font.PLAIN, 12));
        lblRa.setBounds(20, 270, 50, 20);
        contentPane.add(lblRa);

        txtRa = new JTextField();
        txtRa.setFont(new Font("Poppins", Font.PLAIN, 12));
        txtRa.setBounds(51, 268, 80, 25);
        contentPane.add(txtRa);

        JLabel lblNome = new JLabel("Nome:");
        lblNome.setFont(new Font("Poppins", Font.PLAIN, 12));
        lblNome.setBounds(141, 270, 223, 20);
        contentPane.add(lblNome);

        txtNome = new JTextField();
        txtNome.setFont(new Font("Poppins", Font.PLAIN, 12));
        txtNome.setBounds(190, 268, 163, 25);
        contentPane.add(txtNome);

        JLabel lblSenha = new JLabel("Senha:");
        lblSenha.setFont(new Font("Poppins", Font.PLAIN, 12));
        lblSenha.setBounds(363, 270, 50, 20);
        contentPane.add(lblSenha);

        txtSenha = new JPasswordField();
        txtSenha.setFont(new Font("Poppins", Font.PLAIN, 12));
        txtSenha.setBounds(410, 268, 166, 25);
        contentPane.add(txtSenha);

        // Botões de CRUD
        btnAdicionar = new JButton("Adicionar");
        btnAdicionar.setFont(new Font("Poppins", Font.PLAIN, 12));
        btnAdicionar.setBounds(82, 312, 100, 25);
        contentPane.add(btnAdicionar);

        btnEditar = new JButton("Editar");
        btnEditar.setFont(new Font("Poppins", Font.PLAIN, 12));
        btnEditar.setBounds(192, 312, 100, 25);
        contentPane.add(btnEditar);

        btnExcluir = new JButton("Excluir");
        btnExcluir.setFont(new Font("Poppins", Font.PLAIN, 12));
        btnExcluir.setBounds(302, 312, 100, 25);
        contentPane.add(btnExcluir);

        // Botão de Voltar
        btnVoltar = new JButton("Voltar");
        btnVoltar.setFont(new Font("Poppins", Font.PLAIN, 12));
        btnVoltar.setBounds(412, 312, 100, 25);
        contentPane.add(btnVoltar);

        adicionarEventos();
    }

    private void adicionarEventos() {
        btnAdicionar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String RA = txtRa.getText().trim();
                String nome = txtNome.getText().trim();
                String senha = new String(txtSenha.getPassword()).trim();

                if (!RA.isEmpty() && !nome.isEmpty() && !senha.isEmpty()) {
                    adicionarUsuario(RA, nome, senha);
                } else {
                    JOptionPane.showMessageDialog(null, "Preencha todos os campos!", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnEditar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = tableUsuarios.getSelectedRow();
                if (selectedRow != -1) {
                    String RA = txtRa.getText().trim();
                    String nome = txtNome.getText().trim();
                    String senha = new String(txtSenha.getPassword()).trim();
                    if (!RA.isEmpty() && !nome.isEmpty() && !senha.isEmpty()) {
                        editarUsuario(selectedRow, RA, nome, senha);
                    } else {
                        JOptionPane.showMessageDialog(null, "Preencha todos os campos!", "Erro", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Selecione um usuário para editar.", "Aviso", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        btnExcluir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = tableUsuarios.getSelectedRow();
                if (selectedRow != -1) {
                    // Obtém o RA do usuário selecionado
                    String ra = (String) tableModel.getValueAt(selectedRow, 0);
                    
                    // Cria um objeto UsuarioModel para enviar ao cliente
                    UsuarioModel usuario = new UsuarioModel();
                    usuario.setOperacao("excluirUsuario");
                    usuario.setRa(ra); // Define o RA do usuário a ser removido
                    System.out.println("Token do gerenciador usuario: " + getToken());
                    usuario.setToken(getToken());

                    // Converte o usuário para JSON
                    JSONController jsonController = new JSONController();
                    JSONObject res = jsonController.changeToJSON(usuario);

                    // Envia a mensagem ao cliente
                    cliente.enviarMensagem(res);

                    // Remove o usuário da tabela
                    //removerUsuario(selectedRow);
                } else {
                    JOptionPane.showMessageDialog(null, "Selecione um usuário para remover.", "Aviso", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        btnVoltar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                voltarTelaPrincipal();
            }
        });

        tableUsuarios.getSelectionModel().addListSelectionListener(e -> {
            int selectedRow = tableUsuarios.getSelectedRow();
            if (selectedRow != -1) {
                String ra = (String) tableModel.getValueAt(selectedRow, 0);
                String nome = (String) tableModel.getValueAt(selectedRow, 1);
                String senha = (String) tableModel.getValueAt(selectedRow, 2);
                txtRa.setText(ra);
                txtNome.setText(nome);
                txtSenha.setText(senha);
            }
        });
    }

    private void adicionarUsuario(String RA, String nome, String senha) {
        tableModel.addRow(new Object[]{RA, nome, senha});
        limparCampos();
    }

    private void editarUsuario(int row, String ra, String nome, String senha) {
        tableModel.setValueAt(ra, row, 0);
        tableModel.setValueAt(nome, row, 1);
        tableModel.setValueAt(senha, row, 2);
        limparCampos();
    }

    private void removerUsuario(int row) {
        tableModel.removeRow(row);
    }

    private void limparCampos() {
        txtRa.setText("");
        txtNome.setText("");
        txtSenha.setText("");
    }

    public void atualizarTabelaUsuarios(List<UsuarioModel> usuarios) {
        tableModel.setRowCount(0); // Limpa a tabela antes de adicionar novos dados
        for (UsuarioModel usuario : usuarios) {
            tableModel.addRow(new Object[]{usuario.getRa(), usuario.getNome(), usuario.getSenha()});
        }
    }

    private void voltarTelaPrincipal() {
        dispose(); // Fecha a janela atual
        telaAdmin.setVisible(true); // Abre a tela principal do administrador
    }

    public AdminMainView getTelaAdmin() {
        return telaAdmin;
    }

    public void setTelaAdmin(AdminMainView telaAdmin) {
        this.telaAdmin = telaAdmin;
    }

    public ClienteModel getCliente() {
		return cliente;
	}

    public String getToken() {
		return token;
	}
}
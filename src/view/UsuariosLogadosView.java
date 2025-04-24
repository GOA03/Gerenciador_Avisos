package view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import dao.BancoDados;
import dao.UsuarioDAO;
import model.UsuarioModel; // Supondo que você tenha um modelo de usuário

public class UsuariosLogadosView extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JButton btnFechar;
    private List<UsuarioModel> usuariosLogados; // Lista de usuários logados
    private JTable table; // Tabela para exibir os usuários
    private JLabel lblTitulo; // Label do título para atualização dinâmica

    public UsuariosLogadosView() {
        
        carregarComponentes();
        atualizarUsuariosLogados();
    }

    private void carregarComponentes() {
        setTitle("Usuários Logados");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null); // Centraliza a janela

        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        contentPane.setLayout(new BorderLayout(10, 10));
        setContentPane(contentPane);

        // Cabeçalho
        lblTitulo = new JLabel("Usuários Logados: " + (usuariosLogados != null ? usuariosLogados.size() : 0), SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Poppins", Font.BOLD, 18));
        contentPane.add(lblTitulo, BorderLayout.NORTH);

        // Tabela para exibir os usuários
        String[] columnNames = { "RA", "Nome" };
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        contentPane.add(scrollPane, BorderLayout.CENTER);

        // Contador de usuários logados
        JLabel lblContador = new JLabel("Número de usuários logados: " + (usuariosLogados != null ? usuariosLogados.size() : 0), SwingConstants.CENTER);
        contentPane.add(lblContador, BorderLayout.SOUTH);

        // Painel de botões
        JPanel panelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        btnFechar = new JButton("Fechar");
        panelBotoes.add(btnFechar);
        contentPane.add(panelBotoes, BorderLayout.SOUTH);

        // Adiciona ActionListener para o botão de fechar
        btnFechar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // Fecha a tela atual
            }
        });
    }

    public void atualizarUsuariosLogados() {
        Connection conn = null;
        try {
            conn = BancoDados.conectar();
            UsuarioDAO usuarioDAO = new UsuarioDAO(conn);
            usuariosLogados = usuarioDAO.listarLogados();
            atualizarTabela(usuariosLogados);
            
            // Atualiza o título da janela
            lblTitulo.setText("Usuários Logados: " + usuariosLogados.size());
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar usuários logados: " + e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                BancoDados.desconectar();
            } catch (SQLException e) {
                System.err.println("Erro ao desconectar do banco de dados: " + e.getMessage());
            }
        }
    }

    private void atualizarTabela(List<UsuarioModel> usuariosLogados) {
        // Verifica se a tabela foi inicializada antes de tentar acessar o modelo
        if (table != null && table.getModel() instanceof DefaultTableModel) {
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            
            // Limpa a tabela antes de adicionar os novos dados
            model.setRowCount(0);
            
            // Adiciona os usuários logados à tabela
            for (UsuarioModel usuario : usuariosLogados) {
                model.addRow(new Object[] { usuario.getRa(), usuario.getNome() });
            }

            // Atualiza o contador de usuários logados
            JLabel lblContador = (JLabel) contentPane.getComponent(2);
            lblContador.setText("Número de usuários logados: " + usuariosLogados.size());
        }
    }
}

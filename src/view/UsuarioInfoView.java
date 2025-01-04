package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class UsuarioInfoView extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JLabel lblRa;
    private JLabel lblNome;
    private JLabel lblSenha;
    private JButton btnFechar;
    private JButton btnAtualizar;

    public UsuarioInfoView(String token) {
        initializeFrame();
        initializeComponents(token);
    }

    private void initializeFrame() {
        setTitle("Informações do Usuário");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 250);
        setLocationRelativeTo(null); // Centraliza a janela
    }

    private void initializeComponents(String token) {
        contentPane = new JPanel();
        contentPane.setLayout(new GridLayout(5, 2)); // Layout em grade para organizar os componentes
        setContentPane(contentPane);

        // Adicionando os rótulos e informações do usuário
        addLabel("RA:");
        lblRa = new JLabel(/*usuario.getRa()*/);
        contentPane.add(lblRa);

        addLabel("Nome:");
        lblNome = new JLabel(/*usuario.getNome()*/);
        contentPane.add(lblNome);

        addLabel("Senha:");
        lblSenha = new JLabel(/*usuario.getSenha()*/);
        contentPane.add(lblSenha);

        // Botão para atualizar as informações do usuário
        btnAtualizar = new JButton("Atualizar");
        btnAtualizar.addActionListener(createUpdateActionListener(token));
        contentPane.add(btnAtualizar);

        // Botão para fechar a janela
        btnFechar = new JButton("Fechar");
        btnFechar.addActionListener(e -> dispose()); // Fecha a janela ao clicar
        contentPane.add(btnFechar);
    }

    private void addLabel(String text) {
        contentPane.add(new JLabel(text));
    }

    private ActionListener createUpdateActionListener(String token) {
        return e -> abrirFormularioAtualizacao(token); // Abre o formulário de atualização
    }

    private void abrirFormularioAtualizacao(String token) {
        // Cria um novo formulário para atualizar as informações do usuário
        new UsuarioUpdateView(token).setVisible(true);
    }
}
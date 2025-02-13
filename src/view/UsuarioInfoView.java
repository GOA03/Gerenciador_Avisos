package view;

import javax.swing.*;

import org.json.simple.JSONObject;

import controller.JSONController;
import model.ClienteModel;
import model.UsuarioModel;

import java.awt.event.ActionListener;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Dimension;

public class UsuarioInfoView extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JLabel lblRa;
    private JLabel lblNome;
    private JLabel lblSenha;
    private JButton btnFechar;
    private JButton btnAtualizar;
    private JButton btnExcluir;
    private MainView telaPrincipal;
    private ClienteModel cliente;
	private String nome;
	private String ra;
	private String senha;

    public UsuarioInfoView(ClienteModel cliente, String token) {
        this.cliente = cliente;
        initializeFrame();
        initializeComponents(token);
    }

    private void initializeFrame() {
        setTitle("Informações do Usuário");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(350, 220);
        setMinimumSize(new Dimension(350, 220));
        setLocationRelativeTo(null);
    }

    private void initializeComponents(String token) {
        contentPane = new JPanel(new GridBagLayout());
        contentPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setContentPane(contentPane);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        addLabel("RA:", 0, 0, gbc);
        lblRa = addValueLabel("<RA>", 1, 0, gbc);

        addLabel("Nome:", 0, 1, gbc);
        lblNome = addValueLabel("<Nome>", 1, 1, gbc);

        addLabel("Senha:", 0, 2, gbc);
        lblSenha = addValueLabel("<Senha>", 1, 2, gbc);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridBagLayout());
        GridBagConstraints buttonGbc = new GridBagConstraints();
        buttonGbc.insets = new Insets(5, 5, 5, 5);

        btnAtualizar = createButton("Atualizar", e -> abrirFormularioAtualizacao(token));
        btnAtualizar.setPreferredSize(new Dimension(100, 25));
        buttonGbc.gridx = 0;
        buttonPanel.add(btnAtualizar, buttonGbc);

        btnExcluir = createButton("Excluir", e -> excluirUsuario(token));
        btnExcluir.setPreferredSize(new Dimension(100, 25));
        btnExcluir.setToolTipText("Excluir Usuário");
        buttonGbc.gridx = 1;
        buttonPanel.add(btnExcluir, buttonGbc);

        btnFechar = createButton("Fechar", e -> voltarParaMainView());
        btnFechar.setPreferredSize(new Dimension(100, 25));
        buttonGbc.gridx = 2;
        buttonPanel.add(btnFechar, buttonGbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        contentPane.add(buttonPanel, gbc);
        
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                cliente.deslogarUsuario(); // Desloga o usuário ao fechar a janela
                System.exit(0); // Encerra a aplicação
            }
        });
    }

    private JLabel addLabel(String text, int x, int y, GridBagConstraints gbc) {
        JLabel label = new JLabel(text);
        label.setHorizontalAlignment(SwingConstants.RIGHT);
        gbc.gridx = x;
        gbc.gridy = y;
        contentPane.add(label, gbc);
        return label;
    }

    private JLabel addValueLabel(String text, int x, int y, GridBagConstraints gbc) {
        JLabel label = new JLabel(text);
        label.setHorizontalAlignment(SwingConstants.LEFT);
        gbc.gridx = x;
        gbc.gridy = y;
        contentPane.add(label, gbc);
        return label;
    }

    private JButton createButton(String text, ActionListener action) {
        JButton button = new JButton(text);
        button.addActionListener(action);
        return button;
    }

    private void abrirFormularioAtualizacao(String token) {
        dispose();
        new UsuarioUpdateView(cliente, token, this, this.nome, this.senha).setVisible(true);
    }

    private void excluirUsuario(String token) {
        int confirm = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja excluir este usuário?", "Confirmação de Exclusão", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            // Cria um objeto UsuarioModel para enviar ao servidor
            UsuarioModel usuario = new UsuarioModel();
            usuario.setOperacao("excluirUsuario");
            usuario.setRa(this.ra); // Usando o RA do usuário
            usuario.setToken(token); // Usando o token do cliente

            // Converte o usuário para JSON
            JSONController jsonController = new JSONController();
            JSONObject res = jsonController.changeToJSON(usuario);

            // Envia a requisição ao servidor
            if (this.cliente != null) {
                this.cliente.enviarMensagem(res);
            }
        }
    }

    private void voltarParaMainView() {
        dispose();
        if (telaPrincipal != null) {
            telaPrincipal.setVisible(true);
        }
    }

    public void setUsuarioInfo(UsuarioModel usuario) {
    	
    	this.nome = usuario.getNome();
    	this.ra = usuario.getRa();
    	this.senha = usuario.getSenha();
    	
        lblRa.setText(usuario.getRa());
        lblNome.setText(usuario.getNome());
        lblSenha.setText(usuario.getSenha());
    }

    public void setTelaPrincipal(MainView telaPrincipal) {
        this.telaPrincipal = telaPrincipal;
    }
}

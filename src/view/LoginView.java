package view;

import javax.swing.*;
import org.json.simple.JSONObject;
import controller.JSONController;
import model.ClienteModel;
import model.UsuarioModel;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginView extends JFrame {

    private static final long serialVersionUID = 1L;
    private JTextField raField;
    private JPasswordField passwordField;
    private JButton loginButton, registerButton;
    private ClienteModel cliente;

    public LoginView(ClienteModel cliente) {
        this.cliente = cliente;

        setTitle("Login"); 
        setSize(400, 184);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        getContentPane().add(panel);

        JLabel lblRa = new JLabel("RA:");
        raField = new JTextField();
        raField.setPreferredSize(new Dimension(150, 25));

        JLabel lblSenha = new JLabel("Senha:");
        passwordField = new JPasswordField();
        passwordField.setPreferredSize(new Dimension(150, 25));

        loginButton = new JButton("Login");

        // Adicionar ActionListener para o botão Login
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    String ra = raField.getText();
                    @SuppressWarnings("deprecation")
                    String senha = passwordField.getText();

                    UsuarioModel usuario = new UsuarioModel();
                    usuario.setOperacao("login");
                    usuario.setRa(ra); 
                    usuario.setSenha(senha);

                    JSONController loginController = new JSONController();
                    JSONObject res = loginController.changeToJSON(usuario);

                    loginUsuario(res);

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "RA inválido. Por favor, insira um número.");
                }
            }
        });

        registerButton = new JButton("Cadastrar");

        // Adicionar ActionListener para o botão Cadastrar
        registerButton.addActionListener(e -> {
            this.dispose(); // Fecha a tela de login
            new CadastroView(this.cliente).setVisible(true); // Abre a tela de cadastro
        });

        GroupLayout layout = new GroupLayout(panel);
        panel.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        layout.setHorizontalGroup(
            layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(lblRa)
                    .addComponent(lblSenha))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(raField)
                    .addComponent(passwordField)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(loginButton)
                        .addComponent(registerButton)))
        );

        layout.setVerticalGroup(
            layout.createSequentialGroup()
                .addGap(30) // Espaço do topo
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(lblRa)
                    .addComponent(raField))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(lblSenha)
                    .addComponent(passwordField))
                .addGap(20) // Espaço entre os campos e os botões
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(loginButton)
                    .addComponent(registerButton))
                .addGap(30) // Espaço do fundo
        );
    }

    public void loginUsuario(JSONObject res) {
        if (this.cliente == null) {
            System.err.println("O cliente está nulo, você deve primeiro inicializar o cliente e o servidor");
        } else {
        	this.cliente.setLoginView(this);
            this.cliente.enviarMensagem(res);
        }
    }

    public String getRa() {
        return raField.getText();
    }

    public String getPassword() {
        return new String(passwordField.getPassword());
    }

    public void adicionarActionListenerLogin(ActionListener listener) {
        loginButton.addActionListener(listener);
    }
}
package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import org.json.simple.JSONObject;
import controller.JSONController;
import model.ClienteModel;
import model.UsuarioModel;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainView extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private ClienteModel cliente;
    private String token;
    private UsuarioModel usuario;

    public MainView(ClienteModel cliente, String token) {
        this.cliente = cliente;
        this.token = token;

        setTitle("Tela Principal"); // Define um título para a janela
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(414, 300);
        setLocationRelativeTo(null); // Centraliza a janela na tela
        contentPane = new JPanel();
        contentPane.setBackground(new Color(234, 234, 234));
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        JLabel lblNewLabel = new JLabel("Login realizado com sucesso!");
        lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
        lblNewLabel.setBounds(30, 34, 340, 30);
        lblNewLabel.setFont(new Font("Poppins", Font.PLAIN, 20));
        contentPane.add(lblNewLabel);

        JButton btnInfoUsuario = new JButton("Informações do Usuário");
        btnInfoUsuario.setFont(new Font("Poppins", Font.PLAIN, 15));
        btnInfoUsuario.setBounds(100, 80, 200, 30);
        contentPane.add(btnInfoUsuario);

        JButton btnAvisos = new JButton("Ver Avisos");
        btnAvisos.setFont(new Font("Poppins", Font.PLAIN, 15));
        btnAvisos.setBounds(100, 120, 200, 30);
        contentPane.add(btnAvisos);

        JButton btnLogout = new JButton("Logout");
        btnLogout.setFont(new Font("Poppins", Font.PLAIN, 15));
        btnLogout.setBounds(100, 160, 200, 30);
        contentPane.add(btnLogout);

        // Adicionar ActionListener para o botão de logout
        btnLogout.addActionListener(e -> logoutUsuario());

        // Adicionar ActionListener para o botão de informações do usuário
        btnInfoUsuario.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (token != null) {
                    new UsuarioInfoView(token).setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(MainView.this, "Usuário não encontrado.", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Adicionar ActionListener para o botão de avisos
        btnAvisos.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	ocultarTela();
                AvisosView telaAvisos = new AvisosView(cliente, token);
                telaAvisos.setTelaPrincipal(MainView.this);
                telaAvisos.setVisible(true);
            }
        });
    }

    private void logoutUsuario() {
        UsuarioModel usuario = new UsuarioModel();
        usuario.setOperacao("logout");
        usuario.setRa(token); // Utilizando o token para o logout

        JSONController jsonController = new JSONController();
        JSONObject res = jsonController.changeLogoutToJSON(usuario);

        if (cliente != null) {
            cliente.enviarMensagem(res); // Envia a mensagem de logout ao servidor
        }

        dispose(); // Fecha a janela após o logout
    }
    
    private void ocultarTela() {
    	this.setVisible(false);
    }

	public UsuarioModel getUsuario() {
		return usuario;
	}

	public void setUsuario(UsuarioModel usuario) {
		this.usuario = usuario;
	}
}
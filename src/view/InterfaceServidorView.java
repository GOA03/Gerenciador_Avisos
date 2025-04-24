package view;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import controller.ServidorController;
import dao.BancoDados;
import dao.UsuarioDAO;
import model.ServidorModel;

public class InterfaceServidorView extends JFrame {

    private static final long serialVersionUID = 1L;
    private JTextField portaServidor;    
    private JTextArea retornoServidor;
    private JButton conectarServidor;
    private JButton btnUsuariosLogados;
	private UsuariosLogadosView usuariosLogadosView;

    public InterfaceServidorView() {
        setTitle("Servidor");
        setSize(713, 467);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setLayout(new BorderLayout());

        JPanel painelSuperior = new JPanel();
        painelSuperior.setLayout(new FlowLayout());

        JLabel labelPorta = new JLabel("Porta do Servidor:");
        portaServidor = new JTextField(10);
        conectarServidor = new JButton("Conectar");
        btnUsuariosLogados = new JButton("Usuários Logados"); // Botão para abrir a tela de usuários logados

        painelSuperior.add(labelPorta);
        painelSuperior.add(portaServidor);
        painelSuperior.add(conectarServidor);
        painelSuperior.add(btnUsuariosLogados); // Adicionando o botão ao painel superior

        JPanel painelCentral = new JPanel();
        painelCentral.setLayout(new BorderLayout());

        retornoServidor = new JTextArea();
        retornoServidor.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(retornoServidor);

        painelCentral.add(scrollPane, BorderLayout.CENTER);

        getContentPane().add(painelSuperior, BorderLayout.NORTH);
        getContentPane().add(painelCentral, BorderLayout.CENTER);

        // Adicionando o WindowListener para fechar a janela
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    deslogarTodosUsuarios();
                } catch (IOException e1) {
                    e1.printStackTrace();
                } // Chama a função para deslogar todos os usuários
                System.exit(0); // Encerra a aplicação
            }
        });

        // Adiciona o ActionListener para o botão de "Usuários Logados"
        btnUsuariosLogados.addActionListener(e -> abrirUsuariosLogados());
    }

    private void deslogarTodosUsuarios() throws IOException {
        try {
            Connection conn = BancoDados.conectar(); // Conecta ao banco de dados
            UsuarioDAO usuarioDAO = new UsuarioDAO(conn);
            usuarioDAO.deslogarTodosUsuarios(); // Chama o método para deslogar todos os usuários
            System.out.println("Todos os usuários foram deslogados com sucesso.\n");
        } catch (SQLException e) {
            System.err.println("Erro ao deslogar todos os usuários: " + e.getMessage());
        } finally {
            try {
                BancoDados.desconectar(); // Desconecta do banco de dados
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void abrirUsuariosLogados() {
        // Cria uma nova instância da tela de usuários logados e exibe-a
        UsuariosLogadosView usuariosLogadosView = new UsuariosLogadosView();
        this.usuariosLogadosView = usuariosLogadosView;
        usuariosLogadosView.setLocationRelativeTo(this); // Centraliza a janela
        usuariosLogadosView.setVisible(true);
    }

    public JTextField getPortaServidor() {
        return portaServidor;
    }

    public JTextArea getRetornoServidor() {
        return retornoServidor;
    }

    public void adicionarActionListenerConectar(ActionListener listener) {
        conectarServidor.addActionListener(listener);
    }
    
    public void atualizarListaLogados() {
    	
    	if(usuariosLogadosView != null) {
    		usuariosLogadosView.atualizarUsuariosLogados();
    	}
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    InterfaceServidorView view = new InterfaceServidorView();
                    ServidorModel model = new ServidorModel();
                    model.setTelaServidor(view);
                    
                    new ServidorController(view, model);

                    view.setLocationRelativeTo(null); // Centraliza a janela
                    view.setVisible(true);
                    
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}

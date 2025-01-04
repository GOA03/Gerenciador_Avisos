package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import model.ServidorModel;
import controller.ServidorController;

public class InterfaceServidorView extends JFrame {

    private static final long serialVersionUID = 1L;
    private JTextField portaServidor;
    private JTextArea retornoServidor;
    private JButton conectarServidor;

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

        painelSuperior.add(labelPorta);
        painelSuperior.add(portaServidor);
        painelSuperior.add(conectarServidor);

        JPanel painelCentral = new JPanel();
        painelCentral.setLayout(new BorderLayout());

        retornoServidor = new JTextArea();
        retornoServidor.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(retornoServidor);

        painelCentral.add(scrollPane, BorderLayout.CENTER);

        getContentPane().add(painelSuperior, BorderLayout.NORTH);
        getContentPane().add(painelCentral, BorderLayout.CENTER);
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

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    InterfaceServidorView view = new InterfaceServidorView();
                    ServidorModel model = new ServidorModel();
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

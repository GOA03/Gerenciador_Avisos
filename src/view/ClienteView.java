package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import controller.ClienteController;
import model.ClienteModel;

public class ClienteView extends JFrame { 

    private static final long serialVersionUID = 1L;

    private JPanel contentPane;
    private JTextField enderecoServidor;
    private JTextField portaField;
    private JButton btnConectar;
    private ClienteController controller;

    public static void main(String[] args) {	
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    ClienteView frame = new ClienteView();
                    frame.setLocationRelativeTo(null);
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public ClienteView() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 450, 275);
        contentPane = new JPanel();
        contentPane.setBackground(SystemColor.windowBorder);
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        JPanel panel = new JPanel();
        panel.setBackground(new Color(248, 248, 255));
        panel.setBounds(77, 10, 282, 222);
        contentPane.add(panel);
        panel.setLayout(null);

        enderecoServidor = new JTextField();
        enderecoServidor.setFont(new Font("Poppins", Font.PLAIN, 12));
        enderecoServidor.setBackground(new Color(220, 220, 220));
        enderecoServidor.setBounds(24, 72, 234, 30);
        panel.add(enderecoServidor);
        enderecoServidor.setColumns(10);

        JLabel lblNewLabel = new JLabel("Cliente");
        lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
        lblNewLabel.setFont(new Font("Poppins", Font.PLAIN, 16));
        lblNewLabel.setBounds(24, 15, 234, 30);
        panel.add(lblNewLabel);

        JLabel lblIp = new JLabel("IP");
        lblIp.setHorizontalAlignment(SwingConstants.LEFT);
        lblIp.setFont(new Font("Poppins", Font.PLAIN, 12));
        lblIp.setBounds(23, 45, 217, 30);
        panel.add(lblIp);

        btnConectar = new JButton("ENTRAR");
        btnConectar.setFont(new Font("Poppins", Font.PLAIN, 12));
        btnConectar.setBounds(73, 175, 136, 30);
        panel.add(btnConectar);

        JLabel lblPorta = new JLabel("PORTA");
        lblPorta.setHorizontalAlignment(SwingConstants.LEFT);
        lblPorta.setFont(new Font("Poppins", Font.PLAIN, 12));
        lblPorta.setBounds(23, 105, 217, 30);
        panel.add(lblPorta);

        portaField = new JTextField();
        portaField.setFont(new Font("Poppins", Font.PLAIN, 12));
        portaField.setBackground(new Color(220, 220, 220));
        portaField.setColumns(10);
        portaField.setBounds(24, 130, 234, 30);
        panel.add(portaField);

        // Criação do modelo (ClienteModel) e controller (ClienteController)
        ClienteModel clienteModel = new ClienteModel();
        this.controller = new ClienteController(this, clienteModel);
        
        // Vinculando o listener do botão de conectar
        btnConectar.addActionListener(controller.getConectarServidorListener());
    }

    public JTextField getEnderecoServidor() {
        return enderecoServidor;
    }

    public JTextField getPortaField() {
        return portaField;
    }

    public JButton getBtnConectar() {
        return btnConectar;
    }
}

package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import org.json.simple.JSONObject;

import model.ClienteModel;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AvisosView extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JCheckBox chkEsportes;
    private JCheckBox chkClima;
    private JCheckBox chkPolitica;
    private JCheckBox chkEconomia;
    private JCheckBox chkEntretenimento;
    private JTextArea avisosArea;
    private JButton btnAtualizar;
    private JButton btnVoltar;
	private ClienteModel cliente;
	private MainView telaPrincipal;
	private String token;

    public AvisosView(ClienteModel cliente, String token) { 
    	this.cliente = cliente;
    	this.token = token;
    	
        setTitle("Avisos e Notícias");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 450, 518);
        
        setLocationRelativeTo(null); // Centraliza a janela na tela
        
        contentPane = new JPanel();
        contentPane.setBackground(SystemColor.windowBorder); // Cor de fundo da janela
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        JPanel panel = new JPanel();
        panel.setBackground(new Color(248, 248, 255)); // Cor de fundo do painel
        panel.setBounds(20, 15, 400, 453);
        contentPane.add(panel);
        panel.setLayout(null);

        JLabel lblTitulo = new JLabel("Avisos e Notícias");
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Poppins", Font.PLAIN, 16));
        lblTitulo.setBounds(80, 10, 240, 25);
        panel.add(lblTitulo);

        // Criação das caixas de seleção (CheckBoxes) para os tópicos pré-definidos
        chkEsportes = new JCheckBox("Esportes");
        chkEsportes.setFont(new Font("Poppins", Font.PLAIN, 12));
        chkEsportes.setBounds(20, 45, 130, 25);
        panel.add(chkEsportes);

        chkClima = new JCheckBox("Clima");
        chkClima.setFont(new Font("Poppins", Font.PLAIN, 12));
        chkClima.setBounds(150, 45, 130, 25);
        panel.add(chkClima);

        chkPolitica = new JCheckBox("Política");
        chkPolitica.setFont(new Font("Poppins", Font.PLAIN, 12));
        chkPolitica.setBounds(280, 45, 100, 25);
        panel.add(chkPolitica);

        chkEconomia = new JCheckBox("Economia");
        chkEconomia.setFont(new Font("Poppins", Font.PLAIN, 12));
        chkEconomia.setBounds(80, 72, 130, 25);
        panel.add(chkEconomia);

        chkEntretenimento = new JCheckBox("Entretenimento");
        chkEntretenimento.setFont(new Font("Poppins", Font.PLAIN, 12));
        chkEntretenimento.setBounds(210, 72, 130, 25);
        panel.add(chkEntretenimento);

        // Reposicionamento do botão para abaixo das caixas de seleção
        btnAtualizar = new JButton("Atualizar");
        btnAtualizar.setFont(new Font("Poppins", Font.PLAIN, 12));
        btnAtualizar.setBounds(150, 107, 100, 25);
        panel.add(btnAtualizar);

        avisosArea = new JTextArea();
        avisosArea.setFont(new Font("Poppins", Font.PLAIN, 12));
        avisosArea.setEditable(false);
        avisosArea.setLineWrap(true);
        avisosArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(avisosArea); // Adiciona barra de rolagem
        scrollPane.setBounds(20, 147, 360, 265);
        panel.add(scrollPane);

        // Adicionando o botão de Logout
        btnVoltar = new JButton("Voltar");
        btnVoltar.setFont(new Font("Poppins", Font.PLAIN, 12));
        btnVoltar.setBounds(150, 420, 100, 25); // Posição do botão
        panel.add(btnVoltar);
        
        btnVoltar.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				
                dispose();
                telaPrincipal.setVisible(true);
			}
        });
    }
    
    public void logoutUsuario(JSONObject res) {
        if (this.cliente == null) {
            System.err.println("O cliente está nulo, você deve primeiro inicializar o cliente e o servidor");
        } else {
            this.cliente.enviarMensagem(res);
        }
    }

    // Getters para os checkboxes e botão de cadastro
    public JCheckBox getChkEsportes() {
        return chkEsportes;
    }

    public JCheckBox getChkClima() {
        return chkClima;
    }

    public JCheckBox getChkPolitica() {
        return chkPolitica;
    }

    public JCheckBox getChkEconomia() {
        return chkEconomia;
    }

    public JCheckBox getChkEntretenimento() {
        return chkEntretenimento;
    }

    public JButton getBtnAtualizar() {
        return btnAtualizar;
    }

    public JButton getBtnLogout() { 
        return btnVoltar;
    }

    public JTextArea getAvisosArea() {
        return avisosArea;
    }
    
    public String getToken() {
		return token;
	}
    
    public AvisosView getAvisosView() {
		return this;
	}

	public MainView getTelaPrincipal() {
		return telaPrincipal;
	}
	
	public void setTelaPrincipal(MainView telaPrincipal) {
		this.telaPrincipal = telaPrincipal;
	}

	// Método para adicionar avisos/notícias à área de texto
    public void adicionarAviso(String aviso) {
        avisosArea.append(aviso + "\n");
    }
}

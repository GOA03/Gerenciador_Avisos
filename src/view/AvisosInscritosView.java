package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import model.AvisoModel;
import model.ClienteModel;

public class AvisosInscritosView extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JButton btnFechar; // Botão para fechar a janela
    private ClienteModel cliente;
    private JPanel panelAvisos;

    public AvisosInscritosView(ClienteModel cliente, List<AvisoModel> avisos) {
        this.cliente = cliente;

        setTitle("Avisos e Notícias");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(15, 15, 15, 15));
        contentPane.setLayout(new BorderLayout(15, 15));
        setContentPane(contentPane);

        // Header estilizado
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(30, 144, 255));
        headerPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        JLabel lblTitulo = new JLabel("Avisos e Notícias", JLabel.CENTER);
        lblTitulo.setFont(new Font("Poppins", Font.BOLD, 20));
        lblTitulo.setForeground(Color.WHITE);
        headerPanel.add(lblTitulo, BorderLayout.CENTER);
        contentPane.add(headerPanel, BorderLayout.NORTH);

        // Painel principal com layout GridBagLayout para ajuste de proporções
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.BOTH;
        contentPane.add(mainPanel, BorderLayout.CENTER);

        // Painel de avisos estilizado
        panelAvisos = new JPanel();
        panelAvisos.setBorder(BorderFactory.createTitledBorder(new LineBorder(Color.DARK_GRAY, 2), "Avisos",
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Poppins", Font.BOLD, 14), Color.DARK_GRAY));
        panelAvisos.setLayout(new BoxLayout(panelAvisos, BoxLayout.Y_AXIS));
        JScrollPane scrollAvisos = new JScrollPane(panelAvisos);
        gbc.gridx = 0;
        gbc.weightx = 1.0; // Ocupar toda a largura
        gbc.weighty = 1.0; // Ocupar toda a altura
        mainPanel.add(scrollAvisos, gbc);

        // Rodapé com o botão "Fechar"
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 2, 2)); 

        // Adicionando o botão "Fechar"
        btnFechar = new JButton("Fechar");
        customizeButton(btnFechar, Color.RED);
        footerPanel.add(btnFechar);

        contentPane.add(footerPanel, BorderLayout.SOUTH);

        // Exibir avisos
        atualizarAvisosTela(avisos);

        btnFechar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
                // Aqui você pode adicionar a lógica para retornar à tela principal, se necessário
            }
        });

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                cliente.deslogarUsuario(); // Desloga o usuário ao fechar a janela
                System.exit(0); // Encerra a aplicação
            }
        });
    }

    public void atualizarAvisosTela(List<AvisoModel> avisos) {
        panelAvisos.removeAll(); // Remove todos os componentes existentes

        // Verifica se há avisos
        if (avisos == null || avisos.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Não há avisos disponíveis!", "Sem Avisos",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            // Itera sobre os avisos
            for (AvisoModel aviso : avisos) {
                JPanel avisoPanel = new JPanel(new BorderLayout());
                avisoPanel.setBorder(BorderFactory.createCompoundBorder(new LineBorder(Color.LIGHT_GRAY, 2),
                        new EmptyBorder(10, 10, 10, 10)));
                avisoPanel.setBackground(new Color(245, 245, 245));

                // Exibe o título do aviso
                JLabel lblTitulo = new JLabel(aviso.getTitulo());
                lblTitulo.setFont(new Font("Poppins", Font.BOLD, 16));
                avisoPanel.add(lblTitulo, BorderLayout.NORTH);

                // Exibe a descrição do aviso
                JTextArea txtDescricao = new JTextArea(aviso.getDescricao());
                txtDescricao.setWrapStyleWord(true);
                txtDescricao.setLineWrap(true);
                txtDescricao.setEditable(false);
                txtDescricao.setFont(new Font("Poppins", Font.PLAIN, 14));
                txtDescricao.setBorder(null);
                txtDescricao.setPreferredSize(new java.awt.Dimension(0, 50)); // Altura padrão

                avisoPanel.add(txtDescricao, BorderLayout.CENTER);
                panelAvisos.add(avisoPanel);
            }
        }

        panelAvisos.revalidate(); // Revalida o painel para refletir as mudanças
        panelAvisos.repaint(); // Repaint para atualizar a interface
    }

    // Método para customizar botões
    private void customizeButton(JButton button, Color backgroundColor) {
        button.setFont(new Font("Poppins", Font.BOLD, 14));
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setPreferredSize(new java.awt.Dimension(150, 40)); // Tamanho do botão

        // Efeito de hover
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(button.getBackground().darker());
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(backgroundColor);
            }

            public void mousePressed(java.awt.event.MouseEvent evt) {
                button.setBackground(button.getBackground().brighter());
            }

            public void mouseReleased(java.awt.event.MouseEvent evt) {
                button.setBackground(button.getBackground().darker());
            }
        });
    }

    public ClienteModel getCliente() {
        return cliente;
    }
}
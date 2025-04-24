package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import org.json.simple.JSONObject;

import controller.JSONController;
import model.AvisoModel; // Certifique-se de que esta classe existe
import model.CategoriaModel;
import model.ClienteModel;
import model.RespostaModel;
import model.UsuarioModel;

public class AvisosView extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JButton btnInscrever;
    private JButton btnVoltar;
    private ClienteModel cliente;
    private MainView telaPrincipal;
    private String token;
    private List<JCheckBox> checkBoxes;
    private JPanel panelCategorias;
    private JPanel panelAvisos;
    private List<Integer> idsCategorias;
    private List<CategoriaModel> categoriasCadastradas; // Ensure this is initialized
    private List<CategoriaModel> todasCategorias;

    public AvisosView(ClienteModel cliente, String token, List<CategoriaModel> categorias) {
        this.cliente = cliente;
        this.token = token;
        this.idsCategorias = new ArrayList<>();
        this.categoriasCadastradas = new ArrayList<>(); // Initialize the list

        for (CategoriaModel categoria : categorias) {
            this.idsCategorias.add(categoria.getId());
        }

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

        // Painel de categorias estilizado
        panelCategorias = new JPanel();
        panelCategorias.setBorder(BorderFactory.createTitledBorder(new LineBorder(Color.DARK_GRAY, 2), "Categorias",
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Poppins", Font.BOLD, 14), Color.DARK_GRAY));
        panelCategorias.setLayout(new BoxLayout(panelCategorias, BoxLayout.Y_AXIS));
        JScrollPane scrollCategorias = new JScrollPane(panelCategorias);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3; // 1/3 da tela
        gbc.weighty = 1.0;
        mainPanel.add(scrollCategorias, gbc);

        // Painel de avisos estilizado
        panelAvisos = new JPanel();
        panelAvisos.setBorder(BorderFactory.createTitledBorder(new LineBorder(Color.DARK_GRAY, 2), "Avisos",
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Poppins", Font.BOLD, 14), Color.DARK_GRAY));
        panelAvisos.setLayout(new BoxLayout(panelAvisos, BoxLayout.Y_AXIS));
        JScrollPane scrollAvisos = new JScrollPane(panelAvisos);
        gbc.gridx = 1;
        gbc.weightx = 0.7; // 2/3 da tela
        mainPanel.add(scrollAvisos, gbc);

        // Rodapé com botões estilizados
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        btnInscrever = new JButton("+ Categorias");
        customizeButton(btnInscrever, Color.GREEN);
        footerPanel.add(btnInscrever);

        btnInscrever.addActionListener(new ActionListener() {
            private List<CategoriaModel> categoriasDisponiveis = new ArrayList<>(); // Initialize the list

            public void actionPerformed(ActionEvent e) {
                categoriasDisponiveis.clear(); // Clear the list before adding new categories
                for (CategoriaModel categoriaModel : todasCategorias) {
                    boolean isCadastrada = false; // Flag to check if the category is already registered
                    for (CategoriaModel categoriaCadastrada : categoriasCadastradas) {
                        if (categoriaModel.getId() == categoriaCadastrada.getId()) {
                            isCadastrada = true; // Set flag if the category is already registered
                            break; // No need to check further
                        }
                    }
                    if (!isCadastrada) { // If the category is not registered, add it to the list
                        categoriasDisponiveis.add(categoriaModel);
                    }
                }
                InscricaoCategoriasView inscricaoCategoriaView = new InscricaoCategoriasView(cliente,
                        categoriasDisponiveis);
                inscricaoCategoriaView.setVisible(true);
            }
        });

        // Adicionando o botão "Ver Avisos"
        JButton btnVerAvisos = new JButton("Ver Avisos");
        customizeButton(btnVerAvisos, new Color(30, 144, 255));
        footerPanel.add(btnVerAvisos);

        // Adicionando o ActionListener para o botão "Ver Avisos"
        btnVerAvisos.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Clear the existing alerts from the panel
                panelAvisos.removeAll();
                List<Integer> categoriasSelecionadas = getCategoriasSelecionadas();
                if (!categoriasSelecionadas.isEmpty()) {
                    pedirAvisos(categoriasSelecionadas);
                } else {
                    JOptionPane.showMessageDialog(null, "Por favor, selecione uma categoria.", "Nenhuma Categoria Selecionada",
                            JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        // Adicionando o botão "Desinscrever-se"
        JButton btnDesinscrever = new JButton("Desinscrever");
        customizeButton(btnDesinscrever, Color.RED); // Personaliza o botão com a cor vermelha
        footerPanel.add(btnDesinscrever);

        // Adicionando o ActionListener para o botão "Desinscrever-se"
        btnDesinscrever.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Chama o método para descadastrar o usuário das categorias selecionadas
                descadastrarUsuario(getCategoriasSelecionadas());
            }
        });

        // Adicionando o botão "Voltar"
        btnVoltar = new JButton("Voltar");
        customizeButton(btnVoltar, Color.ORANGE);
        footerPanel.add(btnVoltar);

        contentPane.add(footerPanel, BorderLayout.SOUTH);

        checkBoxes = new ArrayList<>();
        pedirCategorias(idsCategorias);

        btnVoltar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
                telaPrincipal.setVisible(true);
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

    protected void pedirAvisos(List<Integer> categorias) {
        for (Integer categoria : categorias) {
            // Cria um objeto RespostaModel para enviar ao cliente
            RespostaModel resposta = new RespostaModel();
            resposta.setOperacao("listarAvisos"); // Define a operação
            resposta.setToken(token); // Usando o token do cliente
            resposta.setIdCategoria(categoria);

            // Converte a resposta para JSON
            JSONController jsonController = new JSONController();
            JSONObject json = jsonController.changeToJSONCategoria(resposta);

            // Envia a mensagem ao cliente
            cliente.enviarMensagem(json);
        }
    }

    protected void descadastrarUsuario(List<Integer> categorias) {
        for (Integer categoria : categorias) {
            // Cria um objeto RespostaModel para enviar ao cliente
            RespostaModel resposta = new RespostaModel();
            resposta.setOperacao("descadastrarUsuarioCategoria"); // Define a operação
            resposta.setToken(token); // Usando o token do cliente
            resposta.setIdCategoria(categoria);
            resposta.setRa(token); // Adicionando o RA do usuário

            // Converte a resposta para JSON
            JSONController jsonController = new JSONController();
            JSONObject json = jsonController.changeToJSONCategoria(resposta);
            
            // Envia a mensagem ao cliente
            cliente.enviarMensagem(json);
        }
    }

    public void pedirCategorias(List<Integer> idsCategorias) {
        UsuarioModel usuario = new UsuarioModel();
        usuario.setOperacao("listarCategorias");
        usuario.setToken(token); // Usando o token do cliente

        // Converte o usuário para JSON
        JSONController jsonController = new JSONController();
        JSONObject res = jsonController.changeToJSON(usuario);

        // Envia a requisição ao servidor
        cliente.enviarMensagem(res);
    }

    public void atualizarCategorias(List<CategoriaModel> categorias) {
        this.todasCategorias = categorias;
        atualizarCategoriasTela(categorias, idsCategorias);
    }

    public void atualizarCategoriasTela(List<CategoriaModel> categorias, List<Integer> idsCategorias) {
        panelCategorias.removeAll(); // Remove todos os componentes existentes
        checkBoxes.clear(); // Limpa a lista de checkboxes

        // Verifica se há categorias
        if (categorias == null || categorias.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Você não não está inscrito em nenhuma categoria!", "Sem inscrições",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            // Itera sobre as categorias
            for (CategoriaModel categoria : categorias) {
                // Verifica se a categoria está presente na lista de IDs
                if (idsCategorias.contains(categoria.getId())) {
                    this.categoriasCadastradas.add(categoria);

                    JCheckBox checkBox = new JCheckBox(categoria.getNome());
                    checkBox.setFont(new Font("Poppins", Font.PLAIN, 14));
                    checkBox.setAlignmentX(Component.LEFT_ALIGNMENT);
                    panelCategorias.add(Box.createVerticalStrut(10)); // Espaçamento entre categorias
                    panelCategorias.add(checkBox);
                    checkBoxes.add(checkBox); // Adiciona o checkbox à lista
                }
            }
        }

        panelCategorias.revalidate(); // Revalida o painel para refletir as mudanças
        panelCategorias.repaint(); // Repaint para atualizar a interface
    }

    public void adicionarAviso(String titulo, String descricao) {
        JPanel avisoPanel = new JPanel(new BorderLayout());
        avisoPanel.setBorder(BorderFactory.createCompoundBorder(new LineBorder(Color.LIGHT_GRAY, 2),
                new EmptyBorder(10, 10, 10, 10)));
        avisoPanel.setBackground(new Color(245, 245, 245));

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Poppins", Font.BOLD, 16));
        avisoPanel.add(lblTitulo, BorderLayout.NORTH);

        JTextArea txtDescricao = new JTextArea(descricao);
        txtDescricao.setWrapStyleWord(true);
        txtDescricao.setLineWrap(true);
        txtDescricao.setEditable(false);
        txtDescricao.setFont(new Font("Poppins", Font.PLAIN, 14));
        txtDescricao.setBorder(null);

        // Definindo a altura máxima para 2 linhas
        int linhaAltura = txtDescricao.getFontMetrics(txtDescricao.getFont()).getHeight();
        txtDescricao.setPreferredSize(new java.awt.Dimension(0, linhaAltura * 2)); // 2 linhas de altura

        // Verifica se a descrição excede 2 linhas
        if (txtDescricao.getLineCount() > 2) {
            avisoPanel.add(new JScrollPane(txtDescricao), BorderLayout.CENTER);
        } else {
            avisoPanel.add(txtDescricao, BorderLayout.CENTER);
        }

        panelAvisos.add(avisoPanel);
        panelAvisos.revalidate();
        panelAvisos.repaint();
    }

    public void atualizarAvisos(List<AvisoModel> avisos) {
        // Verifica se a lista de avisos é nula ou vazia
        if (avisos == null || avisos.isEmpty()) {
            return; // Retorna sem fazer nada
        }

        // Mapa para agrupar avisos por categoria
        Map<String, List<AvisoModel>> avisosPorCategoria = new HashMap<>();

        // Agrupa avisos por categoria
        for (AvisoModel aviso : avisos) {
            if (aviso == null) {
                continue; // Ignora avisos nulos
            }

            String categoriaNome = Optional.ofNullable(aviso.getCategoria())
                                           .map(categoria -> categoria.getNome())
                                           .orElse("Sem Categoria");
            avisosPorCategoria.putIfAbsent(categoriaNome, new ArrayList<>());
            avisosPorCategoria.get(categoriaNome).add(aviso);
        }

        // Cria um painel para cada categoria e adiciona os avisos a ele
        for (Map.Entry<String, List<AvisoModel>> entry : avisosPorCategoria.entrySet()) {
            String categoriaNome = entry.getKey();
            List<AvisoModel> listaAvisos = entry.getValue();

            // Cria um novo painel para a categoria
            JPanel panelCategoria = new JPanel();
            panelCategoria.setBorder(BorderFactory.createTitledBorder(categoriaNome));
            panelCategoria.setLayout(new BoxLayout(panelCategoria, BoxLayout.Y_AXIS));

            // Adiciona cada aviso ao painel da categoria
            for (AvisoModel aviso : listaAvisos) {
                JPanel avisoPanel = criarPainelAviso(aviso);
                panelCategoria.add(avisoPanel);
            }

            // Adiciona o painel da categoria ao painel principal de avisos
            panelAvisos.add(panelCategoria);
        }

        // Atualiza o painel para mostrar os novos avisos
        panelAvisos.revalidate();
        panelAvisos.repaint();
    }

    // Método auxiliar para criar o painel de um aviso
    private JPanel criarPainelAviso(AvisoModel aviso) {
        JPanel avisoPanel = new JPanel(new BorderLayout());
        avisoPanel.setBorder(BorderFactory.createCompoundBorder(new LineBorder(Color.LIGHT_GRAY, 2),
                new EmptyBorder(10, 10, 10, 10)));
        avisoPanel.setBackground(new Color(245, 245, 245));

        JLabel lblTitulo = new JLabel(aviso.getTitulo());
        lblTitulo.setFont(new Font("Poppins", Font.BOLD, 16));
        avisoPanel.add(lblTitulo, BorderLayout.NORTH);

        JTextArea txtDescricao = new JTextArea(aviso.getDescricao());
        txtDescricao.setWrapStyleWord(true);
        txtDescricao.setLineWrap(true);
        txtDescricao.setEditable(false);
        txtDescricao.setFont(new Font("Poppins", Font.PLAIN, 14));
        txtDescricao.setBorder(null);

        // Definindo a altura máxima para 2 linhas
        int linhaAltura = txtDescricao.getFontMetrics(txtDescricao.getFont()).getHeight();
        txtDescricao.setPreferredSize(new java.awt.Dimension(0, linhaAltura * 2)); // 2 linhas de altura

        // Verifica se a descrição excede 2 linhas
        if (txtDescricao.getLineCount() > 2) {
            avisoPanel.add(new JScrollPane(txtDescricao), BorderLayout.CENTER);
        } else {
            avisoPanel.add(txtDescricao, BorderLayout.CENTER);
        }

        return avisoPanel;
    }

    public List<Integer> getCategoriasSelecionadas() {
        List<Integer> categoriasSelecionadas = new ArrayList<>();

        // Percorre todos os checkboxes e verifica quais estão selecionados
        for (int i = 0; i < checkBoxes.size(); i++) {
            JCheckBox checkBox = checkBoxes.get(i);
            if (checkBox.isSelected()) {
                // Encontra o ID correspondente baseado no nome da categoria
                for (CategoriaModel categoria : categoriasCadastradas) {
                    // Verifica se o nome da categoria não é nulo antes de comparar
                    if (categoria.getNome() != null && categoria.getNome().equals(checkBox.getText())) {
                        categoriasSelecionadas.add(categoria.getId());
                        break;
                    }
                }
            }
        }

        // Verifica se nenhuma categoria foi selecionada
        if (categoriasSelecionadas.isEmpty()) {
            // Clear the existing alerts from the panel
            panelAvisos.removeAll();
            // Exibe uma mensagem para o usuário
            JOptionPane.showMessageDialog(null, "Por favor, selecione uma categoria.", "Nenhuma Categoria Selecionada",
                    JOptionPane.WARNING_MESSAGE);
        }

        return categoriasSelecionadas;
    }

    public void setTelaPrincipal(MainView telaPrincipal) {
        this.telaPrincipal = telaPrincipal;
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
}
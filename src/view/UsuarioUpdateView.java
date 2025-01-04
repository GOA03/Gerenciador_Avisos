package view;

import java.awt.GridLayout;

import javax.swing.*;

public class UsuarioUpdateView extends JFrame {

    private static final long serialVersionUID = 1L;
    private JTextField nomeField;
    private JTextField raField;
    private JPasswordField senhaField;
    private JButton btnSalvar;
    private JButton btnCancelar;

    public UsuarioUpdateView(String token) {
        setTitle("Atualizar Informações do Usuário");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null); // Centraliza a janela

        JPanel panel = new JPanel();
        getContentPane().add(panel);
        panel.setLayout(new GridLayout(4, 2));

        panel.add(new JLabel("RA:"));
        raField = new JTextField(); // Use the initialized usuario
        raField.setEditable(false); // RA não deve ser editável
        panel.add(raField);

        panel.add(new JLabel("Nome:"));
        nomeField = new JTextField(); // Use the initialized usuario
        panel.add(nomeField);

        panel.add(new JLabel("Senha:"));
        senhaField = new JPasswordField(); // Use the initialized usuario
        panel.add(senhaField);

        // Botão para salvar as alterações
        btnSalvar = new JButton("Salvar");
        btnSalvar.addActionListener(e -> salvarInformacoes());
        panel.add(btnSalvar);

        // Botão para cancelar a atualização
        btnCancelar = new JButton("Cancelar");
        btnCancelar.addActionListener(e -> dispose()); // Fecha a janela ao clicar
        panel.add(btnCancelar);
    }

    private void salvarInformacoes() {
        // Atualiza as informações do usuário
        //usuario.setNome(nomeField.getText());
        //usuario.setSenha(new String(senhaField.getPassword()));

        // Converte o usuário para JSON e envia para o servidor
        //JSONController jsonController = new JSONController();
        //JSONObject res = jsonController.changeToJSON(usuario);

        // Aqui você deve implementar a lógica para enviar as informações atualizadas ao servidor
        // Por exemplo:
        // cliente.enviarMensagem(res);

        JOptionPane.showMessageDialog(this, "Informações do usuário atualizadas com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        dispose(); // Fecha a janela após salvar
    }
}
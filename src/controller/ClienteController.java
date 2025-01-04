package controller;

import model.ClienteModel;
import view.ClienteView;
import view.LoginView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JOptionPane;

public class ClienteController {
	
    private ClienteView view;
    private ClienteModel cliente;

    public ClienteController(ClienteView view, ClienteModel model) {
    	
        this.view = view;
        this.cliente = model;
    }

    public ActionListener getConectarServidorListener() {
    	
        return new ActionListener() {
        	
            public void actionPerformed(ActionEvent e) {
            	
                String ip = view.getEnderecoServidor().getText();
                String porta = view.getPortaField().getText();
                conectarServidor(ip, porta);
            }
        };
    }

    private void conectarServidor(String ip, String porta) {
    	
        try {
        	
            int portaServidor = Integer.parseInt(porta);
            cliente.conectar(ip, portaServidor);  // Conexão ao servidor
            JOptionPane.showMessageDialog(view, "Conectado ao servidor com sucesso!");
            
            // Fechar a janela de conexão e abrir a tela de login (por exemplo)
            view.dispose();
            new LoginView(this.cliente).setVisible(true);
            
        } catch (NumberFormatException e) {
        	
            JOptionPane.showMessageDialog(view, "Porta inválida!");
            
        } catch (IOException e) {
        	
            JOptionPane.showMessageDialog(view, "Erro ao conectar ao servidor: " + e.getMessage());
        }
    }
}

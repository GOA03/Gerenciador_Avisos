package controller;

import model.ServidorModel;
import view.InterfaceServidorView;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.Socket;

public class ServidorController {
	
    private InterfaceServidorView view;
    
    public ServidorController(InterfaceServidorView view, ServidorModel model) {
        
    	this.view = view;
        this.view.adicionarActionListenerConectar(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int porta = Integer.parseInt(view.getPortaServidor().getText());
                try {
                    model.iniciarServidor(porta);
                    view.getRetornoServidor().append("Servidor iniciado na porta: " + porta + "\n");
                    view.getRetornoServidor().append("Esperando conexão...\n");

                    // Thread para aceitar conexões de clientes
                    new Thread(() -> {
                        while (true) {
                            try {
                                Socket socketCliente = model.esperarConexao();
                                model.lidarComCliente(socketCliente, new ServidorModel.ServidorListener() {
                                    @Override
                                    public void onConexao(String clienteInfo) {
                                        view.getRetornoServidor().append("Nova conexão bem-sucedida com cliente: " + clienteInfo + "\n");
                                    }

                                    @Override
                                    public void onMensagemRecebida(String mensagem) {
                                        view.getRetornoServidor().append("Cliente: " + mensagem + "\n");
                                    }
                                    
                                    @Override
                                    public void onMensagemEnviada(String mensagem) {
                                        view.getRetornoServidor().append("Servidor: " + mensagem + "\n\n");
                                    }

                                    @Override
                                    public void onDesconexao() {
                                        view.getRetornoServidor().append("Cliente desconectou.\n");
                                    }

                                    @Override
                                    public void onErro(String erro) {
                                        view.getRetornoServidor().append(erro + "\n");
                                    }
                                });
                            } catch (IOException ex) {
                                view.getRetornoServidor().append("Erro ao aceitar conexão: " + ex.getMessage() + "\n");
                                break;
                            }
                        }
                    }).start();
                } catch (IOException ex) {
                    view.getRetornoServidor().append("Erro ao iniciar servidor: " + ex.getMessage() + "\n");
                }
            }
        });
    }
}

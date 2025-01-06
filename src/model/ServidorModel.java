package model;

import java.io.*;
import java.net.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.json.simple.JSONObject;

import controller.CadastroController;
import controller.JSONController;
import controller.LoginController;
import dao.BancoDados;
import dao.UsuarioDAO;
import enums.CadastroEnum;
import enums.LoginEnum;

public class ServidorModel {
    private ServerSocket serverSocket;

    // Inicia o servidor
    public void iniciarServidor(int porta) throws IOException {
        try {
            serverSocket = new ServerSocket(porta, 50, InetAddress.getByName("0.0.0.0"));
            System.out.println("Servidor iniciado na porta " + porta);
        } catch (IOException e) {
            System.err.println("Erro ao iniciar o servidor: " + e.getMessage());
            throw new IOException("Não foi possível iniciar o servidor.");
        }
    }

    // Aguarda conexão de um cliente
    public Socket esperarConexao() throws IOException {
        System.out.println("Aguardando conexão...");
        try {
            Socket cliente = serverSocket.accept();
            System.out.println("Cliente conectado: " + cliente.getInetAddress());
            return cliente;
        } catch (IOException e) {
            System.err.println("Erro ao aceitar conexão: " + e.getMessage());
            throw new IOException("Erro ao aceitar conexão do cliente.");
        }
    }

    // Fecha o servidor
    public void fecharServidor() throws IOException {
        if (serverSocket != null && !serverSocket.isClosed()) {
            try {
                serverSocket.close();
                System.out.println("Servidor fechado com sucesso.");
            } catch (IOException e) {
                System.err.println("Erro ao fechar o servidor: " + e.getMessage());
                throw new IOException("Não foi possível fechar o servidor.");
            }
        }
    }

    // Comunicação com o cliente
    public void lidarComCliente(Socket cliente, ServidorListener listener) {
        new Thread(() -> {
            JSONController jsonController = new JSONController();
            LoginController loginController = new LoginController();
            CadastroController cadastroController = new CadastroController();

            try (
                PrintWriter saida = new PrintWriter(cliente.getOutputStream(), true);
                BufferedReader entrada = new BufferedReader(new InputStreamReader(cliente.getInputStream()))
            ) {
                listener.onConexao(cliente.getInetAddress().toString());
                System.out.println("Novo cliente: " + cliente.getInetAddress());

                String mensagemRecebida;
                while ((mensagemRecebida = entrada.readLine()) != null) {
                    System.out.println("C -> S: " + mensagemRecebida);
                    listener.onMensagemRecebida(mensagemRecebida);

                    String operacao = jsonController.getOperacao(mensagemRecebida);
                    processarOperacao(operacao, mensagemRecebida, saida, jsonController, loginController, cadastroController, listener);
                }
            } catch (IOException e) {
                listener.onErro("Erro na comunicação: " + e.getMessage());
                System.err.println("Erro na comunicação: " + e.getMessage());
            } finally {
                fecharConexaoCliente(cliente, listener);
            }
        }).start();
    }

    // Processa as operações recebidas do cliente
    private void processarOperacao(String operacao, String mensagemRecebida, PrintWriter saida, JSONController jsonController, LoginController loginController, CadastroController cadastroController, ServidorListener listener) throws IOException {
        switch (operacao) {
            case "login":
                processarLogin(mensagemRecebida, saida, jsonController, loginController, listener);
                break;
            case "cadastrarUsuario":
                processarCadastro(mensagemRecebida, saida, jsonController, cadastroController, listener);
                break;
            case "logout":
                processarLogout(saida, jsonController, listener);
                break;
            case "listarUsuarios":
                processarListarUsuarios(saida, jsonController, listener);
                break;
            case "excluirUsuario":
            	processarExcluirUsuarios(mensagemRecebida, saida, jsonController, listener);
                break;
            case "editarUsuario":
            	processarEditarUsuarios(mensagemRecebida, saida, jsonController, listener);
                break;
            default:
                enviarErroOperacaoInvalida(operacao, saida, jsonController, listener);
        }
    }

    // Processa a operação de login
    private void processarLogin(String mensagemRecebida, PrintWriter saida, JSONController jsonController, LoginController loginController, ServidorListener listener) {
        UsuarioModel usuario = jsonController.changeLoginToJSON(mensagemRecebida);
        RespostaModel resposta = new RespostaModel();
        if (usuario == null || usuario.getRa() == null || usuario.getSenha() == null) {
            resposta.setStatus(401);
            resposta.setMsg("Dados inválidos.");
        } else {
            LoginEnum statusLogin = null;
            try {
                statusLogin = loginController.validarLogin(usuario);
            } catch (IOException e) {
                e.printStackTrace();
            }
            resposta.setStatus(401);
            if (statusLogin == LoginEnum.SUCESSO) {
                try {
                    String token = loginController.getRa(usuario.getRa());
                    resposta.setStatus(200);
                    //resposta.setOperacao("login");
                    resposta.setToken(token);
                } catch (SQLException e) {
                	System.out.println(e);
                	resposta.setStatus(401);
                    resposta.setMsg("Erro ao acessar o banco de dados.");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                resposta.setMsg(obterMensagemErroLogin(statusLogin));
            }
        }
        enviarResposta(resposta, saida, jsonController, listener);
    }

    // Processa a operação de cadastro
    private void processarCadastro(String mensagemRecebida, PrintWriter saida, JSONController jsonController, CadastroController cadastroController, ServidorListener listener) {
        UsuarioModel usuario = jsonController.changeRegisterJSON(mensagemRecebida);
        CadastroEnum statusCadastro = null;
        try {
            statusCadastro = cadastroController.validarCadastro(usuario);
        } catch (IOException e) {
            e.printStackTrace();
        }
        RespostaModel resposta = new RespostaModel();
        resposta.setOperacao("cadastrarUsuario");

        switch (statusCadastro) {
            case SUCESSO:
                try {
                    Connection conn = BancoDados.conectar();
                    new UsuarioDAO(conn).adicionarUsuario(usuario);
                    resposta.setStatus(201);
                    resposta.setMsg("Cadastro realizado com sucesso.");
                    BancoDados.desconectar();
                } catch (SQLException e) {
                	resposta.setStatus(401);
                    resposta.setMsg("Erro ao acessar o banco de dados.");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case RA_CADASTRADO:
                resposta.setStatus(401);
                resposta.setMsg("Não foi cadastrar pois o USUÁRIO INFORMADO JÁ EXISTE!");
                break;
            default:
                resposta.setStatus(401);
                resposta.setMsg("Os campos recebidos não são válidos.");
        }
        enviarResposta(resposta, saida, jsonController, listener);
    }

    // Processa a operação de logout
    private void processarLogout(PrintWriter saida, JSONController jsonController, ServidorListener listener) {
        RespostaModel resposta = new RespostaModel();
        resposta.setStatus(200);
        //resposta.setOperacao("logout");
        enviarResposta(resposta, saida, jsonController, listener);
    }
    
    private void processarListarUsuarios(PrintWriter saida, JSONController jsonController, ServidorListener listener) throws IOException {
        RespostaModel resposta = new RespostaModel();
        resposta.setOperacao("listarUsuarios");

        try {
            // Conectar ao banco de dados
            Connection conn = BancoDados.conectar();
            UsuarioDAO usuarioDAO = new UsuarioDAO(conn);
            
            // Recuperar a lista de usuários do banco de dados
            List<UsuarioModel> usuarios = usuarioDAO.listarUsuarios();

            // Verificar se a lista de usuários está vazia
            if (usuarios.isEmpty()) {
                resposta.setStatus(201); // Status para lista vazia
                resposta.setUsuarios(usuarios); // Retorna um array vazio
            } else {
                resposta.setStatus(201); // Status para sucesso
                resposta.setUsuarios(usuarios); // Define a lista de usuários
            }
        } catch (SQLException e) {
            resposta.setStatus(401); // Status de erro interno do servidor
            resposta.setMsg("Erro ao acessar o banco de dados: " + e.getMessage()); // Mensagem de erro
        } finally {
            // Enviar a resposta ao cliente
            enviarResposta(resposta, saida, jsonController, listener);
        }
    }
    
    private void processarExcluirUsuarios(String mensagemRecebida, PrintWriter saida, JSONController jsonController, ServidorListener listener) {
        UsuarioModel usuario = jsonController.changeRegisterJSON(mensagemRecebida);
        RespostaModel resposta = new RespostaModel();
        String token = usuario.getToken();

        if (usuario == null || usuario.getRa() == null) {
            resposta.setStatus(401);
            resposta.setMsg("Dados inválidos.");
        } else {
            try {
                Connection conn = BancoDados.conectar();
                UsuarioDAO usuarioDAO = new UsuarioDAO(conn);
                
                // Verifica se o RA existe no banco de dados
                String raExistente = usuarioDAO.getRA(usuario.getRa());
                if (raExistente == null) {
                    resposta.setStatus(401);
                    resposta.setMsg("Usuário não encontrado no banco de dados.");
                    enviarResposta(resposta, saida, jsonController, listener);
                    return; // Sai do método se o usuário não for encontrado
                }

                // Verifica se o token é igual a 2376342
                if ("2376342".equals(token)) {
                    // Permite a exclusão de qualquer usuário
                    usuarioDAO.removerUsuario(usuario); // Chama o método para remover o usuário do banco
                    resposta.setStatus(201);
                    resposta.setOperacao("excluirUsuario");
                    resposta.setMsg("Usuário removido com sucesso.");
                } else {
                    // Permite a exclusão apenas se o RA for igual ao token
                    if (usuario.getRa().equals(token)) {
                        usuarioDAO.removerUsuario(usuario); // Chama o método para remover o usuário do banco
                        resposta.setStatus(201);
                        resposta.setOperacao("excluirUsuario");
                        resposta.setMsg("Usuário removido com sucesso.");
                    } else {
                        // Caso contrário, recusa a permissão
                        resposta.setStatus(401); // Permissão negada
                        resposta.setMsg("Permissão recusada: você não tem autorização para excluir este usuário.");
                    }
                }
            } catch (SQLException e) {
                resposta.setStatus(401);
                resposta.setMsg("Erro ao acessar o banco de dados: " + e.getMessage());
            } catch (IOException e) {
                resposta.setStatus(401);
                resposta.setMsg("Erro de I/O: " + e.getMessage());
            } finally {
                try {
                    BancoDados.desconectar(); // Certifique-se de desconectar após a operação
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        // Envia a resposta ao cliente
        enviarResposta(resposta, saida, jsonController, listener);
    }
    
    private void processarEditarUsuarios(String mensagemRecebida, PrintWriter saida, JSONController jsonController, ServidorListener listener) {
    	System.out.println("Mensagem recebida servidor model: " + mensagemRecebida);
        UsuarioModel usuario = jsonController.changeJSONToUser(mensagemRecebida);
        RespostaModel resposta = new RespostaModel();

        resposta.setOperacao("editarUsuario");
        
        if (usuario == null || usuario.getRa() == null || usuario.getNome() == null || usuario.getSenha() == null) {
            resposta.setStatus(401);
            resposta.setMsg("Dados inválidos.");
        } else {
            try {
                Connection conn = BancoDados.conectar();
                UsuarioDAO usuarioDAO = new UsuarioDAO(conn);

                // Verifica se o RA existe no banco de dados
                String raExistente = usuarioDAO.getRA(usuario.getRa());
                if (raExistente == null) {
                    resposta.setStatus(401);
                    resposta.setMsg("Usuário não encontrado no banco de dados.");
                } else {
                    // Atualiza as informações do usuário
                    usuarioDAO.atualizarUsuario(usuario);
                    resposta.setStatus(201);
                    resposta.setMsg("Usuário atualizado com sucesso.");
                }
            } catch (SQLException e) {
                resposta.setStatus(401);
                resposta.setMsg("Erro ao acessar o banco de dados: " + e.getMessage());
            } catch (IOException e) {
                resposta.setStatus(401);
                resposta.setMsg("Erro de I/O: " + e.getMessage());
            } finally {
                try {
                    BancoDados.desconectar(); // Certifique-se de desconectar após a operação
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        // Envia a resposta ao cliente
        enviarResposta(resposta, saida, jsonController, listener);
    }

    // Envia mensagem de erro para operação inválida
    private void enviarErroOperacaoInvalida(String operacao, PrintWriter saida, JSONController jsonController, ServidorListener listener) {
        RespostaModel resposta = new RespostaModel();
        resposta.setStatus(401);
        resposta.setMsg("Operacao não encontrada: " + operacao);
        enviarResposta(resposta, saida, jsonController, listener);
    }

    // Envia a resposta ao cliente
    private void enviarResposta(RespostaModel resposta, PrintWriter saida, JSONController jsonController, ServidorListener listener) {
        JSONObject respostaJSON = jsonController.changeResponseToJson(resposta);
        System.out.println("S -> C: " + respostaJSON);
        if (listener != null) {
            listener.onMensagemEnviada(respostaJSON.toString());
        }
        saida.println(respostaJSON.toJSONString());
    }

    // Fecha a conexão com o cliente
    private void fecharConexaoCliente(Socket cliente, ServidorListener listener) {
        try {
            cliente.close();
            System.out.println("Conexão encerrada.");
            listener.onDesconexao();
        } catch (IOException e) {
            listener.onErro("Erro ao encerrar conexão: " + e.getMessage());
        }
    }

    // Obtém a mensagem de erro correspondente ao status de login
    private String obterMensagemErroLogin(LoginEnum status) {
        switch (status) {
            case ERRO_USUARIO_E_SENHA:
                return "Credenciais incorretas.";
            case ERRO_JSON:
                return "Não foi possível ler o json recebido.";
            case ERRO_VALIDACAO:
                return "Dados inválidos.";
            case ERRO_BANCO:
                return "Erro ao acessar o banco de dados.";
            default:
                return "Erro desconhecido.";
        }
    }

    // Interface para eventos do servidor
    public interface ServidorListener {
        void onConexao(String clienteInfo);

        void onMensagemRecebida(String mensagem);

        void onDesconexao();

        void onErro(String erro);

        void onMensagemEnviada(String mensagem);
    }
}

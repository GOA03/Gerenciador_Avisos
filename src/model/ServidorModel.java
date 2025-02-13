package model;

import java.io.*;
import java.net.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import controller.CadastroController;
import controller.JSONController;
import controller.LoginController;
import dao.AvisoDAO;
import dao.BancoDados;
import dao.CategoriaDAO;
import dao.InscricaoDAO;
import dao.UsuarioDAO;
import entities.Inscricao;
import enums.CadastroEnum;
import enums.LoginEnum;

public class ServidorModel {
	private ServerSocket serverSocket;
	private String tokenAdmin = "1234567";

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

			try (PrintWriter saida = new PrintWriter(cliente.getOutputStream(), true);
					BufferedReader entrada = new BufferedReader(new InputStreamReader(cliente.getInputStream()))) {
				listener.onConexao(cliente.getInetAddress().toString());
				System.out.println("Novo cliente: " + cliente.getInetAddress());

				String mensagemRecebida;
				while ((mensagemRecebida = entrada.readLine()) != null) {
					System.out.println("C -> S: " + mensagemRecebida);
					listener.onMensagemRecebida(mensagemRecebida);

					String operacao = jsonController.getOperacao(mensagemRecebida);
					processarOperacao(operacao, mensagemRecebida, saida, jsonController, loginController,
							cadastroController, listener);
				}
			} catch (IOException | ParseException | SQLException e) {
				listener.onErro("Erro na comunicação: " + e.getMessage());
				System.err.println("Erro na comunicação: " + e.getMessage());
			} finally {
				fecharConexaoCliente(cliente, listener);
			}
		}).start();
	}

	// Processa as operações recebidas do cliente
	private void processarOperacao(String operacao, String mensagemRecebida, PrintWriter saida,
			JSONController jsonController, LoginController loginController, CadastroController cadastroController,
			ServidorListener listener) throws IOException, ParseException, SQLException {
		switch (operacao) {
		case "login":
			processarLogin(mensagemRecebida, saida, jsonController, loginController, listener);
			break;
		case "cadastrarUsuario":
			processarCadastro(mensagemRecebida, saida, jsonController, cadastroController, listener);
			break;
		case "logout":
			processarLogout(mensagemRecebida, saida, jsonController, listener);
			break;
		case "listarUsuarios":
			processarListarUsuarios(mensagemRecebida, saida, jsonController, listener);
			break;
		case "excluirUsuario":
			processarExcluirUsuarios(mensagemRecebida, saida, jsonController, listener);
			break;
		case "editarUsuario":
			processarEditarUsuarios(mensagemRecebida, saida, jsonController, listener);
			break;
		case "localizarUsuario":
			processarLocalizarUsuarios(mensagemRecebida, saida, jsonController, listener);
			break;
		case "listarCategorias":
			processarListarCategorias(mensagemRecebida, saida, jsonController, listener);
			break;
		case "localizarCategoria":
			processarLocalizarCategoria(mensagemRecebida, saida, jsonController, listener);
			break;
		case "salvarCategoria":
			processarSalvarCategoria(mensagemRecebida, saida, jsonController, listener);
			break;
		case "excluirCategoria":
			processarExcluirCategoria(mensagemRecebida, saida, jsonController, listener);
			break;
		case "listarAvisos":
			processarListarAvisos(mensagemRecebida, saida, jsonController, listener);
			break;
		case "salvarAviso":
			processarSalvarAviso(mensagemRecebida, saida, jsonController, listener);
			break;
		case "localizarAviso":
			processarLocalizarAviso(mensagemRecebida, saida, jsonController, listener);
			break;
		case "excluirAviso":
			processarExcluirAviso(mensagemRecebida, saida, jsonController, listener);
			break;
		case "listarUsuarioCategorias":
			processarListarUsuarioCategorias(mensagemRecebida, saida, jsonController, listener);
			break;
		case "cadastrarUsuarioCategoria":
			processarCadastrarUsuarioCategoria(mensagemRecebida, saida, jsonController, listener);
			break;
		default:
			enviarErroOperacaoInvalida(operacao, saida, jsonController, listener);
		}
	}

	// Processa a operação de login
	private void processarLogin(String mensagemRecebida, PrintWriter saida, JSONController jsonController,
			LoginController loginController, ServidorListener listener) {

		UsuarioModel usuario = jsonController.changeLoginToJSON(mensagemRecebida);
		RespostaModel resposta = new RespostaModel();

		if (usuario == null || usuario.getRa() == null || usuario.getSenha() == null) {

			resposta.setStatus(401);
			resposta.setMsg("Dados invalidos.");
			resposta.setOperacao("login");

		} else {

			LoginEnum statusLogin = null;

			try {
				statusLogin = loginController.validarLogin(usuario);

			} catch (IOException e) {
				e.printStackTrace();
			}

			if (statusLogin == LoginEnum.SUCESSO) {

				try {
					System.out.println(usuario);
					String token = loginController.getRa(usuario.getRa());

					Connection conn = BancoDados.conectar();
					UsuarioDAO usuarioDAO = new UsuarioDAO(conn);
					usuarioDAO.atualizarStatusLogin(usuario.getRa(), true);

					resposta.setStatus(200);
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
				resposta.setStatus(401);
				resposta.setOperacao("login");
			}
		}
		enviarResposta(resposta, saida, jsonController, listener);
	}

	// Processa a operação de cadastro
	private void processarCadastro(String mensagemRecebida, PrintWriter saida, JSONController jsonController,
			CadastroController cadastroController, ServidorListener listener) throws SQLException {
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
				Connection conn = null;
				conn = BancoDados.conectar();
				new UsuarioDAO(conn).adicionarUsuario(usuario);
				resposta.setStatus(201);
				resposta.setMsg("Cadastro realizado com sucesso.");
				BancoDados.desconectar();
			} catch (SQLException e) {
				resposta.setStatus(401);
				resposta.setMsg("Erro ao acessar o banco de dados." + e.getMessage());
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				BancoDados.desconectar();
			}
			break;
		case RA_CADASTRADO:
			resposta.setStatus(401);
			resposta.setMsg("Nao foi cadastrar pois o USUARIO INFORMADO JA EXISTE.");
			break;
		default:
			resposta.setStatus(401);
			resposta.setMsg("Os campos recebidos nao sao validos.");
		}
		enviarResposta(resposta, saida, jsonController, listener);
	}

	// Processa a operação de logout
	private void processarLogout(String mensagemRecebida, PrintWriter saida, JSONController jsonController,
			ServidorListener listener) throws ParseException, IOException {

		RespostaModel resposta = new RespostaModel();

		String token = jsonController.extractToken(mensagemRecebida);
		Connection conn = null;

		try {
			// Conecta ao banco de dados
			conn = BancoDados.conectar();
			if (conn == null) {
				throw new SQLException("Falha ao conectar ao banco de dados.");
			}

			// Atualiza o status de login do usuário no banco de dados
			UsuarioDAO usuarioDAO = new UsuarioDAO(conn);
			usuarioDAO.atualizarStatusLogin(token, false); // Marca como deslogado

			resposta.setStatus(200); // Logout bem-sucedido
			resposta.setMsg("Logout realizado com sucesso.");
		} catch (SQLException e) {
			e.printStackTrace();
			resposta.setStatus(401); // Erro interno do servidor
			resposta.setMsg("Erro ao acessar o banco de dados: " + e.getMessage());
		} finally {
			if (conn != null) {
				try {
					BancoDados.desconectar(); // Fecha explicitamente a conexão usada
				} catch (SQLException e) {
					e.printStackTrace();
					System.err.println("Erro ao desconectar do banco de dados: " + e.getMessage());
				}
			}
		}

		enviarResposta(resposta, saida, jsonController, listener);
	}

	private void processarListarUsuarios(String mensagemRecebida, PrintWriter saida, JSONController jsonController,
			ServidorListener listener) throws IOException, ParseException {
		RespostaModel resposta = new RespostaModel();
		resposta.setOperacao("listarUsuarios");

		try {

			// Extrair o token da mensagem recebida usando o jsonController
			String token = jsonController.extractToken(mensagemRecebida);

			// Verificar se o token é de admin
			if (!isAdminToken(token)) {
				resposta.setStatus(401); // Status de acesso negado
				resposta.setMsg("Acesso negado: voce nao tem permissao para listar usuarios.");
			} else {
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
			}
		} catch (SQLException e) {
			resposta.setStatus(401); // Status de erro interno do servidor
			resposta.setMsg("Erro ao acessar o banco de dados: " + e.getMessage()); // Mensagem de erro
		} finally {
			// Enviar a resposta ao cliente
			enviarResposta(resposta, saida, jsonController, listener);
		}
	}

	private void processarExcluirUsuarios(String mensagemRecebida, PrintWriter saida, JSONController jsonController,
			ServidorListener listener) {
		UsuarioModel usuario = jsonController.changeRegisterJSON(mensagemRecebida);
		RespostaModel resposta = new RespostaModel();
		String token = usuario.getToken();

		if (usuario == null || usuario.getRa() == null) {
			resposta.setStatus(401);
			resposta.setMsg("Dados invalidos.");
		} else {
			try {
				Connection conn = BancoDados.conectar();
				UsuarioDAO usuarioDAO = new UsuarioDAO(conn);

				// Verifica se o RA existe no banco de dados
				String raExistente = usuarioDAO.getRA(usuario.getRa());
				if (raExistente == null) {
					resposta.setStatus(401);
					resposta.setMsg("Usuario nao encontrado no banco de dados.");
					enviarResposta(resposta, saida, jsonController, listener);
					return; // Sai do método se o usuário não for encontrado
				}

				// Verifica se o token é admin
				if (isAdminToken(token)) {
					// Permite a exclusão de qualquer usuário
					usuarioDAO.removerUsuario(usuario); // Chama o método para remover o usuário do banco
					resposta.setStatus(201);
					resposta.setOperacao("excluirUsuario");
					resposta.setMsg("Usuario removido com sucesso.");
				} else {
					// Permite a exclusão apenas se o RA for igual ao token
					if (usuario.getRa().equals(token)) {
						usuarioDAO.removerUsuario(usuario); // Chama o método para remover o usuário do banco
						resposta.setStatus(201);
						resposta.setOperacao("excluirUsuario");
						resposta.setMsg("Usuario removido com sucesso.");
					} else {
						// Caso contrário, recusa a permissão
						resposta.setStatus(401); // Permissão negada
						resposta.setMsg("Permissao recusada: voce nao tem autorizacao para excluir este usuario.");
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

	private void processarEditarUsuarios(String mensagemRecebida, PrintWriter saida, JSONController jsonController,
			ServidorListener listener) {

		System.out.println("Mensagem recebida editar: " + mensagemRecebida);
		UsuarioModel usuario = jsonController.changeJSONToUser(mensagemRecebida);
		RespostaModel resposta = new RespostaModel();

		resposta.setOperacao("editarUsuario");

		if (usuario == null || usuario.getRa() == null || usuario.getNome() == null || usuario.getSenha() == null
				|| usuario.getToken() == null) {

			resposta.setStatus(401);
			resposta.setMsg("Dados invalidos.");

		} else {

			try {

				Connection conn = BancoDados.conectar();
				UsuarioDAO usuarioDAO = new UsuarioDAO(conn);

				// Verifica se o RA existe no banco de dados
				String raExistente = usuarioDAO.getRA(usuario.getRa());

				if (raExistente == null) {
					resposta.setStatus(401);
					resposta.setMsg("Usuario nao encontrado no banco de dados.");

				} else {
					// Verifica se o token é igual ao RA ou se é um admin
					if (!usuario.getToken().equals(usuario.getRa()) && !isAdminToken(usuario.getToken())) {
						resposta.setStatus(403); // Proibido
						resposta.setMsg("Voce nao tem permissao para editar este usuario.");
					} else {
						// Atualiza as informações do usuário
						usuarioDAO.atualizarUsuario(usuario);
						resposta.setStatus(201);
						resposta.setMsg("Usuario atualizado com sucesso.");
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

	private void processarLocalizarUsuarios(String mensagemRecebida, PrintWriter saida, JSONController jsonController,
			ServidorListener listener) throws IOException, ParseException {
		// Cria um objeto RespostaModel para armazenar a resposta
		RespostaModel resposta = new RespostaModel();

		// Converte a mensagem recebida em um objeto UsuarioModel
		UsuarioModel usuario = jsonController.changeToObject(mensagemRecebida);

		// Verifica se o usuário é nulo ou se os dados necessários não estão presentes
		if (usuario == null || usuario.getToken() == null || usuario.getRa() == null) {
			resposta.setStatus(401);
			resposta.setOperacao("localizarUsuario");
			resposta.setMsg("Dados invalidos.");
			enviarResposta(resposta, saida, jsonController, listener);
			return;
		}

		// Verifica se o token é de um administrador
		if (!isAdminToken(usuario.getToken())) { // token de admin
			// Se não for admin, verifica se o RA solicitado é o mesmo do token
			if (!usuario.getRa().equals(usuario.getToken())) {
				resposta.setStatus(401);
				resposta.setOperacao("localizarUsuario");
				resposta.setMsg("Acesso nao autorizado.");
				enviarResposta(resposta, saida, jsonController, listener);
				return;
			}
		}

		// Se o token for válido, tenta localizar o usuário no banco de dados
		try {
			Connection conn = BancoDados.conectar();
			UsuarioDAO usuarioDAO = new UsuarioDAO(conn);

			// Busca o usuário pelo RA
			UsuarioModel usuarioEncontrado = usuarioDAO.getUsuarioPorRa(usuario.getRa());

			if (usuarioEncontrado != null) {
				// Se o usuário for encontrado, retorna os dados
				resposta.setStatus(201);
				resposta.setOperacao("localizarUsuario");
				resposta.setUsuario(usuarioEncontrado);
			} else {
				// Se o usuário não for encontrado
				resposta.setStatus(401);
				resposta.setOperacao("localizarUsuario");
				resposta.setMsg("Usuario nao encontrado.");
			}
		} catch (SQLException e) {
			resposta.setStatus(401);
			resposta.setOperacao("localizarUsuario");
			resposta.setMsg("Erro ao acessar o banco de dados: " + e.getMessage());
		} finally {
			try {
				BancoDados.desconectar(); // Certifique-se de desconectar após a operação
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		// Envia a resposta ao cliente
		enviarResposta(resposta, saida, jsonController, listener);
	}

	private void processarListarCategorias(String mensagemRecebida, PrintWriter saida, JSONController jsonController,
			ServidorListener listener) throws ParseException, IOException {
		RespostaModel resposta = new RespostaModel();
		resposta.setOperacao("listarCategorias");

		try {

			// Conectar ao banco de dados
			Connection conn = BancoDados.conectar();
			CategoriaDAO categoriaDAO = new CategoriaDAO(conn);

			// Recuperar a lista de categorias do banco de dados
			List<CategoriaModel> categorias = categoriaDAO.listarCategorias();

			// Criar o JSON de retorno conforme especificado
			if (categorias.isEmpty()) {
				resposta.setStatus(201);
				resposta.setCategorias(new ArrayList<>()); // Retorna um array vazio
			} else {
				resposta.setStatus(201);
				resposta.setCategorias(categorias); // Define a lista de categorias
			}

		} catch (SQLException e) {
			resposta.setStatus(500); // Status de erro interno do servidor
			resposta.setMsg("Erro ao acessar o banco de dados: " + e.getMessage());
		} finally {
			// Enviar a resposta ao cliente
			enviarResposta(resposta, saida, jsonController, listener);
		}
	}

	private void processarLocalizarCategoria(String mensagemRecebida, PrintWriter saida, JSONController jsonController,
			ServidorListener listener) throws IOException {
		// Cria um objeto RespostaModel para armazenar a resposta
		RespostaModel resposta = new RespostaModel();

		// Converte a mensagem recebida em um objeto CategoriaModel
		CategoriaModel categoria = jsonController.changeJSONToCategoria(mensagemRecebida);

		// Verifica se a categoria é nula ou se o ID não está presente
		if (categoria == null || categoria.getId() <= 0) {
			resposta.setStatus(401);
			resposta.setOperacao("localizarCategoria");
			resposta.setMsg("Dados invalidos.");
		} else {
			try {
				// Conectar ao banco de dados
				Connection conn = BancoDados.conectar();
				CategoriaDAO categoriaDAO = new CategoriaDAO(conn);

				// Busca a categoria pelo ID
				CategoriaModel categoriaEncontrada = categoriaDAO.getCategoriaPorId(categoria.getId());

				if (categoriaEncontrada != null) {
					// Se a categoria for encontrada, retorna os dados
					resposta.setStatus(201);
					resposta.setOperacao("localizarCategoria");
					resposta.setCategoria(categoriaEncontrada);
				} else {
					// Se a categoria não for encontrada
					resposta.setStatus(401);
					resposta.setOperacao("localizarCategoria");
					resposta.setMsg("Categoria nao encontrada.");
				}
			} catch (SQLException e) {
				resposta.setStatus(500);
				resposta.setOperacao("localizarCategoria");
				resposta.setMsg("Erro ao acessar o banco de dados: " + e.getMessage());
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

	private void processarSalvarCategoria(String mensagemRecebida, PrintWriter saida, JSONController jsonController,
			ServidorListener listener) throws ParseException {
		// Cria um objeto RespostaModel para armazenar a resposta
		RespostaModel resposta = new RespostaModel();

		// Converte a mensagem recebida em um objeto CategoriaModel
		CategoriaModel categoria = jsonController.changeJSONToCategoria(mensagemRecebida);

		// Verifica se a categoria é nula ou se o nome não está presente
		if (categoria == null || categoria.getNome() == null || categoria.getNome().isEmpty()) {
			resposta.setStatus(401);
			resposta.setOperacao("salvarCategoria");
			resposta.setMsg("Categoria nao encontrada.");
			enviarResposta(resposta, saida, jsonController, listener);
			return;
		}

		// Verifica se o token é válido (apenas admin pode salvar categorias)
		String token = jsonController.extractToken(mensagemRecebida);
		if (!isAdminToken(token)) {
			resposta.setStatus(401);
			resposta.setOperacao("salvarCategoria");
			resposta.setMsg("Acesso negado: voce não tem permissao para salvar categorias.");
			enviarResposta(resposta, saida, jsonController, listener);
			return;
		}

		try {
			Connection conn = BancoDados.conectar();
			CategoriaDAO categoriaDAO = new CategoriaDAO(conn);

			if (categoriaDAO.getCategoriaPorNome(categoria.getNome()) != null) {
				resposta.setStatus(401);
				resposta.setMsg("Nome da categoria ja existe.");
			} else {
				// Se o ID da categoria for 0, realiza um INSERT
				if (categoria.getId() == 0) {
					categoriaDAO.adicionarCategoria(categoria);
					resposta.setStatus(201);
					resposta.setOperacao("salvarCategoria");
					resposta.setMsg("Categoria salva com sucesso.");
				} else {
					// Caso contrário, realiza um UPDATE
					CategoriaModel categoriaExistente = categoriaDAO.getCategoriaPorId(categoria.getId());
					if (categoriaExistente != null) {
						categoriaDAO.atualizarCategoria(categoria);
						resposta.setStatus(201);
						resposta.setOperacao("salvarCategoria");
						resposta.setMsg("Categoria atualizada com sucesso.");
					} else {
						resposta.setStatus(401);
						resposta.setOperacao("salvarCategoria");
						resposta.setMsg("Categoria nao encontrada.");
					}
				}
			}
		} catch (SQLException e) {
			resposta.setStatus(401);
			resposta.setOperacao("salvarCategoria");
			resposta.setMsg("Erro ao acessar o banco de dados: " + e.getMessage());
		} catch (IOException e) {
			resposta.setStatus(401);
			resposta.setOperacao("salvarCategoria");
			resposta.setMsg("Erro de I/O: " + e.getMessage());
		} finally {
			try {
				BancoDados.desconectar(); // Certifique-se de desconectar após a operação
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		// Envia a resposta ao cliente
		enviarResposta(resposta, saida, jsonController, listener);
	}

	// Método auxiliar para verificar se o token é de um administrador
	private boolean isAdminToken(String token) {
		// Verifica se o token corresponde ao token de admin
		return tokenAdmin.equals(token); // Token de admin definido
	}

	private void processarExcluirCategoria(String mensagemRecebida, PrintWriter saida, JSONController jsonController,
			ServidorListener listener) {
		// Cria um objeto RespostaModel para armazenar a resposta
		RespostaModel resposta = new RespostaModel();

		// Converte a mensagem recebida em um objeto CategoriaModel
		CategoriaModel categoria = jsonController.changeJSONToCategoria(mensagemRecebida);

		// Verifica se a categoria é nula ou se o ID não está presente
		if (categoria == null || categoria.getId() <= 0) {
			resposta.setStatus(401);
			resposta.setOperacao("excluirCategoria");
			resposta.setMsg("Dados invalidos.");
		} else {
			try {
				// Conectar ao banco de dados
				Connection conn = BancoDados.conectar();
				CategoriaDAO categoriaDAO = new CategoriaDAO(conn);

				// Verifica se a categoria existe no banco de dados
				CategoriaModel categoriaExistente = categoriaDAO.getCategoriaPorId(categoria.getId());
				if (categoriaExistente == null) {
					resposta.setStatus(401);
					resposta.setOperacao("excluirCategoria");
					resposta.setMsg("Categoria nao encontrada.");
				} else {
					// Tenta remover a categoria do banco de dados
					try {
						categoriaDAO.removerCategoria(categoria.getId());
						resposta.setStatus(201);
						resposta.setOperacao("excluirCategoria");
						resposta.setMsg("Exclusao realizada com sucesso.");
					} catch (SQLException e) {
						// Se a exclusão falhar devido a restrições de chave estrangeira
						resposta.setStatus(401);
						resposta.setOperacao("excluirCategoria");
						resposta.setMsg("Nao foi possivel excluir, a categoria ja esta alocada a um ou mais avisos.");
					}
				}
			} catch (SQLException e) {
				resposta.setStatus(500);
				resposta.setOperacao("excluirCategoria");
				resposta.setMsg("Erro ao acessar o banco de dados: " + e.getMessage());
			} catch (IOException e) {
				resposta.setStatus(500);
				resposta.setOperacao("excluirCategoria");
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
	
	private void processarListarAvisos(String mensagemRecebida, PrintWriter saida, JSONController jsonController, ServidorListener listener) {
	    RespostaModel resposta = new RespostaModel();
	    resposta.setOperacao("listarAvisos");
	    Connection conn = null;
	    
	    try {
	        RespostaModel respRecebida = jsonController.changeResponseToJson(mensagemRecebida);

	        if (respRecebida == null || respRecebida.getToken() == null) {
	            resposta.setStatus(401);
	            resposta.setMsg("Dados inválidos.");
	            return;
	        }

	        conn = BancoDados.conectar();
	        AvisoDAO avisoDAO = new AvisoDAO(conn);
	        List<AvisoModel> avisos;

	        if (respRecebida.getIdCategoria() == 0) {
	            avisos = avisoDAO.listarAvisos();
	        } else {
	            CategoriaModel categoria = avisoDAO.getCategoriaPorId(respRecebida.getIdCategoria());
	            if (categoria == null) {
	                resposta.setStatus(401);
	                resposta.setMsg("Categoria não encontrada.");
	                return;
	            }
	            avisos = avisoDAO.listarAvisosPorCategoria(respRecebida.getIdCategoria());
	        }

	        if (avisos.isEmpty()) {
	            resposta.setStatus(401);
	            resposta.setMsg("Nenhum aviso encontrado.");
	        } else {
	            resposta.setStatus(201);
	            resposta.setAvisos(avisos);
	        }
	    } catch (SQLException e) {
	        resposta.setStatus(401);
	        resposta.setMsg("Erro ao acessar o banco de dados: " + e.getMessage());
	    } catch (Exception e) {
	        resposta.setStatus(401);
	        resposta.setMsg("Erro inesperado: " + e.getMessage());
	    } finally {
	        try {
				BancoDados.desconectar();
			} catch (SQLException e) {
				e.printStackTrace();
			}
	        enviarResposta(resposta, saida, jsonController, listener);
	    }
	}
	
	private void processarSalvarAviso(String mensagemRecebida, PrintWriter saida, JSONController jsonController, ServidorListener listener) {
	    RespostaModel resposta = new RespostaModel();
	    resposta.setOperacao("salvarAviso"); // Define a operação

	    // Converte a mensagem recebida em um objeto RespostaModel
	    RespostaModel respRecebida = jsonController.changeResponseToJson(mensagemRecebida);
	    System.out.println(respRecebida);
	    
	    // Verifica se a resposta recebida é válida
	    if (respRecebida == null || respRecebida.getToken() == null || respRecebida.getAviso() == null) {
	        resposta.setStatus(401);
	        resposta.setMsg("Os campos recebidos não são válidos.");
	        enviarResposta(resposta, saida, jsonController, listener);
	        return;
	    }

	    // Verifica se o token é de um administrador
	    if (!isAdminToken(respRecebida.getToken())) {
	        resposta.setStatus(401);
	        resposta.setMsg("Acesso negado: você não tem permissão para salvar avisos.");
	        enviarResposta(resposta, saida, jsonController, listener);
	        return;
	    }

	    AvisoModel aviso = respRecebida.getAviso();
	    
	    // Valida os campos do aviso
	    if (aviso.getTitulo() == null || aviso.getTitulo().isEmpty() || aviso.getDescricao() == null || aviso.getDescricao().isEmpty()) {
	        resposta.setStatus(401);
	        resposta.setMsg("Os campos recebidos não são válidos.");
	        enviarResposta(resposta, saida, jsonController, listener);
	        return;
	    }

	    // Verifica se a categoria do aviso é válida
	    if (aviso.getCategoria() == null || aviso.getCategoria().getId() <= 0) {
	        resposta.setStatus(401);
	        resposta.setMsg("Categoria não encontrada.");
	        enviarResposta(resposta, saida, jsonController, listener);
	        return;
	    }

	    try {
	        Connection conn = BancoDados.conectar();
	        AvisoDAO avisoDAO = new AvisoDAO(conn);

	        // Se o ID do aviso for 0, realiza um INSERT
	        if (aviso.getId() == 0) {
	            avisoDAO.adicionarAviso(aviso);
	            resposta.setStatus(201);
	            resposta.setMsg("Aviso salvo com sucesso.");
	        } else {
	            // Caso contrário, realiza um UPDATE
	            AvisoModel avisoExistente = avisoDAO.getAvisoPorId(aviso.getId());
	            if (avisoExistente == null) {
	                resposta.setStatus(401);
	                resposta.setMsg("Aviso não encontrado.");
	            } else {
	                avisoDAO.atualizarAviso(aviso);
	                resposta.setStatus(201);
	                resposta.setMsg("Aviso atualizado com sucesso.");
	            }
	        }
	    } catch (SQLException e) {
	        resposta.setStatus(401);
	        resposta.setMsg("Erro ao acessar o banco de dados: " + e.getMessage());
	    } catch (Exception e) {
	        resposta.setStatus(401);
	        resposta.setMsg("Erro inesperado: " + e.getMessage());
	    } finally {
	        try {
	            BancoDados.desconectar(); // Certifique-se de desconectar após a operação
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }

	    // Envia a resposta ao cliente
	    enviarResposta(resposta, saida, jsonController, listener);
	}

	private void processarLocalizarAviso(String mensagemRecebida, PrintWriter saida, JSONController jsonController,
	        ServidorListener listener) throws IOException {
	    // Converte a mensagem recebida em um objeto RespostaModel
	    RespostaModel resposta = jsonController.changeResponseToJson(mensagemRecebida);
	    
	    // Cria um novo objeto RespostaModel para a resposta
	    RespostaModel respostaEnvio = new RespostaModel();
	    respostaEnvio.setOperacao("localizarAviso"); // Define a operação

	    // Verifica se a resposta recebida é válida
	    if (resposta == null || resposta.getToken() == null || resposta.getIdCategoria() <= 0) {
	        respostaEnvio.setStatus(401); // Status de erro
	        respostaEnvio.setMsg("Dados inválidos."); // Mensagem de erro
	    } else {
	        // Conecta ao banco de dados
	        try (Connection conn = BancoDados.conectar()) {
	            AvisoDAO avisoDAO = new AvisoDAO(conn);
	            AvisoModel aviso = avisoDAO.getAvisoPorId(resposta.getIdCategoria()); // Busca o aviso pelo ID

	            if (aviso != null) {
	                // Se o aviso for encontrado, define os dados na resposta
	                respostaEnvio.setStatus(201); // Status de sucesso
	                respostaEnvio.setAviso(aviso); // Adiciona o aviso à resposta
	            } else {
	                // Se o aviso não for encontrado
	                respostaEnvio.setStatus(401); // Status de erro
	                respostaEnvio.setMsg("Aviso não encontrado."); // Mensagem de erro
	            }
	        } catch (SQLException e) {
	            respostaEnvio.setStatus(401); // Status de erro
	            respostaEnvio.setMsg("Erro ao acessar o banco de dados: " + e.getMessage()); // Mensagem de erro
	        }
	    }

	    // Envia a resposta ao cliente
	    enviarResposta(respostaEnvio, saida, jsonController, listener);
	}
	
	private void processarExcluirAviso(String mensagemRecebida, PrintWriter saida, JSONController jsonController,
	        ServidorListener listener) throws IOException {
	    // Converte a mensagem recebida em um objeto RespostaModel
	    RespostaModel resposta = jsonController.changeResponseToJson(mensagemRecebida);
	    
	    // Cria um novo objeto RespostaModel para a resposta
	    RespostaModel respostaEnvio = new RespostaModel();
	    respostaEnvio.setOperacao("excluirAviso"); // Define a operação

	    // Verifica se a resposta recebida é válida
	    if (resposta == null || resposta.getToken() == null || resposta.getIdCategoria() <= 0) {
	        respostaEnvio.setStatus(401); // Status de erro
	        respostaEnvio.setMsg("Dados inválidos."); // Mensagem de erro
	    } else {
	        // Conecta ao banco de dados
	        try (Connection conn = BancoDados.conectar()) {
	            AvisoDAO avisoDAO = new AvisoDAO(conn);
	            AvisoModel aviso = avisoDAO.getAvisoPorId(resposta.getIdCategoria()); // Busca o aviso pelo ID

	            if (aviso != null) {
	                // Se o aviso for encontrado, tenta removê-lo
	                avisoDAO.excluirAviso(aviso.getId());
	                respostaEnvio.setStatus(201); // Status de sucesso
	                respostaEnvio.setMsg("Exclusão realizada com sucesso."); // Mensagem de sucesso
	            } else {
	                // Se o aviso não for encontrado
	                respostaEnvio.setStatus(401); // Status de erro
	                respostaEnvio.setMsg("Aviso não encontrado."); // Mensagem de erro
	            }
	        } catch (SQLException e) {
	            respostaEnvio.setStatus(401); // Status de erro
	            respostaEnvio.setMsg("Erro ao acessar o banco de dados: " + e.getMessage()); // Mensagem de erro
	        } finally {
				try {
					BancoDados.desconectar();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
	    }

	    // Envia a resposta ao cliente
	    enviarResposta(respostaEnvio, saida, jsonController, listener);
	}
	
	private void processarListarUsuarioCategorias(String mensagemRecebida, PrintWriter saida, JSONController jsonController,
	        ServidorListener listener) throws IOException {
	    // Converte a mensagem recebida em um objeto RespostaModel
	    RespostaModel resposta = jsonController.changeResponseToJson(mensagemRecebida);

	    // Cria um novo objeto RespostaModel para a resposta
	    RespostaModel respostaEnvio = new RespostaModel();
	    respostaEnvio.setOperacao("listarUsuarioCategorias"); // Define a operação

	    // Verifica se a resposta recebida é válida
	    if (resposta == null || resposta.getToken() == null || resposta.getRa() == null || resposta.getRa().isEmpty()) {
	        respostaEnvio.setStatus(401); // Status de erro
	        respostaEnvio.setMsg("Dados inválidos."); // Mensagem de erro
	    } else {
	        // Conecta ao banco de dados
	        try (Connection conn = BancoDados.conectar()) {
	            InscricaoDAO inscricaoDAO = new InscricaoDAO(conn);
	            List<Integer> categorias = inscricaoDAO.buscarCategoriasPorRA(resposta.getRa()); // Busca as categorias pelo RA

	            if (categorias == null) {
	                // Se o usuário não for encontrado
	                respostaEnvio.setStatus(401); // Status de erro
	                respostaEnvio.setMsg("Usuário não encontrado."); // Mensagem de erro
	                respostaEnvio.setIdsCategorias(new ArrayList<>()); // Retorna categorias como um array vazio
	            } else if (categorias.isEmpty()) {
	                // Se o usuário não tiver categorias cadastradas
	                respostaEnvio.setStatus(201); // Status de sucesso
	                respostaEnvio.setIdsCategorias(new ArrayList<>()); // Retorna categorias como um array vazio
	            } else {
	                // Se as categorias forem encontradas
	                respostaEnvio.setStatus(201); // Status de sucesso
	                respostaEnvio.setIdsCategorias(categorias); // Define as categorias na resposta
	            }
	        } catch (SQLException e) {
	            respostaEnvio.setStatus(401); // Status de erro
	            respostaEnvio.setMsg("Erro ao acessar o banco de dados: " + e.getMessage()); // Mensagem de erro
	        } finally {
	            try {
	                BancoDados.desconectar();
	            } catch (SQLException e) {
	                e.printStackTrace();
	            }
	        }
	    }

	    // Envia a resposta ao cliente
	    enviarResposta(respostaEnvio, saida, jsonController, listener);
	}
	
	private void processarCadastrarUsuarioCategoria(String mensagemRecebida, PrintWriter saida, JSONController jsonController,
	        ServidorListener listener) throws IOException {
	    
	    // Converte a mensagem recebida em um objeto RespostaModel
	    RespostaModel resposta = jsonController.changeResponseToJson(mensagemRecebida);
	    
	    // Cria um novo objeto RespostaModel para a resposta
	    RespostaModel respostaEnvio = new RespostaModel();
	    respostaEnvio.setOperacao("cadastrarUsuarioCategoria"); // Define a operação

	    // Verifica se a resposta recebida é válida
	    if (resposta == null || resposta.getToken() == null || resposta.getRa() == null || resposta.getCategoria() == null) {
	        respostaEnvio.setStatus(401); // Status de erro
	        respostaEnvio.setMsg("Dados inválidos."); // Mensagem de erro
	    } else {
	        // Conecta ao banco de dados
	        try (Connection conn = BancoDados.conectar()) {
	            UsuarioDAO usuarioDAO = new UsuarioDAO(conn);
	            
	            // Autentica o usuário
	            UsuarioModel usuario = usuarioDAO.getUsuarioPorRa(resposta.getToken());
	            if (usuario == null) {
	                respostaEnvio.setStatus(401); // Status de erro
	                respostaEnvio.setMsg("Usuário não encontrado."); // Mensagem de erro
	                return;
	            }
	            
	            CategoriaDAO categoriaDAO = new CategoriaDAO(conn);
	            // Verifica se a categoria existe
	            CategoriaModel categoria = categoriaDAO.getCategoriaPorId(resposta.getCategoria().getId());
	            
	            if (categoria == null) {
	                respostaEnvio.setStatus(401); // Status de erro
	                respostaEnvio.setMsg("Categoria não encontrada."); // Mensagem de erro
	                return;
	            }

	            // Verifica se o usuário é um administrador ou se está cadastrando a si mesmo
	            if (!isAdminToken(usuario.getToken()) && !usuario.getRa().equals(resposta.getRa())) {
	                respostaEnvio.setStatus(403); // Status de erro
	                respostaEnvio.setMsg("Você não tem permissão para cadastrar outro usuário."); // Mensagem de erro
	                return;
	            }
	            
	            Inscricao inscricao = new Inscricao();
	            inscricao.setRaUsuario(usuario.getToken());
	            inscricao.setIdCategoria(categoria.getId());
	            
	            InscricaoDAO inscricaoDAO = new InscricaoDAO(conn);

	            // Cadastra o usuário na categoria
	            int sucesso = inscricaoDAO.cadastrar(inscricao);
	            if (sucesso > 0) {
	                respostaEnvio.setStatus(201); // Status de sucesso
	                respostaEnvio.setMsg("Cadastro em categoria realizado com sucesso."); // Mensagem de sucesso
	            } else {
	                respostaEnvio.setStatus(500); // Status de erro
	                respostaEnvio.setMsg("Erro ao cadastrar usuário na categoria."); // Mensagem de erro
	            }
	        } catch (SQLException e) {
	            respostaEnvio.setStatus(500); // Status de erro
	            respostaEnvio.setMsg("Erro ao acessar o banco de dados: " + e.getMessage()); // Mensagem de erro
	        } finally {
	            try {
	                BancoDados.desconectar();
	            } catch (SQLException e) {
	                e.printStackTrace();
	            }
	        }
	    }

	    // Envia a resposta ao cliente
	    enviarResposta(respostaEnvio, saida, jsonController, listener);
	}

	// Envia mensagem de erro para operação inválida
	private void enviarErroOperacaoInvalida(String operacao, PrintWriter saida, JSONController jsonController,
			ServidorListener listener) {
		RespostaModel resposta = new RespostaModel();
		resposta.setStatus(401);
		resposta.setMsg("ERRO: " + operacao);
		enviarResposta(resposta, saida, jsonController, listener);
	}

	// Envia a resposta ao cliente
	private void enviarResposta(RespostaModel resposta, PrintWriter saida, JSONController jsonController,
			ServidorListener listener) {
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
			return "Nao foi possivel ler o json recebido.";
		case ERRO_VALIDACAO:
			return "Dados invalidos.";
		case ERRO_BANCO:
			return "Erro ao acessar o banco de dados.";
		case ERRO_USUARIO_LOGADO:
			return "O usuario ja está logado.";
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

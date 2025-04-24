package model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JOptionPane;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import controller.JSONController;
import view.AdminMainView;
import view.AvisosInscritosView;
import view.AvisosView;
import view.CadastroView;
import view.GerenciadorAvisosView;
import view.GerenciadorCategoriasView;
import view.GerenciadorUsuariosView;
import view.InscricaoCategoriasView;
import view.LoginView;
import view.MainView;
import view.UsuarioInfoView;

public class ClienteModel {

	private Socket socketEcho;
	private PrintWriter saida;
	private BufferedReader entrada;
	private Thread threadEscuta;
	private JSONController jsonController;
	private String token;
	private AvisosView avisosView;
	private LoginView loginView;
	private CadastroView cadastroView;
	private MainView telaPrincipal;
	private AdminMainView telaAdmin;
	private GerenciadorUsuariosView telaGerenciadorUsuario;
	private GerenciadorCategoriasView gerenciadorCategoria;
	private String tokenAdmin = "2099284";
	private UsuarioInfoView telaUsuarioInfo;
	private GerenciadorAvisosView gerenciadorAviso;
	private InscricaoCategoriasView inscricaoCategoriasView;
	private List<CategoriaModel> categoriasCadastradas = new ArrayList<CategoriaModel>();

	// Método para conectar ao servidor
	public synchronized void conectar(String ip, int porta) throws IOException {
		socketEcho = new Socket(ip, porta);
		saida = new PrintWriter(socketEcho.getOutputStream(), true);
		entrada = new BufferedReader(new InputStreamReader(socketEcho.getInputStream()));
		jsonController = new JSONController();

		threadEscuta = new Thread(() -> {
			System.out.println("Escutando mensagens do servidor...");
			try {
				String msg;
				while ((msg = entrada.readLine()) != null) {
					System.out.println("SERVIDOR -> CLIENTE: " + msg);
					RespostaModel resposta = jsonController.changeResponseToJson(msg);

					if (resposta == null) {
						System.out.println("Resposta nula recebida do servidor.");
						continue;
					}

					int status = resposta.getStatus();
					String mensagem = resposta.getMsg();
					String token = resposta.getToken();
					String operacao = resposta.getOperacao();
					List<UsuarioModel> usuarios = resposta.getUsuarios();
					UsuarioModel usuario = resposta.getUsuario();
					List<CategoriaModel> categorias = resposta.getCategorias();
					CategoriaModel categoria = resposta.getCategoria();
					List<AvisoModel> avisos = resposta.getAvisos();

					if (operacao == null) {
						tratarOperacaoNula(status, token, mensagem);
					} else {
						tratarOperacao(operacao, status, mensagem, token, usuarios, usuario, categorias, categoria,
								avisos);
					}
				}
			} catch (IOException e) {
				tratarIOException(e);
			}
		});
		threadEscuta.start();
	}

	// Lida com operações nulas
	private void tratarOperacaoNula(int status, String token, String mensagem) {
		if (status == 200 && token != null) {
			JOptionPane.showMessageDialog(null, "Login realizado com sucesso!");
			tratarLogin(status, mensagem, token);
		} else if (status == 200) {
			JOptionPane.showMessageDialog(null, "Logout realizado com sucesso!");
			new LoginView(ClienteModel.this).setVisible(true);
		} else {
			JOptionPane.showMessageDialog(null, mensagem);
		}
	}

	// Lida com as operações recebidas do servidor
	private void tratarOperacao(String operacao, int status, String mensagem, String token, List<UsuarioModel> usuarios,
			UsuarioModel usuario, List<CategoriaModel> categorias, CategoriaModel categoria, List<AvisoModel> avisos) {
		// Verifica se a operação é válida
		if (operacao.isEmpty()) {
			JOptionPane.showMessageDialog(null, "Operação inválida recebida.", "Erro", JOptionPane.ERROR_MESSAGE);
			return; // Sai do método se a operação for nula ou vazia
		}
		switch (operacao) {
		case "login":
			tratarLogin(status, mensagem, token);
			break;
		case "cadastrarUsuario":
			tratarCadastro(status, mensagem);
			break;
		case "logout":
			tratarLogout(status, mensagem, token);
			break;
		case "listarUsuarios":
			tratarListaUsuario(usuarios);
			break;
		case "excluirUsuario":
			tratarExcluirUsuario(status, mensagem);
			break;
		case "editarUsuario":
			tratarEditarUsuario(status, mensagem);
			break;
		case "localizarUsuario":
			tratarLocalizarUsuario(status, mensagem, usuario);
			break;
		case "listarCategorias":
			tratarListarCategorias(status, mensagem, categorias);
			break;
		case "localizarCategoria":
			tratarLocalizarCategoria(status, mensagem, categoria);
			break;
		case "salvarCategoria":
			tratarSalvarCategoria(status, mensagem);
			break;
		case "excluirCategoria":
			tratarExcluirCategoria(status, mensagem, categoria);
			break;
		case "listarAvisos":
			tratarListarAvisos(status, mensagem, avisos);
			break;
		case "salvarAviso":
			tratarSalvarAviso(status, mensagem);
			break;
		case "excluirAviso":
			tratarExcluirAviso(status, mensagem);
			break;
		case "listarUsuarioCategorias":
			tratarListarUsuarioCategorias(status, mensagem, categorias);
			break;
		case "cadastrarUsuarioCategoria":
			tratarCadastrarUsuarioCategoria(status, mensagem);
			break;
		case "descadastrarUsuarioCategoria":
			tratarDescadastrarUsuarioCategoria(status, mensagem);
			break;
		case "listarUsuarioAvisos":
			tratarListarUsuarioAvisos(status, mensagem, avisos);
			break;
		default:
			JOptionPane.showMessageDialog(null, mensagem + ": " + operacao);
			System.out.println("Operação inválida ou não reconhecida.");
			break;
		}
	}

	// Lida com login
	private void tratarLogin(int status, String mensagem, String token) {

		if (status == 200) {

			this.token = token;

			// Verifica se o token é nulo
			if (token == null) {
				JOptionPane.showMessageDialog(loginView, "Token não recebido ou nulo.", "Erro",
						JOptionPane.ERROR_MESSAGE);
				return; // Sai do método se o token for nulo
			}

			if (isAdminToken(token)) {
				loginView.dispose();
				AdminMainView telaAdimin = new AdminMainView(ClienteModel.this, token);
				telaAdimin.setVisible(true);
				this.telaAdmin = telaAdimin.getEssaTela();
			} else {
				abrirTelaPrincipal(token);
				UsuarioModel usuario = new UsuarioModel();
		        usuario.setOperacao("listarUsuarioAvisos");
		        usuario.setToken(token); // Usando o token do cliente
		        usuario.setRa(token); // Usando o token do cliente

		        // Converte o usuário para JSON
		        JSONController jsonController = new JSONController();
		        JSONObject res = jsonController.changeToJSON(usuario);

		        // Envia a requisição ao servidor
		        enviarMensagem(res);
			}
		} else {
			tratarErro(loginView, mensagem, "Erro de Login");
		}
	}

	// Lida com cadastro
	private void tratarCadastro(int status, String mensagem) {
		if (telaGerenciadorUsuario == null) {

			if (status == 201 || status == 200) {
				JOptionPane.showMessageDialog(null, "Cadastro realizado com sucesso!", "Sucesso",
						JOptionPane.INFORMATION_MESSAGE);
				fecharTelaCadastro();
				new LoginView(this).setVisible(true);
			} else if (status == 404 || status == 422 || status == 401) {
				tratarErro(null, mensagem, "Erro de Cadastro");
			}
		} else {
			if (status == 201 || status == 200) {
				JOptionPane.showMessageDialog(null, "Cadastro realizado com sucesso!", "Sucesso",
						JOptionPane.INFORMATION_MESSAGE);
				telaGerenciadorUsuario.adicionarUsuario();
			} else if (status == 404 || status == 422 || status == 401) {
				tratarErro(null, mensagem, "Erro de Cadastro");
			}
		}
	}

	// Lida com logout
	private void tratarLogout(int status, String mensagem, String token) {
		if (status == 200) {

			// Dispose das views e as define como null se não forem nulas
			if (avisosView != null) {
				avisosView.dispose();
				avisosView = null;
			}
			if (telaPrincipal != null) {
				telaPrincipal.dispose();
				telaPrincipal = null;
			}
			if (loginView != null) {
				loginView.dispose();
				loginView = null;
			}
			if (cadastroView != null) {
				cadastroView.dispose();
				cadastroView = null;
			}
			if (telaAdmin != null) {
				telaAdmin.dispose();
				telaAdmin = null;
			}
			if (telaGerenciadorUsuario != null) {
				telaGerenciadorUsuario.dispose();
				telaGerenciadorUsuario = null;
			}
			if (gerenciadorCategoria != null) {
				gerenciadorCategoria.dispose();
				gerenciadorCategoria = null;
			}

			JOptionPane.showMessageDialog(null, "Logout realizado com sucesso!");
			new LoginView(this).setVisible(true);
		} else {
			tratarErro(loginView, mensagem, "Erro de Logout");
		}
	}

	private void tratarListaUsuario(List<UsuarioModel> usuarios) {

		if (telaAdmin.getToken() != null) {

			String token = telaAdmin.getToken();
			GerenciadorUsuariosView gerenciadorUsuario = new GerenciadorUsuariosView(this, token);
			gerenciadorUsuario.setTelaAdmin(telaAdmin);
			telaGerenciadorUsuario = gerenciadorUsuario;
			gerenciadorUsuario.atualizarTabelaUsuarios(usuarios);
			gerenciadorUsuario.setVisible(true);
		} else {
			System.out.println("Sem tela de Admin");
		}
	}

	private void tratarLocalizarUsuario(int status, String mensagem, UsuarioModel usuario) {
		try {
			// Verifica se o usuário é nulo
			if (usuario == null) {
				telaPrincipal.setVisible(true);
				throw new IllegalArgumentException(mensagem);
			}

			String ra = usuario.getRa();

			// Verifica se o token é nulo ou vazio
			if (ra == null || ra.isEmpty()) {
				throw new IllegalArgumentException("RA do usuário não pode ser nulo ou vazio.");
			}

			UsuarioInfoView telaUsuarioInfo = new UsuarioInfoView(this, ra);
			this.telaUsuarioInfo = telaUsuarioInfo;
			telaUsuarioInfo.setUsuarioInfo(usuario);
			telaUsuarioInfo.setTelaPrincipal(telaPrincipal);
			telaUsuarioInfo.setVisible(true);

		} catch (IllegalArgumentException e) {
			// Tratamento de exceções de argumento inválido
			JOptionPane.showMessageDialog(null, "Erro: " + e.getMessage(), "Erro de Argumento",
					JOptionPane.ERROR_MESSAGE);
		} catch (NullPointerException e) {
			// Tratamento de exceções de ponteiro nulo
			JOptionPane.showMessageDialog(null, "Erro: Um objeto necessário não foi inicializado.",
					"Erro de Ponteiro Nulo", JOptionPane.ERROR_MESSAGE);
		} catch (Exception e) {
			// Tratamento de outras exceções não previstas
			JOptionPane.showMessageDialog(null, "Erro inesperado: " + e.getMessage(), "Erro Inesperado",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void tratarExcluirUsuario(int status, String mensagem) {

		if (telaGerenciadorUsuario != null) {

			if (status == 201) {

				telaGerenciadorUsuario.removerUsuario();
				telaGerenciadorUsuario.mostrarMsg(mensagem);
			} else {

				telaGerenciadorUsuario.mostrarMsg(mensagem);
			}
		} else if (telaUsuarioInfo != null) {
			if (status == 201) {
				// Exibe uma mensagem de sucesso na tela de informações do usuário
				JOptionPane.showMessageDialog(telaUsuarioInfo, mensagem, "Sucesso", JOptionPane.INFORMATION_MESSAGE);
				telaUsuarioInfo.dispose();
				LoginView loginView = new LoginView(this);
				loginView.setVisible(true);
			} else {
				// Exibe uma mensagem de erro na tela de informações do usuário
				JOptionPane.showMessageDialog(telaUsuarioInfo, mensagem, "Erro", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private void tratarEditarUsuario(int status, String mensagem) {

		if (telaGerenciadorUsuario != null || telaPrincipal != null) {

			// Se for admin, usa o Gerenciador de Usuários
			if (telaGerenciadorUsuario != null) {

				if (status == 201) {

					telaGerenciadorUsuario.editarUsuario();
					telaGerenciadorUsuario.mostrarMsg(mensagem);
				} else {
					JOptionPane.showMessageDialog(null, mensagem, "Aviso", JOptionPane.WARNING_MESSAGE);
				}
			}

			if (telaPrincipal != null) {

				if (status == 201) {
					JOptionPane.showMessageDialog(null, mensagem, "Aviso", JOptionPane.INFORMATION_MESSAGE);

				} else {
					JOptionPane.showMessageDialog(null, mensagem, "Aviso", JOptionPane.WARNING_MESSAGE);
				}
			}

		} else {

			JOptionPane.showMessageDialog(null,
					"Atenção: Nenhuma tela para exibição foi criada anteriormente. Não é possível exibir a mensagem corretamente.",
					"Aviso", JOptionPane.WARNING_MESSAGE);
		}
	}

	private void tratarListarCategorias(int status, String mensagem, List<CategoriaModel> categorias) {

		if (telaAdmin != null) {

			if (status == 201) {
				
				this.categoriasCadastradas = categorias;
				
				if (gerenciadorCategoria == null) {
					
					String token = telaAdmin.getToken();
					telaAdmin.setVisible(false);
					GerenciadorCategoriasView gerenciadorCategoria = new GerenciadorCategoriasView(token, this, categorias);
					this.gerenciadorCategoria = gerenciadorCategoria;
					gerenciadorCategoria.setTelaAdmin(telaAdmin);
					gerenciadorCategoria.setVisible(true);
				} else {
					gerenciadorCategoria.atualizarTabelaCategorias(categorias);
				}
			} else {
				telaAdmin.setVisible(true);
			}
		} else if (avisosView != null) {

			if (status == 201) {
				
				avisosView.atualizarCategorias(categorias);
			}
		}
	}

	private void tratarLocalizarCategoria(int status, String mensagem, CategoriaModel categoria) {

		if (gerenciadorCategoria != null) {

			gerenciadorCategoria.excluirCategoria(categoria);
		} else {

			System.out.println("Sem gerenciador categoria!");
		}

	}

	private void tratarSalvarCategoria(int status, String mensagem) {

		if (status == 201) {

			// Se a categoria foi salva com sucesso
			if (this.gerenciadorCategoria != null) {

				// Solicita a atualização da lista de categorias
				gerenciadorCategoria.pedirAtualizacaoCategorias();

				// Utiliza a mensagem recebida para informar o sucesso
				JOptionPane.showMessageDialog(null, mensagem, "Sucesso", JOptionPane.INFORMATION_MESSAGE);
			}
		} else if (status == 401) {

			// Se houve um erro de validação ou autorização
			JOptionPane.showMessageDialog(null, mensagem, "Erro de Validação", JOptionPane.ERROR_MESSAGE);
		}

	}

	private void tratarExcluirCategoria(int status, String mensagem, CategoriaModel categoria) {
		// Verifica se o status indica sucesso (204 - No Content, por exemplo, para
		// exclusão bem-sucedida)
		if (status == 201) {
			// Verifica se o gerenciador de categorias não é nulo
			if (this.gerenciadorCategoria != null) {
				// Atualiza a lista de categorias
				gerenciadorCategoria.pedirAtualizacaoCategorias();
				// Exibe uma mensagem de sucesso
				JOptionPane.showMessageDialog(null, "Categoria excluída com sucesso!", "Sucesso",
						JOptionPane.INFORMATION_MESSAGE);
			} else {
				// Log se o gerenciador de categorias não estiver disponível
				System.err.println("Gerenciador de categorias não disponível!");
			}
		} else {
			// Exibe uma mensagem de erro se a exclusão não foi bem-sucedida
			JOptionPane.showMessageDialog(null, mensagem, "Erro", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void tratarListarAvisos(int status, String mensagem, List<AvisoModel> avisos) {
	    if (status == 201 || status == 200) {
	        if (telaAdmin != null) {

	            if (gerenciadorAviso != null) {
	                gerenciadorAviso.atualizarTabelaMensagens(avisos);
	            } else {
	                // Extrai as categorias dos avisos passados como parâmetro
	            	Set<Integer> categoriaIds = new HashSet<>();
	            	List<CategoriaModel> categorias = new ArrayList<>();

	            	for (CategoriaModel categoria : categoriasCadastradas) {
	            	    
	          
	            	    if (categoria != null && categoriaIds.add(categoria.getId())) {
	            	        categorias.add(categoria);
	            	    }
	            	}
	                
	                // Configura a nova tela de gerenciamento de avisos com a lista de categorias extraídas
	                telaAdmin.setVisible(false);
	                GerenciadorAvisosView gerenciadorAvisosView = new GerenciadorAvisosView(this, token, categorias);
	                setGerenciadorAviso(gerenciadorAvisosView);
	                gerenciadorAvisosView.setTelaAdmin(telaAdmin);
	                gerenciadorAvisosView.atualizarTabelaMensagens(avisos);
	                gerenciadorAvisosView.setVisible(true);
	            }
	        } else if (telaPrincipal != null) {
	            if (avisosView != null) {
	                avisosView.atualizarAvisos(avisos);
	            }
	        }
	    } else {
	        JOptionPane.showMessageDialog(null, mensagem, "Erro", JOptionPane.ERROR_MESSAGE);
	    }
	}


	private void tratarSalvarAviso(int status, String mensagem) {
		if (status == 201) {
			// Se o aviso foi salvo com sucesso
			JOptionPane.showMessageDialog(null, mensagem, "Sucesso", JOptionPane.INFORMATION_MESSAGE);
			if (gerenciadorAviso != null) {
				gerenciadorAviso.pedirAtualizacaoAvisos();
			}
		} else if (status == 401) {
			// Se houve um erro de validação ou autorização
			JOptionPane.showMessageDialog(null, mensagem, "Erro de Validação", JOptionPane.ERROR_MESSAGE);
		} else {
			// Tratamento para outros status, se necessário
			JOptionPane.showMessageDialog(null, "Status inesperado: " + status, "Erro", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void tratarExcluirAviso(int status, String mensagem) {
		if (status == 201) {
			// Se o aviso foi salvo com sucesso
			JOptionPane.showMessageDialog(null, mensagem, "Sucesso", JOptionPane.INFORMATION_MESSAGE);
			if (gerenciadorAviso != null) {
				gerenciadorAviso.pedirAtualizacaoAvisos();
			}
		} else if (status == 401) {
			// Se houve um erro de validação ou autorização
			JOptionPane.showMessageDialog(null, mensagem, "Erro de Validação", JOptionPane.ERROR_MESSAGE);
		} else {
			// Tratamento para outros status, se necessário
			JOptionPane.showMessageDialog(null, "Status inesperado: " + status, "Erro", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void tratarListarUsuarioCategorias(int status, String mensagem, List<CategoriaModel> categorias) {
		if (telaPrincipal != null) {
			if (status == 201) {

				if (avisosView != null) {
					avisosView.dispose();
				}

				telaPrincipal.ocultarTela();
				AvisosView avisoView = new AvisosView(this, telaPrincipal.getToken(), categorias);
				this.avisosView = avisoView;
				avisoView.setTelaPrincipal(telaPrincipal);
				avisoView.setVisible(true);

			} else {
				JOptionPane.showMessageDialog(null, mensagem, "Erro", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private void tratarCadastrarUsuarioCategoria(int status, String mensagem) {
		if (status == 201) {

			if (inscricaoCategoriasView != null) {
				inscricaoCategoriasView.dispose();
				setInscricaoCategoriasView(null);
				avisosView.pedirCategorias(null);
				JOptionPane.showMessageDialog(null, mensagem, "sucesso", JOptionPane.INFORMATION_MESSAGE);

				UsuarioModel usuario = new UsuarioModel();
				usuario.setOperacao("listarUsuarioCategorias");
				usuario.setToken(token); // Usando o token do cliente
				usuario.setRa(token);

				// Converte o usuário para JSON
				JSONController jsonController = new JSONController();
				JSONObject res = jsonController.changeToJSON(usuario);

				// Envia a requisição ao servidor
				enviarMensagem(res);
			}
		} else {
			JOptionPane.showMessageDialog(null, mensagem, "Erro", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void tratarDescadastrarUsuarioCategoria(int status, String mensagem) {

		if (status == 201) {

			if (avisosView != null) {
				avisosView.pedirCategorias(null);
				JOptionPane.showMessageDialog(null, mensagem, "sucesso", JOptionPane.INFORMATION_MESSAGE);

				UsuarioModel usuario = new UsuarioModel();
				usuario.setOperacao("listarUsuarioCategorias");
				usuario.setToken(token); // Usando o token do cliente
				usuario.setRa(token);

				// Converte o usuário para JSON
				JSONController jsonController = new JSONController();
				JSONObject res = jsonController.changeToJSON(usuario);

				// Envia a requisição ao servidor
				enviarMensagem(res);
			}
		} else {
			JOptionPane.showMessageDialog(null, mensagem, "Erro", JOptionPane.ERROR_MESSAGE);
		}

	}
	
	private void tratarListarUsuarioAvisos(int status, String mensagem, List<AvisoModel> avisos) {
		
		if (status == 201) {
			
			AvisosInscritosView avisosInscritosView = new AvisosInscritosView(this, avisos);
			avisosInscritosView.setVisible(true);
		} else {
			JOptionPane.showMessageDialog(null, "Erro ao listar Avisos que o usuario está cadastrado: " + mensagem);
		}
	}

	// Lida com erros exibindo mensagens apropriadas
	private void tratarErro(Object parent, String mensagem, String titulo) {
		if (mensagem == null || mensagem.trim().isEmpty()) {
			mensagem = "Erro desconhecido. Sem mensagem disponível.";
		}
		JOptionPane.showMessageDialog((parent instanceof java.awt.Component ? (java.awt.Component) parent : null),
				mensagem, titulo, JOptionPane.ERROR_MESSAGE);
	}

	// Lida com exceções de IO
	private void tratarIOException(IOException e) {
		if (e.getMessage() != null && e.getMessage().contains("Connection reset")) {
			JOptionPane.showMessageDialog(null, "O servidor foi fechado.", "Servidor Fechado",
					JOptionPane.WARNING_MESSAGE);
			System.exit(0);
		} else {
			e.printStackTrace();
		}
	}

	public void abrirTelaPrincipal(String token) {
		if (loginView != null) {
			loginView.dispose();
			MainView telaPrincipal = new MainView(this, token);
			this.telaPrincipal = telaPrincipal;
			telaPrincipal.setVisible(true);
		}
	}

	public void deslogarUsuario() {
		UsuarioModel usuario = new UsuarioModel();
		usuario.setOperacao("logout");
		usuario.setToken(token); // Usando o token do cliente

		JSONController jsonController = new JSONController();
		JSONObject res = jsonController.changeLogoutToJSON(usuario);

		enviarMensagem(res);
	}

	public AvisosView getAvisosView() {
		return avisosView;
	}

	public void fecharTelaCadastro() {
		if (cadastroView != null) {
			cadastroView.dispose();
		}
	}

	public void setAvisosView(AvisosView avisosView) {
		this.avisosView = avisosView;
	}

	public synchronized void enviarMensagem(String mensagem) throws IOException {
		saida.println(mensagem);
	}

	public synchronized String receberResposta() throws IOException {
		return entrada.readLine();
	}

	public JSONObject receberRespostaJSON() throws IOException, ParseException, org.json.simple.parser.ParseException {
		String resposta = receberResposta();
		JSONParser parser = new JSONParser();
		return (JSONObject) parser.parse(resposta);
	}

	public synchronized void fecharConexao() throws IOException {
		if (saida != null)
			saida.close();
		if (entrada != null)
			entrada.close();
		if (socketEcho != null)
			socketEcho.close();
	}

	public void enviarMensagem(JSONObject msg) {
		System.out.println("CLIENTE -> SERVIDOR: " + msg.toString());
		saida.println(msg.toString());
	}

	public boolean isAdminToken(String token) {
		// Verifica se o token corresponde ao token de admin
		return tokenAdmin.equals(token); // Token de admin definido
	}

	public void setLoginView(LoginView loginView) {
		this.loginView = loginView;
	}

	public void setCadastroView(CadastroView cadastroView) {
		this.cadastroView = cadastroView;
	}

	public String getToken() {
		return token;
	}

	public void setGerenciadorCategoria(GerenciadorCategoriasView gerenciadorCategoria) {
		this.gerenciadorCategoria = gerenciadorCategoria;
	}

	public void setGerenciadorAviso(GerenciadorAvisosView gerenciadorAviso) {
		this.gerenciadorAviso = gerenciadorAviso;
	}

	public void setTelaAdmin(AdminMainView telaAdmin) {
		this.telaAdmin = telaAdmin;
	}

	public void setInscricaoCategoriasView(InscricaoCategoriasView inscricaoCategoriasView) {
		this.inscricaoCategoriasView = inscricaoCategoriasView;
	}

}

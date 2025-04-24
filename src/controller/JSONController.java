package controller;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import model.AvisoModel;
import model.CategoriaModel;
import model.RespostaModel;
import model.UsuarioModel;

public class JSONController {

	// Converte um objeto UsuarioModel em JSON
	public JSONObject changeToJSON(UsuarioModel usuario) {
		JSONObject user = new JSONObject();
		addIfNotNull(user, "operacao", usuario.getOperacao());
		addIfNotNull(user, "ra", usuario.getRa());
		addIfNotNull(user, "senha", usuario.getSenha());
		addIfNotNull(user, "nome", usuario.getNome());
		addIfNotNull(user, "token", usuario.getToken());
		return user;
	}

	// Converte logout de UsuarioModel em JSON
	public JSONObject changeLogoutToJSON(UsuarioModel usuario) {
		JSONObject user = new JSONObject();
		addIfNotNull(user, "operacao", usuario.getOperacao());
		addIfNotNull(user, "token", usuario.getToken());
		return user;
	}

	// Converte uma string JSON em UsuarioModel
	public UsuarioModel changeToObject(String jsonString) throws ParseException {
		try {
			JSONObject jsonObject = parseJSON(jsonString);
			UsuarioModel usuario = new UsuarioModel();

			String ra = getStringFromJson(jsonObject, "ra");
			if (ra != null) {
				usuario.setRa(ra);
			}

			String senha = getStringFromJson(jsonObject, "senha");
			if (senha != null) {
				usuario.setSenha(senha);
			}

			String nome = getStringFromJson(jsonObject, "nome");
			if (nome != null) {
				usuario.setNome(nome);
			}

			String operacao = getStringFromJson(jsonObject, "operacao");
			if (operacao != null) {
				usuario.setOperacao(operacao);
			}

			String token = getStringFromJson(jsonObject, "token");
			if (token != null) {
				usuario.setToken(token);
			}

			return usuario;
		} catch (ClassCastException e) {
			System.err.println("Erro de tipo ao processar JSON: " + e.getMessage());
			return null;
		}
	}

	// Obtém a operação de uma mensagem JSON
	public String getOperacao(String mensagemRecebida) {
		try {
			JSONObject jsonObject = parseJSON(mensagemRecebida);
			Object operacaoObj = jsonObject.get("operacao");
			return operacaoObj != null ? operacaoObj.toString() : "Operacao nao encontrada.";
		} catch (ParseException e) {
			return "Nao foi possivel ler o JSON recebido.";
		}
	}

	// Converte uma string JSON para um objeto UsuarioModel
	public UsuarioModel changeLoginToJSON(String mensagemRecebida) {
		try {
			JSONObject jsonObject = parseJSON(mensagemRecebida);
			UsuarioModel usuario = new UsuarioModel();
			usuario.setRa(getStringFromJson(jsonObject, "ra"));
			usuario.setSenha(getStringFromJson(jsonObject, "senha"));
			return usuario;
		} catch (ParseException e) {
			System.err.println("Erro ao parsear JSON: " + e.getMessage());
			e.printStackTrace();
			return null;
		} catch (ClassCastException e) {
			System.err.println("Erro de tipo ao processar JSON de login: " + e.getMessage());
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	// Converte RespostaModel para JSON
	public JSONObject changeResponseToJson(RespostaModel resposta) {
		JSONObject res = new JSONObject();
		addIfNotNull(res, "operacao", resposta.getOperacao());
		addIfNotNull(res, "ra", resposta.getRa());
		addIfNotNull(res, "senha", resposta.getSenha());
		addIfNotNull(res, "nome", resposta.getNome());
		res.put("status", resposta.getStatus());
		addIfNotNull(res, "token", resposta.getToken());
		addIfNotNull(res, "mensagem", resposta.getMsg());

		// Adiciona a lista de usuários, se existir
		if (resposta.getUsuarios() != null) {
			JSONArray usuariosArray = new JSONArray();
			for (UsuarioModel usuario : resposta.getUsuarios()) {
				JSONObject usuarioJson = new JSONObject();
				usuarioJson.put("ra", usuario.getRa());
				usuarioJson.put("nome", usuario.getNome());
				usuarioJson.put("senha", usuario.getSenha());
				usuariosArray.add(usuarioJson);
			}
			res.put("usuarios", usuariosArray); // Adiciona a lista de usuários ao JSON
		}

		// Adiciona um único usuário, se existir
		if (resposta.getUsuario() != null) {
			JSONObject usuarioJson = new JSONObject();
			usuarioJson.put("ra", resposta.getUsuario().getRa());
			usuarioJson.put("nome", resposta.getUsuario().getNome());
			usuarioJson.put("senha", resposta.getUsuario().getSenha());
			res.put("usuario", usuarioJson); // Adiciona o usuário ao JSON
		}

		// Adiciona a lista de categorias, se existir
		if (resposta.getCategorias() != null) {
			JSONArray categoriasArray = new JSONArray();
			for (CategoriaModel categoria : resposta.getCategorias()) {
				JSONObject categoriaJson = new JSONObject();
				categoriaJson.put("id", categoria.getId());
				categoriaJson.put("nome", categoria.getNome());
				categoriasArray.add(categoriaJson);
			}
			res.put("categorias", categoriasArray); // Adiciona a lista de categorias ao JSON
		}

		// Adiciona uma única categoria, se existir
		if (resposta.getCategoria() != null) {
			JSONObject categoriaJson = new JSONObject();
			categoriaJson.put("id", resposta.getCategoria().getId());
			categoriaJson.put("nome", resposta.getCategoria().getNome());
			res.put("categoria", categoriaJson); // Adiciona a categoria ao JSON
		}

		// Adiciona a lista de avisos, se existir
		if (resposta.getAvisos() != null) {
			JSONArray avisosArray = new JSONArray();
			for (AvisoModel aviso : resposta.getAvisos()) {
				JSONObject avisoJson = new JSONObject();
				avisoJson.put("id", aviso.getId());
				avisoJson.put("titulo", aviso.getTitulo());
				avisoJson.put("descricao", aviso.getDescricao());
				if (aviso.getCategoria() != null) {
					JSONObject categoriaJson = new JSONObject();
					categoriaJson.put("id", aviso.getCategoria().getId());
					categoriaJson.put("nome", aviso.getCategoria().getNome());
					avisoJson.put("categoria", categoriaJson); // Adiciona a categoria do aviso
				}
				avisosArray.add(avisoJson);
			}
			res.put("avisos", avisosArray); // Adiciona a lista de avisos ao JSON
		}

		// Adiciona a lista de IDs das categorias, se existir
		if (resposta.getIdsCategorias() != null) {
			JSONArray categoriasArray = new JSONArray();
			for (Integer id : resposta.getIdsCategorias()) {
				categoriasArray.add(id); // Adiciona apenas o ID da categoria
			}
			res.put("categorias", categoriasArray); // Adiciona a lista de IDs das categorias ao JSON
		}

		return res;
	}

	// Converte uma string JSON em RespostaModel
	public RespostaModel changeResponseToJson(String msg) {
		try {
			if (msg == null || msg.trim().isEmpty())
				return null;
			JSONObject jsonObject = parseJSON(msg);
			RespostaModel resposta = new RespostaModel();

			// Mantém as partes originais do código
			resposta.setOperacao(getStringFromJson(jsonObject, "operacao"));
			resposta.setRa(getStringFromJson(jsonObject, "ra"));
			resposta.setSenha(getStringFromJson(jsonObject, "senha"));
			resposta.setNome(getStringFromJson(jsonObject, "nome"));
			Object statusObj = jsonObject.get("status");
			if (statusObj != null) {
				resposta.setStatus(Integer.parseInt(statusObj.toString()));
			}
			resposta.setToken(getStringFromJson(jsonObject, "token"));
			resposta.setMsg(getStringFromJson(jsonObject, "mensagem"));

			if (jsonObject.containsKey("id")) {
				resposta.setIdCategoria(Integer.parseInt(jsonObject.get("id").toString()));
			}

			// Lógica para lidar com a lista de usuários
			if (jsonObject.containsKey("usuarios")) {
				JSONArray usuariosArray = (JSONArray) jsonObject.get("usuarios");
				List<UsuarioModel> usuarios = new ArrayList<>();
				for (Object obj : usuariosArray) {
					JSONObject usuarioJson = (JSONObject) obj;
					UsuarioModel usuario = new UsuarioModel();
					usuario.setRa(getStringFromJson(usuarioJson, "ra"));
					usuario.setNome(getStringFromJson(usuarioJson, "nome"));
					usuario.setSenha(getStringFromJson(usuarioJson, "senha"));
					usuarios.add(usuario);
				}
				resposta.setUsuarios(usuarios); // Define a lista de usuários na resposta
			}

			// Lógica para lidar com um único usuário
			if (jsonObject.containsKey("usuario")) {
				JSONObject usuarioJson = (JSONObject) jsonObject.get("usuario");
				UsuarioModel usuario = new UsuarioModel();
				usuario.setRa(getStringFromJson(usuarioJson, "ra"));
				usuario.setNome(getStringFromJson(usuarioJson, "nome"));
				usuario.setSenha(getStringFromJson(usuarioJson, "senha"));
				resposta.setUsuario(usuario); // Define o usuário na resposta
			}

			// Lógica para lidar com a lista de categorias
			if (jsonObject.containsKey("categorias")) {
				Object categoriasObj = jsonObject.get("categorias");

				// Verifica se é um JSONArray de objetos de categoria
				if (categoriasObj instanceof JSONArray) {
					JSONArray categoriasArray = (JSONArray) categoriasObj;
					List<CategoriaModel> categorias = new ArrayList<>();

					for (Object obj : categoriasArray) {
						if (obj instanceof JSONObject) {
							JSONObject categoriaJson = (JSONObject) obj;
							CategoriaModel categoria = new CategoriaModel();
							categoria.setId(Integer.parseInt(categoriaJson.get("id").toString()));
							categoria.setNome(getStringFromJson(categoriaJson, "nome"));
							categorias.add(categoria);
						} else if (obj instanceof Long) { // Alterado para Long
							// Se for um ID, apenas adiciona à lista de IDs
							CategoriaModel categoria = new CategoriaModel();
							categoria.setId(((Long) obj).intValue()); // Converte Long para int
							categorias.add(categoria);
						}
					}
					resposta.setCategorias(categorias); // Define a lista de categorias na resposta
				}
			}

			// Lógica para lidar com uma única categoria
			if (jsonObject.containsKey("categoria")) {
				Object categoriaObj = jsonObject.get("categoria");

				if (categoriaObj instanceof JSONObject) {
					JSONObject categoriaJson = (JSONObject) categoriaObj;
					CategoriaModel categoria = new CategoriaModel();
					categoria.setId(Integer.parseInt(categoriaJson.get("id").toString()));
					categoria.setNome(getStringFromJson(categoriaJson, "nome"));
					resposta.setCategoria(categoria); // Define a categoria na resposta
				} else if (categoriaObj instanceof Number) { // Garante que pega qualquer tipo numérico (Integer, Long,
																// etc.)
					resposta.setIdCategoria(((Number) categoriaObj).intValue()); // Converte para int corretamente
				}
			}

			// Lógica para lidar com a lista de avisos
			if (jsonObject.containsKey("avisos")) {
				JSONArray avisosArray = (JSONArray) jsonObject.get("avisos");
				List<AvisoModel> avisos = new ArrayList<>();
				for (Object obj : avisosArray) {
					JSONObject avisoJson = (JSONObject) obj;
					AvisoModel aviso = new AvisoModel();
					Object idObj = avisoJson.get("id");
					if (idObj != null) {
						aviso.setId(Integer.parseInt(idObj.toString()));
					}
					aviso.setTitulo(getStringFromJson(avisoJson, "titulo"));
					aviso.setDescricao(getStringFromJson(avisoJson, "descricao"));

					// Lógica para lidar com a categoria do aviso
					if (avisoJson.containsKey("categoria")) {
						JSONObject categoriaJson = (JSONObject) avisoJson.get("categoria");
						CategoriaModel categoria = new CategoriaModel();
						categoria.setId(Integer.parseInt(categoriaJson.get("id").toString()));
						categoria.setNome(getStringFromJson(categoriaJson, "nome"));
						aviso.setCategoria(categoria); // Define a categoria do aviso
					}

					avisos.add(aviso);
				}
				resposta.setAvisos(avisos); // Define a lista de avisos na resposta
			}

			// Tratamento de um único aviso
			if (jsonObject.containsKey("aviso")) {
				JSONObject avisoJson = (JSONObject) jsonObject.get("aviso");
				AvisoModel aviso = new AvisoModel();

				aviso.setId(Integer.parseInt(avisoJson.get("id").toString()));
				aviso.setTitulo(getStringFromJson(avisoJson, "titulo"));
				aviso.setDescricao(getStringFromJson(avisoJson, "descricao"));

				// Corrigir a extração da categoria (vem como ID, não como objeto)
				if (avisoJson.containsKey("categoria")) {
					CategoriaModel categoria = new CategoriaModel();
					categoria.setId(Integer.parseInt(avisoJson.get("categoria").toString()));
					aviso.setCategoria(categoria);
				}

				resposta.setAviso(aviso); // Define o aviso na resposta
			}

			return resposta;
		} catch (ParseException e) {
			// Tratar erro de parsing
			System.err.println("Erro ao parsear JSON de resposta: " + e.getMessage());
			return null; // Retorna null em caso de erro de parsing
		} catch (ClassCastException e) {
			// Tratar erro de tipo ao processar JSON
			System.err.println("Erro de tipo ao processar JSON de resposta: " + e.getMessage());
			return null; // Retorna null em caso de erro de tipo
		} catch (Exception e) {
			// Tratar outros erros
			System.err.println("Erro inesperado: " + e.getMessage());
			return null; // Retorna null em caso de erro inesperado
		}
	}

	public RespostaModel changeListarAvisosToJson(String msg) {
		try {
			if (msg == null || msg.trim().isEmpty()) {
				return null;
			}

			JSONParser parser = new JSONParser();
			JSONObject jsonObject = (JSONObject) parser.parse(msg);
			RespostaModel resposta = new RespostaModel();

			// Define campos básicos
			resposta.setOperacao(getStringFromJson(jsonObject, "operacao"));
			resposta.setToken(getStringFromJson(jsonObject, "token"));
			resposta.setIdCategoria(getIntFromJson(jsonObject, "categoria"));

			return resposta;
		} catch (ParseException e) {
			System.err.println("Erro ao parsear JSON: " + e.getMessage());
			return null;
		} catch (Exception e) {
			System.err.println("Erro inesperado: " + e.getMessage());
			return null;
		}
	}

	// Converte uma string JSON de registro em UsuarioModel
	public UsuarioModel changeRegisterJSON(String mensagemRecebida) {
		try {
			JSONObject jsonObject = parseJSON(mensagemRecebida);
			UsuarioModel usuario = new UsuarioModel();
			setIfExists(jsonObject, "ra", usuario::setRa);
			setIfExists(jsonObject, "senha", usuario::setSenha);
			setIfExists(jsonObject, "nome", usuario::setNome);
			setIfExists(jsonObject, "token", usuario::setToken);
			return usuario;
		} catch (ParseException e) {
			System.err.println("Erro ao parsear JSON de registro: " + e.getMessage());
			e.printStackTrace();
			return null;
		}
	}

	// Métodos utilitários
	private JSONObject parseJSON(String jsonString) throws ParseException {
		return (JSONObject) new JSONParser().parse(jsonString);
	}

	private String getStringFromJson(JSONObject json, String key) {
		if (json == null) {
			System.err.println("JSONObject is null");
			return null; // Retorna null se o JSONObject for nulo
		}
		Object value = json.get(key);
		return value != null ? value.toString() : null; // Retorna o valor como String ou null se não existir
	}

	private int getIntFromJson(JSONObject jsonObject, String key) {
		Object value = jsonObject.get(key);

		if (value == null) {
			System.err.println("Chave '" + key + "' não encontrada ou é null.");
			return 0; // Valor padrão
		}

		if (value instanceof Number) {
			return ((Number) value).intValue(); // Converte qualquer tipo numérico (Integer, Long, etc.)
		}

		if (value instanceof String) {
			try {
				return Integer.parseInt((String) value); // Converte String para int
			} catch (NumberFormatException e) {
				System.err.println("Erro ao converter '" + key + "' para inteiro: " + value);
			}
		}

		System.err.println("Valor para a chave '" + key + "' não é um número válido.");
		return 0; // Valor padrão
	}

	@SuppressWarnings("unchecked")
	private void addIfNotNull(JSONObject json, String key, Object value) {
		if (value != null) {
			json.put(key, value);
		}
	}

	private void setIfExists(JSONObject json, String key, java.util.function.Consumer<String> setter) {
		Object value = json.get(key);
		if (value != null) {
			setter.accept(value.toString());
		}
	}

	@SuppressWarnings("unchecked")
	public JSONObject changeUserUpdateToJSON(UsuarioModel usuario) {
		JSONObject user = new JSONObject();
		user.put("operacao", "editarUsuario"); // Define o valor fixo para "operacao"
		user.put("token", usuario.getToken()); // Adiciona o token diretamente

		JSONObject usuarioObj = new JSONObject(); // Cria o objeto interno "usuario"
		addIfNotNull(usuarioObj, "ra", usuario.getRa());
		addIfNotNull(usuarioObj, "senha", usuario.getSenha());
		addIfNotNull(usuarioObj, "nome", usuario.getNome());

		user.put("usuario", usuarioObj); // Adiciona o objeto interno ao JSON principal
		return user;
	}

	// Converte uma string JSON em UsuarioModel
	public UsuarioModel changeJSONToUser(String jsonString) {
		try {
			JSONObject jsonObject = parseJSON(jsonString); // Parseia o JSON principal

			// Extrai o objeto "usuario" do JSON
			JSONObject usuarioJson = (JSONObject) jsonObject.get("usuario");

			UsuarioModel usuario = new UsuarioModel();

			// Preenche o UsuarioModel com os dados do objeto "usuario"
			usuario.setRa(getStringFromJson(usuarioJson, "ra"));
			usuario.setNome(getStringFromJson(usuarioJson, "nome"));
			usuario.setSenha(getStringFromJson(usuarioJson, "senha"));

			usuario.setToken(getStringFromJson(jsonObject, "token")); // Define o token no UsuarioModel

			return usuario; // Retorna o objeto UsuarioModel preenchido
		} catch (ParseException e) {
			System.err.println("Erro ao parsear JSON: " + e.getMessage());
			return null; // Retorna null em caso de erro
		} catch (ClassCastException e) {
			System.err.println("Erro de tipo ao processar JSON: " + e.getMessage());
			return null; // Retorna null em caso de erro
		}
	}

	@SuppressWarnings("unchecked")
	public JSONObject changeToSalvarCategoriaJSON(RespostaModel resposta) {
		JSONObject res = new JSONObject();
		addIfNotNull(res, "operacao", resposta.getOperacao());
		addIfNotNull(res, "token", resposta.getToken());

		// Verifica se a categoria está presente e adiciona o ID e nome da categoria ao
		// JSON
		if (resposta.getCategoria() != null) {
			CategoriaModel categoria = resposta.getCategoria();
			JSONObject categoriaJson = new JSONObject();
			addIfNotNull(categoriaJson, "id", categoria.getId());
			addIfNotNull(categoriaJson, "nome", categoria.getNome());
			res.put("categoria", categoriaJson); // Adiciona o objeto categoria ao JSON
		}

		return res;
	}

	public JSONObject changeToExcluirCategoriaJSON(RespostaModel resposta) {
		JSONObject res = new JSONObject();
		addIfNotNull(res, "operacao", resposta.getOperacao());
		addIfNotNull(res, "token", resposta.getToken());

		// Verifica se a categoria está presente e adiciona o ID e nome da categoria ao
		// JSON
		if (resposta.getCategoria() != null) {
			CategoriaModel categoria = resposta.getCategoria();
			addIfNotNull(res, "id", categoria.getId());
			addIfNotNull(res, "nome", categoria.getNome());
		}

		return res;
	}

	public CategoriaModel changeJSONToCategoria(String jsonString) {
		try {
			JSONObject jsonObject = parseJSON(jsonString); // Parseia o JSON principal
			CategoriaModel category = new CategoriaModel(); // Cria um novo objeto CategoriaModel

			// Verifica se o JSON contém um objeto "categoria"
			JSONObject categoriaObject = (JSONObject) jsonObject.get("categoria");
			if (categoriaObject != null) {
				// Extrai o ID da categoria do JSON
				String categoryIdStr = getStringFromJson(categoriaObject, "id");
				if (categoryIdStr != null) {
					category.setId(Integer.parseInt(categoryIdStr)); // Define o ID da categoria
				}

				// Extrai o nome da categoria do JSON
				String categoryName = getStringFromJson(categoriaObject, "nome");
				if (categoryName != null) {
					category.setNome(categoryName); // Define o nome da categoria
				}
			} else {
				// Se não houver objeto "categoria", verifica se há um ID diretamente no JSON
				String categoryIdStr = getStringFromJson(jsonObject, "id");
				if (categoryIdStr != null) {
					category.setId(Integer.parseInt(categoryIdStr)); // Define o ID da categoria
				}
			}

			return category; // Retorna o objeto CategoriaModel preenchido
		} catch (ParseException e) {
			System.err.println("Erro ao parsear JSON de categoria: " + e.getMessage());
			return null; // Retorna null em caso de erro
		} catch (ClassCastException e) {
			System.err.println("Erro de tipo ao processar JSON de categoria: " + e.getMessage());
			return null; // Retorna null em caso de erro
		} catch (NumberFormatException e) {
			System.err.println("Erro ao converter o ID da categoria: " + e.getMessage());
			return null; // Retorna null em caso de erro
		}
	}

	public String extractToken(String mensagemRecebida) throws ParseException {
		JSONObject jsonObject = parseJSON(mensagemRecebida);
		return getStringFromJson(jsonObject, "token"); // Pega o token do JSON
	}

	@SuppressWarnings("unchecked")
	public JSONObject changeToJSON(RespostaModel resposta) {
		JSONObject resp = new JSONObject();

		// Adiciona os campos principais
		addIfNotNull(resp, "operacao", resposta.getOperacao());
		addIfNotNull(resp, "ra", resposta.getRa());
		addIfNotNull(resp, "senha", resposta.getSenha());
		addIfNotNull(resp, "nome", resposta.getNome());

		// Criação do objeto aviso
		if (resposta.getAviso() != null) {
			JSONObject avisoJson = new JSONObject();
			addIfNotNull(avisoJson, "id", resposta.getAviso().getId()); // ID do aviso
			addIfNotNull(avisoJson, "titulo", resposta.getAviso().getTitulo()); // Título do aviso
			addIfNotNull(avisoJson, "descricao", resposta.getAviso().getDescricao()); // Descrição do aviso

			// Adiciona a categoria ao objeto aviso
			if (resposta.getAviso().getCategoria() != null) {
				addIfNotNull(avisoJson, "categoria", resposta.getAviso().getCategoria().getId()); // ID da categoria
			}

			// Adiciona o objeto aviso ao JSON principal
			resp.put("aviso", avisoJson);
		}

		// Adiciona o token e o id da categoria
		addIfNotNull(resp, "token", resposta.getToken());
		addIfNotNull(resp, "id", resposta.getIdCategoria());

		return resp;
	}
	@SuppressWarnings("unchecked")
	public JSONObject changeToJSONSalvarCategoria(RespostaModel resposta) {
		JSONObject resp = new JSONObject();

		// Adiciona os campos principais
		addIfNotNull(resp, "operacao", resposta.getOperacao());
		addIfNotNull(resp, "ra", resposta.getRa());

		// Criação do objeto aviso
		if (resposta.getAviso() != null) {
			JSONObject avisoJson = new JSONObject();
			addIfNotNull(avisoJson, "id", resposta.getAviso().getId()); // ID do aviso
			addIfNotNull(avisoJson, "titulo", resposta.getAviso().getTitulo()); // Título do aviso
			addIfNotNull(avisoJson, "descricao", resposta.getAviso().getDescricao()); // Descrição do aviso

			// Adiciona a categoria ao objeto aviso
			if (resposta.getAviso().getCategoria() != null) {
				addIfNotNull(avisoJson, "categoria", resposta.getAviso().getCategoria().getId()); // ID da categoria
			}

			// Adiciona o objeto aviso ao JSON principal
			resp.put("aviso", avisoJson);
		}

		// Adiciona o token e o id da categoria
		addIfNotNull(resp, "token", resposta.getToken());

		return resp;
	}

	public JSONObject changeToJSONCategoria(RespostaModel resposta) {
		JSONObject resp = new JSONObject();

		// Adiciona os campos principais
		addIfNotNull(resp, "operacao", resposta.getOperacao());
		addIfNotNull(resp, "ra", resposta.getRa());
		addIfNotNull(resp, "token", resposta.getToken());
		addIfNotNull(resp, "categoria", resposta.getIdCategoria());

		return resp;
	}

	public JSONObject changeToJSONIncreverseCategoria(RespostaModel resposta) {
		JSONObject resp = new JSONObject();

		// Adiciona os campos principais
		addIfNotNull(resp, "operacao", resposta.getOperacao());
		addIfNotNull(resp, "ra", resposta.getRa());
		addIfNotNull(resp, "token", resposta.getToken());
		addIfNotNull(resp, "categoria", resposta.getCategoriaId());

		return resp;
	}
}

package controller;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

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
        addIfNotNull(user, "token", usuario.getRa());
        return user;
    }

    // Converte uma string JSON em UsuarioModel
    public UsuarioModel changeToObject(String jsonString) throws ParseException {
        try {
            JSONObject jsonObject = parseJSON(jsonString);
            UsuarioModel usuario = new UsuarioModel();
            usuario.setRa(getStringFromJson(jsonObject, "ra"));
            usuario.setSenha(getStringFromJson(jsonObject, "senha"));
            usuario.setNome(getStringFromJson(jsonObject, "nome"));
            usuario.setOperacao(getStringFromJson(jsonObject, "operacao"));
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
            return operacaoObj != null ? operacaoObj.toString() : "Operação não encontrada.";
        } catch (ParseException e) {
            return "ERRO: Não foi possível ler o JSON recebido.";
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
        
        return res;
    }

 // Converte uma string JSON em RespostaModel
    public RespostaModel changeResponseToJson(String msg) {
        try {
            if (msg == null || msg.trim().isEmpty()) return null;
            JSONObject jsonObject = parseJSON(msg);
            RespostaModel resposta = new RespostaModel();
            
            // Mantém as partes originais do código
            resposta.setOperacao(getStringFromJson(jsonObject, "operacao"));
            resposta.setRa(getStringFromJson(jsonObject, "ra"));
            resposta.setSenha(getStringFromJson(jsonObject, "senha"));
            resposta.setNome(getStringFromJson(jsonObject, "nome"));
            resposta.setStatus(Integer.parseInt(jsonObject.get("status").toString()));
            resposta.setToken(getStringFromJson(jsonObject, "token"));
            resposta.setMsg(getStringFromJson(jsonObject, "mensagem"));

            // Nova lógica para lidar com a lista de usuários
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

            return resposta;
        } catch (ParseException e) {
            System.err.println("Erro ao parsear JSON de resposta: " + e.getMessage());
            e.printStackTrace();
            return null;
        } catch (ClassCastException e) {
            System.err.println("Erro de tipo ao processar JSON de resposta: " + e.getMessage());
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
        Object value = json.get(key);
        return value != null ? value.toString() : null;
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

            return usuario; // Retorna o objeto UsuarioModel preenchido
        } catch (ParseException e) {
            System.err.println("Erro ao parsear JSON: " + e.getMessage());
            return null; // Retorna null em caso de erro
        } catch (ClassCastException e) {
            System.err.println("Erro de tipo ao processar JSON: " + e.getMessage());
            return null; // Retorna null em caso de erro
        }
    }
}

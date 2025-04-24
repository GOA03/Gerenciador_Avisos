# Sistema de Avisos

Este projeto é um sistema de avisos que permite o cadastro e login de usuários, além de gerenciar avisos por categorias.

## Funcionalidades Principais

1. **Cadastro e Login de Usuários**
   - Permite que novos usuários se cadastrem no sistema.
   - Usuários podem realizar login utilizando suas credenciais.

2. **Administração de Categorias**
   - Acesso à funcionalidade de gerenciamento de categorias após login como administrador.
   - Opções para adicionar, editar ou remover categorias.
   - Validação para garantir que os nomes das categorias sejam únicos.

3. **Administração de Avisos**
   - Acesso à funcionalidade de gerenciamento de avisos após login como administrador.
   - Opções para adicionar, editar ou remover avisos.

4. **Gerenciamento de Usuários**
   - Visualização, adição, edição e remoção de usuários na tela principal do admin.
   - Campos obrigatórios para cadastro de novos usuários: RA, nome e senha.

5. **Funcionalidade de Logout**
   - Permite que os usuários retornem à tela de login a qualquer momento.

6. **Acesso Restrito para Usuários Comuns**
   - Usuários comuns têm acesso limitado às funcionalidades do sistema, como visualizar avisos e realizar logout.

## Instalação das Bibliotecas

As bibliotecas necessárias já estão disponíveis na pasta `bibliotecas`. Caso não as encontre, siga as orientações abaixo para baixá-las:

### Bibliotecas Necessárias:
- `json-simple-1.1.1.jar`
- `mysql-connector-j-8.0.32.jar`

### Links para Download:
- [json-simple](https://github.com/fangyidong/json-simple)
- [MySQL Connector/J](https://dev.mysql.com/downloads/connector/j/)

Após o download, mova os arquivos para a pasta `bibliotecas` do projeto.

## Configuração do Banco de Dados

1. **Inicie o Servidor MySQL**
   - Certifique-se de que o servidor MySQL do XAMPP está em execução.

2. **Criação do Banco de Dados e Tabelas**
   - Acesse o MySQL através de um cliente (phpMyAdmin ou terminal).
   - Execute os comandos SQL abaixo para criar o banco de dados e as tabelas necessárias:

```sql
-- Criação do banco de dados
CREATE DATABASE sistemaavisos;

-- Seleção do banco de dados
USE sistemaavisos;

-- Criação da tabela usuario
CREATE TABLE usuario (
    ra VARCHAR(20) PRIMARY KEY,      -- RA do usuário como chave primária
    senha VARCHAR(100) NOT NULL,     -- Senha do usuário
    nome VARCHAR(100) NOT NULL        -- Nome do usuário
);

-- Criação da tabela categorias
CREATE TABLE categorias (
    id INT AUTO_INCREMENT PRIMARY KEY,  -- ID da categoria como chave primária e auto-incremento
    nome VARCHAR(50) NOT NULL UNIQUE     -- Nome da categoria, deve ser único
);
```
## Execução do Projeto

### 1. Inicie o Servidor MySQL
- Certifique-se de que o MySQL do XAMPP está ativo.

### 2. Configure as Credenciais de Acesso
- Verifique se as credenciais no arquivo de propriedades do projeto correspondem ao banco de dados configurado.

### 3. Execute o Projeto
- Após as configurações acima, inicie o projeto no ambiente de desenvolvimento.

### 4. Conexão Cliente-Servidor
- Abra e execute as classes `InterfaceServidorView` e `ClienteView`.
- Conecte os dois com a porta e o IP desejados.

### 5. Cadastro e Login
- Cadastre-se utilizando o protocolo do projeto.
- Após um cadastro bem-sucedido, efetue o login.
- Para retornar à tela de login, use a funcionalidade de **logout**.

### 6. Administração de Categorias e Avisos
- Após o login como admin (RA padrão: `1234567`), você terá acesso à funcionalidade de gerenciamento de categorias e avisos.
- Utilize a opção **"Gerenciar Categorias"** ou **"Gerenciar Avisos"**  na tela principal do admin para adicionar, editar ou remover categorias ou avisos.

### 7. Gerenciamento de Usuários
- Na tela principal do admin, utilize a opção **"Gerenciar Usuários"** para visualizar, adicionar, editar ou remover usuários.

### 8. Logout
- Para retornar à tela de login, utilize a funcionalidade de **logout** disponível na tela principal ou feche a interface da aplicação.


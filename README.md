#  SuperNova VET

Sistema web desenvolvido para gerenciamento e monitoramento de pets.

O projeto utiliza **Java com Spring Boot** no backend, **Oracle Database** para persistência dos dados, **Flyway** para versionamento do banco de dados, **Spring Security** para autenticação e autorização e **React + Vite** no frontend.

O sistema possui diferentes níveis de acesso para administradores, veterinários e tutores.

## Sprint 4

Este repositório é a API usada por todo o projeto SuperNova VET: a aplicação de Java Advanced, o aplicativo mobile e a integração com o banco de dados da disciplina de *Mastering Relational and Non-Relational Database*.

**O que foi adicionado nesta sprint**
- `PetProcedureRepository`: chama os procedimentos do package `PKG_PET_APP` (`pr_inserir_tutor`, `pr_inserir_pet`, `pr_registrar_vacina`, `pr_atualizar_peso_pet`, `pr_excluir_pet`, `pr_listar_pets`) e o export do dataset em JSON, usando `SimpleJdbcCall`.
- `PetProcedureController`: expõe os endpoints em `/procedures/**`. Erros do Oracle (`ORA-20001` a `ORA-20006`) são devolvidos como HTTP 400 com a mensagem tratada pela procedure.
- `POST /auth/register`: cadastro público de usuários com perfil `TUTOR`, usado pela tela de cadastro do app mobile.
- `PUT /tutores/{id}`: a senha passou a ser opcional na edição (em branco mantém a senha atual).
- `DELETE /tutores/{id}`: retorna 409 quando o tutor possui pets vinculados.
- `SecurityConfig`: as rotas `/procedures/**` exigem perfil `ADMIN` ou `VETERINARIO`.

**Repositórios relacionados**
- Banco de dados (SQL, JSON e MongoDB): https://github.com/FelipeAugusto99/SP4-DATABASE-SUPERNOVA-VET
- Aplicativo mobile: https://github.com/FelipeAugusto99/SP01MOBILE-SUPERNOVA-VET

**Como testar a integração com o banco**
1. Rode o script SQL do repositório de Database no Oracle.
2. Defina as variáveis de ambiente `DB_USERNAME` e `DB_PASSWORD` (as credenciais não ficam no repositório).
3. Suba a API com `./mvnw spring-boot:run`.
4. Abra o Swagger em `http://localhost:8080/swagger-ui/index.html` e clique em **Authorize** com o usuário administrador criado pela aplicação.
5. Teste, por exemplo, `POST /procedures/pets` (sucesso) e o mesmo endpoint com um `idTutor` inexistente (retorna 400 com `ORA-20003`).

Como o script de Database já cria a tabela `ch_tutor` com as colunas `ds_senha` e `ds_perfil`, o Flyway usa `baseline-version=3`: em um banco criado pelo script, as migrations até a V3 são consideradas aplicadas.

---

##  Integrantes

- Kaique Mascarenhas dos Santos
- Felipe Augusto Lopes Ferreira

---

##  Objetivo do Projeto

O **SuperNova VET** foi desenvolvido para auxiliar no gerenciamento e monitoramento de animais cadastrados.

O sistema permite:

- Cadastro de pets
- Atualização de pets
- Exclusão de pets
- Consulta de pets
- Gerenciamento de tutores
- Monitoramento dos pets por nível de risco
- Consulta de animais críticos
- Resumo dos animais por nível de risco
- Autenticação de usuários
- Controle de acesso baseado em perfis
- Visualização dos próprios pets pelo tutor

O sistema possui três perfis de acesso:

- **ADMIN**
- **VETERINARIO**
- **TUTOR**

Cada perfil possui permissões diferentes dentro da aplicação.

---

#  Tecnologias Utilizadas

## Backend

- Java
- Spring Boot
- Spring Web
- Spring Data JPA
- Spring Security
- Bean Validation
- BCrypt
- Flyway
- Maven
- Swagger / OpenAPI

## Banco de Dados

- Oracle Database

## Frontend

- React
- Vite
- JavaScript
- CSS
- Fetch API

---

#  Estrutura do Projeto

```text
SUPERNOVAVET_JAVA_ADVANCED/
│
├── src/
│   └── main/
│       ├── java/
│       │   └── Novamonitor/
│       │       ├── controller/
│       │       ├── entity/
│       │       ├── exception/
│       │       ├── repository/
│       │       └── security/
│       │
│       └── resources/
│           ├── db/
│           │   └── migration/
│           └── application.properties
│
├── frontend/
│   ├── src/
│   ├── package.json
│   ├── vite.config.js
│   └── README.md
│
├── pom.xml
└── README.md
```

---

#  Banco de Dados

O projeto utiliza **Oracle Database** para persistência dos dados.

Entre as principais entidades utilizadas pela aplicação estão:

## Tutor

Representa os tutores e usuários utilizados pelo sistema.

Principais informações:

- ID
- Nome
- E-mail
- Telefone
- Senha
- Perfil

## Pet

Representa os animais cadastrados no sistema.

Principais informações:

- ID
- Nome
- Idade
- Espécie
- Nível de risco
- Tutor responsável

Cada pet possui relacionamento com um tutor.

---

#  Flyway

O projeto utiliza **Flyway** para controle e versionamento das alterações realizadas no banco de dados.

As migrations ficam localizadas em:

```text
src/main/resources/db/migration
```

Como o banco de dados já existia antes da implementação do Flyway, foi utilizado o recurso de **baseline**.

A partir do baseline, novas alterações realizadas no banco passaram a ser controladas através das migrations.

Uma das migrations utilizadas no projeto é:

```text
V3__adicionar_seguranca_tutor.sql
```

Essa migration adicionou os campos necessários para autenticação e controle de perfil dos tutores.

Exemplo:

```sql
ALTER TABLE ch_tutor
    ADD (
        ds_senha VARCHAR2(255),
        ds_perfil VARCHAR2(30)
    );
```

As migrations são verificadas automaticamente pelo Flyway durante a inicialização da aplicação.

---

#  Spring Security

A aplicação utiliza **Spring Security** para autenticação e autorização.

A autenticação utilizada no projeto é **HTTP Basic Authentication**.

As senhas dos usuários são protegidas utilizando:

```text
BCrypt
```

O sistema possui três perfis:

```text
ADMIN
VETERINARIO
TUTOR
```

Cada perfil possui permissões diferentes.

---

#  Perfil ADMIN

O administrador possui o maior nível de acesso dentro da aplicação.

O ADMIN pode:

- Visualizar todos os pets
- Cadastrar pets
- Editar pets
- Excluir pets
- Visualizar tutores
- Cadastrar tutores
- Editar tutores
- Excluir tutores
- Visualizar pets críticos
- Visualizar resumo de risco

---

#  Perfil VETERINARIO

O veterinário possui acesso ao gerenciamento dos animais, porém possui algumas restrições administrativas.

O VETERINARIO pode:

- Visualizar todos os pets
- Cadastrar pets
- Editar pets
- Visualizar tutores
- Visualizar pets críticos
- Visualizar resumo de risco

O VETERINARIO não pode:

- Excluir pets
- Cadastrar tutores
- Editar tutores
- Excluir tutores

---

#  Perfil TUTOR

O tutor possui acesso restrito às informações relacionadas aos próprios animais.

O TUTOR pode:

- Visualizar somente os próprios pets
- Visualizar o resumo dos próprios pets no frontend
- Visualizar seus próprios animais classificados como críticos

O TUTOR não pode:

- Visualizar os pets de outros tutores
- Cadastrar pets
- Editar pets
- Excluir pets
- Visualizar a lista de tutores
- Cadastrar tutores
- Editar tutores
- Excluir tutores

---

#  Permissões

| Funcionalidade | ADMIN | VETERINARIO | TUTOR |
|---|:---:|:---:|:---:|
| Visualizar todos os pets | ✅ | ✅ | ❌ |
| Visualizar próprios pets | ✅ | ✅ | ✅ |
| Cadastrar pet | ✅ | ✅ | ❌ |
| Editar pet | ✅ | ✅ | ❌ |
| Excluir pet | ✅ | ❌ | ❌ |
| Visualizar tutores | ✅ | ✅ | ❌ |
| Cadastrar tutor | ✅ | ❌ | ❌ |
| Editar tutor | ✅ | ❌ | ❌ |
| Excluir tutor | ✅ | ❌ | ❌ |
| Pets críticos globais | ✅ | ✅ | ❌ |
| Resumo de risco global | ✅ | ✅ | ❌ |

---

#  Pets do Tutor Autenticado

Foi criado um endpoint específico para garantir que cada tutor consiga visualizar somente os animais associados à própria conta.

Endpoint:

```http
GET /pets/meus
```

O sistema utiliza o usuário autenticado pelo Spring Security para identificar automaticamente o tutor.

No backend, o e-mail é obtido através de:

```java
authentication.getName()
```

Depois os pets são pesquisados utilizando o e-mail do tutor autenticado.

Exemplo no repository:

```java
List<Pet> findByTutorEmail(String email);
```

Dessa forma, o tutor não precisa informar seu próprio ID na URL.

Isso também impede que um tutor simplesmente altere um ID na requisição para tentar visualizar os animais de outro usuário.

---

#  Funcionalidades Além do CRUD

Além das operações tradicionais de cadastro, consulta, alteração e exclusão, o sistema possui funcionalidades específicas de negócio.

##  Pets Críticos

Endpoint:

```http
GET /pets/criticos
```

Essa funcionalidade retorna os animais classificados com nível de risco:

```text
ALTO
```

Ela pode ser utilizada pelos perfis administrativos para identificar rapidamente os animais que necessitam de maior atenção.

No frontend do TUTOR, os pets críticos são obtidos somente a partir dos próprios animais do usuário autenticado.

---

##  Resumo de Risco

Endpoint:

```http
GET /pets/resumo-risco
```

Essa funcionalidade apresenta a quantidade de animais cadastrados em cada nível de risco.

Exemplo:

```json
{
  "BAIXO": 3,
  "MEDIO": 2,
  "ALTO": 1,
  "TOTAL": 6
}
```

Os níveis utilizados são:

```text
BAIXO
MEDIO
ALTO
```

No frontend do TUTOR, o resumo é calculado somente utilizando os animais retornados pelo endpoint:

```http
GET /pets/meus
```

Assim, o tutor não recebe informações globais dos outros animais cadastrados.

---

#  Consulta por Nível de Risco

Também é possível consultar animais por um nível específico de risco.

Endpoint:

```http
GET /pets/risco/{nivelRisco}
```

Exemplo:

```http
GET /pets/risco/ALTO
```

---

#  Consulta por Espécie

O sistema também permite pesquisar animais por espécie.

Endpoint:

```http
GET /pets/especie/{especie}
```

Exemplo:

```http
GET /pets/especie/Cachorro
```

---

#  Validações

A aplicação utiliza **Bean Validation** para validar os dados recebidos pela API.

Entre as validações implementadas para os pets estão:

- Nome obrigatório
- Espécie obrigatória
- Tutor obrigatório
- Idade obrigatória
- Idade não pode ser negativa
- Idade deve respeitar o limite definido
- Nível de risco obrigatório
- Nível de risco deve possuir um valor válido

Os níveis aceitos são:

```text
BAIXO
MEDIO
ALTO
```

Também existem validações para os dados dos tutores, como:

- Nome obrigatório
- E-mail obrigatório
- Formato válido de e-mail
- Telefone obrigatório
- Quantidade válida de números no telefone

Quando informações inválidas são enviadas, a aplicação retorna:

```text
HTTP 400 Bad Request
```

Exemplo de dados inválidos:

```json
{
  "nome": "",
  "idade": -5,
  "especie": "",
  "nivelRisco": "URGENTE",
  "tutor": null
}
```

Exemplo de resposta:

```json
{
  "nome": "O nome do pet é obrigatório",
  "idade": "A idade não pode ser negativa",
  "especie": "A espécie é obrigatória",
  "nivelRisco": "O nível de risco deve ser BAIXO, MEDIO ou ALTO",
  "tutor": "O tutor é obrigatório"
}
```

O tratamento das validações é realizado através de um tratamento global de exceções.

---

#  Swagger / OpenAPI

A aplicação possui documentação da API através do **Swagger/OpenAPI**.

Com o backend executando, acesse:

```text
http://localhost:8080/swagger-ui/index.html
```

O Swagger permite visualizar e testar os endpoints disponíveis.

Como a API utiliza Spring Security, para testar endpoints protegidos é necessário utilizar o botão:

```text
Authorize
```

e informar as credenciais de um usuário válido.

---

#  Principais Endpoints

## Autenticação

### Usuário autenticado

```http
GET /auth/me
```

Retorna informações do usuário autenticado.

Exemplo:

```json
{
  "email": "usuario@email.com",
  "perfil": "TUTOR"
}
```

---

#  Endpoints de Pets

## Listar todos os pets

```http
GET /pets
```

Permitido para:

```text
ADMIN
VETERINARIO
```

---

## Listar os próprios pets

```http
GET /pets/meus
```

Retorna os pets associados ao usuário autenticado.

O endpoint é utilizado principalmente pelo perfil TUTOR.

---

## Listar pets críticos

```http
GET /pets/criticos
```

Retorna os pets classificados com risco:

```text
ALTO
```

---

## Resumo de risco

```http
GET /pets/resumo-risco
```

Retorna a quantidade de animais em cada nível de risco.

---

## Buscar pets por risco

```http
GET /pets/risco/{nivelRisco}
```

---

## Buscar pets por espécie

```http
GET /pets/especie/{especie}
```

---

## Cadastrar pet

```http
POST /pets
```

Permitido para:

```text
ADMIN
VETERINARIO
```

Exemplo:

```json
{
  "nome": "Thor",
  "idade": 4,
  "especie": "Cachorro",
  "nivelRisco": "MEDIO",
  "tutor": {
    "email": "tutor@email.com"
  }
}
```

---

## Atualizar pet

```http
PUT /pets/{id}
```

Permitido para:

```text
ADMIN
VETERINARIO
```

---

## Excluir pet

```http
DELETE /pets/{id}
```

Permitido somente para:

```text
ADMIN
```

---

#  Endpoints de Tutores

## Listar tutores

```http
GET /tutores
```

Permitido para:

```text
ADMIN
VETERINARIO
```

---

## Buscar tutor por ID

```http
GET /tutores/{id}
```

Permitido para:

```text
ADMIN
VETERINARIO
```

---

## Cadastrar tutor

```http
POST /tutores
```

Permitido somente para:

```text
ADMIN
```

---

## Atualizar tutor

```http
PUT /tutores/{id}
```

Permitido somente para:

```text
ADMIN
```

---

## Excluir tutor

```http
DELETE /tutores/{id}
```

Permitido somente para:

```text
ADMIN
```

---

#  Autenticação

O sistema utiliza **HTTP Basic Authentication**.

O fluxo de autenticação funciona da seguinte forma:

```text
Usuário informa e-mail e senha
          ↓
Spring Security
          ↓
CustomUserDetailsService
          ↓
TutorRepository
          ↓
Busca usuário pelo e-mail
          ↓
BCrypt valida a senha
          ↓
Perfil do usuário é carregado
          ↓
Spring Security verifica a permissão da rota
```

Caso as credenciais estejam incorretas, a aplicação retorna:

```text
HTTP 401 Unauthorized
```

Caso o usuário esteja autenticado, mas não possua permissão para determinada operação:

```text
HTTP 403 Forbidden
```

---

#  Proteção das Senhas

As senhas dos usuários são protegidas utilizando:

```text
BCryptPasswordEncoder
```

As senhas não são retornadas nas respostas JSON da API.

Isso evita que o hash da senha seja exposto ao consultar os dados dos tutores.

---

#  Configuração do Banco de Dados

Por segurança, o usuário e a senha do Oracle não ficam diretamente armazenados no código-fonte.

O arquivo:

```text
src/main/resources/application.properties
```

utiliza variáveis de ambiente.

Exemplo:

```properties
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
```

No Windows, as variáveis podem ser configuradas através do CMD:

```cmd
setx DB_USERNAME "SEU_USUARIO"
setx DB_PASSWORD "SUA_SENHA"
```

Depois de configurar as variáveis, é necessário fechar e abrir novamente o terminal e a IDE para que os novos valores sejam carregados.

Cada computador que executar o projeto deverá possuir suas próprias variáveis de ambiente configuradas.

---

#  Como Executar o Backend

## 1. Clonar o projeto

```bash
git clone (https://github.com/kaique-masc/SUPERNOVAVET_JAVA_ADVANCED.git)
```

## 2. Entrar na pasta

```bash
cd SUPERNOVAVET_JAVA_ADVANCED
```

---

## 3. Configurar o Oracle

Configure:

```text
DB_USERNAME
DB_PASSWORD
```

com as credenciais necessárias para acessar o banco Oracle.

---

## 4. Instalar as dependências

O projeto utiliza Maven.

Execute:

```bash
mvn clean install
```

Ou utilize o Maven integrado da IDE.

---

## 5. Executar a aplicação

Execute a classe principal do Spring Boot pela IDE.

Também é possível utilizar:

```bash
mvn spring-boot:run
```

Quando a aplicação iniciar corretamente, o backend ficará disponível em:

```text
http://localhost:8080
```

---

#  Como Executar o Frontend

O frontend está localizado na pasta:

```text
frontend
```

Entre nela:

```bash
cd frontend
```

Instale as dependências:

```bash
npm install
```

Depois execute:

```bash
npm run dev
```

O Vite mostrará no terminal o endereço da aplicação.

Normalmente:

```text
http://localhost:5173
```

O backend precisa estar executando em:

```text
http://localhost:8080
```

para que o frontend consiga acessar a API.

---

#  Funcionamento do Frontend

O frontend identifica o usuário autenticado através do endpoint:

```http
GET /auth/me
```

Depois disso, a interface é adaptada de acordo com o perfil retornado pelo backend.

---

## Frontend do ADMIN

O administrador possui acesso a:

- Dashboard
- Pets
- Pets críticos
- Tutores

Também possui acesso às ações administrativas disponíveis.

---

## Frontend do VETERINARIO

O veterinário possui acesso a:

- Dashboard
- Pets
- Pets críticos
- Tutores

Pode cadastrar e editar pets.

As ações administrativas restritas ao ADMIN não ficam disponíveis.

---

## Frontend do TUTOR

O tutor possui acesso a:

- Dashboard
- Seus pets
- Seus pets críticos

O TUTOR não possui acesso à tela de gerenciamento de tutores.

Na tela de pets, o frontend utiliza:

```http
GET /pets/meus
```

em vez de:

```http
GET /pets
```

Dessa forma, somente os animais vinculados ao usuário autenticado são apresentados.

O Dashboard do TUTOR também é calculado utilizando somente seus próprios animais.

A tela de pets críticos do TUTOR considera somente seus próprios animais classificados com nível de risco:

```text
ALTO
```

---

#  Integração Frontend e Backend

O frontend realiza requisições HTTP para a API Spring Boot.

Durante o desenvolvimento, o Vite utiliza proxy para encaminhar as requisições para:

```text
http://localhost:8080
```

Exemplo de fluxo:

```text
Frontend React
      ↓
GET /api/pets/meus
      ↓
Vite Proxy
      ↓
Spring Boot
      ↓
Spring Security
      ↓
PetController
      ↓
PetRepository
      ↓
Oracle Database
```

---

#  Testes Realizados

Durante o desenvolvimento foram realizados testes dos diferentes níveis de acesso.

Entre os cenários testados estão:

### ADMIN

- Autenticação com sucesso
- Consulta de pets
- Consulta de tutores
- Cadastro de tutor
- Atualização de tutor
- Exclusão de tutor
- Exclusão de pet

### VETERINARIO

- Autenticação com sucesso
- Consulta de todos os pets
- Consulta de tutores
- Cadastro de pet
- Atualização de pet
- Bloqueio de operações administrativas

### TUTOR

- Autenticação com sucesso
- Acesso ao endpoint `/pets/meus`
- Visualização somente dos próprios pets
- Bloqueio do endpoint global `/pets`
- Bloqueio da listagem de tutores
- Bloqueio de operações de cadastro, edição e exclusão

### Validação

Também foram realizados testes enviando dados inválidos através do Swagger.

A API retornou:

```text
HTTP 400 Bad Request
```

com as mensagens correspondentes aos campos inválidos.

---

#  Códigos HTTP Utilizados

A aplicação utiliza códigos HTTP para representar o resultado das requisições.

Alguns exemplos:

| Código | Significado |
|---|---|
| 200 | Requisição realizada com sucesso |
| 201 | Recurso criado com sucesso |
| 204 | Operação realizada sem conteúdo de retorno |
| 400 | Dados inválidos |
| 401 | Usuário não autenticado ou credenciais incorretas |
| 403 | Usuário autenticado sem permissão |
| 404 | Recurso não encontrado |

---

#  Uso de Inteligência Artificial

Ferramentas de inteligência artificial foram utilizadas como apoio durante o desenvolvimento do projeto.

A IA foi utilizada para:

- Auxílio na estruturação do projeto
- Apoio na implementação de funcionalidades
- Explicação e correção de erros
- Revisão de código
- Apoio na configuração do Spring Security
- Apoio na implementação do Flyway
- Apoio na integração entre frontend e backend
- Apoio na criação da documentação

As implementações foram analisadas, testadas e adaptadas de acordo com as necessidades e regras do projeto.

---

#  Considerações Finais

O **SuperNova VET** apresenta uma aplicação web completa integrando frontend, backend e banco de dados.

O projeto implementa:

- Java com Spring Boot
- API REST
- Oracle Database
- Spring Data JPA
- Flyway
- Spring Security
- BCrypt
- Três perfis de acesso
- Controle de autorização por perfil
- Bean Validation
- Tratamento de erros
- Swagger/OpenAPI
- React + Vite
- Integração frontend e backend
- Funcionalidades além do CRUD
- Visualização dos próprios pets pelo tutor

A implementação do perfil **TUTOR** garante que o usuário tenha acesso somente aos animais associados à própria conta, enquanto os perfis **VETERINARIO** e **ADMIN** possuem permissões superiores de acordo com suas responsabilidades dentro do sistema.

# SUPERNOVAVET JAVA ADVANCED

API REST desenvolvida com Spring Boot para gerenciamento veterinário de pets e tutores.

## Tecnologias Utilizadas

- Java 21
- Spring Boot
- Spring Data JPA
- Oracle Database
- Maven
- Swagger / OpenAPI

---

## Funcionalidades

- Cadastro de pets
- Cadastro de tutores
- Listagem de registros
- Busca por ID
- Atualização de dados
- Exclusão de registros
- Integração com Oracle
- Documentação automática com Swagger

---

## Estrutura do Projeto

```text
src
 ├── controller
 ├── entity
 ├── repository
 ├── service
 ├── config
 └── dto
```

---

## Como Executar o Projeto

### 1. Clonar repositório

```bash
git clone https://github.com/kaique-masc/SUPERNOVAVET_JAVA_ADVANCED.git
```

---

### 2. Abrir no IntelliJ IDEA

Importe o projeto como Maven Project.

---

### 3. Configurar Oracle Database

No arquivo:

```properties
src/main/resources/application.properties
```

Configure:

```properties
spring.datasource.url=jdbc:oracle:thin:@oracle.fiap.com.br:1521:orcl
spring.datasource.username=rm563982
spring.datasource.password=051206
spring.jpa.hibernate.ddl-auto=update
```

---

### 4. Rodar aplicação

Execute a classe principal:

```text
NovamonitorApplication.java
```

---

## Swagger

Acesse:

```text
http://localhost:8080/swagger-ui/index.html
```

---

## Endpoints Principais

## Pets

| Método | Endpoint |
|--------|----------|
| GET | /pets |
| GET | /pets/{id} |
| POST | /pets |
| PUT | /pets/{id} |
| DELETE | /pets/{id} |

---

## Tutores

| Método | Endpoint |
|--------|----------|
| GET | /tutores |
| GET | /tutores/{id} |
| POST | /tutores |
| PUT | /tutores/{id} |
| DELETE | /tutores/{id} |

---

## Exemplo JSON - Pet

```json
{
  "nome": "Thor",
  "idade": 5,
  "raca": "Golden Retriever",
  "nivelRisco": "ALTO"
}
```

---

## Exemplo JSON - Tutor

```json
{
  "nome": "Kaique",
  "email": "kaique@email.com",
  "telefone": "11999999999"
}
```

---

## Autor

Projeto acadêmico desenvolvido utilizando Java Spring Boot + Oracle Database.

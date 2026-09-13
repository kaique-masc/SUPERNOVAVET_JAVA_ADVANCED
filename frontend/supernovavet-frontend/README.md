# SuperNova VET - Frontend Atualizado

Frontend React + Vite integrado ao backend Spring Boot.

## Perfis

### ADMIN
- Dashboard global
- Lista todos os pets
- Cadastra, edita e exclui pets
- Lista, cadastra, edita e exclui tutores
- Visualiza pets críticos globais

### VETERINARIO
- Dashboard global
- Lista todos os pets
- Cadastra e edita pets
- Consulta tutores
- Visualiza pets críticos globais
- Não exclui pets
- Não altera/exclui tutores

### TUTOR
- Dashboard calculado somente com os próprios pets
- Tela Pets usa `GET /pets/meus`
- Tela Pets críticos mostra somente os próprios pets com risco ALTO
- Não acessa a tela de tutores
- Não possui ações de cadastro, edição ou exclusão de pets

## Backend esperado

O frontend utiliza:

- `GET /auth/me`
- `GET /pets`
- `GET /pets/meus`
- `GET /pets/resumo-risco`
- `GET /pets/criticos`
- `POST /pets`
- `PUT /pets/{id}`
- `DELETE /pets/{id}`
- `GET /tutores`
- `POST /tutores`
- `PUT /tutores/{id}`
- `DELETE /tutores/{id}`

O backend deve estar em `http://localhost:8080`.

## Executar

```bash
npm install
npm run dev
```

Abra o endereço mostrado pelo Vite, normalmente:

```text
http://localhost:5173
```

## Observação

O projeto usa Basic Auth e guarda as credenciais em `sessionStorage` somente para fins acadêmicos/demonstração.

## Tecnologias Utilizadas

- **Java 21**
- **Spring Boot 3**
- **Spring Data JPA**
- **Banco de Dados PostgreSQL** (produção/desenvolvimento)
- **Banco de Dados H2** (testes)
- **Docker** e **Docker Compose** (para ambiente de desenvolvimento)
- **Maven** (gerenciamento de dependências)

## Estrutura do Projeto

- java: Código-fonte da aplicação
- resources: Arquivos de configuração e scripts SQL
- java: Testes automatizados
- docker-compose.yml: Subida de containers PostgreSQL e PgAdmin
- pom.xml: Gerenciamento de dependências Maven

## Instalação e Execução

### 1. Clonar o repositório

```sh
git clone https://github.com/marcionavarro/devsuperior
cd intensicao-java-spring
```

### 2. Subir o banco de dados com Docker

Certifique-se de ter o Docker instalado. Execute:

```sh
docker-compose up -d
```

O PostgreSQL estará disponível na porta `5433` e o PgAdmin na porta `5050`.

### 3. Configurar o banco de dados

O sistema já está configurado para conectar no banco criado pelo Docker. Veja o arquivo application-dev.properties.

### 4. Rodar a aplicação

No terminal, execute:

```sh
./mvnw spring-boot:run
```

Ou, se estiver no Windows:

```sh
mvnw.cmd spring-boot:run
```

A API estará disponível em `http://localhost:8080`.

## Endpoints Principais

- `GET /games` — Lista todos os jogos
- `GET /games/{id}` — Detalhes de um jogo
- `GET /lists` — Lista todas as listas de jogos
- `GET /lists/{listId}/games` — Jogos de uma lista específica
- `POST /lists/{listId}/replacement` — Reordena jogos em uma lista

## Observações

- O projeto utiliza variáveis de ambiente para perfis e CORS. Veja application.properties.
- O script `import.sql` popula o banco com dados iniciais.

---

Desenvolvido no Intensivão Java Spring - DevSuperior 🚀
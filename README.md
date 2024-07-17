# API Rest para Planejador de Viagens (Plann.er)

## Descrição
Esse é um projeto de uma API para planejador de viagens, baseado no projeto do evento NLW Journey na trilha Java. Armazena os dados do dono da viagem, convidados, dados da viagem, atividades e links.

## Funcionalidades

- Cadastro de dono da viagem
- Cadastro de convidados com email
- Cadastro da viagem
- Confirmação da viagem por email, que após confirmar, redireciona para uma página html de agradecimento pela viagem
- Confirmação de cada convidado, utilizando seu nome e email cadastrado pelo dono da viagem, enviando um email de Presença Confirmada
- Cadastro de atividade
- Marcação de atividade como completa ou pendente
- Cadastro de links importantes
- Validação de emails e domínios
- Validação de links

## Tecnologias

- Spring Framework
- Java 21
- Maven
- PostgreSQL
- Docker e Docker Compose
- Thymeleaf
- JavaMailSender
- Spring Webflux

## Documentação da API

### Viagem

#### Criação da Viagem

```http
POST /trips
```

##### Request:

| Body               | Tipo           | Descrição                                                             |
| :----------------- | :------------- | :-------------------------------------------------------------------- |
| `destination`      | `string`       | **Obrigatório**. Destino da viagem (Ex: São Paulo, SP)                |
| `starts_at`        | `string`       | **Obrigatório**. Data de início da viagem (Ex: "2024-08-22T20:50:00") |
| `ends_at`          | `string`       | **Obrigatório**. Data de fim da viagem (Ex: "2024-08-30T10:30:00")    |
| `emails_to_invite` | `string Array` | **Obrigatório**. Emails dos convidados                                |
| `owner_name`       | `string`       | **Obrigatório**. Nome do dono da viagem                               |
| `owner_email`      | `string`       | **Obrigatório**. Email do dono da viagem                              |

##### Response:

- Response 200 - retornando Id da viagem, em formato UUID
- Response 401 - Ao inserir data de início superior a data de fim, inserir data de fim anterior a data de início ou email do dono da viagem inválido

#

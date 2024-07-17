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
- Response 401 - Ao inserir data de início superior a data de fim, inserir data de fim anterior a data de início
- Response 401 - Email do dono da viagem inválido

#

#### Ler Viagem

```http
GET /trips/{id}
```

**`id` ID da Viagem a ser consultada**

##### Response:

- Response 200 (application/json)
- Response 404 - Viagem não encontrada

  - Body
      
          {
            "tripId": "1b61512c-4dc1-4e07-a984-a8f0b1e64802",
            "ownerName": "Usuário",
            "ownerEmail": "email@email.com",
            "isConfirmedTrip": false,
            "startsAt": "2024-08-25T21:51:54.7342",
            "endsAt": "2024-08-31T21:51:54.7342",
            "participants": [
              {
                "id": "4c381190-9292-4787-a34c-3c51dc071d93",
                "isConfirmedParticipant": false,
                "participantName": "",
                "participantEmail": "email2@email.com"
              }
            ],
            "activities": [
              {
                "id": "4d5fd624-5346-473d-9d5a-c434e7bba23f",
                "isCompleted": false,
                "activityName": "Jogar tênis",
                "activityDate": "2024-08-27",
                "activityHour": "17:30:00"
              }
            ],
            "links": [
              {
                "id": "4a5d6b07-6522-459b-a80c-e30b138277c1",
                "titleLink": "Reserva AirBnB",
                "url": "https://www.airbnb.com.br/104700012"
              }
            ]
          }

#

#### Atualizar Viagem

```http
PUT /trips/{id}
```

**`id` ID da Viagem a ser atualizada**

##### Request:

| Body               | Tipo           | Descrição                                                                  |
| :----------------- | :------------- | :------------------------------------------------------------------------- |
| `destination`      | `string`       | **Obrigatório**. Novo destino da viagem (Ex: Coritiba, PR)                 |
| `starts_at`        | `string`       | **Obrigatório**. Nova data de início da viagem (Ex: "2024-08-22T20:50:00") |
| `ends_at`          | `string`       | **Obrigatório**. Nova data de fim da viagem (Ex: "2024-08-30T10:30:00")    |

OBS: Cada parâmetro é opcional, então poderá atualizar qualquer um, seja dois parâmetros, um parâmetro, etc.

##### Response:

- Response 200 - retornando Id da viagem, em formato UUID
- Response 401 - Ao inserir data de início superior a data de fim ou inserir data de fim anterior a data de início
- Response 404 - Viagem não encontrada

#

#### Confirmar Viagem ou Confirmar Convidado

```http
POST /trips/{id}/confirm
```
**`id` ID da Viagem a ser confirmada**

##### Filtros de buscas (Obrigatórios)

| queryParams      |   Tipo    |                              Descrição |
| :--------------- | :-------: | -------------------------------------: |
| `confirmer`      | `boolean` | **false** - se for para confirmar a viagem (enviando email de confirmação para o dono da viagem) / **true** - caso viagem estiver confirmada (útil para confirmar convidado ou enviar email de "Viagem Confirmada" direto para o dono da viagem. **Obs: o envio do email de "Viagem Confirmada" é automático, não há necessidade de utilização para esse fim.** |
| `participantReq` | `boolean` | **false** - se não for para confirmar convidado, apenas confirmar viagem / **true** - caso for confirmar convidado, **enviar request com dados do convidado** |

Ex (Confirmar Viagem):

```http
POST /trips/{id}/confirm?confirmer=false&participantReq=false
```

Ex (Confirmar Convidado):

```http
POST /trips/{id}/confirm?confirmer=true&participantReq=true
```

##### Request (Confirmar convidado):

| Body        | Tipo           | Descrição                                          |
| :---------- | :------------- | :------------------------------------------------- |
| `name`      | `string`       | **Obrigatório**. Nome do convidado                 |
| `email`     | `string`       | **Obrigatório**. Email do convidado para validação |

##### Response:

- Response 200 - "To confirm..." - Confirmar viagem por email
- Response 200 - "Trip confirmed!" - Viagem confirmada
- Resoinse 200 - "Participant confirmed!" - Convidado confirmado 
- Response 401 - Request nula (Caso participantReq estiver **true**)
- Response 401 - Email do convidado inválido
- Response 404 - Viagem não encontrada

#

#### Ler Convidados

```http
GET /trips/{id}/participants
```

**`id` ID da Viagem a ser consultada**

##### Response:

- Response 200 (application/json)
- Response 404 - Viagem não encontrada

  - Body
    
          [
            {
              "id": "4c381190-9292-4787-a34c-3c51dc071d93",
              "isConfirmedParticipant": true,
              "participantName": "Usuário 1",
              "participantEmail": "email2@email.com"
            },
            {
              "id": "4c381190-9292-4787-a34c-3c51dc071d94",
              "isConfirmedParticipant": true,
              "participantName": "Usuário 2",
              "participantEmail": "email3@email.com"
            },
          ]

#

#### Criar Atividade

```http
POST /trips/{id}/activities
```

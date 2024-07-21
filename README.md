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
- Response 400 - Ao inserir data de início superior a data de fim, inserir data de fim anterior a data de início
- Response 400 - Email do dono da viagem inválido

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
- Response 400 - Ao inserir data de início superior a data de fim ou inserir data de fim anterior a data de início
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
- Response 400 - Request nula (Caso participantReq estiver **true**)
- Response 400 - Email do convidado inválido
- Response 404 - Viagem não encontrada

#

#### Ler Convidados

```http
GET /trips/{id}/participants
```

**`id` ID da Viagem para consultar convidados**

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

**`id` ID da Viagem para criar atividades**

##### Request:

| Body            | Tipo           | Descrição                                            |
| :-------------- | :------------- | :--------------------------------------------------- |
| `name`          | `string`       | **Obrigatório**. Nome da atividade                   |
| `activity_date` | `string`       | **Obrigatório**. Dia da atividade (Ex: "2024-08-22") |
| `activity_hour` | `string`       | **Obrigatório**. Hora da atividade (Ex: "10:30:00")  |

##### Response:

- Response 200
- Response 400 - Data da atividade anterior a data da viagem ou data da atividade superior a data de fim viagem
- Response 400 - Request nula
- Response 404 - Viagem não encontrada

#

#### Ler Atividades

```http
GET /trips/{id}/activities
```

**`id` ID da Viagem para consultar atividades**

##### Response:

- Response 200 (application/json)
- Response 404 - Viagem não encontrada

  - Body
    
          [
            {
              "id": "4d5fd624-5346-473d-9d5a-c434e7bba23f",
              "isCompleted": false,
              "activityName": "Jogar tênis",
              "activityDate": "2024-08-27",
              "activityHour": "17:30:00"
            },
            {
              "id": "4d5fd624-5346-473d-9d5a-c434e7dsa20f",
              "isCompleted": true,
              "activityName": "Ir à piscina",
              "activityDate": "2024-08-27",
              "activityHour": "18:00:00"
            }
          ]

#

#### Completar ou descompletar Atividade

```http
GET /trips/activities/{activityId}/complete
```

**`activityId` ID da atividade para completar ou descompletar**

##### Filtros de buscas (Obrigatórios)

| queryParams      |   Tipo    |                                                           Descrição |
| :--------------- | :-------: | ------------------------------------------------------------------: |
| `uncomplete`     | `boolean` | **false** - completar atividade / **true** - descompletar atividade |

##### Response:

- Response 200 - "Activity uncompleted!" - Atividade descompletada
- Response 200 - "Activity completed!" - Atividade completada
- Response 404 - Atividade não encontrada

#

#### Criar Link

```http
POST /trips/{id}/links
```

**`id` ID da viagem para criar link**

##### Request:

| Body         | Tipo           | Descrição                     |
| :----------- | :------------- | :---------------------------- |
| `title_link` | `string`       | **Obrigatório**. Nome do link |
| `url`        | `string`       | **Obrigatório**. Url do link  |

##### Response:

- Response 200 - retornando Id do link, em formato UUID
- Response 400 - Request nula
- Response 400 - Url do link inválida
- Response 404 - Viagem não encontrada

#

#### Ler Links

```http
GET /trips/{id}/links
```

**`id` ID da viagem para consultar links**

##### Response:

- Response 200 (application/json)
- Response 404 - Viagem não encontrada

  - Body
    
          [
            {
              "id": "4a5d6b07-6522-459b-a80c-e30b138277c1",
              "titleLink": "Reserva AirBnB",
              "url": "https://www.airbnb.com.br/104700012"
            }
          ]

#

## Instalação (Recomendo o uso da IDE Intellij IDEA para execução do projeto. Já que a instalação pelo VS Code é um tanto complexa. Além de executar a instalação em um ambiente Ubuntu ou usar o WSL)

1. Clone o repositório:
   ```sh
   git clone https://github.com/H3nriqueL1ma/spring-todolist-api.git
    ```
2. Configure um banco de dados PostgreSQL, ou qualquer banco de sua escolha, se atentando a mudar a dependência de Driver Postgres para um Driver do banco escolhido.
3. Atualize o arquivo 'application.properties' com as configurações do seu banco de dados, e configurações do seu servidor SMTP.
4. Instale as dependências:
    ```sh
    sudo apt install openjdk-21-jdk
    ```
    ```sh
    sudo apt install maven
    ```
5. Monte o pacote:
    ```sh
    mvn clean package -DskipTests
    ```
7. Execute o arquivo .jar:
    ```sh
    java -jar target/api-0.0.1-SNAPSHOT.jar
    ```
    
## Licença

Distribuído sob a licença MIT. Veja `LICENSE` para mais informações.

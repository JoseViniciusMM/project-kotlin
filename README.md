# 🎟️ Fila Cidadã API

![Kotlin](https://img.shields.io/badge/kotlin-%237F52FF.svg?style=for-the-badge&logo=kotlin&logoColor=white)
![Ktor](https://img.shields.io/badge/Ktor-%23087CFA.svg?style=for-the-badge&logo=ktor&logoColor=white)
![MongoDB](https://img.shields.io/badge/MongoDB-%234ea94b.svg?style=for-the-badge&logo=mongodb&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-black?style=for-the-badge&logo=JSON%20web%20tokens)
![Gradle](https://img.shields.io/badge/Gradle-02303A.svg?style=for-the-badge&logo=Gradle&logoColor=white)

Uma API RESTful robusta e escalável desenvolvida em **Kotlin** com o framework **Ktor**. O sistema foi projetado para atuar como o motor central (core business) de uma plataforma de gestão de filas, permitindo controle multicamadas de instituições, emissão dinâmica de senhas e atendimento hierárquico.

---

## 🚀 Funcionalidades Principais (Features)

* **🏢 Arquitetura Multi-Tenant (Multi-Instituição):** Isolamento lógico de dados. Cada instituição possui suas próprias filas, atendentes e configurações, sem vazamento de dados entre elas.
* **🔐 Autenticação e Segurança Avançada:** Implementação completa de **JWT** (Access e Refresh Tokens), com fluxos de recuperação de senha, reenvio de confirmação e introspecção de token.
* **🛡️ Controle de Acesso Baseado em Papéis (RBAC):** Proteção granular de rotas baseada em 4 níveis de privilégios:
    * `ADMIN_PLATAFORMA`: Controle total do sistema e aprovação de instituições.
    * `ADMIN_INSTITUICAO`: Gerenciamento de filas e atendentes do seu próprio estabelecimento.
    * `ATENDENTE`: Permissão focada na máquina de estados das senhas (chamar, finalizar, cancelar).
    * `CIDADAO`: Acesso de leitura e autoemissão de senhas.
* **🎫 Máquina de Estados de Senhas:** Fluxo completo de atendimento, do momento que a senha é gerada até sua finalização ou cancelamento.
* **📱 Emissão via QR Code:** Suporte a geração de senhas públicas sem necessidade de autenticação (Fast-pass) usando QR Codes criptografados.
* **🌐 Landing Pages Dinâmicas:** Endpoints dedicados para gerenciamento e exibição de páginas públicas institucionais (com slugs customizados e renderização HTML).

---

## 🛠️ Tecnologias e Arquitetura

O projeto adota uma arquitetura em camadas limpa (*Clean-like*), garantindo forte desacoplamento, responsabilidade única e alta testabilidade.

### Stack Tecnológico
* **Linguagem:** Kotlin
* **Framework Web:** Ktor Server (Netty)
* **Injeção de Dependência:** Koin
* **Banco de Dados:** MongoDB (Acesso via biblioteca assíncrona KMongo)
* **Serialização:** Kotlinx Serialization
* **Documentação:** Swagger UI / OpenAPI

### Padrão de Camadas
1. **Routes (Controllers):** Utiliza Extension Functions do Ktor para mapear os verbos HTTP. Nenhuma lógica de negócio reside aqui. O tráfego de dados é blindado pelo uso de **DTOs (Data Transfer Objects)** de Request e Response.
2. **Services:** Onde a "mágica" acontece. Regras de negócio, cálculos, validações de segurança e preparação de dados.
3. **Repositories:** Camada exclusiva de persistência. Abstrai a complexidade das queries BSON do MongoDB.
4. **Models:** Entidades ricas de domínio que representam as coleções do banco de dados.

---

## 📁 Estrutura de Diretórios

```text
src/main/kotlin/br/com/filacidada/
├── config/       # Módulos de Injeção de Dependência (Koin) e Configurações de JWT/DB
├── dtos/         # Contratos de I/O da API (Pastas /request e /response)
├── models/       # Entidades de Domínio e Enums de estado
├── plugins/      # Interceptors do Ktor (Security, CORS, ContentNegotiation, StatusPages)
├── repositories/ # Contratos (Interfaces) e Implementações do repositório KMongo
├── routes/       # Definição das rotas REST e proteção via `authorize()`
├── service/      # Casos de uso e lógica de negócio central
└── utils/        # Classes utilitárias, helpers de paginação e validações
```


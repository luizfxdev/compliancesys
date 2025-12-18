# 🚛 ComplianceSys - Sistema de Conformidade para Lei do Caminhoneiro

[![Java](https://img.shields.io/badge/Java-8+-orange?style=for-the-badge&logo=java)](https://www.oracle.com/java/)
[![Tomcat](https://img.shields.io/badge/Apache%20Tomcat-9.0-yellow?style=for-the-badge&logo=apache-tomcat)](http://tomcat.apache.org/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-17-blue?style=for-the-badge&logo=postgresql)](https://www.postgresql.org/)
[![Gradle](https://img.shields.io/badge/Gradle-9.2-green?style=for-the-badge&logo=gradle)](https://gradle.org/)
[![Gson](https://img.shields.io/badge/Gson-2.10.1-red?style=for-the-badge)](https://github.com/google/gson)
[![HikariCP](https://img.shields.io/badge/HikariCP-5.1.0-lightblue?style=for-the-badge)](https://github.com/brettwooldridge/HikariCP)
[![JUnit](https://img.shields.io/badge/JUnit-5.10-green?style=for-the-badge&logo=junit5)](https://junit.org/junit5/)
[![License](https://img.shields.io/badge/License-MIT-purple?style=for-the-badge)](LICENSE)

## 📋 Sobre o Projeto

**ComplianceSys** é uma API REST robusta desenvolvida para garantir a conformidade com a **Lei 13.103/2015 (Lei do Caminhoneiro)**. O sistema monitora jornadas de trabalho de motoristas profissionais, calcula tempos de direção e descanso, e previne violações das normas de segurança no transporte rodoviário.

### 🎯 Objetivos

- ✅ **Conformidade Legal**: Garantir que motoristas operem dentro dos limites estabelecidos pela lei
- 🛡️ **Segurança Operacional**: Prevenir fadiga do motorista através de monitoramento preciso
- 💰 **Evitar Multas**: Fornecer registros de auditoria precisos para fiscalizações
- 📱 **Integração Mobile**: API REST para comunicação com Gateway Mobile (GW Mobile)

### 🌟 Diferenciais

- 🕐 **Cálculos Temporais Precisos**: Uso da API `java.time` para manipulação imutável de datas
- 🔒 **Pool de Conexões**: HikariCP para performance e estabilidade
- 📊 **Serialização Segura**: Gson para comunicação JSON confiável
- 🧪 **Alta Cobertura de Testes**: JUnit 5 + AssertJ para validação de regras de negócio
- 🏗️ **Arquitetura em Camadas**: DAO, Service, Controller para manutenibilidade

---

## 📚 Documentação

- 📖 [Lei do Caminhoneiro - Detalhes](./docs/lei-13103-requirements.md)
- 🗄️ [Diagrama de Entidade-Relacionamento (DER)](https://dbdiagram.io/d/6943ec39e4bb1dd3a98e2931)

---

## 🏗️ Estrutura do Projeto
```
compliancesys/
├── build.gradle
├── settings.gradle
├── README.md
├── src/
│   ├── main/
│   │   ├── java/com/compliancesys/
│   │   │   ├── controller/         # Servlets REST
│   │   │   ├── dao/                # Data Access Objects
│   │   │   │   └── impl/
│   │   │   ├── model/              # Entidades de domínio
│   │   │   │   └── enums/
│   │   │   ├── service/            # Regras de negócio
│   │   │   │   └── impl/
│   │   │   ├── util/               # Utilitários (Gson, Validator, Time)
│   │   │   │   └── impl/
│   │   │   └── exception/          # Exceções customizadas
│   │   └── resources/
│   │       ├── database.properties
│   │       └── schema.sql
│   └── test/
│       └── java/com/compliancesys/
│           ├── dao/
│           ├── service/
│           └── util/
└── docs/
    ├── DER.png
    └── lei-13103-requirements.md
```

---

## 🚀 Tecnologias Utilizadas

### Backend
- **Java 8+**: Linguagem principal
- **Apache Tomcat 9.0**: Servidor de aplicação
- **PostgreSQL 17**: Banco de dados relacional
- **Gradle 9.2**: Gerenciador de dependências e build

### Bibliotecas
- **HikariCP 5.1.0**: Pool de conexões JDBC de alta performance
- **Gson 2.10.1**: Serialização/deserialização JSON
- **BCrypt 0.4**: Hash de senhas

### Testes
- **JUnit Jupiter 5.10.0**: Framework de testes
- **Mockito 5.6.0**: Mocking para testes unitários
- **AssertJ 3.25.3**: Assertions fluentes

---

## ⚙️ Pré-requisitos

- ☕ Java JDK 8 ou superior
- 🐘 PostgreSQL 17+
- 🐱 Apache Tomcat 9.0+
- 🔧 Gradle 9.2+ (ou use o wrapper `./gradlew`)

---

## 📦 Instalação e Configuração

### 1️⃣ Clone o Repositório
```bash
git clone https://github.com/seu-usuario/compliancesys.git
cd compliancesys
```

### 2️⃣ Configure o Banco de Dados

#### Criar o banco de dados:
```bash
sudo -u postgres psql
```
```sql
CREATE DATABASE compliancesys_db;
CREATE USER postgres WITH PASSWORD 'sua_senha';
GRANT ALL PRIVILEGES ON DATABASE compliancesys_db TO postgres;
\q
```

#### Configurar credenciais:

Edite `src/main/resources/database.properties`:
```properties
db.url=jdbc:postgresql://localhost:5432/compliancesys_db
db.username=postgres
db.password=sua_senha
db.driver=org.postgresql.Driver
db.hikari.maxPoolSize=10
db.hikari.minIdle=5
db.hikari.connectionTimeout=30000
db.hikari.idleTimeout=600000
db.hikari.maxLifetime=1800000
```

#### Executar o schema:
```bash
sudo -u postgres psql -d compliancesys_db -f src/main/resources/schema.sql
```

### 3️⃣ Inserir Dados de Teste
```bash
sudo -u postgres psql -d compliancesys_db
```
```sql
-- Inserir empresa
INSERT INTO companies (cnpj, legal_name, trading_name) 
VALUES ('12345678000100', 'Transportadora ABC Ltda', 'Transportadora ABC');

-- Inserir motorista
INSERT INTO drivers (company_id, name, cpf, license_number, license_category, birth_date, phone, email) 
VALUES (1, 'João Silva', '12345678900', 'ABC123456789', 'E', '1985-05-15', '83999999999', 'joao@abc.com');

-- Inserir veículo
INSERT INTO vehicles (plate, manufacturer, model, year, company_id) 
VALUES ('ABC1234', 'Scania', 'R450', 2023, 1);

\q
```

---

## 🏃 Executando o Projeto

### Build do Projeto
```bash
./gradlew clean war
```

### Deploy no Tomcat
```bash
cp build/libs/compliancesys.war /caminho/para/tomcat/webapps/
/caminho/para/tomcat/bin/startup.sh
```

### Verificar se está rodando
```bash
curl http://localhost:8080/compliancesys/api/journeys/
```

---

## 🧪 Executando Testes

### Rodar todos os testes:
```bash
./gradlew test
```

### Ver relatório de cobertura:
```bash
./gradlew test jacocoTestReport
```

O relatório será gerado em: `build/reports/jacoco/test/html/index.html`

---

## 📡 Endpoints da API

### 🚛 Journeys (Jornadas)

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| `POST` | `/api/journeys/` | Criar nova jornada |
| `GET` | `/api/journeys/` | Listar todas as jornadas |
| `GET` | `/api/journeys/{id}` | Buscar jornada por ID |
| `GET` | `/api/journeys/driver/{driverId}` | Buscar jornadas por motorista |
| `GET` | `/api/journeys/vehicle/{vehicleId}` | Buscar jornadas por veículo |
| `GET` | `/api/journeys/company/{companyId}` | Buscar jornadas por empresa |
| `GET` | `/api/journeys/driver/{driverId}/date/{date}` | Buscar jornada por motorista e data |
| `PUT` | `/api/journeys/{id}` | Atualizar jornada |
| `DELETE` | `/api/journeys/{id}` | Deletar jornada |

### 📝 Exemplo de Request - POST Journey
```json
{
  "driverId": 1,
  "vehicleId": 1,
  "companyId": 1,
  "journeyDate": "2025-12-17",
  "startLocation": "João Pessoa, PB",
  "totalDrivingTimeMinutes": 480,
  "totalRestTimeMinutes": 60,
  "complianceStatus": "COMPLIANT",
  "dailyLimitExceeded": false
}
```

---

## 📸 Testes da API (Thunder Client)

### ✅ CRUD Completo Testado

1. [POST - Criar Jornada](https://drive.google.com/file/d/1OGy4LPxk0N227Ly1q917eBDLSARyVPxy/view?usp=drive_link)
2. [GET - Listar Todas](https://drive.google.com/file/d/1h78VOq7o_x5WRz8DzhsgDQc5Qr6GQi6l/view?usp=drive_link)
3. [GET - Buscar por ID](https://drive.google.com/file/d/1_PrQg1QIDh71SQSJfqd3zdoz2OgTRKPh/view?usp=drive_link)
4. [GET - Por Motorista](https://drive.google.com/file/d/1RBwshSSU_sW_7_EQrnmzKIeaKMjw_YaZ/view?usp=drive_link)
5. [GET - Por Veículo](https://drive.google.com/file/d/1JxKREgVAj81nan_GpOBrYYsk7Sl9YWSE/view?usp=drive_link)
6. [GET - Por Empresa](https://drive.google.com/file/d/1OcYNtcGA9qqsb3pg0zSIdnY-2FDXFi2j/view?usp=drive_link)
7. [GET - Por Motorista e Data](https://drive.google.com/file/d/1EeVOQYk5mMUCBy36hqZmSzOpOzZmnN85/view?usp=drive_link)
8. [PUT - Atualizar](https://drive.google.com/file/d/1HB6oh_Qqt3JEoFClyaOaXiXg54nWSFz3/view?usp=drive_link)
9. [DELETE - Deletar](https://drive.google.com/file/d/1wxbZSVuQxEqQc2Uu-68CCjnwFM7o3P5N/view?usp=drive_link)

---

## 🎯 Regras de Negócio (Lei 13.103/2015)

- ⏱️ **Jornada máxima diária**: 8 horas de direção
- 🛑 **Descanso obrigatório**: 30 minutos a cada 4 horas de direção
- 📅 **Limite semanal**: 44 horas de trabalho
- 🚨 **Validações automáticas**: Sistema alerta violações em tempo real

Consulte a [documentação completa da lei](./docs/lei-13103-requirements.md) para detalhes.

---

## 🤝 Contribuindo

Contribuições são bem-vindas! Para contribuir:

1. Fork o projeto
2. Crie uma branch para sua feature (`git checkout -b feature/NovaFuncionalidade`)
3. Commit suas mudanças (`git commit -m 'Adiciona nova funcionalidade'`)
4. Push para a branch (`git push origin feature/NovaFuncionalidade`)
5. Abra um Pull Request

---

## 📄 Licença

Este projeto está sob a licença MIT. Veja o arquivo [LICENSE](LICENSE) para mais detalhes.

---

### 👨‍💻 Autor

**Luiz Felipe de Oliveira**

- GitHub: [@luizfxdev](https://github.com/luizfxdev)
- Linkedin: [in/luizfxdev](https://www.linkedin.com/in/luizfxdev)
- Portfólio: [luizfxdev.com.br](https://luizfxdev.com.br)

---

<div align="center">
  <sub>🚛 ComplianceSys - Dirigindo com Segurança e Conformidade 🛡️</sub>
</div>

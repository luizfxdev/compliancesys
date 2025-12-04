-- schema.sql para ComplianceSys - Sistema de Conformidade para Motoristas de Caminhão (Lei 13.103/2015)
-- Script DDL para PostgreSQL

-- Tabela: companies
CREATE TABLE companies (
    id SERIAL PRIMARY KEY,
    cnpj varchar(18) UNIQUE NOT NULL, -- Ajustado para 18 (XX.XXX.XXX/XXXX-XX)
    legal_name varchar(255) NOT NULL, -- Alinhado com POJO
    trading_name varchar(255),         -- Alinhado com POJO
    created_at timestamptz DEFAULT (now()),
    updated_at timestamptz DEFAULT (now()) -- Adicionado para alinhar com POJO
);

-- Tabela: drivers
CREATE TABLE drivers (
    id SERIAL PRIMARY KEY,
    name varchar(255) NOT NULL,
    cpf varchar(14) UNIQUE NOT NULL, -- Ajustado para 14 (XXX.XXX.XXX-XX)
    birth_date date NOT NULL,          -- Alinhado com POJO
    license_number varchar(20) UNIQUE NOT NULL, -- Alinhado com POJO
    -- company_id integer, -- Mantido comentado para alinhar com POJO atual (sem companyId) - Decisão de design
    created_at timestamptz DEFAULT (now()),
    updated_at timestamptz DEFAULT (now()) -- Adicionado para alinhar com POJO
);

-- Tabela: vehicles
CREATE TABLE vehicles (
    id SERIAL PRIMARY KEY,
    plate varchar(10) UNIQUE NOT NULL,
    model varchar(100) NOT NULL,
    year integer NOT NULL,
    company_id integer NOT NULL, -- Adicionado para alinhar com POJO Vehicle
    created_at timestamptz DEFAULT (now()),
    updated_at timestamptz DEFAULT (now()) -- Adicionado para alinhar com POJO
);

-- Tabela: time_records
CREATE TABLE time_records (
    id SERIAL PRIMARY KEY,
    driver_id integer NOT NULL,
    record_time timestamptz NOT NULL, -- Renomeado para alinhar com POJO
    event_type varchar(50) NOT NULL,   -- Alinhado com POJO (enum como string)
    location varchar(255),             -- Adicionado para alinhar com POJO TimeRecord
    -- vehicle_id integer, -- Removido para alinhar com POJO atual (sem vehicleId) - Decisão de design
    created_at timestamptz DEFAULT (now()),
    updated_at timestamptz DEFAULT (now()) -- Adicionado para alinhar com POJO
);

-- Tabela: journeys
CREATE TABLE journeys (
    id SERIAL PRIMARY KEY,
    driver_id integer NOT NULL,
    journey_date date NOT NULL,
    total_driving_time_minutes integer NOT NULL DEFAULT 0, -- Alinhado com POJO (Duration em minutos)
    total_rest_time_minutes integer NOT NULL DEFAULT 0,    -- Alinhado com POJO (Duration em minutos)
    compliance_status varchar(50) NOT NULL DEFAULT 'PENDING', -- Alinhado com POJO (enum como string)
    daily_limit_exceeded boolean NOT NULL DEFAULT FALSE,   -- Adicionado para alinhar com POJO
    created_at timestamptz DEFAULT (now()),
    updated_at timestamptz DEFAULT (now()) -- Adicionado para alinhar com POJO
);

-- Tabela: compliance_audits
CREATE TABLE compliance_audits (
    id SERIAL PRIMARY KEY,
    journey_id integer NOT NULL,
    audit_date timestamptz NOT NULL, -- Renomeado para alinhar com POJO
    status varchar(50) NOT NULL,      -- Alinhado com POJO (enum como string)
    details text,                     -- Alinhado com POJO
    created_at timestamptz DEFAULT (now()),
    updated_at timestamptz DEFAULT (now()) -- Adicionado para alinhar com POJO
);

-- Tabela: mobile_communications
CREATE TABLE mobile_communications (
    id SERIAL PRIMARY KEY,
    record_id integer NOT NULL,
    send_timestamp timestamptz NOT NULL,
    send_success boolean NOT NULL,
    error_message text,
    created_at timestamptz DEFAULT (now()),
    updated_at timestamptz DEFAULT (now()) -- Adicionado para alinhar com POJO
);

-- Chaves Estrangeiras
-- ALTER TABLE drivers ADD FOREIGN KEY (company_id) REFERENCES companies (id); -- Mantido comentado
ALTER TABLE vehicles ADD FOREIGN KEY (company_id) REFERENCES companies (id) ON DELETE CASCADE; -- Adicionado
ALTER TABLE time_records ADD FOREIGN KEY (driver_id) REFERENCES drivers (id) ON DELETE CASCADE;
-- ALTER TABLE time_records ADD FOREIGN KEY (vehicle_id) REFERENCES vehicles (id); -- Removido
ALTER TABLE journeys ADD FOREIGN KEY (driver_id) REFERENCES drivers (id) ON DELETE CASCADE;
ALTER TABLE compliance_audits ADD FOREIGN KEY (journey_id) REFERENCES journeys (id) ON DELETE CASCADE;
ALTER TABLE mobile_communications ADD FOREIGN KEY (record_id) REFERENCES time_records (id) ON DELETE CASCADE;

-- Índices para otimização de performance
CREATE INDEX idx_timerecord_driver_timestamp ON time_records (driver_id, record_time);
-- CREATE INDEX idx_timerecord_vehicle_timestamp ON time_records (vehicle_id, event_timestamp); -- Removido
CREATE UNIQUE INDEX idx_journey_driver_date ON journeys (driver_id, journey_date);
CREATE INDEX idx_complianceaudit_journey_id ON compliance_audits (journey_id);
CREATE INDEX idx_mobilecomm_record_send ON mobile_communications (record_id, send_timestamp);
CREATE INDEX idx_mobilecomm_send_success ON mobile_communications (send_success);

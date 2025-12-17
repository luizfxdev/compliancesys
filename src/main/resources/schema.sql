-- schema.sql para ComplianceSys - Sistema de Conformidade para Motoristas de Caminhão (Lei 13.103/2015)
-- Script DDL para PostgreSQL

CREATE TABLE companies (
    id SERIAL PRIMARY KEY,
    cnpj varchar(18) UNIQUE NOT NULL,
    legal_name varchar(255) NOT NULL,
    trading_name varchar(255),
    created_at timestamptz DEFAULT (now()),
    updated_at timestamptz DEFAULT (now())
);

CREATE TABLE drivers (
    id SERIAL PRIMARY KEY,
    company_id integer,
    name varchar(255) NOT NULL,
    cpf varchar(14) UNIQUE NOT NULL,
    license_number varchar(20) UNIQUE NOT NULL,
    license_category varchar(10),
    license_expiration date,
    birth_date date NOT NULL,
    phone varchar(20),
    email varchar(255) UNIQUE,
    created_at timestamptz DEFAULT (now()),
    updated_at timestamptz DEFAULT (now())
);

CREATE TABLE vehicles (
    id SERIAL PRIMARY KEY,
    plate varchar(10) UNIQUE NOT NULL,
    manufacturer varchar(100),
    model varchar(100) NOT NULL,
    year integer NOT NULL,
    company_id integer NOT NULL,
    created_at timestamptz DEFAULT (now()),
    updated_at timestamptz DEFAULT (now())
);

CREATE TABLE time_records (
    id SERIAL PRIMARY KEY,
    driver_id integer NOT NULL,
    record_time timestamptz NOT NULL,
    event_type varchar(50) NOT NULL,
    location varchar(255),
    created_at timestamptz DEFAULT (now()),
    updated_at timestamptz DEFAULT (now())
);

CREATE TABLE journeys (
    id SERIAL PRIMARY KEY,
    driver_id integer NOT NULL,
    journey_date date NOT NULL,
    total_driving_time_minutes integer NOT NULL DEFAULT 0,
    total_rest_time_minutes integer NOT NULL DEFAULT 0,
    compliance_status varchar(50) NOT NULL DEFAULT 'PENDING',
    daily_limit_exceeded boolean NOT NULL DEFAULT FALSE,
    created_at timestamptz DEFAULT (now()),
    updated_at timestamptz DEFAULT (now())
);

CREATE TABLE compliance_audits (
    id SERIAL PRIMARY KEY,
    journey_id integer NOT NULL,
    driver_id integer,
    audit_date date NOT NULL,
    audit_timestamp timestamptz,
    status varchar(50) NOT NULL,
    violations text,
    total_work_duration integer DEFAULT 0,
    max_continuous_driving integer DEFAULT 0,
    auditor_name varchar(255),
    notes text,
    details text,
    created_at timestamptz DEFAULT (now()),
    updated_at timestamptz DEFAULT (now())
);

CREATE TABLE mobile_communications (
    id SERIAL PRIMARY KEY,
    record_id integer NOT NULL,
    send_timestamp timestamptz NOT NULL,
    send_success boolean NOT NULL,
    error_message text,
    created_at timestamptz DEFAULT (now()),
    updated_at timestamptz DEFAULT (now())
);

ALTER TABLE drivers ADD FOREIGN KEY (company_id) REFERENCES companies (id) ON DELETE SET NULL;
ALTER TABLE vehicles ADD FOREIGN KEY (company_id) REFERENCES companies (id) ON DELETE CASCADE;
ALTER TABLE time_records ADD FOREIGN KEY (driver_id) REFERENCES drivers (id) ON DELETE CASCADE;
ALTER TABLE journeys ADD FOREIGN KEY (driver_id) REFERENCES drivers (id) ON DELETE CASCADE;
ALTER TABLE compliance_audits ADD FOREIGN KEY (journey_id) REFERENCES journeys (id) ON DELETE CASCADE;
ALTER TABLE compliance_audits ADD FOREIGN KEY (driver_id) REFERENCES drivers (id) ON DELETE SET NULL;
ALTER TABLE mobile_communications ADD FOREIGN KEY (record_id) REFERENCES time_records (id) ON DELETE CASCADE;

CREATE INDEX idx_driver_company_id ON drivers (company_id);
CREATE INDEX idx_driver_email ON drivers (email);
CREATE INDEX idx_vehicle_company_id ON vehicles (company_id);
CREATE INDEX idx_timerecord_driver_timestamp ON time_records (driver_id, record_time);
CREATE UNIQUE INDEX idx_journey_driver_date ON journeys (driver_id, journey_date);
CREATE INDEX idx_complianceaudit_journey_id ON compliance_audits (journey_id);
CREATE INDEX idx_complianceaudit_driver_id ON compliance_audits (driver_id);
CREATE INDEX idx_mobilecomm_record_send ON mobile_communications (record_id, send_timestamp);
CREATE INDEX idx_mobilecomm_send_success ON mobile_communications (send_success);
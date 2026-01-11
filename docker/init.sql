-- Crear bases de datos
CREATE DATABASE clientes_db;
CREATE DATABASE cuentas_db;

-- Crear usuario admin
CREATE USER admin WITH PASSWORD 'admin';
GRANT ALL PRIVILEGES ON DATABASE clientes_db TO admin;
GRANT ALL PRIVILEGES ON DATABASE cuentas_db TO admin;

\c clientes_db
GRANT ALL ON SCHEMA public TO admin;

\c cuentas_db
GRANT ALL ON SCHEMA public TO admin;
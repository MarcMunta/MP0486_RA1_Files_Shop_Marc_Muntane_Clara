-- =====================================================
-- Script SQL para crear las tablas de la tienda
-- Base de datos: shop
-- Autor: Marc Muntané Clarà
-- =====================================================

-- Crear la base de datos si no existe
CREATE DATABASE IF NOT EXISTS shop;

-- Usar la base de datos
USE shop;

-- =====================================================
-- Tabla: inventory
-- Almacena los productos actuales del inventario
-- =====================================================
CREATE TABLE IF NOT EXISTS inventory (
    id INT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    wholesalerPrice DOUBLE NOT NULL,
    available BOOLEAN DEFAULT TRUE,
    stock INT DEFAULT 0
);

-- =====================================================
-- Tabla: historical_inventory
-- Almacena snapshots históricos del inventario
-- =====================================================
CREATE TABLE IF NOT EXISTS historical_inventory (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_product INT NOT NULL,
    name VARCHAR(100) NOT NULL,
    wholesalerPrice DOUBLE NOT NULL,
    available BOOLEAN DEFAULT TRUE,
    stock INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =====================================================
-- Tabla: employee
-- Almacena los empleados para autenticación
-- =====================================================
CREATE TABLE IF NOT EXISTS employee (
    employeeId INT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    password VARCHAR(100) NOT NULL
);

-- =====================================================
-- Datos iniciales de prueba
-- =====================================================

-- Insertar productos de ejemplo
INSERT INTO inventory (id, name, wholesalerPrice, available, stock) VALUES
(1, 'Manzana', 10.00, TRUE, 50),
(2, 'Pera', 15.00, TRUE, 30),
(3, 'Hamburguesa', 25.00, TRUE, 20),
(4, 'Fresa', 8.00, TRUE, 40),
(5, 'Leche', 5.00, TRUE, 100);

-- Insertar empleado de prueba (ID: 1, Password: 1234)
INSERT INTO employee (employeeId, name, password) VALUES
(1, 'Admin', '1234'),
(123, 'Test', 'test');

-- =====================================================
-- Verificar que las tablas se crearon correctamente
-- =====================================================
SELECT 'Tablas creadas correctamente!' AS mensaje;
SELECT * FROM inventory;
SELECT * FROM employee;

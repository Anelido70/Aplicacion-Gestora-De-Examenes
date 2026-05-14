-- ============================================
-- GESTIÓN DE EXÁMENES - IES CLARA DEL REY
-- Script de creación de base de datos
-- ============================================

CREATE DATABASE IF NOT EXISTS gestion_examenes;
USE gestion_examenes;

-- Tabla de usuarios (profesores y alumnos)
CREATE TABLE usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    nombre_completo VARCHAR(100),
    email VARCHAR(100),
    rol ENUM('PROFESOR', 'ALUMNO') DEFAULT 'ALUMNO',
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla de preguntas
CREATE TABLE preguntas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    codigo VARCHAR(20) NOT NULL UNIQUE,
    autor_id INT NOT NULL,
    curso VARCHAR(50),
    grupo VARCHAR(20),
    modulo VARCHAR(100),
    ra VARCHAR(100),
    tema VARCHAR(100),
    enunciado TEXT NOT NULL,
    tipo ENUM('TEST', 'DESARROLLO') NOT NULL,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    palabras_clave VARCHAR(255),
    FOREIGN KEY (autor_id) REFERENCES usuarios(id) ON DELETE CASCADE
);

-- Respuestas tipo test
CREATE TABLE respuestas_test (
    id INT AUTO_INCREMENT PRIMARY KEY,
    pregunta_id INT NOT NULL,
    texto_respuesta VARCHAR(255) NOT NULL,
    es_correcta BOOLEAN NOT NULL DEFAULT FALSE,
    FOREIGN KEY (pregunta_id) REFERENCES preguntas(id) ON DELETE CASCADE
);

-- Respuestas desarrollo
CREATE TABLE respuestas_desarrollo (
    id INT AUTO_INCREMENT PRIMARY KEY,
    pregunta_id INT NOT NULL UNIQUE,
    texto_modelo TEXT,
    FOREIGN KEY (pregunta_id) REFERENCES preguntas(id) ON DELETE CASCADE
);

-- Exámenes generados
CREATE TABLE examenes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(100) NOT NULL,
    creador_id INT NOT NULL,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    parametros_filtro TEXT,
    FOREIGN KEY (creador_id) REFERENCES usuarios(id)
);

-- Relación examen-preguntas
CREATE TABLE examen_preguntas (
    examen_id INT NOT NULL,
    pregunta_id INT NOT NULL,
    orden INT,
    PRIMARY KEY (examen_id, pregunta_id),
    FOREIGN KEY (examen_id) REFERENCES examenes(id) ON DELETE CASCADE,
    FOREIGN KEY (pregunta_id) REFERENCES preguntas(id) ON DELETE CASCADE
);

-- Auditoría (trazabilidad)
CREATE TABLE auditoria (
    id INT AUTO_INCREMENT PRIMARY KEY,
    usuario_id INT NOT NULL,
    accion VARCHAR(50) NOT NULL,
    entidad VARCHAR(50),
    entidad_id INT,
    detalle TEXT,
    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);

-- Resultados de exámenes de alumnos
CREATE TABLE resultados_examen (
    id INT AUTO_INCREMENT PRIMARY KEY,
    alumno_id INT NOT NULL,
    examen_id INT NOT NULL,
    nota DECIMAL(5,2),
    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (alumno_id) REFERENCES usuarios(id),
    FOREIGN KEY (examen_id) REFERENCES examenes(id)
);
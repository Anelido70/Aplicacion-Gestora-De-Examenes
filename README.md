<img width="499" height="434" alt="Login Gestor Examenes" src="https://github.com/user-attachments/assets/8d752a34-9ff6-4933-9a2c-8cbce7aefc6b" />
<img width="567" height="345" alt="MainFrameProfesor Gestor Examenes" src="https://github.com/user-attachments/assets/ca5b7c3c-7971-4a31-ad54-2892636acb44" />
<img width="567" height="323" alt="Creacion de Preguntas de Examen" src="https://github.com/user-attachments/assets/1b096f89-bcd4-48a9-87c9-e56e09453b29" />
<img width="567" height="377" alt="Creacion de Examenes" src="https://github.com/user-attachments/assets/f32072b7-e2d8-4b7b-bff3-06ea476d51ea" />
<img width="567" height="486" alt="Vista Previa de Examenes" src="https://github.com/user-attachments/assets/2020fa20-140f-4b11-943f-745d4da77063" />
<img width="567" height="416" alt="Modo Práctica Gestor de Examenes" src="https://github.com/user-attachments/assets/22dab22e-20e2-456d-97ad-f86b3f71f87a" />
<img width="390" height="342" alt="Resultado de la práctica" src="https://github.com/user-attachments/assets/3295e871-75f7-488b-9707-9a358b9116e2" />

# 📚 Gestión de Exámenes - IES Clara del Rey

Aplicación de escritorio para la gestión de exámenes desarrollada con **Java Swing** y **MariaDB** aplicando el patrón **MVC**.

---

## ✨ Funcionalidades

| Funcionalidad | Descripción |
|---------------|-------------|
| 🔐 **Login seguro** | Autenticación con SHA-256 + salt |
| 👥 **Roles** | Profesor (gestión total) y Alumno (consulta + práctica) |
| 📝 **CRUD preguntas** | Crear, listar, modificar y eliminar preguntas |
| 📋 **Tipos de preguntas** | Tipo test (4 opciones, 1 correcta) y desarrollo |
| 🔍 **Búsqueda** | Por palabras clave y búsqueda avanzada (módulo, RA, tema, autor, fecha) |
| 📄 **Generar exámenes** | Filtros, selección manual y aleatoria |
| 🖨️ **Exportar** | Examen a TXT e impresión |
| 👁️ **Ver exámenes** | Listado, visualización y eliminación |
| 🎯 **Modo práctica** | Alumnos responden y obtienen nota automática |
| 📜 **Auditoría** | Trazabilidad de todas las operaciones |

---

## 🛠️ Tecnologías

| Tecnología | Uso |
|------------|-----|
| **Java 8+** | Lenguaje principal |
| **Swing** | Interfaz gráfica de escritorio |
| **MariaDB 12.2** | Base de datos relacional |
| **JDBC** | Conexión Java-Base de datos |
| **SHA-256 + Salt** | Seguridad de contraseñas |
| **VS Code** | Entorno de desarrollo |

---

## 📁 Estructura del proyecto (MVC)
```
src/
├── Main.java                          Punto de entrada
├── config/
│   └── InicializadorBD.java           Creación automática de admin
├── database/
│   └── DatabaseConnection.java        Conexión JDBC a MariaDB
├── model/
│   ├── Usuario.java                   Modelo de usuario (rol profesor/alumno)
│   ├── Pregunta.java                  Clase base de pregunta
│   ├── PreguntaTest.java              Pregunta con 4 opciones
│   └── PreguntaDesarrollo.java        Pregunta con texto modelo
├── dao/
│   ├── UsuarioDAO.java                Acceso a datos de usuarios
│   ├── PreguntaDAO.java               Acceso a datos de preguntas
│   └── AuditoriaDAO.java              Registro de trazabilidad
├── service/
│   └── AuthService.java               SHA-256 + salt
├── controller/
│   ├── UsuarioController.java         Lógica de autenticación
│   ├── PreguntaController.java        Lógica de preguntas
│   └── ExamenController.java          Lógica de generación de exámenes
└── view/
    ├── LoginUI.java                   Ventana de inicio de sesión
    ├── RegistroUI.java                Ventana de registro de alumnos
    ├── MainFrameProfesor.java         Ventana principal del profesor
    ├── MainFrameAlumno.java           Ventana principal del alumno
    ├── CrearPreguntaDialog.java       Diálogo para crear preguntas
    ├── ModificarPreguntaDialog.java   Diálogo para modificar preguntas
    ├── GenerarExamenDialog.java       Diálogo para generar exámenes
    ├── VerExamenesDialog.java         Diálogo para ver exámenes guardados
    ├── ModoPracticaDialog.java        Modo práctica con autocorrección
    ├── BusquedaAvanzadaDialog.java    Búsqueda con filtros
    └── VisorAuditoriaDialog.java      Visualización del registro de auditoría
```

---

## 🗄️ Base de datos (8 tablas)

| Tabla | Contenido |
|-------|-----------|
| `usuarios` | Profesores y alumnos con contraseñas hasheadas |
| `preguntas` | Datos generales de cada pregunta |
| `respuestas_test` | 4 opciones por pregunta tipo test |
| `respuestas_desarrollo` | Texto modelo de preguntas de desarrollo |
| `examenes` | Exámenes generados |
| `examen_preguntas` | Relación examen-preguntas |
| `auditoria` | Registro de todas las operaciones |
| `resultados_examen` | Notas de alumnos en modo práctica |

---

## 🚀 Instalación y ejecución

### Requisitos
- Java 8 o superior
- MariaDB 10.x o 12.x
- Conector JDBC de MariaDB (`mariadb-java-client.jar`)

### Pasos
1. **Clonar el repositorio**
   ```bash
   git clone https://github.com/Anelido70/Aplicacion-Gestora-De-Examenes.git
2. **Crear la base de datos**
   - Ejecutar el script SQL de schema.sql en MariaDB
   - Se creará la BD gestion_examenes con 8 tablas
3. **Configurar la conexión**
   - Editar DatabaseConnection.java con tu usuario y contraseña de MariaDB
4. **Ejecutar**
   - Abrir el proyecto en VS Code
   - Añadir mariadb-java-client.jar al classpath
   - Ejecutar Main.java
5. **Usuario por defecto**
   - Usuario: admin
   - Contraseña: admin123
   - El sistema lo crea automáticamente al iniciar

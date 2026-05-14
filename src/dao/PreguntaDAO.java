package dao;

import database.DatabaseConnection;
import model.Pregunta;
import model.PreguntaTest;
import model.PreguntaDesarrollo;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PreguntaDAO {

    // Insertar pregunta base
    public int insertarPregunta(Pregunta pregunta) throws SQLException {
        String sql = "INSERT INTO preguntas (codigo, autor_id, curso, grupo, modulo, ra, tema, enunciado, tipo, palabras_clave) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, pregunta.getCodigo());
            stmt.setInt(2, pregunta.getAutorId());
            stmt.setString(3, pregunta.getCurso());
            stmt.setString(4, pregunta.getGrupo());
            stmt.setString(5, pregunta.getModulo());
            stmt.setString(6, pregunta.getRa());
            stmt.setString(7, pregunta.getTema());
            stmt.setString(8, pregunta.getEnunciado());
            stmt.setString(9, pregunta.getTipo());
            stmt.setString(10, pregunta.getPalabrasClave());
            
            stmt.executeUpdate();
            
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return -1;
    }

    // Insertar respuestas de tipo test
    public void insertarRespuestasTest(int preguntaId, List<String> respuestas, int indiceCorrecta) throws SQLException {
        String sql = "INSERT INTO respuestas_test (pregunta_id, texto_respuesta, es_correcta) VALUES (?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            for (int i = 0; i < respuestas.size(); i++) {
                stmt.setInt(1, preguntaId);
                stmt.setString(2, respuestas.get(i));
                stmt.setBoolean(3, (i + 1) == indiceCorrecta);
                stmt.executeUpdate();
            }
        }
    }

    // Insertar respuesta de desarrollo
    public void insertarRespuestaDesarrollo(int preguntaId, String textoModelo) throws SQLException {
        String sql = "INSERT INTO respuestas_desarrollo (pregunta_id, texto_modelo) VALUES (?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, preguntaId);
            stmt.setString(2, textoModelo);
            stmt.executeUpdate();
        }
    }

    // Obtener todas las preguntas
    public List<Pregunta> obtenerTodas() throws SQLException {
        List<Pregunta> preguntas = new ArrayList<>();
        String sql = "SELECT * FROM preguntas ORDER BY fecha_creacion DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                preguntas.add(mapearPreguntaBasica(rs));
            }
        }
        return preguntas;
    }

    // Eliminar pregunta
    public boolean eliminar(int id) throws SQLException {
        String sql = "DELETE FROM preguntas WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    // Mapeo básico de pregunta
    private Pregunta mapearPreguntaBasica(ResultSet rs) throws SQLException {
        Pregunta pregunta = new Pregunta();
        pregunta.setId(rs.getInt("id"));
        pregunta.setCodigo(rs.getString("codigo"));
        pregunta.setAutorId(rs.getInt("autor_id"));
        pregunta.setCurso(rs.getString("curso"));
        pregunta.setGrupo(rs.getString("grupo"));
        pregunta.setModulo(rs.getString("modulo"));
        pregunta.setRa(rs.getString("ra"));
        pregunta.setTema(rs.getString("tema"));
        pregunta.setEnunciado(rs.getString("enunciado"));
        pregunta.setTipo(rs.getString("tipo"));
        pregunta.setFechaCreacion(rs.getTimestamp("fecha_creacion"));
        pregunta.setPalabrasClave(rs.getString("palabras_clave"));
        return pregunta;
    }
    // Buscar preguntas por palabra clave
public List<Pregunta> buscarPorPalabraClave(String palabra) throws SQLException {
    List<Pregunta> preguntas = new ArrayList<>();
    String sql = "SELECT * FROM preguntas WHERE palabras_clave LIKE ? OR enunciado LIKE ? ORDER BY fecha_creacion DESC";
    
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        
        String patron = "%" + palabra + "%";
        stmt.setString(1, patron);
        stmt.setString(2, patron);
        
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            preguntas.add(mapearPreguntaBasica(rs));
        }
    }
    return preguntas;
}

// Actualizar pregunta
public boolean actualizar(Pregunta pregunta) throws SQLException {
    String sql = "UPDATE preguntas SET enunciado=?, curso=?, grupo=?, modulo=?, ra=?, tema=?, palabras_clave=? WHERE id=?";
    
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        
        stmt.setString(1, pregunta.getEnunciado());
        stmt.setString(2, pregunta.getCurso());
        stmt.setString(3, pregunta.getGrupo());
        stmt.setString(4, pregunta.getModulo());
        stmt.setString(5, pregunta.getRa());
        stmt.setString(6, pregunta.getTema());
        stmt.setString(7, pregunta.getPalabrasClave());
        stmt.setInt(8, pregunta.getId());
        
        return stmt.executeUpdate() > 0;
    }
}

// Obtener una pregunta por ID
public Pregunta obtenerPorId(int id) throws SQLException {
    String sql = "SELECT * FROM preguntas WHERE id = ?";
    
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        
        stmt.setInt(1, id);
        ResultSet rs = stmt.executeQuery();
        
        if (rs.next()) {
            return mapearPreguntaBasica(rs);
        }
    }
    return null;
}
// Obtener respuestas de una pregunta test
public List<String> obtenerRespuestasTest(int preguntaId) throws SQLException {
    List<String> respuestas = new ArrayList<>();
    String sql = "SELECT texto_respuesta FROM respuestas_test WHERE pregunta_id = ? ORDER BY id";
    
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setInt(1, preguntaId);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            respuestas.add(rs.getString("texto_respuesta"));
        }
    }
    return respuestas;
}

// Obtener índice de la respuesta correcta
public int obtenerIndiceCorrecta(int preguntaId) throws SQLException {
    String sql = "SELECT id FROM respuestas_test WHERE pregunta_id = ? ORDER BY id";
    List<Integer> ids = new ArrayList<>();
    
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setInt(1, preguntaId);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            ids.add(rs.getInt("id"));
        }
    }
    
    // Buscar cuál es correcta
    String sqlCorrecta = "SELECT id FROM respuestas_test WHERE pregunta_id = ? AND es_correcta = TRUE";
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sqlCorrecta)) {
        stmt.setInt(1, preguntaId);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            int idCorrecta = rs.getInt("id");
            // Devolver el índice (1-4) de la correcta
            return ids.indexOf(idCorrecta) + 1;
        }
    }
    return 1;
}

// Actualizar respuestas test
public void actualizarRespuestasTest(int preguntaId, List<String> respuestas, int indiceCorrecta) throws SQLException {
    Connection conn = DatabaseConnection.getConnection();
    
    try {
        conn.setAutoCommit(false);
        
        // 1. Obtener los IDs de las respuestas existentes ordenadas
        String selectSql = "SELECT id FROM respuestas_test WHERE pregunta_id = ? ORDER BY id";
        PreparedStatement selectStmt = conn.prepareStatement(selectSql);
        selectStmt.setInt(1, preguntaId);
        ResultSet rs = selectStmt.executeQuery();
        
        List<Integer> ids = new ArrayList<>();
        while (rs.next()) {
            ids.add(rs.getInt("id"));
        }
        selectStmt.close();
        
        // 2. Actualizar cada respuesta
        String updateSql = "UPDATE respuestas_test SET texto_respuesta = ?, es_correcta = ? WHERE id = ?";
        PreparedStatement updateStmt = conn.prepareStatement(updateSql);
        
        for (int i = 0; i < ids.size() && i < respuestas.size(); i++) {
            updateStmt.setString(1, respuestas.get(i));
            updateStmt.setBoolean(2, (i + 1) == indiceCorrecta);
            updateStmt.setInt(3, ids.get(i));
            updateStmt.executeUpdate();
        }
        updateStmt.close();
        
        conn.commit();
        conn.close();
    } catch (SQLException e) {
        conn.rollback();
        conn.close();
        throw e;
    }
}

// Actualizar respuesta desarrollo
public void actualizarRespuestaDesarrollo(int preguntaId, String texto) throws SQLException {
    String sql = "UPDATE respuestas_desarrollo SET texto_modelo = ? WHERE pregunta_id = ?";
    
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setString(1, texto);
        stmt.setInt(2, preguntaId);
        stmt.executeUpdate();
    }
}
// Búsqueda avanzada con filtros
public List<Pregunta> buscarConFiltros(String modulo, String ra, String tema, String tipo) throws SQLException {
    List<Pregunta> preguntas = new ArrayList<>();
    StringBuilder sql = new StringBuilder("SELECT * FROM preguntas WHERE 1=1");
    
    List<Object> parametros = new ArrayList<>();
    
    if (modulo != null && !modulo.isEmpty()) {
        sql.append(" AND modulo LIKE ?");
        parametros.add("%" + modulo + "%");
    }
    if (ra != null && !ra.isEmpty()) {
        sql.append(" AND ra LIKE ?");
        parametros.add("%" + ra + "%");
    }
    if (tema != null && !tema.isEmpty()) {
        sql.append(" AND tema LIKE ?");
        parametros.add("%" + tema + "%");
    }
    if (tipo != null && !tipo.isEmpty() && !tipo.equals("TODOS")) {
        sql.append(" AND tipo = ?");
        parametros.add(tipo);
    }
    
    sql.append(" ORDER BY fecha_creacion DESC");
    
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
        
        for (int i = 0; i < parametros.size(); i++) {
            stmt.setObject(i + 1, parametros.get(i));
        }
        
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            preguntas.add(mapearPreguntaBasica(rs));
        }
    }
    return preguntas;
}

// Obtener respuestas de una pregunta test por su ID
public List<String> getRespuestasTest(int preguntaId) throws SQLException {
    List<String> respuestas = new ArrayList<>();
    String sql = "SELECT texto_respuesta, es_correcta FROM respuestas_test WHERE pregunta_id = ? ORDER BY id";
    
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setInt(1, preguntaId);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            String texto = rs.getString("texto_respuesta");
            if (rs.getBoolean("es_correcta")) {
                texto = texto + " ✓";
            }
            respuestas.add(texto);
        }
    }
    return respuestas;
}

// Obtener respuesta de desarrollo
public String getRespuestaDesarrollo(int preguntaId) throws SQLException {
    String sql = "SELECT texto_modelo FROM respuestas_desarrollo WHERE pregunta_id = ?";
    
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setInt(1, preguntaId);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return rs.getString("texto_modelo");
        }
    }
    return "";
}

// Insertar examen en la BD
public int guardarExamen(String titulo, int creadorId, String parametrosFiltro) throws SQLException {
    String sql = "INSERT INTO examenes (titulo, creador_id, parametros_filtro) VALUES (?, ?, ?)";
    
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
        stmt.setString(1, titulo);
        stmt.setInt(2, creadorId);
        stmt.setString(3, parametrosFiltro);
        stmt.executeUpdate();
        
        ResultSet rs = stmt.getGeneratedKeys();
        if (rs.next()) {
            return rs.getInt(1);
        }
    }
    return -1;
}

// Asociar pregunta a examen
public void agregarPreguntaAExamen(int examenId, int preguntaId, int orden) throws SQLException {
    String sql = "INSERT INTO examen_preguntas (examen_id, pregunta_id, orden) VALUES (?, ?, ?)";
    
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setInt(1, examenId);
        stmt.setInt(2, preguntaId);
        stmt.setInt(3, orden);
        stmt.executeUpdate();
    }
}
// Obtener todos los exámenes
public List<String[]> obtenerExamenes() throws SQLException {
    List<String[]> examenes = new ArrayList<>();
    String sql = "SELECT e.id, e.titulo, e.fecha_creacion, u.username, " +
                 "(SELECT COUNT(*) FROM examen_preguntas ep WHERE ep.examen_id = e.id) as num_preguntas " +
                 "FROM examenes e JOIN usuarios u ON e.creador_id = u.id ORDER BY e.fecha_creacion DESC";
    
    try (Connection conn = DatabaseConnection.getConnection();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {
        
        while (rs.next()) {
            String[] fila = new String[5];
            fila[0] = String.valueOf(rs.getInt("id"));
            fila[1] = rs.getString("titulo");
            fila[2] = rs.getString("fecha_creacion").substring(0, 10);
            fila[3] = rs.getString("username");
            fila[4] = rs.getString("num_preguntas");
            examenes.add(fila);
        }
    }
    return examenes;
}

// Obtener preguntas de un examen
public List<Pregunta> obtenerPreguntasDeExamen(int examenId) throws SQLException {
    List<Pregunta> preguntas = new ArrayList<>();
    String sql = "SELECT p.* FROM preguntas p " +
                 "JOIN examen_preguntas ep ON p.id = ep.pregunta_id " +
                 "WHERE ep.examen_id = ? ORDER BY ep.orden";
    
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setInt(1, examenId);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            preguntas.add(mapearPreguntaBasica(rs));
        }
    }
    return preguntas;
}

// Eliminar examen
public boolean eliminarExamen(int id) throws SQLException {
    String sql = "DELETE FROM examenes WHERE id = ?";
    
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setInt(1, id);
        return stmt.executeUpdate() > 0;
    }
}
}
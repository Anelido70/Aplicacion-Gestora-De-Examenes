package controller;

import dao.PreguntaDAO;
import model.Pregunta;
import dao.AuditoriaDAO;
import model.PreguntaTest;
import model.PreguntaDesarrollo;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PreguntaController {

    private PreguntaDAO preguntaDAO;
    private AuditoriaDAO auditoriaDAO;
    private int autorId;

    public PreguntaController(PreguntaDAO preguntaDAO, int autorId) {
        this.preguntaDAO = preguntaDAO;
        this.autorId = autorId;
        this.auditoriaDAO = new AuditoriaDAO();
    }

    // Crear pregunta tipo test
    public void crearPreguntaTest(String enunciado, String r1, String r2, String r3, String r4, 
                                  int correcta, String curso, String grupo, String modulo, 
                                  String ra, String tema, String palabrasClave) {
        try {
            PreguntaTest pregunta = new PreguntaTest();
            pregunta.setCodigo(generarCodigo());
            pregunta.setEnunciado(enunciado);
            pregunta.setAutorId(autorId);
            pregunta.setCurso(curso);
            pregunta.setGrupo(grupo);
            pregunta.setModulo(modulo);
            pregunta.setRa(ra);
            pregunta.setTema(tema);
            pregunta.setPalabrasClave(palabrasClave);

            List<String> respuestas = new ArrayList<>();
            respuestas.add(r1);
            respuestas.add(r2);
            respuestas.add(r3);
            respuestas.add(r4);
            pregunta.setRespuestas(respuestas);
            pregunta.setIndiceCorrecta(correcta);

            int preguntaId = preguntaDAO.insertarPregunta(pregunta);
            preguntaDAO.insertarRespuestasTest(preguntaId, respuestas, correcta);
            auditoriaDAO.registrar(autorId, "CREAR", "PREGUNTA", preguntaId, "Pregunta tipo TEST: " + enunciado);
            System.out.println("✅ Pregunta tipo test creada");
        } catch (SQLException e) {
            System.out.println("❌ Error al crear pregunta test: " + e.getMessage());
        }
    }

    // Crear pregunta tipo desarrollo
    public void crearPreguntaDesarrollo(String enunciado, String respuesta, String curso, 
                                        String grupo, String modulo, String ra, 
                                        String tema, String palabrasClave) {
        try {
            PreguntaDesarrollo pregunta = new PreguntaDesarrollo();
            pregunta.setCodigo(generarCodigo());
            pregunta.setEnunciado(enunciado);
            pregunta.setAutorId(autorId);
            pregunta.setCurso(curso);
            pregunta.setGrupo(grupo);
            pregunta.setModulo(modulo);
            pregunta.setRa(ra);
            pregunta.setTema(tema);
            pregunta.setPalabrasClave(palabrasClave);
            pregunta.setTextoModelo(respuesta);

            int preguntaId = preguntaDAO.insertarPregunta(pregunta);
            preguntaDAO.insertarRespuestaDesarrollo(preguntaId, respuesta);
            auditoriaDAO.registrar(autorId, "CREAR", "PREGUNTA", preguntaId, "Pregunta tipo DESARROLLO: " + enunciado);
            System.out.println("✅ Pregunta de desarrollo creada");
        } catch (SQLException e) {
            System.out.println("❌ Error al crear pregunta desarrollo: " + e.getMessage());
        }
    }

    // Obtener todas las preguntas
    public List<Pregunta> obtenerTodas() {
        try {
            return preguntaDAO.obtenerTodas();
        } catch (SQLException e) {
            System.out.println("❌ Error al listar preguntas: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // Eliminar pregunta
    public void eliminarPregunta(int id) {
        auditoriaDAO.registrar(autorId, "ELIMINAR", "PREGUNTA", id, "Pregunta eliminada");
        try {
            if (preguntaDAO.eliminar(id)) {
                System.out.println("✅ Pregunta eliminada");
            } else {
                System.out.println("❌ No se encontró la pregunta");
            }
        } catch (SQLException e) {
            System.out.println("❌ Error al eliminar: " + e.getMessage());
        }
    }

    private String generarCodigo() {
        return "PRG-" + System.currentTimeMillis();
    }
    // Buscar por palabra clave
public List<Pregunta> buscarPorPalabraClave(String palabra) {
    try {
        return preguntaDAO.buscarPorPalabraClave(palabra);
    } catch (SQLException e) {
        System.out.println("❌ Error al buscar: " + e.getMessage());
        return new ArrayList<>();
    }
}

// Actualizar pregunta
public void actualizarPregunta(int id, String enunciado, String curso, String grupo, 
                               String modulo, String ra, String tema, String palabrasClave) {
    try {
        Pregunta pregunta = new Pregunta();
        pregunta.setId(id);
        pregunta.setEnunciado(enunciado);
        pregunta.setCurso(curso);
        pregunta.setGrupo(grupo);
        pregunta.setModulo(modulo);
        pregunta.setRa(ra);
        pregunta.setTema(tema);
        pregunta.setPalabrasClave(palabrasClave);
        
        if (preguntaDAO.actualizar(pregunta)) {
            System.out.println("✅ Pregunta actualizada");
        }
    } catch (SQLException e) {
        System.out.println("❌ Error al actualizar: " + e.getMessage());
    }
}

// Obtener pregunta por ID
public Pregunta obtenerPorId(int id) {
    try {
        return preguntaDAO.obtenerPorId(id);
    } catch (SQLException e) {
        return null;
    }
}
}
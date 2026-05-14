package controller;

import dao.PreguntaDAO;
import model.Pregunta;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ExamenController {

    private PreguntaDAO preguntaDAO;
    private int usuarioId;

    public ExamenController(PreguntaDAO preguntaDAO, int usuarioId) {
        this.preguntaDAO = preguntaDAO;
        this.usuarioId = usuarioId;
    }

    // Buscar preguntas con filtros
    public List<Pregunta> buscarConFiltros(String modulo, String ra, String tema, String tipo) {
        try {
            return preguntaDAO.buscarConFiltros(modulo, ra, tema, tipo);
        } catch (SQLException e) {
            System.out.println("❌ Error al buscar preguntas: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // Seleccionar preguntas aleatorias
    public List<Pregunta> seleccionarAleatorias(List<Pregunta> disponibles, int cantidad) {
        List<Pregunta> copia = new ArrayList<>(disponibles);
        Collections.shuffle(copia);
        return copia.subList(0, Math.min(cantidad, copia.size()));
    }

    // Guardar examen en BD
    public int guardarExamen(String titulo, String parametrosFiltro) {
        try {
            return preguntaDAO.guardarExamen(titulo, usuarioId, parametrosFiltro);
        } catch (SQLException e) {
            System.out.println("❌ Error al guardar examen: " + e.getMessage());
            return -1;
        }
    }

    // Asociar pregunta a examen
    public void agregarPreguntaAExamen(int examenId, int preguntaId, int orden) {
        try {
            preguntaDAO.agregarPreguntaAExamen(examenId, preguntaId, orden);
        } catch (SQLException e) {
            System.out.println("❌ Error al asociar pregunta: " + e.getMessage());
        }
    }

    // Obtener respuestas tipo test
    public List<String> getRespuestasTest(int preguntaId) {
        try {
            return preguntaDAO.getRespuestasTest(preguntaId);
        } catch (SQLException e) {
            return new ArrayList<>();
        }
    }

    // Obtener respuesta desarrollo
    public String getRespuestaDesarrollo(int preguntaId) {
        try {
            return preguntaDAO.getRespuestaDesarrollo(preguntaId);
        } catch (SQLException e) {
            return "";
        }
    }
}

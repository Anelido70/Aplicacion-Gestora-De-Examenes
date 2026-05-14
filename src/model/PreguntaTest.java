package model;

import java.util.List;

public class PreguntaTest extends Pregunta {
    private List<String> respuestas;  // 4 respuestas
    private int indiceCorrecta;       // 1-4

    public PreguntaTest() {
        setTipo("TEST");
    }

    public List<String> getRespuestas() { return respuestas; }
    public void setRespuestas(List<String> respuestas) { this.respuestas = respuestas; }

    public int getIndiceCorrecta() { return indiceCorrecta; }
    public void setIndiceCorrecta(int indiceCorrecta) { this.indiceCorrecta = indiceCorrecta; }
}

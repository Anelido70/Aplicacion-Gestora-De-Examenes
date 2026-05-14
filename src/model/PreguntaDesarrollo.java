package model;

public class PreguntaDesarrollo extends Pregunta {
    private String textoModelo;

    public PreguntaDesarrollo() {
        setTipo("DESARROLLO");
    }

    public String getTextoModelo() { return textoModelo; }
    public void setTextoModelo(String textoModelo) { this.textoModelo = textoModelo; }
}

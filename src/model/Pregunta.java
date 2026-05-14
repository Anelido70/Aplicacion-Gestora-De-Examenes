package model;

import java.sql.Timestamp;

public class Pregunta {
    private int id;
    private String codigo;
    private int autorId;
    private String curso;
    private String grupo;
    private String modulo;
    private String ra;
    private String tema;
    private String enunciado;
    private String tipo;  // "TEST" o "DESARROLLO"
    private Timestamp fechaCreacion;
    private String palabrasClave;

    // Constructor vacío
    public Pregunta() {}

    // Constructor completo
    public Pregunta(int id, String codigo, int autorId, String curso, String grupo,
                    String modulo, String ra, String tema, String enunciado,
                    String tipo, Timestamp fechaCreacion, String palabrasClave) {
        this.id = id;
        this.codigo = codigo;
        this.autorId = autorId;
        this.curso = curso;
        this.grupo = grupo;
        this.modulo = modulo;
        this.ra = ra;
        this.tema = tema;
        this.enunciado = enunciado;
        this.tipo = tipo;
        this.fechaCreacion = fechaCreacion;
        this.palabrasClave = palabrasClave;
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public int getAutorId() { return autorId; }
    public void setAutorId(int autorId) { this.autorId = autorId; }

    public String getCurso() { return curso; }
    public void setCurso(String curso) { this.curso = curso; }

    public String getGrupo() { return grupo; }
    public void setGrupo(String grupo) { this.grupo = grupo; }

    public String getModulo() { return modulo; }
    public void setModulo(String modulo) { this.modulo = modulo; }

    public String getRa() { return ra; }
    public void setRa(String ra) { this.ra = ra; }

    public String getTema() { return tema; }
    public void setTema(String tema) { this.tema = tema; }

    public String getEnunciado() { return enunciado; }
    public void setEnunciado(String enunciado) { this.enunciado = enunciado; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public Timestamp getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(Timestamp fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public String getPalabrasClave() { return palabrasClave; }
    public void setPalabrasClave(String palabrasClave) { this.palabrasClave = palabrasClave; }

    @Override
    public String toString() {
        return "Pregunta{id=" + id + ", tipo=" + tipo + ", enunciado='" + enunciado + "'}";
    }
}

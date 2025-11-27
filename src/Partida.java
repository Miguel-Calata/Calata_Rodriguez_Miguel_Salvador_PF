import java.io.Serializable;

public class Partida implements Serializable {

    private int ronda;
    private int puntaje;
    private long semilla;
    private String nombre;

    public Partida() {
        this.ronda = 0;
        this.puntaje = 0;
        this.semilla = System.currentTimeMillis();
        this.nombre = "Invitado";
    }

    public int getRonda() {
        return ronda;
    }

    public void setRonda(int ronda) {
        this.ronda = ronda;
    }

    public int getPuntaje() {
        return puntaje;
    }

    public void setPuntaje(int puntaje) {
        this.puntaje = puntaje;
    }

    public long getSemilla() {
        return semilla;
    }

    public void setSemilla(long semilla) {
        this.semilla = semilla;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}

package clases;

/**
 * Clase que representa una reserva de un pasajero.
 *
 * @author Alexis Leonel Bustamante Hecht - FAI2355
 */
public class Reserva {

    public Aerolinea aerolinea;
    public int hora;
    private final Terminal terminal;
    private final int puertaEmbarque;

    public Reserva(Aerolinea aerolinea, int hora, Terminal terminal, int puerta) {
        this.aerolinea = aerolinea;
        this.hora = hora;
        this.terminal = terminal;
        this.puertaEmbarque = puerta;
    }

    // Metodos de la clase -----------------------------------------------------
    public Aerolinea getAerolinea() {
        return aerolinea;
    }

    public int getHora() {
        return hora;
    }

    public Terminal getTerminal() {
        return terminal;
    }

    public int getPuertaEmbarque() {
        return puertaEmbarque;
    }

}

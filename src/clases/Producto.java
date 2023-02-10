package clases;

/**
 * Clase que representa a un producto
 *
 * @author Alexis Leonel Bustamante Hecht - FAI2355
 */
public class Producto {

    private final int id;
    private int stock;
    private final double precio;

    public Producto(int id, int stock, double precio) {
        this.id = id;
        this.stock = stock;
        this.precio = precio;
    }

    // Metodos para Cajera -----------------------------------------------------
    /**
     * Metodo sincronizado para que no entren dos cajeras a vender al mismo
     * tiempo
     */
    public synchronized void restarStock() {
        this.stock -= 1;
    }

    // Metodos para Repositor --------------------------------------------------
    /**
     * Metodo sincronizado para que no entren dos repositores a agregar stock al
     * mismo
     *
     * @param cantidad productos agregados
     */
    public synchronized void agregarStock(int cantidad) {
        this.stock += cantidad;
    }

    // Metodos de la clase -----------------------------------------------------
    public int getId() {
        return id;
    }

    public int getStock() {
        return stock;
    }

    public double getPrecio() {
        return precio;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

}

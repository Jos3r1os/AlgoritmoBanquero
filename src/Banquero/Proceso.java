package Banquero;

public class Proceso {

    private String nombre;
    private int[] maximo;
    private int[] asignado;

    public Proceso(String nombre, int[] maximo, int[] asignado) {
        this.nombre = nombre;
        this.maximo = maximo;
        this.asignado = asignado;
    }

    public int[] getNecesidad() {
        int[] nec = new int[maximo.length];
        for (int j = 0; j < maximo.length; j++) {
            nec[j] = maximo[j] - asignado[j];
        }
        return nec;
    }

    public boolean puedeTerminar(int[] libres) {
        int[] nec = getNecesidad();
        for (int j = 0; j < nec.length; j++) {
            if (nec[j] > libres[j]) {
                return false;
            }
        }
        return true;
    }

    public void asignar(int[] cantidad) {
        for (int j = 0; j < asignado.length; j++) {
            asignado[j] += cantidad[j];
        }
    }

    public void quitar(int[] cantidad) {
        for (int j = 0; j < asignado.length; j++) {
            asignado[j] -= cantidad[j];
        }
    }

    public String getNombre() {
        return nombre;
    }

    public int[] getMaximo() {
        return maximo;
    }

    public int[] getAsignado() {
        return asignado;
    }
}
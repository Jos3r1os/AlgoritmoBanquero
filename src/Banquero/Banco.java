package Banquero;

import java.util.ArrayList;
import java.util.Arrays;

public class Banco {

    private ArrayList<Proceso> procesos = new ArrayList<>();
    private int[] disponible;
    private String mensaje = "";

    public Banco(int[] disponible) {
        this.disponible = disponible;
    }

    public void agregar(Proceso p) {
        procesos.add(p);
    }

    public Resultado verificar() {
        Resultado r = new Resultado();
        int[] trabajo = disponible.clone();
        boolean[] termino = new boolean[procesos.size()];

        boolean huboCambio = true;
        while (huboCambio) {
            huboCambio = false;
            for (int i = 0; i < procesos.size(); i++) {
                Proceso p = procesos.get(i);
                if (!termino[i] && p.puedeTerminar(trabajo)) {
                    String antes = Arrays.toString(trabajo);
                    for (int j = 0; j < trabajo.length; j++) {
                        trabajo[j] += p.getAsignado()[j];
                    }
                    termino[i] = true;
                    huboCambio = true;
                    r.getSecuencia().add(p.getNombre());
                    r.getPasos().add(p.getNombre() + " necesita " + Arrays.toString(p.getNecesidad())
                            + " y hay " + antes + ", termina y libera "
                            + Arrays.toString(p.getAsignado()) + ". Disponible: " + Arrays.toString(trabajo));
                }
            }
        }

        for (int i = 0; i < procesos.size(); i++) {
            if (!termino[i]) {
                r.getBloqueados().add(procesos.get(i).getNombre());
            }
        }
        return r;
    }

    public boolean solicitar(int indice, int[] pedido) {
        Proceso p = procesos.get(indice);
        int[] nec = p.getNecesidad();

        for (int j = 0; j < pedido.length; j++) {
            if (pedido[j] > nec[j]) {
                mensaje = p.getNombre() + " está pidiendo más de lo que declaró como máximo.";
                return false;
            }
        }
        for (int j = 0; j < pedido.length; j++) {
            if (pedido[j] > disponible[j]) {
                mensaje = "No hay recursos suficientes, " + p.getNombre() + " tiene que esperar.";
                return false;
            }
        }


        p.asignar(pedido);
        for (int j = 0; j < pedido.length; j++) {
            disponible[j] -= pedido[j];
        }

        if (verificar().esSeguro()) {
            mensaje = "Solicitud concedida.";
            return true;
        }

        p.quitar(pedido);
        for (int j = 0; j < pedido.length; j++) {
            disponible[j] += pedido[j];
        }
        mensaje = "Solicitud negada: el sistema quedaría en estado inseguro.";
        return false;
    }

    public Proceso getProceso(int i) {
        return procesos.get(i);
    }

    public int[] getDisponible() {
        return disponible;
    }

    public String getMensaje() {
        return mensaje;
    }
}
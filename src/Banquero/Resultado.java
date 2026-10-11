package Banquero;

import java.util.ArrayList;

public class Resultado {

    private ArrayList<String> secuencia = new ArrayList<>();
    private ArrayList<String> pasos = new ArrayList<>();
    private ArrayList<String> bloqueados = new ArrayList<>();

    public boolean esSeguro() {
        return bloqueados.isEmpty();
    }

    public ArrayList<String> getSecuencia() {
        return secuencia;
    }

    public ArrayList<String> getPasos() {
        return pasos;
    }

    public ArrayList<String> getBloqueados() {
        return bloqueados;
    }
}
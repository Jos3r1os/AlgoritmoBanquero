package Banquero;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.Arrays;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class Ventana extends JFrame {

    private final int N = 5;
    private final int M = 3;

    private final Color AZUL = new Color(38, 70, 110);
    private final Color VERDE = new Color(39, 134, 84);
    private final Color ROJO = new Color(192, 57, 43);
    private final Color GRIS = new Color(120, 130, 140);

    private JTable tabla = new JTable();
    private DefaultTableModel modelo;
    private JTextField[] txtDisp = new JTextField[M];
    private JComboBox<String> cmbProceso = new JComboBox<>();
    private JTextField txtPedido = new JTextField(10);
    private JLabel lblEstado = new JLabel();
    private JTextArea txtPasos = new JTextArea(8, 20);
    private boolean recalculando = false;

    public Ventana() {
        setTitle("Algoritmo Banquero");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(840, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(245, 247, 250));

        add(crearTitulo(), BorderLayout.NORTH);
        add(crearCentro(), BorderLayout.CENTER);
        add(crearAbajo(), BorderLayout.SOUTH);

        cargarEjemplo();
    }

    private JPanel crearTitulo() {
        JLabel titulo = new JLabel("Algoritmo del Banquero");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 22));
        titulo.setForeground(Color.WHITE);
        JLabel sub = new JLabel("Estados seguros y detección de interbloqueo");
        sub.setForeground(new Color(200, 215, 235));

        JPanel textos = new JPanel(new GridLayout(2, 1));
        textos.setOpaque(false);
        textos.add(titulo);
        textos.add(sub);

        JButton btnEjemplo = boton("Cargar ejemplo", new Color(70, 105, 150));
        btnEjemplo.addActionListener(e -> cargarEjemplo());
        JPanel derecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 8));
        derecha.setOpaque(false);
        derecha.add(btnEjemplo);

        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(AZUL);
        p.setBorder(BorderFactory.createEmptyBorder(14, 18, 14, 18));
        p.add(textos, BorderLayout.WEST);
        p.add(derecha, BorderLayout.EAST);
        return p;
    }

    private JPanel crearCentro() {
        String[] columnas = {"Proceso", "Asig. A", "Asig. B", "Asig. C",
                "Máx. A", "Máx. B", "Máx. C", "Nec. A", "Nec. B", "Nec. C"};

        modelo = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int fila, int col) {
                return col >= 1 && col <= 2 * M;
            }
        };
        for (int i = 0; i < N; i++) {
            modelo.addRow(new Object[]{"P" + i, 0, 0, 0, 0, 0, 0, 0, 0, 0});
            cmbProceso.addItem("P" + i);
        }
        modelo.addTableModelListener(e -> recalcularNecesidad());

        DefaultTableCellRenderer centrado = new DefaultTableCellRenderer();
        centrado.setHorizontalAlignment(JLabel.CENTER);

        tabla.setModel(modelo);
        tabla.setDefaultRenderer(Object.class, centrado);
        tabla.setRowHeight(28);
        tabla.setFont(new Font("SansSerif", Font.PLAIN, 14));
        tabla.setGridColor(new Color(220, 225, 230));
        tabla.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        tabla.getTableHeader().setReorderingAllowed(false);
        tabla.setFillsViewportHeight(true);
        tabla.putClientProperty("terminateEditOnFocusLost", true);

        JButton btnVerificar = boton("Verificar estado", AZUL);
        btnVerificar.addActionListener(e -> verificar());
        JButton btnSolicitar = boton("Solicitar", AZUL);
        btnSolicitar.addActionListener(e -> solicitar());

        JPanel fila1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        fila1.setOpaque(false);
        fila1.add(negrita("Disponible"));
        for (int j = 0; j < M; j++) {
            txtDisp[j] = new JTextField("0", 3);
            txtDisp[j].setHorizontalAlignment(JTextField.CENTER);
            fila1.add(new JLabel((char) ('A' + j) + ":"));
            fila1.add(txtDisp[j]);
        }
        fila1.add(btnVerificar);

        JLabel ayuda = new JLabel("cantidades separadas por espacio");
        ayuda.setForeground(GRIS);

        JPanel fila2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        fila2.setOpaque(false);
        fila2.add(negrita("Solicitud"));
        fila2.add(cmbProceso);
        fila2.add(new JLabel("pide"));
        fila2.add(txtPedido);
        fila2.add(btnSolicitar);
        fila2.add(ayuda);

        JPanel controles = new JPanel(new GridLayout(2, 1));
        controles.setOpaque(false);
        controles.add(fila1);
        controles.add(fila2);

        JPanel p = new JPanel(new BorderLayout(0, 8));
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(14, 14, 6, 14));
        p.add(new JScrollPane(tabla), BorderLayout.CENTER);
        p.add(controles, BorderLayout.SOUTH);
        return p;
    }

    private JPanel crearAbajo() {
        lblEstado.setOpaque(true);
        lblEstado.setForeground(Color.WHITE);
        lblEstado.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblEstado.setBorder(BorderFactory.createEmptyBorder(9, 12, 9, 12));

        txtPasos.setEditable(false);
        txtPasos.setFont(new Font("Monospaced", Font.PLAIN, 13));
        txtPasos.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(0, 14, 14, 14));
        p.add(lblEstado, BorderLayout.NORTH);
        p.add(new JScrollPane(txtPasos), BorderLayout.CENTER);
        return p;
    }

    private JButton boton(String texto, Color color) {
        JButton b = new JButton(texto);
        b.setBackground(color);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("SansSerif", Font.BOLD, 13));
        b.setFocusPainted(false);
        b.setOpaque(true);
        b.setBorderPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }

    private JLabel negrita(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(new Font("SansSerif", Font.BOLD, 14));
        return l;
    }

    private void recalcularNecesidad() {
        if (recalculando) {
            return;
        }
        recalculando = true;
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < M; j++) {
                Object valor;
                try {
                    valor = numero(modelo.getValueAt(i, 1 + M + j)) - numero(modelo.getValueAt(i, 1 + j));
                } catch (NumberFormatException ex) {
                    valor = "?";
                }
                modelo.setValueAt(valor, i, 1 + 2 * M + j);
            }
        }
        recalculando = false;
    }

    private void cargarEjemplo() {
        int[][] asig = {{0, 1, 0}, {2, 0, 0}, {3, 0, 2}, {2, 1, 1}, {0, 0, 2}};
        int[][] max = {{7, 5, 3}, {3, 2, 2}, {9, 0, 2}, {2, 2, 2}, {4, 3, 3}};
        int[] disp = {3, 3, 2};

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < M; j++) {
                modelo.setValueAt(asig[i][j], i, 1 + j);
                modelo.setValueAt(max[i][j], i, 1 + M + j);
            }
        }
        for (int j = 0; j < M; j++) {
            txtDisp[j].setText("" + disp[j]);
        }
        txtPedido.setText("");
        txtPasos.setText("");
        pintarEstado("Cambia los datos  y presiona \"Verificar estado\"", GRIS);
    }

    private void verificar() {
        Banco banco = leerBanco();
        if (banco != null) {
            mostrar(banco.verificar(), "");
        }
    }

    private void solicitar() {
        Banco banco = leerBanco();
        if (banco == null) {
            return;
        }

        String[] partes = txtPedido.getText().trim().split("[ ,]+");
        int[] pedido = new int[M];
        try {
            if (partes.length != M) {
                throw new NumberFormatException();
            }
            for (int j = 0; j < M; j++) {
                pedido[j] = numero(partes[j]);
            }
        } catch (NumberFormatException ex) {
            error("Escribe " + M + " cantidades separadas por espacio. Ejemplo: 1 0 2");
            return;
        }

        int i = cmbProceso.getSelectedIndex();
        String encabezado = "P" + i + " solicita " + Arrays.toString(pedido) + "\n";

        if (banco.solicitar(i, pedido)) {
            for (int j = 0; j < M; j++) {
                modelo.setValueAt(banco.getProceso(i).getAsignado()[j], i, 1 + j);
                txtDisp[j].setText("" + banco.getDisponible()[j]);
            }
            mostrar(banco.verificar(), encabezado + banco.getMensaje() + "\n\n");
        } else {
            pintarEstado(banco.getMensaje(), ROJO);
            txtPasos.setText(encabezado + banco.getMensaje() + "\nNo se modificó nada.");
        }
    }

    private Banco leerBanco() {
        if (tabla.isEditing()) {
            tabla.getCellEditor().stopCellEditing();
        }
        try {
            int[] disp = new int[M];
            for (int j = 0; j < M; j++) {
                disp[j] = numero(txtDisp[j].getText());
            }
            Banco banco = new Banco(disp);

            for (int i = 0; i < N; i++) {
                int[] asig = new int[M];
                int[] max = new int[M];
                for (int j = 0; j < M; j++) {
                    asig[j] = numero(modelo.getValueAt(i, 1 + j));
                    max[j] = numero(modelo.getValueAt(i, 1 + M + j));
                    if (asig[j] > max[j]) {
                        error("P" + i + " tiene asignado más que su máximo en el recurso " + (char) ('A' + j) + ".");
                        return null;
                    }
                }
                banco.agregar(new Proceso("P" + i, max, asig));
            }
            return banco;
        } catch (NumberFormatException ex) {
            error("solo se aceptan números enteros desde 0.");
            return null;
        }
    }

    private void mostrar(Resultado r, String encabezado) {
        StringBuilder sb = new StringBuilder(encabezado);
        int paso = 1;
        for (String linea : r.getPasos()) {
            sb.append(paso++).append(". ").append(linea).append("\n");
        }

        if (r.esSeguro()) {
            pintarEstado("Estado seguro: " + String.join(" → ", r.getSecuencia()), VERDE);
        } else {
            String bloqueados = String.join(", ", r.getBloqueados());
            sb.append("\nNo pueden terminar: ").append(bloqueados);
            sb.append("\nA ninguno le alcanza lo disponible");
            pintarEstado("Posible interbloqueo entre: " + bloqueados, ROJO);
        }
        txtPasos.setText(sb.toString());
        txtPasos.setCaretPosition(0);
    }

    private void pintarEstado(String texto, Color color) {
        lblEstado.setText(texto);
        lblEstado.setBackground(color);
    }

    private void error(String texto) {
        JOptionPane.showMessageDialog(this, texto, "Aviso", JOptionPane.WARNING_MESSAGE);
    }

    private int numero(Object o) {
        int v = Integer.parseInt(o.toString().trim());
        if (v < 0) {
            throw new NumberFormatException();
        }
        return v;
    }
}
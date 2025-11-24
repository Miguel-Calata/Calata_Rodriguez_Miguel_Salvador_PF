import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Random;

public class VentanaSimon extends JFrame {

    // Panel de los botones
    private JPanel panelPrincipal;
    private JPanel panelBotones; // Panel de los botones
    private JPanel panelColor; // Panel donde se muestran los colores con márgenes


    // Etiqueta de informacion
    private JLabel etiquetainfo; // Etiqueta de informacion

    // Botones de colores
    private JButton botonAzul;
    private JButton botonVerde;
    private JButton botonAmarillo;
    private JButton botonRojo;

    private boolean juegoTerminado = false;

    private static final Color[] colorJuego = // Colores para el juego
            {new Color(0x76C2F1), new Color(0x7CC270), new Color(0xF0CE60), new Color(0xD85946)};

    // Duraciones para la animación
    private static final int DURACION_COLOR = 700;   // ms que dura el color
    private static final int DURACION_NEGRO = 300;   // ms que dura la pantalla negra entre colores

    Random rndJuego = new Random(23);  // semilla que rige la secuencia

    ArrayList<Integer> acumuladoJuego = new ArrayList<>();
    ArrayList<Integer> acumuladoRespuesta = new ArrayList<>();


    public VentanaSimon(){
        super("Simon dice");

        this.setBounds( 100, 100, 600, 400);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Inicializar los componentes
        inicializarComponentes();

        // Gestion de eventos
        gestionDeEventos();

        this.setVisible(true);

        //Mostrar secuencia
        mostrarSecuencia();
    }

    private void inicializarComponentes() {
        // Panel
        panelPrincipal = new JPanel(new BorderLayout());
        panelBotones = new JPanel(new GridLayout(1, 4,10,30));

        // *** Panel de color con márgenes
        panelColor = new JPanel(new BorderLayout());
        panelColor.setBorder(new EmptyBorder(40, 40, 40, 40)); // márgenes alrededor
        panelColor.setBackground(Color.BLACK);

        // etiquetas
        etiquetainfo = new JLabel("");

        // Colores
        botonAzul = new JButton("D");
        botonAzul.setBackground(new Color(0x76C2F1)); // Azul, por ejemplo
        botonAzul.setPreferredSize(new Dimension(120, 60));
        botonAzul.setForeground(Color.WHITE);         // color del texto
        botonAzul.setFocusPainted(false);   // sin borde de foco
        botonAzul.setBorderPainted(false);  // sin borde 3D
        botonAzul.setOpaque(true);          // asegura que pinte el fondo

        botonVerde = new JButton("F");
        botonVerde.setBackground(new Color(0x7CC270)); // verde, por ejemplo
        botonVerde.setPreferredSize(new Dimension(120, 60));
        botonVerde.setForeground(Color.WHITE);         // color del texto
        botonVerde.setFocusPainted(false);   // sin borde de foco
        botonVerde.setBorderPainted(false);  // sin borde 3D
        botonVerde.setOpaque(true);

        botonAmarillo = new JButton("J");
        botonAmarillo.setBackground(new Color(0xF0CE60)); // Amarillo, por ejemplo
        botonAmarillo.setPreferredSize(new Dimension(120, 60));
        botonAmarillo.setForeground(Color.BLACK);         // color del texto
        botonAmarillo.setFocusPainted(false);   // sin borde de foco
        botonAmarillo.setBorderPainted(false);  // sin borde 3D
        botonAmarillo.setOpaque(true);

        botonRojo = new JButton("K");
        botonRojo.setBackground(new Color(0xD85946)); // Rojo, por ejemplo
        botonRojo.setPreferredSize(new Dimension(120, 60));
        botonRojo.setForeground(Color.WHITE);         // color del texto
        botonRojo.setFocusPainted(false);
        botonRojo.setBorderPainted(false);
        botonRojo.setOpaque(true);

        panelBotones.add(botonAzul);
        panelBotones.add(botonVerde);
        panelBotones.add(botonAmarillo);
        panelBotones.add(botonRojo);

        panelBotones.setBorder(new EmptyBorder(10,20,10,20));

        // Configuro la etiqueta de informacion
        etiquetainfo.setText("Introduce la secuencia: ");
        etiquetainfo.setHorizontalAlignment(JLabel.CENTER);
        etiquetainfo.setVerticalAlignment(JLabel.CENTER);
        etiquetainfo.setFont( new Font("Arial", Font.BOLD, 16 ));
        etiquetainfo.setForeground(Color.WHITE);
        etiquetainfo.setVisible(false); //  para que no sea visible mientras se muestra la secuencia

        // *** La etiqueta va dentro del panelColor (que es el panel "central")
        panelColor.add(etiquetainfo, BorderLayout.CENTER);

        // *** El panelColor va en el centro de panelPrincipal
        panelPrincipal.add(panelColor, BorderLayout.CENTER);
        panelPrincipal.setBorder( new EmptyBorder(10,20,10,20));

        add(panelPrincipal, BorderLayout.CENTER);
        add(panelBotones, BorderLayout.SOUTH);

        // *** Fondo general negro
        //panelPrincipal.setBackground(Color.BLACK);
    }

    private void gestionDeEventos() {
        GestionEventosBotones geb = new GestionEventosBotones();

        botonAzul.addActionListener(geb);
        botonVerde.addActionListener(geb);
        botonAmarillo.addActionListener(geb);
        botonRojo.addActionListener(geb);
    }

    private void mostrarSecuencia() {
        if (juegoTerminado) {
            return; // no se ejecuta nadaaaaaaaaaa
        }

        // Dejamos el arreglo de la respuesta vacio para que no influya en las demas respuestas
        acumuladoRespuesta.clear();

        // Desactivar botones mientras se muestra la secuencia
        botonAzul.setEnabled(false);
        botonVerde.setEnabled(false);
        botonAmarillo.setEnabled(false);
        botonRojo.setEnabled(false);
        etiquetainfo.setVisible(false);

        int nuevo = rndJuego.nextInt(4); // 0–3
        acumuladoJuego.add(nuevo);

        System.out.println("Secuencia actual: " + acumuladoJuego);

        // Índice que recorre la secuencia
        final int[] idx = {0};

        //Estado: si estamos mostrando color (true) o negro (false)
        final boolean[] mostrandoColor = {true};

        // Timer que inicia con la duración del color
        Timer timer = new Timer(DURACION_COLOR, null);

        timer.addActionListener(e -> {
            if (idx[0] < acumuladoJuego.size()) {
                if (mostrandoColor[0]) {
                    // Mostrar color
                    int valor = acumuladoJuego.get(idx[0]);
                    System.out.println("Mostrando índice " + idx[0] + " → color " + valor);
                    panelColor.setBackground(colorJuego[valor]);

                    // Siguiente paso: negro, con duración corta
                    mostrandoColor[0] = false;
                    timer.setDelay(DURACION_NEGRO);
                } else {
                    // *** Mostrar negro de separación
                    panelColor.setBackground(Color.BLACK);

                    // Pasamos al siguiente color
                    mostrandoColor[0] = true;
                    idx[0]++;

                    // Siguiente paso: color, con duración normal
                    timer.setDelay(DURACION_COLOR);
                }
            } else {
                // Terminó la secuencia
                timer.stop();
                panelColor.setBackground(Color.BLACK);

                // Volver a activar botones y mostrar etiqueta
                botonAzul.setEnabled(true);
                botonVerde.setEnabled(true);
                botonAmarillo.setEnabled(true);
                botonRojo.setEnabled(true);
                etiquetainfo.setVisible(true);
            }
        });

        // Arranca la animación
        timer.start();
    }

    private void revisarSecuencia(int respuesta) {
        System.out.println("la respueta en lo que sea que sea la ronda es: " + acumuladoJuego.get(acumuladoRespuesta.size()-1));
        System.out.println("respueta dada por el usuario: " + respuesta);

        if (respuesta == acumuladoJuego.get(acumuladoRespuesta.size()-1)){
            System.out.println("Respuesta correcta");
        }
        else {
            System.out.println("Respuesta incorrecta");

            juegoTerminado = true;
            System.out.println("Se equivoco");

            // *** Ponemos en rojo tanto el fondo general como el panel de color
            panelPrincipal.setBackground(Color.RED);
            panelColor.setBackground(Color.RED);

            etiquetainfo.setText("Fin del juego");
            etiquetainfo.setVisible(true);

            botonAzul.setEnabled(false);
            botonVerde.setEnabled(false);
            botonAmarillo.setEnabled(false);
            botonRojo.setEnabled(false);
        }

        System.out.println("\n");
        System.out.println(acumuladoRespuesta.size() + " el tamanio del arreglo de la respuesta");
        System.out.println(acumuladoJuego.size() + "el tamanio del arreglo del juego");

        if (acumuladoRespuesta.size() == acumuladoJuego.size()){
            mostrarSecuencia();
        }
    }

    public void accionBotones(){
        System.out.println("Se agrego " + acumuladoRespuesta.get((acumuladoRespuesta.size()-1)) + " al arreglo acumuladoJuego");
        System.out.println("Respuestas del usuario"+ acumuladoRespuesta);
        System.out.println("Numeros del juego"+ acumuladoJuego);
        System.out.println(acumuladoJuego.size() + "el tamanio del arreglo del juego" + "\n " + acumuladoRespuesta.size() + " el tamanio del arreglo de la respuesta");
        revisarSecuencia(acumuladoRespuesta.get((acumuladoRespuesta.size()-1)));
    }

    private class GestionEventosBotones implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == botonAzul) {
                System.out.println("Azul");
                acumuladoRespuesta.add(0);
                accionBotones();
            } else if (e.getSource() == botonVerde) {
                System.out.println("Verde");
                acumuladoRespuesta.add(1);
                accionBotones();
            } else if (e.getSource() == botonAmarillo) {
                System.out.println("Amarillo");
                acumuladoRespuesta.add(2);
                accionBotones();
            } else if (e.getSource() == botonRojo) {
                System.out.println("Rojo");
                acumuladoRespuesta.add(3);
                accionBotones();
            }
        }
    }
}
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;

public class VentanaSimon extends JFrame {
    // Panel de los botones
    private JPanel panelPrincipal;
    private JPanel panelBotones; // Panel de los botones
    private JPanel panelColor; // Panel donde se muestran los colores con margenes
    private JPanel panelInfo; // Panel donde se muestran las rondas y el puntaje

    // Etiqueta de informacion
    private JLabel etiquetainfo; // Etiqueta de informacion
    private JLabel etiquetaPuntaje; // Etiqueta de puntaje
    private JLabel etiquetaRonda; // Etiqueta de la ronda

    // Botones de colores
    private JButton botonAzul;
    private JButton botonVerde;
    private JButton botonAmarillo;
    private JButton botonRojo;

    private boolean juegoTerminado = false; // Marca si el juego aun esta en curso

    private static final Color[] COLORES_JUEGO = // Colores para el juego
            {new Color(0x76C2F1), new Color(0x7CC270), new Color(0xF0CE60), new Color(0xD85946)};

    // Duraciones para la animación
    private static final int DURACION_COLOR = 700;   // ms que dura el color
    private static final int DURACION_NEGRO = 300;   // ms que dura la pantalla negra entre colores

    Random rndJuego;  // semilla que rige la secuencia

    private String nombreArch = "partida.dat";

    ArrayList<Integer> acumuladoJuego = new ArrayList<>(); // Arreglo dinamico que contiene la secuencia aleatoria del juego
    ArrayList<Integer> acumuladoRespuesta = new ArrayList<>(); // Arreglo dinamico que contiene lo que responde el jugador

    private Partida partidaActual; // La informacion de la partida que se esta jugando


    public VentanaSimon(){
        super("Simon dice");

        this.setBounds( 100, 100, 600, 400);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Cargar icono desde la raiz
        Image icono = Toolkit.getDefaultToolkit().getImage(getClass().getResource("icono.png"));

        setIconImage(icono);

        // Cargar partida o crear una nueva
        partidaActual = cargarPartida();

        // Inicializar los componentes
        inicializarComponentes();

        // Gestion de eventos
        gestionDeEventos();

        // Inicializar el estado del juego (generador de secuencia y secuencia acumulada)
        rndJuego = new Random(partidaActual.getSemilla());
        acumuladoJuego.clear();
        for (int i = 0; i < partidaActual.getRonda(); i++) {
            acumuladoJuego.add(rndJuego.nextInt(4));
        }

        this.setVisible(true);

        if (partidaActual.getNombre().equals("Invitado")) {
            String nombre = JOptionPane.showInputDialog(this, "Introduce tu nombre: ",
                    "Nombre", JOptionPane.PLAIN_MESSAGE);

            if (nombre == null || nombre.isEmpty()) {
                nombre = "Invitado";
            }
            partidaActual.setNombre(nombre);
            guardarPartida();
        }


        // Actualiza el titulo de la ventana con el nombre del jugador
        VentanaSimon.this.setTitle("Simon dice - " + partidaActual.getNombre());

        //Mostrar secuencia
        mostrarSecuencia();
    }

    private void inicializarComponentes() {
        // Creacion de los paneles
        panelPrincipal = new JPanel(new BorderLayout());
        panelBotones = new JPanel(new GridLayout(1, 4,10,30));
        panelInfo = new JPanel(new GridLayout(1,2));
        panelColor = new JPanel(new BorderLayout());

        // Configuracion de paneles
        panelPrincipal.setBorder( new EmptyBorder(10,20,10,20));
        panelBotones.setBorder(new EmptyBorder(10,20,10,20)); // margenes a los contenedores
        panelColor.setBorder(new EmptyBorder(40, 40, 40, 40));
        panelColor.setBackground(Color.BLACK);

        // Creacion de las etiquetas
        etiquetainfo = new JLabel("");
        etiquetaPuntaje = new JLabel("");
        etiquetaRonda = new JLabel("");

        // Creacion y configuracion de los botones de Colores
        botonAzul = new JButton("D");
        botonAzul.setBackground(new Color(0x76C2F1)); // Azul
        botonAzul.setPreferredSize(new Dimension(120, 60));
        botonAzul.setForeground(Color.WHITE);         // color del texto
        botonAzul.setFocusPainted(false);   // sin borde de foco
        botonAzul.setBorderPainted(false);  // sin borde 3D
        botonAzul.setOpaque(true);          // asegura que pinte el fondo

        botonVerde = new JButton("F");
        botonVerde.setBackground(new Color(0x7CC270)); // verde
        botonVerde.setPreferredSize(new Dimension(120, 60));
        botonVerde.setForeground(Color.WHITE);
        botonVerde.setFocusPainted(false);
        botonVerde.setBorderPainted(false);
        botonVerde.setOpaque(true);

        botonAmarillo = new JButton("J");
        botonAmarillo.setBackground(new Color(0xF0CE60)); // Amarillo
        botonAmarillo.setPreferredSize(new Dimension(120, 60));
        botonAmarillo.setForeground(Color.BLACK);
        botonAmarillo.setFocusPainted(false);
        botonAmarillo.setBorderPainted(false);
        botonAmarillo.setOpaque(true);

        botonRojo = new JButton("K");
        botonRojo.setBackground(new Color(0xD85946)); // Rojo
        botonRojo.setPreferredSize(new Dimension(120, 60));
        botonRojo.setForeground(Color.WHITE);
        botonRojo.setFocusPainted(false);
        botonRojo.setBorderPainted(false);
        botonRojo.setOpaque(true);

        // Agregar los botones al panel de botones
        panelBotones.add(botonAzul);
        panelBotones.add(botonVerde);
        panelBotones.add(botonAmarillo);
        panelBotones.add(botonRojo);



        // Configuro la etiqueta de informacion
        etiquetainfo.setText("Introduce la secuencia: ");
        etiquetainfo.setHorizontalAlignment(JLabel.CENTER);
        etiquetainfo.setVerticalAlignment(JLabel.CENTER);
        etiquetainfo.setFont( new Font("Arial", Font.BOLD, 20 ));
        etiquetainfo.setForeground(Color.WHITE);
        etiquetainfo.setVisible(false); //  para que no sea visible mientras se muestra la secuencia

        // Configuracion de la etiqueta de puntaje
        etiquetaPuntaje.setText("Puntaje: " + partidaActual.getPuntaje());
        etiquetaRonda.setText("Ronda: " + (partidaActual.getRonda() + 1));

        // Se agregan los puntajes al panel de informacion
        panelInfo.add(etiquetaRonda);
        panelInfo.add(etiquetaPuntaje);

        // La etiqueta va dentro del panelColor (que es el panel "central")
        panelColor.add(etiquetainfo, BorderLayout.CENTER);

        // El panel de informacion va en el norte de panelPrincipal
        panelPrincipal.add(panelInfo, BorderLayout.NORTH);

        // El panelColor va en el centro de panelPrincipal
        panelPrincipal.add(panelColor, BorderLayout.CENTER);

        // Se agregan los paneles al frame
        add(panelPrincipal, BorderLayout.CENTER);
        add(panelBotones, BorderLayout.SOUTH);
    }

    private void gestionDeEventos() {
        GestionEventosBotones geb = new GestionEventosBotones(); // Se crea el objeto de escucha

        // Se asigna el escuchador a los botones
        botonAzul.addActionListener(geb);
        botonVerde.addActionListener(geb);
        botonAmarillo.addActionListener(geb);
        botonRojo.addActionListener(geb);

        // Eventos de teclado, para poder presionar teclas
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (juegoTerminado) return;

                switch (e.getKeyCode()) {
                    case KeyEvent.VK_D:
                        botonAzul.doClick();
                        break;
                    case KeyEvent.VK_F:
                        botonVerde.doClick();
                        break;
                    case KeyEvent.VK_J:
                        botonAmarillo.doClick();
                        break;
                    case KeyEvent.VK_K:
                        botonRojo.doClick();
                        break;
                }
            }
        });
    }

    private void mostrarSecuencia() {
        if (juegoTerminado) {
            return; // no se ejecuta nadaaaaaaaaaa
        }

        // Deja el arreglo de la respuesta vacio para que no influya en las demas respuestas
        acumuladoRespuesta.clear();

        // Desactivar los botones mientras se muestra la secuencia
        botonAzul.setEnabled(false);
        botonVerde.setEnabled(false);
        botonAmarillo.setEnabled(false);
        botonRojo.setEnabled(false);
        etiquetainfo.setVisible(false);

        // Si el juego no tiene secuencia se establece como 0 la ronda
        if (acumuladoJuego.isEmpty()) {
            partidaActual.setRonda(0);
        }

        // Se asigna un numero aleatorio de nuestro rndJuego a la secuencia
        int nuevo = rndJuego.nextInt(4); // 0–3
        acumuladoJuego.add(nuevo);

        System.out.println("Secuencia actual: " + acumuladoJuego);

        // Índice que recorre la secuencia
        final int[] idx = {0};

        //si estamos mostrando color (true) o negro (false)
        final boolean[] mostrandoColor = {true};

        // Timer que inicia con la duración del color
        Timer timer = new Timer(DURACION_COLOR, null);

        timer.addActionListener(e -> {
            if (idx[0] < acumuladoJuego.size()) {
                if (mostrandoColor[0]) {
                    // Mostrar color
                    int valor = acumuladoJuego.get(idx[0]);
                    System.out.println("Mostrando índice " + idx[0] + " → color " + valor);
                    panelColor.setBackground(COLORES_JUEGO[valor]);

                    // Siguiente paso: negro, con duración corta
                    mostrandoColor[0] = false;
                    timer.setDelay(DURACION_NEGRO);
                } else {
                    // Mostrar negro de separación
                    panelColor.setBackground(Color.BLACK);

                    // Pasamos al siguiente color
                    mostrandoColor[0] = true;
                    idx[0]++;

                    // Siguiente paso: color, con duración normal
                    timer.setDelay(DURACION_COLOR);
                }
            } else {
                // Termino la secuencia
                timer.stop();
                panelColor.setBackground(Color.BLACK);

                // Volver a activar botones y mostrar etiqueta
                botonAzul.setEnabled(true);
                botonVerde.setEnabled(true);
                botonAmarillo.setEnabled(true);
                botonRojo.setEnabled(true);
                etiquetainfo.setVisible(true);
                requestFocusInWindow(); // Devuelve el foco a la venta para que las teclas funcionen
            }
        });
        // Arranca la animación
        timer.start();
    }

    private void revisarSecuencia(int respuesta) {
        System.out.println("la respueta en lo que sea que sea la ronda es: " + acumuladoJuego.get(acumuladoRespuesta.size()-1));
        System.out.println("respueta dada por el usuario: " + respuesta);

        // Evalua si la respuesta es correcta
        if (respuesta == acumuladoJuego.get(acumuladoRespuesta.size()-1)){ // la respuesta del jugador == la respuesta del juego
            System.out.println("Respuesta correcta");
            partidaActual.setPuntaje(partidaActual.getPuntaje() + 1); // se actualiza el puntaje del objeto partida
            etiquetaPuntaje.setText("Puntaje: " + partidaActual.getPuntaje()); // se actualiza la etiqueta del puntaje
        }
        else {
            System.out.println("Respuesta incorrecta");

            juegoTerminado = true;
            System.out.println("Se equivoco");

            // El fondo se pone en rojo
            panelColor.setBackground(Color.RED);

            etiquetainfo.setText("Fin del juego");
            etiquetainfo.setVisible(true);

            // Desactivar los botones
            botonAzul.setEnabled(false);
            botonVerde.setEnabled(false);
            botonAmarillo.setEnabled(false);
            botonRojo.setEnabled(false);

            reiniciar();
        }

        System.out.println("\n");
        System.out.println(acumuladoRespuesta.size() + " el tamanio del arreglo de la respuesta");
        System.out.println(acumuladoJuego.size() + "el tamanio del arreglo del juego");

        // Evaluamos si el juego no ha terminado y si el jugador ya termino la ronda
        if (!juegoTerminado && (acumuladoRespuesta.size() == acumuladoJuego.size())){
            partidaActual.setRonda(acumuladoJuego.size());
            guardarPartida(); // guardamos la partida
            etiquetaRonda.setText("Ronda: " + (acumuladoJuego.size() + 1)); // actualizamos ronda
            mostrarSecuencia(); // mostramos la secuencia
        }
    }

    /***
     * No lo voy a negar, este metodo es medio innecesario pero lo ignorare por el momento...
     */
    public void accionBotones(){
        System.out.println("Se agrego " + acumuladoRespuesta.get((acumuladoRespuesta.size()-1)) + " al arreglo acumuladoJuego");
        System.out.println("Respuestas del usuario"+ acumuladoRespuesta);
        System.out.println("Numeros del juego"+ acumuladoJuego);
        System.out.println(acumuladoJuego.size() + "el tamanio del arreglo del juego" + "\n " + acumuladoRespuesta.size()
                + " el tamanio del arreglo de la respuesta");
        revisarSecuencia(acumuladoRespuesta.get((acumuladoRespuesta.size()-1))); // mandamos la respuesta del usuario a revisarSecuencia
    }

    /***
     * Metodo para reiniciar la partida, cuando el jugador se equivoca, la partida se reinicia o la aplicacion se detiene
     */
    public void reiniciar(){
        // Pregunta al usuario si quiere continuar con otra partida
        int respuesta = JOptionPane.showConfirmDialog(this,"Quieres jugar otra partida?",
                "Fin del juego, quieres continuar?", JOptionPane.YES_NO_OPTION);

        // En caso de que la opcion sea si, se reinicia la partida
        if (respuesta == JOptionPane.YES_OPTION){
            String nombreActual = partidaActual.getNombre(); // dejamos el mismo nombre
            partidaActual = new Partida(); // creamos una nueva partida
            partidaActual.setNombre(nombreActual); //
            partidaActual.setSemilla(System.currentTimeMillis()); // Nueva semilla
            guardarPartida(); // guardamos la partida
            acumuladoRespuesta.clear(); // limpiamos los arreglos
            acumuladoJuego.clear();
            rndJuego = new Random(partidaActual.getSemilla()); // Asignamos la nueva semilla al rndJuego
            juegoTerminado = false;
            panelColor.setBackground(Color.BLACK);
            etiquetainfo.setVisible(false);
            etiquetainfo.setText("Introduce la secuencia: ");
            etiquetaPuntaje.setText("Puntaje: " + partidaActual.getPuntaje());
            etiquetaRonda.setText("Ronda: " + (partidaActual.getRonda() + 1));
            mostrarSecuencia();
        } else if (respuesta == JOptionPane.NO_OPTION) {
            // Reiniciar el estado de la partida guardada
            partidaActual = new Partida(); // crea una partida vacia
            guardarPartida(); // guarda
            System.exit(0); // sale de la aplicacion
        }
    }

    /***
     * Metodo para cargar la partida guardada en el archivo partida.dat
     */
    private Partida cargarPartida() {
        Partida partidaCargada = new Partida(); // Crear objeto con la partida por omisión
        Path ruta = Paths.get(nombreArch);

        // Verificar si no existe el archivo
        if (!Files.exists(ruta)) {
            return partidaCargada; // Regresar la partida nueva
        }

        // Recuperar el objeto del archivo
        try (ObjectInputStream entradaArch = new ObjectInputStream(Files.newInputStream(ruta))) {
            partidaCargada = (Partida) entradaArch.readObject();

            // Si la partida tiene progreso, preguntar si se quiere continuar
            if (partidaCargada.getRonda() > 0) {
                int respuesta = JOptionPane.showConfirmDialog(this,
                        "Se encontró una partida guardada. ¿Quieres continuarla?",
                        "Partida guardada",
                        JOptionPane.YES_NO_OPTION);

                if (respuesta == JOptionPane.NO_OPTION) {
                    partidaCargada = new Partida(); // Crear una partida desde cero
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar la partida guardada.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        return partidaCargada;
    }


    private void guardarPartida() {
        // Usar try con recursos para asegurar que el archivo se cierre correctamente
        try (ObjectOutputStream salidaArch = new ObjectOutputStream(Files.newOutputStream(Paths.get(nombreArch)))) {
            salidaArch.writeObject(partidaActual);
        } catch (Exception e) {
            // Mostrar un mensaje de error al usuario si algo sale mal
            JOptionPane.showMessageDialog(this, "Error al guardar la partida.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    /***
     * Clase privada que gestiona los eventos de los botones
     */

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
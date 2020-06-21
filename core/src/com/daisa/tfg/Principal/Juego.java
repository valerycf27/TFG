package com.daisa.tfg.Principal;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.daisa.tfg.Constantes.ConstantesJuego;
import com.daisa.tfg.Personajes.Personaje;
import com.daisa.tfg.Screens.ConectarJugadoresScreen;
import com.daisa.tfg.Screens.ElegirPersonajeScreen;
import com.daisa.tfg.Screens.LoginScreen;
import com.daisa.tfg.Screens.MenuPrincipalScreen;
import com.daisa.tfg.Screens.RankingScreen;
import com.daisa.tfg.Util.JuegoAssetManager;

public class Juego extends Game {

    public SpriteBatch batch;
    public ExtendViewport viewport;
    public OrthographicCamera camera;

    public Preferencias preferencias;

    public JuegoAssetManager manager;

    private BluetoothCallBack bluetoothCallBack;
    private FirebaseCallBack firebaseCallBack;
    private Array<String> nombreDispositivosVisibles;
    public ConectarJugadoresScreen conectarJugadoresScreen;
    public RankingScreen rankingScreen;
    Juego juego;

    public boolean yoPreparado = false, rivalPreparado = false;
    private int miPuntuacion = 0, rivalPuntuacion = 0;
    private String nombreUsuario = null;

    private TextureRegion fondoMenu;

    @Override
    public void create() {
        Gdx.app.setLogLevel(Application.LOG_DEBUG);
        batch = new SpriteBatch();
        //Se utiliza una camara y un viewport para escalar las Screens en los diferentes dispositivos
        camera = new OrthographicCamera(1920, 1080);
        viewport = new ExtendViewport(720, 1280, camera);

        juego = this;
        preferencias = new Preferencias();

        manager = new JuegoAssetManager();

        fondoMenu = new TextureRegion(new Sprite(new Texture("Fondos/fondoMenus.jpg")));

        setScreen(new LoginScreen(juego));
    }

    @Override
    public void dispose() {
        manager.managerJuego.dispose();
    }


    public int getMiPuntuacion() {
        return miPuntuacion;
    }

    public void setMiPuntuacion(int miPuntuacion) {
        this.miPuntuacion = miPuntuacion;
    }

    public int getRivalPuntuacion() {
        return rivalPuntuacion;
    }

    public void setRivalPuntuacion(int rivalPuntuacion) {
        this.rivalPuntuacion = rivalPuntuacion;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public TextureRegion getFondoMenu() {
        return fondoMenu;
    }

    public Preferencias getPreferencias(){
        return this.preferencias;
    }

    public void setBluetoothCallBack(BluetoothCallBack callback) {
        bluetoothCallBack = callback;
    }

    public void setFirebaseCallBack(FirebaseCallBack firebaseCallBack) {
        this.firebaseCallBack = firebaseCallBack;
    }

    /**
     * Envia una lista actualizada de dispositivos Bluetooth
     */
    public void refrescarListaDispositivos() {
        conectarJugadoresScreen.refrescarLista(nombreDispositivosVisibles);
    }

    /**
     * Se actualizan los nombres de los dispositivos disponibles
     * @param nombreDispositivosVisibles lista actualizada de nombres
     */
    public void anadirDispositivo(Array<String> nombreDispositivosVisibles) {
        this.nombreDispositivosVisibles = nombreDispositivosVisibles;
    }

    /**
     * Se envia un mensaje al rival para indicar que ya se ha elegido un personaje
     */
    public void comenzarPartida() {
        this.writeLIBGDX("true");
    }

    /**
     * Se recibe el mensaje de que el rival ya ha elegido su personaje
     * @param readMessage
     */
    public void rivalPreparado(String readMessage) {
        rivalPreparado = Boolean.parseBoolean(readMessage);
    }

    /**
     * Se leen los datos de la bala del rival
     * @param readMessage datos que se han recibido
     */
    public void balaRecibida(String readMessage) {
        try{
            String[] mensaje = readMessage.split(ConstantesJuego.SEPARADOR_DATOS);
            final float balaX = Float.parseFloat(mensaje[0]);
            final int idPJRival = Integer.parseInt(mensaje[1]);
            final int tamanoBala = Integer.parseInt(mensaje[2]);
            Gdx.app.postRunnable(new Runnable() {
                @Override
                public void run() {
                    Personaje.anadirBalaRival(balaX, idPJRival, tamanoBala);
                }
            });
        }catch(Exception e){
            Gdx.app.debug("DEBUG", "[ERROR] Error al convertir los datos dela bala rival");
        }
    }

    /**
     * Cuando se pierde la conexion con el rival, se vuelve a la pantalla principal
     */
    public void conexionPerdida() {
        irAMenuPrincipal();
    }

    /**
     * Se pone en el hilo principal a MenuPrincipalScreen
     */
    public void irAMenuPrincipal() {
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                setScreen(new MenuPrincipalScreen(juego));
            }
        });
    }

    /**
     * Cuando se pierde la conexion con el rival, se vuelve a la pantalla principal
     */
    public void rivalDesconectado() {
        irAMenuPrincipal();
    }

    /**
     * Se envia una lista actualizada de las puntuaciones
     * @param ranking nueva lista conlas puntuaciones
     */
    public void refrescarListaRanking(Array<String> ranking) {
        rankingScreen.refrescarLista(ranking);
    }

    /**
     * Se pone en el hilo principal a ConectarJugadoresScreen
     */
    public void irAConectarJugadores() {
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                conectarJugadoresScreen = new ConectarJugadoresScreen(juego);
                setScreen(conectarJugadoresScreen);
            }
        });
    }

    /**
     * Se pone en el hilo principal a ElegirPersonajeScreen
     */
    public void elegirPersonajes(){
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                reproducirSonido(manager.managerJuego.get(ConstantesJuego.SONIDO_FIN_PARTIDA, Sound.class));
                setScreen(new ElegirPersonajeScreen(juego));
            }
        });
    }

    /**
     * Metodo para reproducir la musica pasada por parammetro
     * @param musica musica que se quiere reproducir
     */
    public void reproducirMusica(Music musica){
        if(preferencias.musicaActivada() && !musica.isPlaying()){
            Gdx.app.debug("DEBUG", "Musica activada con volumen: " + preferencias.volumenMusica());
            musica.play();
        }
    }

    /**
     * Metodo para reproducir el sonido pasado por parammetro
     * @param sonido sonido que se quiere reproducir
     */
    public void reproducirSonido(Sound sonido){
        if(preferencias.sonidosActivados()){
            Gdx.app.debug("DEBUG", "Sonido con volumen: " + preferencias.volumenSonidos());
            sonido.play(preferencias.volumenSonidos());
        }
    }

    /**
     * <p>
     *      Interfaz que se utiliza para la comunicacion entre el ServicioBluetooth y LIBGDX
     * </p>
     * <p>
     *     Metodos:
     *     <p>{@link #activityForResultBluetooth()}</p>
     *     <p>{@link #conectarDispositivosBluetooth(String nombreDispositivo)}</p>
     *     <p>{@link #habilitarSerDescubiertoBluetooth()}</p>
     *     <p>{@link #bluetoothEncendido()}</p>
     *     <p>{@link #descubrirDispositivosBluetooth()}</p>
     *     <p>{@link #escucharBluetooth()}</p>
     *     <p>{@link #write(String string)}</p>
     *     <p>{@link #stop()}</p>
     * </p>
     */
    public interface BluetoothCallBack {
        /**
         * Crea un ActivityForResult en el que se solicita al usuario que permita el uso del Bluetooth
         */
        void activityForResultBluetooth();
        /**
         * Se busca el dispositivo cuyo nombre es el que se ha pulsado en la Screen
         * @param nombreDispositivo nombre del dispositivo que se ha pulsado
         */
        void conectarDispositivosBluetooth(String nombreDispositivo);
        /**
         * Crea una activity en la que se pregunta al usuario si quiere que el dispositivo sea descubierto.
         * A partir de la API 25, se permite descubir al dispositivo sin necesidad de que el usuario lo autorice.
         */
        void habilitarSerDescubiertoBluetooth();
        /**
         * Comprueba si el Bluetooth esta encendido
         * @return true si el Bluetooth esta encendido,
         * false si no
         */
        boolean bluetoothEncendido();
        /**
         * Comienza a descubrir dispositivos Bluetooth cercanos
         */
        void descubrirDispositivosBluetooth();
        /**
         * Se crea una instancia de HiloAceptar en la que se espera que otros dispositivos quieran emparejarse.
         * Estado pasa a ESCUCHANDO
         */
        void escucharBluetooth();
        /**
         * Se mandan los datos del mensaje a través de una instancia de HiloConectado.
         * Solo se envia el mensaje si estamos conectados.
         * @param mensaje datos que se envian
         */
        void write(String mensaje);
        /**
         * Limpia los valores de los dispositivos encontrados previamente y para todos los hilos activos
         * Estado pasa a NULO
         */
        void stop();
    }

    /**
     * Llama a {@link BluetoothCallBack#activityForResultBluetooth()}
     */
    public void activityForResultBluetoothLIBGDX(){
        bluetoothCallBack.activityForResultBluetooth();
    }

    /**
     * Llama a {@link BluetoothCallBack#conectarDispositivosBluetooth(String nombreDispositivo)}
     */
    public void conectarDispositivosBluetoothLIBGDX(String nombreDispositivo){
        bluetoothCallBack.conectarDispositivosBluetooth(nombreDispositivo);
    }

    /**
     * Llama a {@link BluetoothCallBack#habilitarSerDescubiertoBluetooth()}
     */
    public void habilitarSerDescubiertoBluetoothLIBGDX(){
        bluetoothCallBack.habilitarSerDescubiertoBluetooth();
    }

    /**
     * Llama a {@link BluetoothCallBack#bluetoothEncendido()}
     */
    public boolean bluetoothEncendidoLIBGDX() {
        return bluetoothCallBack.bluetoothEncendido();
    }

    /**
     * Llama a {@link BluetoothCallBack#descubrirDispositivosBluetooth()}
     */
    public void descubrirDispositivosBluetoothLIBGDX() {
        bluetoothCallBack.descubrirDispositivosBluetooth();
    }

    /**
     * Llama a {@link BluetoothCallBack#escucharBluetooth()}
     */
    public void escucharBluetoothLIBGDX() {
        bluetoothCallBack.escucharBluetooth();
    }

    /**
     * Llama a {@link BluetoothCallBack#write(String mensaje)}
     */
    public void writeLIBGDX(String mensaje){
        bluetoothCallBack.write(mensaje);
    }

    /**
     * Llama a {@link BluetoothCallBack#bluetoothEncendido()}
     */
    public void stopLIBGDX(){
        bluetoothCallBack.stop();
    }

    /**
     * <p>
     *      Interfaz que se utiliza para la comunicacion entre el ServicioFireBase y LIBGDX
     * </p>
     * <p>
     *     Metodos:
     *     <p>{@link #comprobacionUsuario(String nombreUsuario, String contrasena, Procedencia procedencia)}</p>
     *     <p>{@link #pintarToast(String mensaje)}</p>
     *     <p>{@link #recogerPuntuaciones()}</p>
     * </p>
     */
    public interface FirebaseCallBack{
        /**
         * Se encarga de comprobar que el usuario y la contraseña son correctos y cumplen con los requisitos
         * @param nombreUsuario nombre de usuario que se comprueba si existe o no
         * @param contrasena contraseña que se comprueba si coincide y si cumple las normas
         * @param procedencia comprueba de donde se ha llamado a este metodo
         * @see Juego.Procedencia
         */
        void comprobacionUsuario(String nombreUsuario, String contrasena, Procedencia procedencia);

        /**
         * Muestra un toast al usuario
         * @param mensaje mensaje que se quiere mostrar
         */
        void pintarToast(String mensaje);

        /**
         * Se encarga de recuperar las puntuaciones de la BBDD y las ordena de mayor a menor
         * para luego mostrarlas en el ranking.
         */
        void recogerPuntuaciones();
    }

    /**
     * Llama a {@link FirebaseCallBack#comprobacionUsuario(String nombreUsuario, String contrasena, Procedencia procedencia)}
     */
    public void comprobacionUsuarioLIBGDX(String nombreUsuario, String contrasena, Procedencia procedencia){
        firebaseCallBack.comprobacionUsuario(nombreUsuario, contrasena, procedencia);
    }

    /**
     * Llama a {@link FirebaseCallBack#crearToastLIBGDX(String mensaje)}
     */
    public void crearToastLIBGDX(String mensaje) {
        firebaseCallBack.pintarToast(mensaje);
    }

    /**
     * Llama a {@link FirebaseCallBack#recogerPuntuaciones()}
     */
    public void recogerPuntuacionesLibGDX() {
        firebaseCallBack.recogerPuntuaciones();
    }

    public enum Procedencia{
        LOGIN_SCREEN, REGISTRO_SCREEN
    }
}

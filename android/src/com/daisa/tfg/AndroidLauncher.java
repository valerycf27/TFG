package com.daisa.tfg;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.utils.Array;
import com.daisa.tfg.Constantes.ConstantesBluetooth;
import com.daisa.tfg.Constantes.ConstantesJuego;
import com.daisa.tfg.Principal.Juego;
import com.daisa.tfg.Screens.ConectarJugadoresScreen;

import java.util.LinkedHashSet;
import java.util.Set;

public class AndroidLauncher extends AndroidApplication {

    ServicioBluetooth servicioBluetooth;
    ServicioFirebase servicioFirebase;
    IntentFilter filtroEncontradoDispositivo = new IntentFilter(BluetoothDevice.ACTION_FOUND);

    AndroidLauncher androidLauncher;
    Juego juego;
    UtilAndroid utilAndroid;
    public static Array<String> nombreDispositivosVisibles = new Array<>();
    Set<BluetoothDevice> SetDispositivosVisibles = new LinkedHashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        androidLauncher = this;

        // Oculta la barra de navegación y la de estado
        View decorView = getWindow().getDecorView();
        int opciones = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(opciones);

        utilAndroid = new UtilAndroid();
        juego = new Juego();
        servicioFirebase = new ServicioFirebase(androidLauncher, juego, androidLauncher, handler);
        servicioBluetooth = new ServicioBluetooth(androidLauncher, juego, androidLauncher, handler);

        //Se registra el objeto que se encargará de descubrir a los dispositivos Bluetooth cercanos
        androidLauncher.registerReceiver(mReceiver, filtroEncontradoDispositivo);

        initialize(juego, config);
    }

    //Se ocultan los botones y la barra de navegación cuando se vuelva al juego depués de usar otras aplicaciones
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
                ocultarBotonesVirtuales();
    }

    /**
     * Oculta los botones virtuales y la barra de navegación en dispositivos a partir
     * de Android KITKAT.
     */
    @TargetApi(19)
    private void ocultarBotonesVirtuales() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        /*
        Se comprueba si el ActivityForResult es el que pide activar el Bluetooth:
            -Si el usuario lo permite, se va a la Screen para elegir el dispositivo al que se quiera conectar
            -Si no, se le comunica mediante un toast que no se puede jugar sin Bluetooth
         */
        if (requestCode == ConstantesBluetooth.SOLICITAR_BLUETOOTH) {
            if (resultCode == Activity.RESULT_OK) {
                Log.d("DEBUG", "AndroidLauncher::Se permite el Bluetooth");
                juego.irAConectarJugadores();
            } else
                Toast.makeText(this, "No se puede jugar sin bluetooth", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    /*Objeto utilizado para la comunicación entre los hilos generados (Bluetooth, Firebase)
        y el hilo principal.
        Este objeto es necesario ya que el hilo principal es el unico que puede modificar datos de su UI
        Ejemplo: escribir un toast, cambiar de Screen...
     */
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                //Caso en el que se recibe un mensaje por Bluetooth
                case ConstantesBluetooth.LEER_MENSAJE:
                    byte[] bufferLeido = (byte[]) msg.obj;
                    //Se construye el mensaje a partir de buffer
                    String mensajeRecibido = new String(bufferLeido, 0, msg.arg1);

                    switch (mensajeRecibido) {
                        //Caso en el que el rival ya ha elegido a su personaje para jugar
                        case "true":
                            Toast.makeText(androidLauncher, "El rival ha elegido", Toast.LENGTH_SHORT).show();
                            juego.rivalPreparado(mensajeRecibido);
                            break;
                         /*
                          Caso en el que se ha terminado la partida por lo que se actualiza la puntuación del jugador
                          tanto en la variable que almacena la puntuación de la partida actual como en
                          la BBDD para el ranking
                         */
                        case "fin":
                            juego.setMiPuntuacion(juego.getMiPuntuacion() + 1);
                            servicioFirebase.guardarPuntuacionBBDD(juego.getNombreUsuario() , 1);
                            juego.elegirPersonajes();
                            break;
                        //Caso en el que se reciben los datos de la bala a pintar
                        default:
                            juego.balaRecibida(mensajeRecibido);
                            break;
                    }
                    break;

                //Caso en el que se quiere comunicar al usuario mediante un toast el dispositivo al que se ha concetado
                case ConstantesBluetooth.MENSAJE_NOMBRE_DISPOSITIVO:
                    CharSequence dispositivoConectado = "Conectado con " + msg.getData().getString(ConstantesBluetooth.NOMBRE_DISPOSITIVO);
                    Toast.makeText(androidLauncher, dispositivoConectado, Toast.LENGTH_SHORT).show();
                    juego.elegirPersonajes();
                    break;
                //Caso en el que se quiere pintar un toast con un mensaje personalizado
                case ConstantesBluetooth.MENSAJE_TOAST:
                    CharSequence content = msg.getData().getString(ConstantesBluetooth.TOAST);
                    Toast.makeText(androidLauncher, content, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    /*
    Objeto que se encargará de registrar los dispositivos Bluetooth cercanos:
        -Si tienen nombre, se les añadirá a la lista de los dispositivos a los que podríamos conectarnos.
            -Cada vez que se registra un nuevo dispositivo con nombre, se refresca la lista de los
            dispositivos cercanos en ConectarJugadoresScreen.
     */
    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                if (device != null) {
                    String nombreDispositivo = device.getName();
                    if (nombreDispositivo != null) {
                        if (SetDispositivosVisibles.add(device)) {
                            Log.d("DEBUG", "AndroidLauncher::Dispositivo añadido a la lista: " + nombreDispositivo);
                            nombreDispositivosVisibles.add(nombreDispositivo);
                            juego.anadirDispositivo(nombreDispositivosVisibles);
                            juego.refrescarListaDispositivos();
                        }
                    } else {
                        Log.d("DEBUG", "AndroidLauncher::Dispositivo sin nombre, no se muestra en la lista");
                    }
                } else {
                    Log.d("DEBUG", "AndroidLauncher::[ERROR] al recibir nombre del dispositivo");
                }
            }
        }
    };
}
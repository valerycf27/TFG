package com.daisa.tfg;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.daisa.tfg.Constantes.ConstantesBluetooth;
import com.daisa.tfg.Principal.Juego;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.UUID;

import static com.daisa.tfg.ServicioBluetooth.EstadosBluetooth.*;

/**
 * Clase que se encarga de todas las acciones relacionadas con el Bluetooth
 */
public class ServicioBluetooth implements Juego.BluetoothCallBack {

    final BluetoothAdapter bluetoothAdapter;
    private final Activity activity;
    private final Handler handler;
    Juego juego;
    AndroidLauncher androidLauncher;
    EstadosBluetooth estado;
    HiloAceptar hiloAceptar;
    HiloConectar hiloConectar;
    HiloConectado hiloConectado;

    boolean esHost;

    /**
     * @param activity activity de la aplicacion
     * @param juego objeto usado para cominarse con LIBGDX
     * @param androidLauncher objeto usado para acceder a los objetos de AndroiLauncher
     * @param handler objeto usado para la comunicacion entre los hilos y el hilos principal
     */
    public ServicioBluetooth(Activity activity, Juego juego, AndroidLauncher androidLauncher, Handler handler) {
        this.activity = activity;
        this.juego = juego;
        this.androidLauncher = androidLauncher;
        this.handler = handler;

        juego.setBluetoothCallBack(this);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        //Si el adaptador es nulo, significa que no se soporta el Bluetooth
        if (bluetoothAdapter == null) {
            Toast.makeText(this.activity, "No se puede jugar sin bluetooth", Toast.LENGTH_LONG).show();
            activity.finish();
        }

        estado = NULO;
    }

    //Hilo que se encarga de realizar las conexiones entre los dispositivos (SERVIDOR)
    private class HiloAceptar extends Thread {
        //Socket que permitirá que otros dispositivos se conecten a este
        private final BluetoothServerSocket serverSocket;

        public HiloAceptar() {
            BluetoothServerSocket tmpServerSocket = null;
            try {
                //Se asigna un nombre y un identificador único que permitirá que los dispositivos se conecten
                tmpServerSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord("TFG", UUID.fromString("DDD59690-4FBA-11E2-BCFD-0800200C9A66"));

            } catch (IOException e) {
                Log.d("DEBUG", "accept() ha fallado");
            }
            serverSocket = tmpServerSocket;
        }

        @Override
        public void run() {
            BluetoothSocket socket;
            while (estado != CONECTADO) {
                try {
                    //se bloquea el hilo hasta que se establezca una conexión (o de un error)
                    socket = serverSocket.accept();
                } catch (IOException e) {
                    Log.d("DEBUG", "accept() ha fallado");
                    break;
                }
                if (socket != null) {
                    Log.d("DEBUG", "Conexión aceptada :" + estado);

                    //Se sincroniza para que ningún hilo pueda modificar el socket
                    synchronized (ServicioBluetooth.this) {
                        switch (estado) {
                            case ESCUCHANDO:
                            case CONECTANDO:
                                // Comienza el hilo conectado
                                conectado(socket, socket.getRemoteDevice());
                                break;
                            //Si el estado es NULO o CONECTADO, se cierra el socket
                            case NULO:
                            case CONECTADO:
                                try {
                                    socket.close();
                                } catch (IOException e) {
                                    Log.d("DEBUG", "[ERROR] No se ha podido cerrar el socket");
                                }
                                break;
                        }
                    }
                    try {
                        //Se cierra el socket para evitar problemas de sincronización
                        serverSocket.close();
                    } catch (IOException e) {
                        Log.d("DEBUG", "[ERROR] No se ha podido cerrar el serverSocket");
                    }
                    break;
                }
            }
        }

        /**
         * Cierra el socket actual
         */
        public void cancel() {
            try {
                serverSocket.close();
            } catch (IOException e) {
                Log.d("DEBUG", "[ERROR] No se ha podido cerrar el serverSocket");
            }
        }
    }

    //Hilo que se encarga de crear una conexión segura (CLIENTE)
    private class HiloConectar extends Thread {

        private final BluetoothSocket socket;
        private final BluetoothDevice bluetoothDevice;

        public HiloConectar(BluetoothDevice device) {
            BluetoothSocket tmpSocket = null;
            bluetoothDevice = device;

            try {
                //Se crea un socket seguro entre los dispositivos mediante el uso de una clave única
                tmpSocket = device.createRfcommSocketToServiceRecord(UUID.fromString("DDD59690-4FBA-11E2-BCFD-0800200C9A66"));
            } catch (IOException e) {
                Log.d("DEBUG", "[ERROR] No se puede crear una conexión segura entre los dispositivos");
            }
            socket = tmpSocket;
        }

        public void run() {

            //Siempre hay que cancelar el descubrimiento de nuevos dispositivos porque ralentizaría la conexión
            //Fuente: Android Docs
            bluetoothAdapter.cancelDiscovery();

            try {
                socket.connect();
            } catch (IOException connectException) {
                try {
                    socket.close();
                } catch (IOException closeException) {
                    Log.d("DEBUG", "[ERROR] No se ha podido cerrar el socket");
                }
                conexiónFallida();
                return;
            }

            //Se resetea el hilo porque ya se ha establecido la conexión entre dispositivos
            synchronized (ServicioBluetooth.this) {
                hiloConectar = null;
            }

            conectado(socket, bluetoothDevice);
        }

        /**
         * Cierra el socket actual
         */
        public void cancel() {
            try {
                socket.close();
            } catch (IOException e) {
                Log.d("DEBUG", "[ERROR] No se ha podido cerrar el socket");
            }
        }
    }

    //Hilo que se encarga de la transmisión de mensajes entre los dispositivos
    private class HiloConectado extends Thread {
        private final BluetoothSocket socket;
        private final InputStream inputStream;
        private final OutputStream outputStream;

        public HiloConectado(BluetoothSocket socket) {
            this.socket = socket;
            InputStream tmpInputStream = null;
            OutputStream tmpOutputStream = null;

            try {
                Log.d("DEBUG", "HiloConectado::Se crea el input y el output");
                tmpInputStream = this.socket.getInputStream();
                tmpOutputStream = this.socket.getOutputStream();
            } catch (IOException e) {
                Log.d("DEBUG", "[ERROR] No se ha podido crear el input o el output");
            }

            inputStream = tmpInputStream;
            outputStream = tmpOutputStream;
        }

        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;

            while (true) {
                try {
                    //Se leen los datos del inputStream
                    bytes = inputStream.read(buffer);
                    //Se envían esos datos al hilo principal
                    handler.obtainMessage(ConstantesBluetooth.LEER_MENSAJE, bytes, -1, buffer).sendToTarget();
                } catch (IOException e) {
                    conexionPerdida();
                    break;
                }
            }
        }

        /**
         * Envía los datos a través del outputStream
         *
         * @param buffer datos a enviar
         */
        public void write(byte[] buffer) {
            try {
                outputStream.write(buffer);
            } catch (IOException e) {
                Log.d("DEBUG", "[ERROR] No se ha podido enviar el mensaje");
            }
        }

        /**
         * Cierra el socket actual
         */
        public void cancel() {
            try {
                socket.close();
            } catch (IOException e) {
                Log.d("DEBUG", "[ERROR] No se ha podido cerrar el socket");
            }
        }
    }

    /**
     * Crea una instancia de HiloConectar para conectarse con un dispositivo.
     * @param dispositivo dispositivo al que se quiere conectar
     */
    public synchronized void conectarDispositivos(BluetoothDevice dispositivo) {
        esHost = false;

        //Cancela cualquier hilo que quiera crear una nueva conexión
        if (estado == CONECTANDO) {
            if (hiloConectar != null) {
                hiloConectar.cancel();
                hiloConectar = null;
            }
        }
        //Cancela cualquier hilo que tenga una conexión activa
        if (hiloConectado != null) {
            hiloConectado.cancel();
            hiloConectado = null;
        }

        hiloConectar = new HiloConectar(dispositivo);
        hiloConectar.start();
        estado = CONECTANDO;
    }

    /**
     * Se crea una instancia de HiloConectado para comenzar la transmision de mensajes.
     * @param socket socket por el que se ha va a realizar la conexion
     * @param dispositivo dispositivo al que se ha conectado
     */
    private synchronized void conectado(BluetoothSocket socket, BluetoothDevice dispositivo) {
        pararHilosActivos();

        //Se crea el hilo para la transmisión de mensajes
        hiloConectado = new HiloConectado(socket);
        hiloConectado.start();

        conexionRealizada(dispositivo.getName());
    }

    /**
     * Se comunica al hilo principal el dispositivo con el que se ha realizado la conexion.
     * Estado pasa a CONECTADO
     * @param nombreDispositivo dispositivo al que se ha conectado
     */
    public void conexionRealizada(String nombreDispositivo){
        Message msg = handler.obtainMessage(ConstantesBluetooth.MENSAJE_NOMBRE_DISPOSITIVO);
        Bundle bundle = new Bundle();
        bundle.putString(ConstantesBluetooth.NOMBRE_DISPOSITIVO, nombreDispositivo);
        msg.setData(bundle);
        handler.sendMessage(msg);
        estado = CONECTADO;
    }

    /**
     * Comunica al hilo principal que se ha perdido la conexión con el dispositivo al que se había conectado.
     * Estado pasa a NULO
     */
    private void conexionPerdida() {
        Message msg = handler.obtainMessage(ConstantesBluetooth.MENSAJE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(ConstantesBluetooth.TOAST, "El rival se ha desconectado");
        msg.setData(bundle);
        handler.sendMessage(msg);

        estado = NULO;
        juego.rivalDesconectado();
    }

    /**
     * Comunica al hilo principal para indicar que ha habido un fallo durante la conexión entre los dispositivos.
     * Estado pasa a NULO
     */
    private void conexiónFallida() {
        Message msg = handler.obtainMessage(ConstantesBluetooth.MENSAJE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(ConstantesBluetooth.TOAST, "Error al conectar dispositivos");
        msg.setData(bundle);
        handler.sendMessage(msg);

        estado = NULO;
        juego.conexionPerdida();
    }

    @Override
    public void activityForResultBluetooth() {
        Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        activity.startActivityForResult(enableIntent, ConstantesBluetooth.SOLICITAR_BLUETOOTH);
    }

    @Override
    public void conectarDispositivosBluetooth(String nombreDispositivo) {
        ArrayList<BluetoothDevice> list = new ArrayList<>(androidLauncher.SetDispositivosVisibles);

        int pos = -1;
        for (BluetoothDevice device : list) {
            if (device.getName().equals(nombreDispositivo)) {
                pos = list.indexOf(device);
            }
        }
        conectarDispositivos(list.get(pos));
    }

    @Override
    public void habilitarSerDescubiertoBluetooth() {
        Log.d("DEBUG", "ServicioBluetooth::Se permite que se descubra al dispositivo");
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 3600);
        activity.startActivity(intent);
    }

    @Override
    public boolean bluetoothEncendido() {
        Log.d("DEBUG", "¿Bluetooth Encencido? " + bluetoothAdapter.isEnabled());
        return bluetoothAdapter.isEnabled();
    }

    @Override
    public void descubrirDispositivosBluetooth() {
        Log.d("DEBUG", "ServicioBluetooth::Se comienza a buscar dispositivos");
        //Se cancelan todas las busquedas activas
        if (bluetoothAdapter.isDiscovering())
            bluetoothAdapter.cancelDiscovery();

        //Se comprueba si se ha empezado a descubrir dispositivos correctamente
        if (bluetoothAdapter.startDiscovery())
            Log.d("DEBUG", "ServicioBluetooth::Busqueda de dispositivos empezada correctamente");
        else
            Log.d("DEBUG", "ServicioBluetooth::[ERROR] al comenzar la busqueda de dispositivos");
    }

    @Override
    public void escucharBluetooth() {
        esHost = true;

        //Cancela cualquier hilo que quiera crear una nueva conexión
        if (hiloConectar != null) {
            hiloConectar.cancel();
            hiloConectar = null;
        }

        //Cancela cualquier hilo que tenga una conexión activa
        if (hiloConectado != null) {
            hiloConectado.cancel();
            hiloConectado = null;
        }

        //Empieza un hilo a escucha en el BluetoothServerSocket
        if (hiloAceptar == null) {
            hiloAceptar = new HiloAceptar();
            hiloAceptar.start();
        }

        estado = ESCUCHANDO;
    }

    @Override
    public void write(String mensaje) {
        //Se crea una instancia de HiloConectado para mandar el mensaje
        HiloConectado hiloConectado;

        synchronized (this) {
            if (estado != EstadosBluetooth.CONECTADO)
                return;
            hiloConectado = this.hiloConectado;
        }
        //Se envía el mensaje asíncronamente
        hiloConectado.write(mensaje.getBytes());
    }

    @Override
    public void stop() {
        androidLauncher.SetDispositivosVisibles.clear();
        AndroidLauncher.nombreDispositivosVisibles.clear();
        pararHilosActivos();
        estado = NULO;
    }

    /**
     * Para todos los hilos activos
     */
    public synchronized void pararHilosActivos() {
        //Cancela cualquier hilo que quiera crear una nueva conexión
        if (hiloConectar != null) {
            hiloConectar.cancel();
            hiloConectar = null;
        }
        //Cancela cualquier hilo que tenga una conexión activa
        if (hiloConectado != null) {
            hiloConectado.cancel();
            hiloConectado = null;
        }
        //Cancela cualquier hilo que este esperando una nueva conexion
        if (hiloAceptar != null) {
            hiloAceptar.cancel();
            hiloAceptar = null;
        }
    }

    /**
     * Estados de la conexión Bluetooth
     */
    public enum EstadosBluetooth {
        CONECTADO, CONECTANDO, ESCUCHANDO, NULO
    }
}

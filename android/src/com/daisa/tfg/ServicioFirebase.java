package com.daisa.tfg;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import com.badlogic.gdx.utils.Array;
import com.daisa.tfg.Constantes.ConstantesBluetooth;
import com.daisa.tfg.Constantes.ConstantesJuego;
import com.daisa.tfg.Principal.Juego;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ServicioFirebase implements Juego.FirebaseCallBack {

    FirebaseFirestore db;
    Activity activity;
    Juego juego;
    Handler handler;
	AndroidLauncher androidLauncher;

    /**
     * @param activity activity de la aplicacion
     * @param juego objeto usado para cominarse con LIBGDX
     * @param androidLauncher objeto usado para acceder a los objetos de AndroiLauncher
     * @param handler objeto usado para la comunicacion entre los hilos y el hilos principal
     */
    public ServicioFirebase(Activity activity, Juego juego, AndroidLauncher androidLauncher, Handler handler) {
        this.activity = activity;
        this.juego = juego;
        this.handler = handler;
        db = FirebaseFirestore.getInstance();
        juego.setFirebaseCallBack(this);
		this.androidLauncher = androidLauncher;
    }

    @Override
    public void comprobacionUsuario(final String nombreUsuario, final String contrasena, final Juego.Procedencia procedencia) {
        db.collection("usuarios")
                .whereEqualTo("nombre", nombreUsuario)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if(procedencia == Juego.Procedencia.LOGIN_SCREEN){
                                if (task.getResult().size() >= 1)
                                    comprobarUsuarioContrasena(nombreUsuario, contrasena);
                                else
                                    pintarToast("Usuario no encontrado");
                            }else{
                                if (task.getResult().size() >= 1)
                                    pintarToast("El nombre de usuario ya existe");
                                else
                                    if (controlesNombreUsuario(nombreUsuario) && !controlesContrasena(contrasena))
                                        registrarUsuario(nombreUsuario, contrasena);
                            }

                        } else {
                            Log.d("DEBUG", "[ERROR] Tarea comprobacionUsuario no finalizada");
                            Log.d("DEBUG", "[ERROR] al recuperar los documentos: ", task.getException());
                        }
                    }
                });
    }

    @Override
    public void pintarToast(String mensaje) {
        Message msg = handler.obtainMessage(ConstantesBluetooth.MENSAJE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(ConstantesBluetooth.TOAST, mensaje);
        msg.setData(bundle);
        handler.sendMessage(msg);
    }

    @Override
    public void recogerPuntuaciones() {
        db.collection("puntuaciones")
                .orderBy("puntuacion", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Array<String> temp = new Array<>();
                            for (DocumentSnapshot documento : task.getResult()) {
                                temp.add(documento.get("nombre") + "\t\t" + documento.get("puntuacion"));
                            }
                            juego.refrescarListaRanking(temp);

                        } else {
                            Log.d("DEBUG", "[ERROR] Tarea recogerPuntuaciones no finalizada");
                            Log.d("DEBUG", "Error getting documents: ", task.getException());
                        }
                    }
                });

    }

    /**
     * Se encarga de comprobar que el nombreusuario cumple con los requisitos minimos
     * @param nombreUsuario nombre que se quiere comprobar
     * @return true si se cumplen los requisitos, false si no
     */
    private boolean controlesNombreUsuario(String nombreUsuario) {
        boolean correcto = true;
        if (nombreUsuario.length() < 5 || nombreUsuario.length() > 15) {
            correcto = false;
            pintarToast("El nombre de usuario tiene que tener entre 5 y 15 caracteres");
        } else if (nombreUsuario.indexOf("@") > 0 || nombreUsuario.indexOf("$") > 0 || nombreUsuario.indexOf("€") > 0 || nombreUsuario.indexOf("&") > 0) {
            correcto = false;
            pintarToast("El nombre de usuario no puede contener ninguno de los siguientes caracteres especiales (@, $, €, &)");
        } else if (nombreUsuario.toUpperCase().equals(nombreUsuario)) {
            correcto = false;
            pintarToast("El nombre de usuario tiene que contener al menos una minúscula");
        } else if (nombreUsuario.toLowerCase().equals(nombreUsuario)) {
            correcto = false;
            pintarToast("El nombre de usuario tiene que contener al menos una mayúscula");
        }

        return correcto;
    }

    /**
     * Se encarga de comprobar que la contrasena cumple con los requisitos minimos
     * @param contrasena contrasena que se quiere comprobar
     * @return true si se cumplen los requisitos, false si no
     */
    private boolean controlesContrasena(String contrasena) {
        boolean hayError = false;
        if (contrasena.length() < 5 || contrasena.length() > 10) {
            hayError = true;
            pintarToast("La contraseña tiene que tener entre 5 y 10 caracteres");
        } else if (contrasena.indexOf("@") > 0 || contrasena.indexOf("$") > 0 || contrasena.indexOf("€") > 0 || contrasena.indexOf("&") > 0) {
            hayError = true;
            pintarToast("La contraseña no puede contener ninguno de los siguientes caracteres especiales (@, $, €, &)");
        } else if (contrasena.toUpperCase().equals(contrasena)) {
            hayError = true;
            pintarToast("La contraseña tiene que contener al menos una minúscula");
        } else if (contrasena.toLowerCase().equals(contrasena)) {
            hayError = true;
            pintarToast("La contraseña tiene que contener al menos una mayúscula");
        } else if (androidLauncher.utilAndroid.hayNumerosEnString(contrasena)) {
            hayError = true;
            pintarToast("La contraseña tiene que contener al menos un número");
        }

        return hayError;
    }

    /**
     * Se comprueba que la contraseña pertenece al nombre de usuario
     * @param nombreUsuario nombre de usuario
     * @param contrasena contraseña que se quiere comprobar
     */
    private void comprobarUsuarioContrasena(final String nombreUsuario, String contrasena) {
        db.collection("usuarios")
                .whereEqualTo("nombre", nombreUsuario)
                .whereEqualTo("contrasena", contrasena)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().size() >= 1) {
                                juego.irAMenuPrincipal();
                                juego.setNombreUsuario(nombreUsuario);
                            } else
                                pintarToast("Contraseña incorrecta");
                        } else {
                            Log.d("DEBUG", "[ERROR] Tarea comprobarUsuarioContrasena no finalizada");
                            Log.d("DEBUG", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    /**
     * Se inserta un nuevo usuario en la BBDD
     * @param nombreUsuario usuario que se quiere registrar
     * @param contrasena contrasena del usuario
     */
    private void registrarUsuario(final String nombreUsuario, String contrasena) {

        registrarUsuarioEnPuntuaciones(nombreUsuario);

        Map<String, Object> usuarioNuevo = new HashMap<>();
        usuarioNuevo.put("nombre", nombreUsuario);
        usuarioNuevo.put("contrasena", contrasena);

        db.collection("usuarios")
                .add(usuarioNuevo)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("DEBUG", "Se ha añadido el usuario correctamente con el ID: " + documentReference.getId());
                        juego.irAMenuPrincipal();
                        juego.setNombreUsuario(nombreUsuario);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("DEBUG", "[ERROR] Al añadir el nuevo usuario", e);
                    }
                });
    }

    /**
     * Se anade el usuario en la coleccion de Puntuaciones cuando se registre
     * @param nombreUsuario usuario a insertar
     */
    public void registrarUsuarioEnPuntuaciones (final String nombreUsuario){

        Map<String, Object> usuarioNuevo = new HashMap<>();
        usuarioNuevo.put("nombre", nombreUsuario);
        usuarioNuevo.put("puntuacion", 0);

        db.collection("puntuaciones")
                .add(usuarioNuevo)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("DEBUG", "Se ha añadido el usuario correctamente con el ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("DEBUG", "[ERROR] Al añadir el nuevo usuario en Puntuaciones", e);
                    }
                });
    }

    /**
     * Se actualiza la puntuacion del usuario en la BBDD
     * @param nombreUsuario usuario del que se modifica la puntuacion
     * @param miPuntuacion nueva puntuacion del usuario
     */
    public void guardarPuntuacionBBDD(final String nombreUsuario, final int miPuntuacion) {
        db.collection("puntuaciones")
                .whereEqualTo("nombre", nombreUsuario)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot documento : task.getResult()) {
                                DocumentReference docRef = documento.getReference();
                                WriteBatch batch = db.batch();

                                int puntuacionAntigua = Integer.parseInt(documento.get("puntuacion").toString());

                                Map<String, Object> mapTemp = new HashMap<>();
                                mapTemp.put("puntuacion", puntuacionAntigua + miPuntuacion);
                                batch.update(docRef, mapTemp);
                                batch.commit();
                            }

                        } else {
                            Log.d("DEBUG", "[ERROR] Tarea guardarPuntuacionBBDD no finalizada");
                            Log.d("DEBUG", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
}

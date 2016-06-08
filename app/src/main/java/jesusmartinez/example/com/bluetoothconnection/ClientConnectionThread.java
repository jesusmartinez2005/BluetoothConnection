package jesusmartinez.example.com.bluetoothconnection;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;

/**
 * Created by jesusmartinez on 21/05/16.
 */
public class ClientConnectionThread extends Thread {
    private final ConnectionListener connectionListener;
    private final BluetoothSocket socket;

    public ClientConnectionThread(BluetoothDevice device, ConnectionListener connectionListener) {
        // Guardo el escuchador de conexión
        this.connectionListener = connectionListener;

        BluetoothSocket socketTemp = null;

        try {
            socketTemp = device.createInsecureRfcommSocketToServiceRecord(MainActivity.THE_UUID);
        } catch (IOException e) {
            connectionListener.onConnectionFailed("No se pudo crear el socket: " + e.getMessage());
        }
        //guardo el socket
        socket = socketTemp;
    }

    public void run() {
        try {
            // Conecto
            socket.connect();
            // Indico que se conectó
            connectionListener.onConnected(socket);
            // Si la conexión falla lo indico
        } catch (IOException e) {
            connectionListener.onConnectionFailed(
                    "Error al conectar: " + e.getMessage());

            try {
                socket.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }


    ///esta la pongo yo de gratis!!!!
    public void cancel() {
        try {
            socket.close();
        } catch (IOException e) {} connectionListener.onConnectionFailed("Se canceló el hilo");
    }
}

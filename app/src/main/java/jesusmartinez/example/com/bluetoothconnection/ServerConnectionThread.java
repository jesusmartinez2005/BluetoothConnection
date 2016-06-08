package jesusmartinez.example.com.bluetoothconnection;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by jesusmartinez on 20/05/16.
 */
public class ServerConnectionThread extends Thread {




    ServerConnectionThread serverConnectionThread;
    private final ConnectionListener connectionListener;
    private final BluetoothServerSocket acceptSocket;


    public ServerConnectionThread(ConnectionListener connectionListener) { // Guardo el escuchador de conexión
        this.connectionListener = connectionListener;
        BluetoothServerSocket acceptSocketTemp = null;
        try {
            acceptSocketTemp = BluetoothAdapter.getDefaultAdapter().listenUsingInsecureRfcommWithServiceRecord(MainActivity.NAME, MainActivity.THE_UUID);
        } catch (IOException e) {
            connectionListener.onConnectionFailed("No se pudo crear el socket" + e.getMessage());
        }
        //guardo el socket
        acceptSocket = acceptSocketTemp;
    }

    public void run() {
        // Socket de comunicación
        BluetoothSocket socket = null;
        while (true) {

            try {
                // Espero una conexión
                socket = acceptSocket.accept();
            } catch (IOException e) {
                connectionListener.onConnectionFailed("Error al aceptar el socket: " + e.getMessage());
                break;
            }
            if (socket != null) {
                // Indico que se ha realizado la conexión
                connectionListener.onConnected(socket); // Cierro el socket de conexión
                try {
                    acceptSocket.close();
                } catch (IOException e) {
                }
                break;
            }
        }
    }

    public void cancel() {
        try {
            acceptSocket.close();
        } catch (IOException e) {} connectionListener.onConnectionFailed("Se canceló el hilo");
    }
}



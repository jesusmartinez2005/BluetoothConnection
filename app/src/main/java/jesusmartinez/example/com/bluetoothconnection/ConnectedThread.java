package jesusmartinez.example.com.bluetoothconnection;

import android.app.Activity;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Vibrator;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by jesusmartinez on 08/06/16.
 */
public class ConnectedThread extends Thread {

    // Socket de conexión
    private final BluetoothSocket socket;
    // Canales de entrada y salida
    private final InputStream inputStream;
    private final OutputStream outputStream;

    // Activity
    private Activity activity;
    // Escuchador de conexión
    private ConnectionListener connectionListener;
    // Para la vibración
    Vibrator vibrator;

    public ConnectedThread(BluetoothSocket socket, Activity activity, ConnectionListener connectionListener) {
        // Guardo el socket
        this.socket = socket;
        // Guardo la activity
        this.activity = activity;
        // Guardo el escuchador de conexión
        this.connectionListener = connectionListener;
        // Obtengo el vibrador
        vibrator = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);
        // Obtengo los canales de entrada y salida
        InputStream inTemp = null;
        OutputStream outTemp = null;
        try {
            inTemp = socket.getInputStream();
            outTemp = socket.getOutputStream();
        } catch (IOException e) {
            connectionListener.onDisconnected(
                    "No se pueden abrir los canales de entrada y salida: "
                            + e.getMessage());
        }
        //Guardo los canales
        inputStream = inTemp;
        outputStream = outTemp;
    }

    public void run() {
        byte[] buffer = new byte[1024];
        int readed;
        // Hasta que la  comunicación se cierre

        while (true) {
            try {
                // Espero datos de entrada
                readed = inputStream.read(buffer);
                if (readed > 0) {
                    // Obtengo la cadena
                    final byte temp[] = new byte[readed];
                    System.arraycopy(buffer, 0, temp, 0, readed);
                    // Muestro el mensaje
                    activity.runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(activity, new String(temp), Toast.LENGTH_SHORT).show();
                        }
                    });
                    // Hago vibrar
                    vibrator.vibrate(100);
                }
            } catch (IOException e) {
                // Error al leer, termino
                connectionListener.onDisconnected(
                        "Error al leer: " + e.getMessage());
                break;
            }
        }
    }

    // Función para enviar datos
    public void send(byte [] buffer) {
        try {
            outputStream.write(buffer);
        } catch (IOException e) {
            // La conexión se ha cerrado
            connectionListener.onDisconnected(
                    "Error al escribir: " + e.getMessage());
        }
    }

    // Función para cerrar la conexión
    public void cancel() {
        try {
            socket.close();
        } catch (IOException e) {}
    }



}

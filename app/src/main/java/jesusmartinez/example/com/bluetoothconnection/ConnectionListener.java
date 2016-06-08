package jesusmartinez.example.com.bluetoothconnection;

import android.bluetooth.BluetoothSocket;

/**
 * Created by jesusmartinez on 20/05/16.
 */
public interface ConnectionListener {
    // Función que será llamada cuando se establezca una conexión

    public void onConnected(BluetoothSocket socket);
    // Función que será llamada si hay un fallo en el
    // establecimiento de la conexión
    public void onConnectionFailed(final String message);
    // Función que será llamada cuando se finalice la conexión
    public void onDisconnected(final String message);
}

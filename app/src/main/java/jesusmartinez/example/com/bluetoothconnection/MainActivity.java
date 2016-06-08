package jesusmartinez.example.com.bluetoothconnection;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.UUID;

public class MainActivity extends AppCompatActivity implements ConnectionListener {

    final int REQUEST_DISCOVERABLE = 1;
    final int REQUEST_ENABLE_BLUETOOTH = 2;
    ServerConnectionThread serverConnectionThread;
    ClientConnectionThread clientConnectionThread;
    public static final String NAME = "BluetoothConnectionExample";
    public static final UUID THE_UUID = UUID.fromString("42ad6984-fd66-4b11-900f-a52b454e34ae");//UUID.randomUUID();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Registro el receiver para cuando se selecciona un dispositivo
        registerReceiver(bluetoothDeviceSelectedReceiver, new IntentFilter( "android.bluetooth.devicepicker.action.DEVICE_SELECTED"));
    }


    public void onClickServer(View view) {
        // Hago al dispositivo visible
        Intent discoverableIntent =
                new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 120);
        startActivityForResult(discoverableIntent, REQUEST_DISCOVERABLE);

    }

    public void onClickClient(View view) {
// Activo el bluetooth si no está activo
        if (!BluetoothAdapter.getDefaultAdapter().isEnabled()) {
            Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetoothIntent, REQUEST_ENABLE_BLUETOOTH);
            return;
        }
        // Abro la actividad para seleccionar un dispositivo
        openSelectDevice();
    }

            @Override
            protected void onActivityResult ( int requestCode, int resultCode, Intent data){
                super.onActivityResult(requestCode, resultCode, data);

                switch (requestCode) {
                    case REQUEST_DISCOVERABLE:
                        if (resultCode != RESULT_CANCELED) {
                            ((TextView) findViewById(R.id.textViewState)).setText("Servidor esperando conexión...");
                            // Arranco el hilo de conexión
                            serverConnectionThread = new ServerConnectionThread(this);
                            serverConnectionThread.start();
                        }
                        break;
                    case REQUEST_ENABLE_BLUETOOTH:
                        if (resultCode == RESULT_OK) {
                            // Abro la actividad para seleccionar un dispositivo
                            openSelectDevice();
                        }
                        break;
                }
            }

    public BroadcastReceiver bluetoothDeviceSelectedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Obtengo el dispositivo
            BluetoothDevice device = (BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

            // Si habrá un hilo de conexión lo termino
            if (clientConnectionThread != null) {
                clientConnectionThread.cancel();
                {
                    // Cambio el estado del texto
                    runOnUiThread(new Runnable() {
                        public void run() {
                            ((TextView) findViewById(R.id.textViewState)).setText("Cliente esperando conexión...");
                        }
                    });
            // Creo el hilo de conexión
                    clientConnectionThread = new ClientConnectionThread(device, MainActivity.this);
                    clientConnectionThread.start();
                }

            }
        }

    };

    private void openSelectDevice() {
        startActivity(new Intent("android.bluetooth.devicepicker.action.LAUNCH")
                .putExtra( "android.bluetooth.devicepicker.extra.NEED_AUTH", false)
                .putExtra("android.bluetooth.devicepicker.extra.FILTER_TYPE", 0)
                .setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS));
    }

            @Override
            public void onConnected (BluetoothSocket socket){

            }

            @Override
            public void onConnectionFailed (String message){

            }

            @Override
            public void onDisconnected (String message){

            }
        }

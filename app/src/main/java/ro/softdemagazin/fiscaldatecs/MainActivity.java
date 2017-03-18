package ro.softdemagazin.fiscaldatecs;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.ParcelUuid;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.datecs.fiscalprinter.FPBase;
import com.datecs.fiscalprinter.FiscalPrinterException;
import com.datecs.fiscalprinter.rou.DP25ROU;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    public DP25ROU dtcs;

    public MainActivity() {
        dtcs = null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        OutputStream outputStream = null;
        InputStream inStream = null;

        BluetoothAdapter blueAdapter = BluetoothAdapter.getDefaultAdapter();
        if (blueAdapter != null) {
            if (blueAdapter.isEnabled()) {
                Set<BluetoothDevice> bondedDevices = blueAdapter.getBondedDevices();

                if(bondedDevices.size() > 0) {
                    Object[] devices = (Object []) bondedDevices.toArray();
                    BluetoothDevice device = (BluetoothDevice) devices[0];

                    ParcelUuid[] uuids = device.getUuids();
                    BluetoothSocket socket = null;
                    try {
                        socket = device.createRfcommSocketToServiceRecord(uuids[0].getUuid());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    try {
                        socket.connect();
                        outputStream = socket.getOutputStream();
                        inStream = socket.getInputStream();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                Log.e("error", "No appropriate paired devices.");
            } else {
                Log.e("error", "Bluetooth is disabled.");
            }
        }



        try {
            dtcs = new DP25ROU(inStream, outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        final Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                try {
                    dtcs.openFiscalCheckWithDefaultValues();
                    dtcs.command49Variant0Version10("test", "A", "55.22", "1.000");
                    dtcs.totalInCash();
                    dtcs.command56Variant0Version0();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}

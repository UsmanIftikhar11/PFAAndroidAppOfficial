package com.pfa.pfaapp.printing;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.dantsu.escposprinter.EscPosPrinter;
import com.dantsu.escposprinter.EscPosPrinterCommands;
import com.dantsu.escposprinter.connection.DeviceConnection;
import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnection;
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections;
import com.dantsu.escposprinter.textparser.PrinterTextParserImg;
import com.pfa.pfaapp.BaseActivity;
import com.pfa.pfaapp.PFADrawerActivity;
import com.pfa.pfaapp.PrinterCommands;
import com.pfa.pfaapp.R;
import com.pfa.pfaapp.adapters.PFATableAdapter;
import com.pfa.pfaapp.printing.async.AsyncBluetoothEscPosPrint;
import com.pfa.pfaapp.printing.async.AsyncEscPosPrint;
import com.pfa.pfaapp.printing.async.AsyncEscPosPrinter;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.zip.Inflater;

public class PrinterActivity extends AppCompatActivity {

    Button scan,print;
    String value;
    private BluetoothConnection selectedDevice;
    private static final int TIME_DELAY = 2000;
    private static long back_pressed;
    OutputStream outputStream;
    DeviceConnection  deviceConnection;
    EscPosPrinterCommands escPosPrinterCommands;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_printer);
         scan = (Button) findViewById(R.id.button_bluetooth_browse);

        Intent intent = getIntent();
        value = intent.getStringExtra("PrintHtml");


        scan = findViewById(R.id.button_bluetooth_browse);
        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                browseBluetoothDevice();
            }
        });
        print = findViewById(R.id.button_bluetooth);
        print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedDevice == null){
                    Toast.makeText(PrinterActivity.this, "Connect for Printing", Toast.LENGTH_SHORT).show();
                }else {
                    printBluetooth();
                }
//                    Intent intent1 = new Intent(PrinterActivity.this, PFATableAdapter.class);
//                startActivity(intent1);

//                if (back_pressed + TIME_DELAY > System.currentTimeMillis()) {
//                 PrinterActivity.super.onBackPressed();
//                }
//                else {
                  call();
//                }
            }
        });

    }

    private void call() {
        new Timer().schedule(new TimerTask(){
                    @Override
                    public void run(){
                       finish();
                    }
        }, 7000);
    }



    /*==============================================================================================
    ======================================BLUETOOTH PART============================================
    ==============================================================================================*/

    public static final int PERMISSION_BLUETOOTH = 1;

    @SuppressLint("MissingSuperCall")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            switch (requestCode) {
                case PrinterActivity.PERMISSION_BLUETOOTH:
                    this.printBluetooth();
                    break;
            }
        }
    }


    public void browseBluetoothDevice() {
        final BluetoothConnection[] bluetoothDevicesList = (new BluetoothPrintersConnections()).getList();

        if (bluetoothDevicesList != null) {
            final String[] items = new String[bluetoothDevicesList.length + 1];
            items[0] = "Default printer";
            int i = 0;
            for (BluetoothConnection device : bluetoothDevicesList) {
                items[++i] = device.getDevice().getName();
            }

            AlertDialog.Builder alertDialog = new AlertDialog.Builder(PrinterActivity.this);
            alertDialog.setTitle("Bluetooth printer selection");
            alertDialog.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    int index = i - 1;
                    if(index == -1) {
                        selectedDevice = null;
                    } else {
                        selectedDevice = bluetoothDevicesList[index];
                    }
                    scan.setText(items[i]);
                    Toast.makeText(PrinterActivity.this, "Connected with "+items[i], Toast.LENGTH_SHORT).show();
                }
            });

            AlertDialog alert = alertDialog.create();
            alert.setCanceledOnTouchOutside(false);
            alert.show();

        }
    }

    public void printBluetooth() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH}, PrinterActivity.PERMISSION_BLUETOOTH);
        } else {
            new AsyncBluetoothEscPosPrint(this).execute(this.getAsyncEscPosPrinter(selectedDevice));
            selectedDevice.disconnect();
            Toast.makeText(this, "Disconnect with "+selectedDevice.getDevice().getName(), Toast.LENGTH_SHORT).show();
            selectedDevice = null;

            scan.setText("SCAN PRINTER");

        }
//        escPosPrinterCommands.disconnect();
    }



    @SuppressLint("SimpleDateFormat")
    public AsyncEscPosPrinter getAsyncEscPosPrinter(DeviceConnection printerConnection) {
        SimpleDateFormat format = new SimpleDateFormat("'on' yyyy-MM-dd 'at' HH:mm:ss");
        AsyncEscPosPrinter printer = new AsyncEscPosPrinter(printerConnection, 203, 48f, 32);


        return printer.setTextToPrint(
//                "[L]<img>" + PrinterTextParserImg.bitmapToHexadecimalString(printer, this.getApplicationContext().getResources().getDrawableForDensity(R.drawable.comp, DisplayMetrics.DENSITY_MEDIUM)) + "</img>"
                        "[L]<b>" + value + "</b>"

//                        "[R]<qrcode size='20'>http://www.developpeur-web.dantsu.com/</qrcode>"


        );


    }
}


package com.dantsu.escposprinter.connection.bluetooth;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;

import androidx.annotation.Nullable;

import com.dantsu.escposprinter.exceptions.EscPosConnectionException;

public class BluetoothPrintersConnections extends BluetoothConnections {

    /**
     * Easy way to get the first bluetooth printer paired / connected.
     *
     * @return a EscPosPrinterCommands instance
     */
    @Nullable
    public static BluetoothConnection selectFirstPaired() {
        BluetoothPrintersConnections printers = new BluetoothPrintersConnections();
        BluetoothConnection[] bluetoothPrinters = printers.getList();

        if (bluetoothPrinters != null && bluetoothPrinters.length > 0) {
            for (BluetoothConnection printer : bluetoothPrinters) {
                try {
                    return printer.connect();
                } catch (EscPosConnectionException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * Connects to a Bluetooth printer with the specified MAC address.
     *
     * @param macAddress The MAC address of the printer to connect to.
     * @return A connected BluetoothConnection if successful, null otherwise.
     */
    @Nullable
    public static BluetoothConnection connectByMacAddress(String macAddress) {
        if (macAddress == null || macAddress.isEmpty()) {
            return null;
        }

        BluetoothPrintersConnections printers = new BluetoothPrintersConnections();
        BluetoothConnection[] bluetoothPrinters = printers.getList();

        if (bluetoothPrinters != null && bluetoothPrinters.length > 0) {
            for (BluetoothConnection printer : bluetoothPrinters) {
                if (printer.getDevice().getAddress().equals(macAddress)) {
                    try {
                        return printer.connect();
                    } catch (EscPosConnectionException e) {
                        e.printStackTrace();
                        return null;
                    }
                }
            }
        }
        // No printer with matching MAC address found
        return null;
    }

    /**
     * Get a list of bluetooth printers.
     *
     * @return an array of EscPosPrinterCommands
     */
    @SuppressLint("MissingPermission")
    @Nullable
    public BluetoothConnection[] getList() {
        BluetoothConnection[] bluetoothDevicesList = super.getList();

        if (bluetoothDevicesList == null) {
            return null;
        }

        int i = 0;
        BluetoothConnection[] printersTmp = new BluetoothConnection[bluetoothDevicesList.length];
        for (BluetoothConnection bluetoothConnection : bluetoothDevicesList) {
            BluetoothDevice device = bluetoothConnection.getDevice();

            int majDeviceCl = device.getBluetoothClass().getMajorDeviceClass(),
                    deviceCl = device.getBluetoothClass().getDeviceClass();

            if (majDeviceCl == BluetoothClass.Device.Major.IMAGING && (deviceCl == 1664 || deviceCl == BluetoothClass.Device.Major.IMAGING)) {
                printersTmp[i++] = new BluetoothConnection(device);
            }
        }
        BluetoothConnection[] bluetoothPrinters = new BluetoothConnection[i];
        System.arraycopy(printersTmp, 0, bluetoothPrinters, 0, i);
        return bluetoothPrinters;
    }

}

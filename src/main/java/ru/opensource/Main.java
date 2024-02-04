package ru.opensource;

import ru.opensource.connection.ADBService;
import ru.opensource.device.AndroidDevice;
import ru.opensource.exception.ADBNotFoundException;
import ru.opensource.exception.ADBShellExecutionException;
import ru.opensource.exception.AndroidDeviceNotAvailableException;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) throws ADBNotFoundException, AndroidDeviceNotAvailableException, ADBShellExecutionException, IOException, NoSuchAlgorithmException {
        ADBService service = new ADBService();
        ArrayList<String> result = service.getAvailableDevices();
        AndroidDevice device = new AndroidDevice(result.get(0), service);
    }
}
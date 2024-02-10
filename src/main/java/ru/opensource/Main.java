package ru.opensource;
import ru.opensource.connection.ADBService;
import ru.opensource.device.AndroidDeviceInfo;
import ru.opensource.exception.ADBNotFoundException;
import ru.opensource.exception.ADBPermissionCollectingException;
import ru.opensource.exception.ADBShellExecutionException;
import ru.opensource.exception.AndroidDeviceNotAvailableException;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) throws ADBNotFoundException, AndroidDeviceNotAvailableException, ADBShellExecutionException, IOException, NoSuchAlgorithmException, ADBPermissionCollectingException {
        ADBService service = new ADBService();
        ArrayList<String> result = service.getAvailableDevices();
        AndroidDeviceInfo info = new AndroidDeviceInfo(result.get(0), service);
        System.out.println(info.getModel());
        System.out.println(info.getBrand());
        System.out.println(info.getAndroidVersion());
        System.out.println(info);
    }
}
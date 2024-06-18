package ru.opensource;
import ru.opensource.adblibrary.connection.ADBService;
import ru.opensource.adblibrary.device.AndroidDevice;
import ru.opensource.adblibrary.exceptions.ADBException;
import ru.opensource.adblibrary.permission.ApplicationPermissionProvider;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws ADBException, IOException, NoSuchAlgorithmException {
        ADBService service = new ADBService();
        ArrayList<String> result = service.getAvailableDevices();
        AndroidDevice device = new AndroidDevice(result.get(0), service, 5);
        boolean res = device.installAppFromApk("/home/mrglaster/Downloads/HelloWorld.apk");
    }
}
package ru.opensource;
import ru.opensource.connection.ADBService;
import ru.opensource.device.AndroidDeviceInfo;
import ru.opensource.exceptions.ADBNotFoundException;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) throws ADBNotFoundException {
        ADBService service = new ADBService();
        ArrayList<String> result = service.getAvailableDevices();
        AndroidDeviceInfo info = new AndroidDeviceInfo(result.get(0), service);
        System.out.println(info.getModel());
        System.out.println(info.getBrand());
        System.out.println(info.getAndroidVersion());
        System.out.println(info);
    }
}
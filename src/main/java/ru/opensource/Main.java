package ru.opensource;
import ru.opensource.adblibrary.connection.ADBService;
import ru.opensource.adblibrary.device.AndroidDevice;
import ru.opensource.adblibrary.exceptions.ADBException;
import ru.opensource.adblibrary.permission.ApplicationPermissionProvider;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) throws ADBException, IOException, NoSuchAlgorithmException {
        ADBService service = new ADBService();
        ArrayList<String> result = service.getAvailableDevices();
        AndroidDevice device = new AndroidDevice(result.get(0), service, 5);
        ApplicationPermissionProvider app = new ApplicationPermissionProvider();

        //System.out.println(device.getApplications().get(0).getInstallPermissions());
        String packageName = "com.metr.smartsocket";
        String permission = "android.permission.RECEIVE_BOOT_COMPLETED";

        app.revokePermissionShellOnly(service, result.get(0), packageName, permission);
        //app.grantPermission(service, result.get(0), packageName, permission);
    }
}
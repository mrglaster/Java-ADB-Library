package ru.enplus.adblibrary.providers;

import ru.enplus.adblibrary.connection.ADBService;
import ru.enplus.adblibrary.exceptions.ADBException;
import ru.enplus.adblibrary.exceptions.ADBIncorrectPathException;
import ru.enplus.adblibrary.exceptions.ADBShellExecutionException;
import ru.enplus.adblibrary.exceptions.InvalidPackageNameException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Provides application management functionalities for an Android device using ADB (Android Debug Bridge).
 * This class extends the AndroidDataProvider to include methods for installing and uninstalling applications.
 */
public class ApplicationManagementProvider extends AndroidDataProvider {

    /**
     * Constructs an ApplicationManagementProvider with the specified ADB service and device ID.
     *
     * @param adbService the ADB service used to interact with the Android device
     * @param deviceId   the ID of the Android device
     */
    public ApplicationManagementProvider(ADBService adbService, String deviceId) {
        super(adbService, deviceId);
    }

    /**
     * Uninstalls an application from the Android device.
     *
     * @param packageName the package name of the application to uninstall
     * @throws ADBException if there is an error during uninstallation or the package name is invalid
     */
    public void uninstallApp(String packageName) throws ADBException {
        if (!packageName.contains(".")) {
            throw new InvalidPackageNameException("Invalid package name: " + packageName);
        }
        String command = getCommandGenerator().generateUninstallApplicationCommand(packageName);
        getAdbService().logInfo("Uninstalling App: " + packageName);
        try {
            Process process = Runtime.getRuntime().exec(command);
            super.getAdbService().getProcessOutputHandler().getProcessOutput(process);
        } catch (IOException e) {
            throw new ADBShellExecutionException("Unable to uninstall app " + packageName + "! Check if the device is available!");
        }
    }

    /**
     * Installs an application on the Android device from the specified APK file.
     *
     * @param apkPath the file path of the APK to install
     * @return true if the installation is successful, false otherwise
     * @throws ADBException if there is an error during installation or the APK path is incorrect
     */
    public boolean installAppFromApk(String apkPath) throws ADBException {
        if (apkPath.isEmpty() || !new File(apkPath).exists()) {
            throw new ADBIncorrectPathException("APK file " + apkPath + " does not exist!");
        }
        String command = getCommandGenerator().generateInstallApplicationCommand(apkPath);
        getAdbService().logInfo("Installing apk " + apkPath + " with command " + command);
        try {
            Process process = Runtime.getRuntime().exec(command);
            ArrayList<String> rows = super.getAdbService().getProcessOutputHandler().getProcessOutput(process);
            for (var i : rows) {
                if (i.contains("Success")) {
                    return true;
                }
            }
            return false;
        } catch (IOException e) {
            throw new ADBShellExecutionException("Device not available!");
        }
    }
}

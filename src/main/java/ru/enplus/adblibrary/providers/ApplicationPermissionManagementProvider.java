package ru.enplus.adblibrary.providers;

import ru.enplus.adblibrary.application.AndroidApplication;
import ru.enplus.adblibrary.connection.ADBService;
import ru.enplus.adblibrary.exceptions.ADBException;
import ru.enplus.adblibrary.exceptions.ADBShellExecutionException;
import ru.enplus.adblibrary.exceptions.OperationNotAllowedException;
import ru.enplus.adblibrary.exceptions.UnknownPackageException;
import ru.enplus.adblibrary.exceptions.UnknownPermissionException;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Provides methods for managing application permissions on an Android device using ADB (Android Debug Bridge).
 * This class extends the AndroidDataProvider to include methods for granting and revoking permissions.
 */
public class ApplicationPermissionManagementProvider extends AndroidDataProvider {

    /**
     * Constructs an ApplicationPermissionManagementProvider with the specified ADB service and device ID.
     *
     * @param adbService the ADB service used to interact with the Android device
     * @param deviceId   the ID of the Android device
     */
    public ApplicationPermissionManagementProvider(ADBService adbService, String deviceId) {
        super(adbService, deviceId);
    }

    /**
     * Grants a permission to an application package using ADB shell command.
     *
     * @param applicationPackage the package name of the application
     * @param permission         the permission to grant
     * @throws ADBException if there is an error during the command execution or if the package/permission is unknown
     */
    public void grantPermissionShellOnly(String applicationPackage, String permission) throws ADBException {
        String command = getAdbService().getCommandBase(getDeviceId()) + "pm grant " + applicationPackage + " " + permission;
        try {
            Process process = Runtime.getRuntime().exec(command);
            ArrayList<String> output = getAdbService().getProcessOutputHandler().getProcessOutput(process);
            String initRow = output.get(0);
            if (initRow.contains("Unknown package")) {
                throw new UnknownPackageException("Unknown package: " + applicationPackage);
            }
            if (initRow.contains("Unknown permission")) {
                throw new UnknownPermissionException("Unknown permission: " + permission);
            }
            if (initRow.contains("Operation not allowed")) {
                throw new OperationNotAllowedException(initRow);
            }
            getAdbService().getAdbServiceLogger().fine("Permission " + permission + " was granted to " + applicationPackage);
        } catch (IOException e) {
            throw new ADBShellExecutionException("Something went wrong during ADB Shell command execution");
        }
    }

    /**
     * Grants a permission to an application using ADB shell command.
     *
     * @param app        the Android application to grant the permission to
     * @param permission the permission to grant
     * @throws ADBException if there is an error during the command execution or if the package/permission is unknown
     */
    public void grantPermissionShellOnly(AndroidApplication app, String permission) throws ADBException {
        grantPermissionShellOnly(app.getPackageName(), permission);
    }

    /**
     * Revokes a permission from an application package using ADB shell command.
     *
     * @param applicationPackage the package name of the application
     * @param permission         the permission to revoke
     * @throws ADBException if there is an error during the command execution or if the package/permission is unknown
     */
    public void revokePermissionShellOnly(String applicationPackage, String permission) throws ADBException {
        String command = getAdbService().getCommandBase(getDeviceId()) + "pm revoke " + applicationPackage + " " + permission;
        System.out.println(command);
        try {
            Process process = Runtime.getRuntime().exec(command);
            ArrayList<String> output = getAdbService().getProcessOutputHandler().getProcessOutput(process);
            String initRow = output.get(0);
            if (initRow.contains("Unknown package")) {
                throw new UnknownPackageException("Unknown package: " + applicationPackage);
            }
            if (initRow.contains("Unknown permission")) {
                throw new UnknownPermissionException("Unknown permission: " + permission);
            }
            System.out.println(initRow);
            if (initRow.contains("Operation not allowed")) {
                throw new OperationNotAllowedException(initRow);
            }
            getAdbService().getAdbServiceLogger().fine("Permission " + permission + " was revoked from " + applicationPackage);
        } catch (IOException e) {
            throw new ADBShellExecutionException("Something went wrong during ADB Shell command execution");
        }
    }

    /**
     * Revokes a permission from an application using ADB shell command.
     *
     * @param app        the Android application to revoke the permission from
     * @param permission the permission to revoke
     * @throws ADBException if there is an error during the command execution or if the package/permission is unknown
     */
    public void revokePermissionShellOnly(AndroidApplication app, String permission) throws ADBException {
        revokePermissionShellOnly(app.getPackageName(), permission);
    }
}

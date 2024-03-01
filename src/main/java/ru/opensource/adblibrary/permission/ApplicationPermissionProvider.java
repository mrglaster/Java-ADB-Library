package ru.opensource.adblibrary.permission;

import ru.opensource.adblibrary.application.AndroidApplication;
import ru.opensource.adblibrary.connection.ADBService;
import ru.opensource.adblibrary.exceptions.ADBException;
import ru.opensource.adblibrary.exceptions.ADBShellExecutionException;
import ru.opensource.adblibrary.exceptions.OperationNotAllowedException;
import ru.opensource.adblibrary.exceptions.UnknownPackageException;
import ru.opensource.adblibrary.exceptions.UnknownPermissionException;

import java.io.IOException;
import java.util.ArrayList;

public class ApplicationPermissionProvider {

    /**Grants permission to the package by its String name with adb shell command only*/
    public void grantPermissionShellOnly(ADBService adbService, String deviceId, String applicationPackage, String permission) throws ADBException {
        String command = adbService.getCommandBase(deviceId) + "pm grant " + applicationPackage + " " + permission;
        try {
            Process process = Runtime.getRuntime().exec(command);
            ArrayList<String> output = adbService.getProcessOutputHandler().getProcessOutput(process);
            String initRow = output.get(0);
            if (initRow.contains("Unknown package")){
                throw new UnknownPackageException("Unknown package: " + applicationPackage);
            }
            if (initRow.contains("Unknown permission")){
                throw new UnknownPermissionException("Unknown permission: " + permission);
            }
            if (initRow.contains("Operation not allowed")){
                throw new OperationNotAllowedException(initRow);
            }
            adbService.getAdbServiceLogger().fine("Permission " + permission + " was granted to " + applicationPackage);
        } catch (IOException e) {
            throw new ADBShellExecutionException("Something went wrong during ADB Shell command execution");
        }
    }

    /**Grants permission to the package with adb shell command only*/
    public void grantPermissionShellOnly(ADBService adbService, String deviceId, AndroidApplication app, String permission) throws ADBException{
        grantPermissionShellOnly(adbService, deviceId, app.getPackageName(), permission);
    }

    /**Revokes permission to the package by its String name with adb shell command only*/
    public void revokePermissionShellOnly(ADBService adbService, String deviceId, String applicationPackage, String permission) throws ADBException{
        String command = adbService.getCommandBase(deviceId) + "pm revoke " + applicationPackage + " " + permission;
        System.out.println(command);
        try {
            Process process = Runtime.getRuntime().exec(command);
            ArrayList<String> output = adbService.getProcessOutputHandler().getProcessOutput(process);
            String initRow = output.get(0);
            if (initRow.contains("Unknown package")){
                throw new UnknownPackageException("Unknown package: " + applicationPackage);
            }
            if (initRow.contains("Unknown permission")){
                throw new UnknownPermissionException("Unknown permission: " + permission);
            }
            System.out.println(initRow);
            if (initRow.contains("Operation not allowed")){
                throw new OperationNotAllowedException(initRow);
            }
            adbService.getAdbServiceLogger().fine("Permission " + permission + " was revoked from " + applicationPackage);
        } catch (IOException e) {
            throw new ADBShellExecutionException("Something went wrong during ADB Shell command execution");
        }
    }
    /**Revokes permission from the package with adb shell command only*/
    public void revokePermissionShellOnly(ADBService adbService, String deviceId, AndroidApplication app, String permission) throws ADBException{
        revokePermissionShellOnly(adbService, deviceId, app.getPackageName(), permission);
    }
}

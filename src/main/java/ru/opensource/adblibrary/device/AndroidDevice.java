package ru.opensource.adblibrary.device;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import ru.opensource.adblibrary.application.AndroidApplication;
import ru.opensource.adblibrary.exceptions.ADBException;
import ru.opensource.adblibrary.permission.ApplicationPermission;
import ru.opensource.adblibrary.calc.HashCalc;
import ru.opensource.adblibrary.connection.ADBService;
import ru.opensource.adblibrary.exceptions.ADBPermissionCollectingException;
import ru.opensource.adblibrary.exceptions.ADBShellExecutionException;
import ru.opensource.adblibrary.exceptions.AndroidDeviceNotAvailableException;
import ru.opensource.adblibrary.exceptions.InvalidPackageNameException;
import ru.opensource.adblibrary.permission.ApplicationPermissionProvider;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class AndroidDevice extends AndroidDeviceInfo {

    private final float versionNumeric;

    private final ApplicationPermissionProvider applicationPermissionProvider = new ApplicationPermissionProvider();

    private final short GRANT_PERMISSION_SHELL_ONLY = 0;
    private final short GRANT_PERMISSION_APP_UPDATE = 1;



    @Getter
    @Setter
    private ArrayList<AndroidApplication> applications;


    private static final int COLLECT_REQUESTED_PERMISSIONS = 1;
    private static final int COLLECT_INSTALL_PERMISSIONS = 2;
    private static final int COLLECT_RUNTIME_PERMISSIONS = 3;
    private final int restriction;

    public AndroidDevice(@NotNull String deviceId, ADBService adbService) throws ADBException, IOException, NoSuchAlgorithmException{
        super(deviceId, adbService);
        if (!adbService.getAvailableDevices().contains(deviceId)){
            throw new AndroidDeviceNotAvailableException("Android device " + deviceId + "is not available!");
        }
        this.restriction = -1;
        this.applications = new ArrayList<>();
        this.versionNumeric = Float.parseFloat(truncateAndroidVersion());
        collectApplications();
        collectApplicationProperties();
    }

    public AndroidDevice(@NotNull String deviceId, ADBService adbService, int appsListRestriction) throws ADBException, IOException, NoSuchAlgorithmException {
        super(deviceId, adbService);
        if (!adbService.getAvailableDevices().contains(deviceId)){
            throw new AndroidDeviceNotAvailableException("Android device " + deviceId + "is not available!");
        }
        this.restriction = appsListRestriction;
        this.applications = new ArrayList<>();
        this.versionNumeric = Float.parseFloat(truncateAndroidVersion());
        collectApplications();
        collectApplicationProperties();
    }

    private void collectApplications() throws ADBShellExecutionException {
        String command = super.getAdbService().getCommandBase(super.getDeviceId()) + " pm list packages -f ";
        getAdbService().logInfo("Getting applications list with command: " + command);
        try {
            Process process = Runtime.getRuntime().exec(command);
            ArrayList<String> result = super.getAdbService().getProcessOutputHandler().getProcessOutput(process);
            for (var application : result){
                String currentApp = application;
                currentApp = currentApp.replace("package:/", "/");
                String[] params = currentApp.split("=");
                AndroidApplication app = new AndroidApplication();
                app.setPath(params[0]);
                app.setPackageName(params[1]);
                this.applications.add(app);
            }
        } catch (IOException e) {
            throw new ADBShellExecutionException("Unable to get the applications list! Check if the device is available!");
        }
    }

    private void collectApplicationProperties() throws IOException, NoSuchAlgorithmException, ADBException {
        int res = 0;
        for (var application : this.applications){
            collectApplicationPermissions(application);
            String[] hashes = collectApplicationHashes(application.getPath());
            application.setSha1(hashes[0]);
            application.setSha256(hashes[1]);
            application.setSha512(hashes[2]);
            res++;
            if (this.restriction >= 0 && res >= restriction) {
                break;
            }
        }
    }

    private String[] collectApplicationHashes(String applicationPath) throws IOException, NoSuchAlgorithmException {
        String sha1 = "";
        String sha256 = "";
        String sha512 = "";
        String tempApkName = "temp.apk";
        if (versionNumeric >= 8.0){
           sha1 = collectApplicationHashAuto("sha1sum", applicationPath);
           sha256 = collectApplicationHashAuto("sha256sum", applicationPath);
           sha512 = collectApplicationHashAuto("sha512sum", applicationPath);
        }
        if (versionNumeric >= 6.0){
            sha1 = collectApplicationHashAuto("sha1sum", applicationPath);
        }
        if (versionNumeric <= 6.0){
            String command = super.getAdbService().getCommandBaseNoShell(super.getDeviceId()) +   "pull " + applicationPath + ' ' + tempApkName;
            getAdbService().logInfo("Grabbing apk file using command: " + command);
            getAdbService().getProcessOutputHandler().getProcessOutput(Runtime.getRuntime().exec(command));
            sha256 = HashCalc.calculateSha256Hash(tempApkName);
            sha512= HashCalc.calculateSha512Hash(tempApkName);
            if (sha1.length() == 0 ){
                sha1 = HashCalc.calculateSha1Hash(tempApkName);
            }
        }
        return new String[]{sha1, sha256, sha512};
    }

    private String truncateAndroidVersion() {
        int firstDotIndex = super.getAndroidVersion().indexOf('.');
        if (firstDotIndex != -1) {
            int secondDotIndex = super.getAndroidVersion().indexOf('.', firstDotIndex + 1);
            if (secondDotIndex != -1) {
                return super.getAndroidVersion().substring(0, secondDotIndex + 1);
            }
        }
        return super.getAndroidVersion();
    }

    private String collectApplicationHashAuto(String hashFunction, String applicationPath) throws IOException {
       return getAdbService().getProcessOutputHandler().getProcessOutput(Runtime.getRuntime().exec(super.getAdbService().getCommandBase(super.getDeviceId()) + ' ' + hashFunction + ' ' + applicationPath)).get(0).split(" ")[0];
    }

    private void collectApplicationPermissions(AndroidApplication application) throws ADBPermissionCollectingException {
        String command = super.getAdbService().getCommandBase(this.getDeviceId()) + " dumpsys package " + application.getPackageName();
        try {
            ArrayList<String> collectedStrings = super.getAdbService().getProcessOutputHandler().getProcessOutput(Runtime.getRuntime().exec(command));
            int currentState = 0;
            for (var row : collectedStrings){
                if (row.contains("requested permissions")){
                    currentState = COLLECT_REQUESTED_PERMISSIONS;
                    continue;
                }
                if (row.contains("install permissions")){
                    currentState = COLLECT_INSTALL_PERMISSIONS;
                    continue;
                }
                if (row.contains("runtime permissions")){
                    currentState = COLLECT_RUNTIME_PERMISSIONS;
                }
                if (currentState != 0 && row.contains("android.permission")){
                    String currentRow = row.strip();
                    ApplicationPermission currentPermission = new ApplicationPermission();
                    currentPermission.setGranted(currentRow.contains("true"));
                    currentPermission.setPermissionName(currentRow.split(":")[0]);
                    switch (currentState) {
                        case 1 -> {
                            currentPermission.setGranted(true);
                            application.addRequestedPermissions(currentPermission);
                        }
                        case 2 -> application.addInstallPermissions(currentPermission);
                        case 3 -> application.addRuntimePermission(currentPermission);
                    }
                }
            }
        } catch (IOException e) {
            throw new ADBPermissionCollectingException("Something went wrong during package " + application.getPackageName() + " permissions collecting! Check if the device is availabe!");
        }

    }

    public void uninstallApp(String packageName) throws InvalidPackageNameException, ADBException {
        if (!packageName.contains(".")){
            throw new InvalidPackageNameException("Invalid package name: " + packageName);
        }
        String command = "adb uninstall " + packageName;
        getAdbService().logInfo("Uninstalling App: " + packageName);
        try {
            Process process = Runtime.getRuntime().exec(command);
            super.getAdbService().getProcessOutputHandler().getProcessOutput(process);
            for (int i = 0; i < applications.size() ; i++) {
                if (applications.get(i).getPackageName().equals(packageName)){
                    applications.remove(i);
                    break;
                }
            }
        } catch (IOException e) {
            throw new ADBShellExecutionException("Unable to uninstall app " + packageName + " ! Check if the device is available!");
        }
    }
}

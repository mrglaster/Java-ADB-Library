package ru.opensource.adblibrary.device;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import ru.opensource.adblibrary.application.AndroidApplication;
import ru.opensource.adblibrary.exceptions.*;
import ru.opensource.adblibrary.permission.ApplicationPermission;
import ru.opensource.adblibrary.calc.HashCalc;
import ru.opensource.adblibrary.connection.ADBService;
import ru.opensource.adblibrary.permission.ApplicationPermissionProvider;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AndroidDevice extends AndroidDeviceInfo {

    private final float versionNumeric;

    private final ApplicationPermissionProvider applicationPermissionProvider = new ApplicationPermissionProvider();

    @Getter
    @Setter
    private ArrayList<AndroidApplication> applications;


    private static final int COLLECT_REQUESTED_PERMISSIONS = 1;
    private static final int COLLECT_INSTALL_PERMISSIONS = 2;
    private static final int COLLECT_RUNTIME_PERMISSIONS = 3;
    private int restriction = -1;
    private final List<String> supportedIpVersions = Arrays.asList(new String[]{"ipv4", "ipv6"});

    @Getter
    private final ArrayList<String> networkInterfaces = new ArrayList<>();

    public AndroidDevice(@NotNull String deviceId, ADBService adbService) throws ADBException, IOException, NoSuchAlgorithmException {
        super(deviceId, adbService);
        if (!adbService.getAvailableDevices().contains(deviceId)) {
            throw new AndroidDeviceNotAvailableException("Android device " + deviceId + "is not available!");
        }
        this.restriction = -1;
        this.applications = new ArrayList<>();
        this.versionNumeric = Float.parseFloat(truncateAndroidVersion());
        collectApplications();
        collectApplicationProperties();
        collectNetworkInterface();
    }

    public AndroidDevice(@NotNull String deviceId, ADBService adbService, int appsListRestriction) throws ADBException, IOException, NoSuchAlgorithmException {
        super(deviceId, adbService);
        if (!adbService.getAvailableDevices().contains(deviceId)) {
            throw new AndroidDeviceNotAvailableException("Android device " + deviceId + "is not available!");
        }
        this.restriction = appsListRestriction;
        this.applications = new ArrayList<>();
        this.versionNumeric = Float.parseFloat(truncateAndroidVersion());
        collectApplications();
        collectApplicationProperties();
        collectNetworkInterface();

    }

    private void collectApplications() throws ADBShellExecutionException {
        String command = super.getAdbService().getCommandBase(super.getDeviceId()) + " pm list packages -f ";
        final String fileFormat = ".apk";
        getAdbService().logInfo("Getting applications list with command: " + command);
        try {
            Process process = Runtime.getRuntime().exec(command);
            ArrayList<String> result = super.getAdbService().getProcessOutputHandler().getProcessOutput(process);
            int appCounter = 0;
            for (var application : result) {
                String currentApp = application;
                currentApp = currentApp.replace("package:/", "/");

                String[] params = currentApp.split(fileFormat + "=");
                AndroidApplication app = new AndroidApplication();
                app.setPath(params[0] + fileFormat);
                app.setPackageName(params[1]);
                this.applications.add(app);
                appCounter++;
                if (this.restriction > 0 && appCounter == restriction) break;
            }
        } catch (IOException e) {
            throw new ADBShellExecutionException("Unable to get the applications list! Check if the device is available!");
        }
    }


    private void collectApplicationProperties() throws IOException, NoSuchAlgorithmException, ADBException {
        int res = 0;
        for (var application : this.applications) {
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
        if (versionNumeric >= 8.0) {
            sha1 = collectApplicationHashAuto("sha1sum", applicationPath);
            sha256 = collectApplicationHashAuto("sha256sum", applicationPath);
            sha512 = collectApplicationHashAuto("sha512sum", applicationPath);
        }
        if (versionNumeric >= 6.0) {
            sha1 = collectApplicationHashAuto("sha1sum", applicationPath);
        }
        if (versionNumeric <= 6.0) {
            String command = super.getAdbService().getCommandBaseNoShell(super.getDeviceId()) + "pull " + applicationPath + ' ' + tempApkName;
            getAdbService().logInfo("Grabbing apk file using command: " + command);
            getAdbService().getProcessOutputHandler().getProcessOutput(Runtime.getRuntime().exec(command));
            sha256 = HashCalc.calculateSha256Hash(tempApkName);
            sha512 = HashCalc.calculateSha512Hash(tempApkName);
            if (sha1.isEmpty()) {
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
                return super.getAndroidVersion().substring(0, secondDotIndex);
            }
        }
        return super.getAndroidVersion();
    }

    private String collectApplicationHashAuto(String hashFunction, String applicationPath) throws IOException {
        return getAdbService().getProcessOutputHandler().getProcessOutput(Runtime.getRuntime().exec(super.getAdbService().getCommandBase(super.getDeviceId()) + ' ' + hashFunction + ' ' + applicationPath)).get(0).split(" ")[0];
    }

    private void collectApplicationPermissions(AndroidApplication application) throws ADBException {
        String command = super.getAdbService().getCommandBase(this.getDeviceId()) + " dumpsys package " + application.getPackageName();
        try {
            ArrayList<String> collectedStrings = super.getAdbService().getProcessOutputHandler().getProcessOutput(Runtime.getRuntime().exec(command));
            int currentState = 0;
            for (var row : collectedStrings) {
                if (row.contains("requested permissions")) {
                    currentState = COLLECT_REQUESTED_PERMISSIONS;
                    continue;
                }
                if (row.contains("install permissions")) {
                    currentState = COLLECT_INSTALL_PERMISSIONS;
                    continue;
                }
                if (row.contains("runtime permissions")) {
                    currentState = COLLECT_RUNTIME_PERMISSIONS;
                }
                if (currentState != 0 && row.contains("android.permission")) {
                    String currentRow = row.strip();
                    ApplicationPermission currentPermission = new ApplicationPermission();
                    currentPermission.setGranted(currentRow.contains("true"));
                    currentPermission.setPermissionName(currentRow.split(":")[0]);
                    if (currentPermission.isDangerous()) {
                        application.addDangerousPermission(currentPermission);
                    }
                    switch (currentState) {
                        case 1 -> {
                            currentPermission.setGranted(true);
                            application.addRequestedPermission(currentPermission);
                        }
                        case 2 -> application.addInstallPermission(currentPermission);
                        case 3 -> application.addRuntimePermission(currentPermission);
                    }
                }
            }
        } catch (IOException e) {
            throw new ADBPermissionCollectingException("Something went wrong during package " + application.getPackageName() + " permissions collecting! Check if the device is availabe!");
        }

    }

    public void uninstallApp(String packageName) throws ADBException {
        if (!packageName.contains(".")) {
            throw new InvalidPackageNameException("Invalid package name: " + packageName);
        }
        String command = super.getAdbService().getAdbPath() + " uninstall " + packageName;
        getAdbService().logInfo("Uninstalling App: " + packageName);
        try {
            Process process = Runtime.getRuntime().exec(command);
            super.getAdbService().getProcessOutputHandler().getProcessOutput(process);
            for (int i = 0; i < applications.size(); i++) {
                if (applications.get(i).getPackageName().equals(packageName)) {
                    applications.remove(i);
                    break;
                }
            }
        } catch (IOException e) {
            throw new ADBShellExecutionException("Unable to uninstall app " + packageName + " ! Check if the device is available!");
        }
    }

    private void collectNetworkInterface() throws ADBException {
        String command = super.getAdbService().getCommandBase(super.getDeviceId()) + " ifconfig";
        getAdbService().logInfo("Collecting network interfaces");
        try {
            Process process = Runtime.getRuntime().exec(command);
            ArrayList<String> ifconfigRows = super.getAdbService().getProcessOutputHandler().getProcessOutput(process);
            for (var row : ifconfigRows) {
                if (row.contains("encap")) {
                    this.networkInterfaces.add(row.split(" ")[0]);
                }
            }
        } catch (IOException e) {
            throw new ADBShellExecutionException("Device not available!");
        }
    }

    public String getInterfaceIpAddr(String interfaceName, String ipVersion) throws ADBException {

        if (!this.networkInterfaces.contains(interfaceName)) {
            throw new NetworkInterfaceNotFoundException("Interface " + interfaceName + " not found!");
        }
        String command = super.getAdbService().getCommandBase(super.getDeviceId()) + " ip addr show " + interfaceName;
        getAdbService().logInfo("Getting IP for interface " + interfaceName + " with " + command);
        try {
            Process process = Runtime.getRuntime().exec(command);
            ArrayList<String> rows = super.getAdbService().getProcessOutputHandler().getProcessOutput(process);
            for (var row : rows) {
                if (Objects.equals(ipVersion, supportedIpVersions.get(0)) && row.contains("inet")) {
                    String ipPattern = "inet\\s(\\d+\\.\\d+\\.\\d+\\.\\d+)/\\d+";
                    Pattern pattern = Pattern.compile(ipPattern);
                    Matcher matcher = pattern.matcher(row);
                    if (matcher.find()) {
                        return matcher.group(1);
                    }
                } else if (Objects.equals(ipVersion, supportedIpVersions.get(1)) && row.contains("inet6")) {
                    String ipPattern = "inet6\\s([\\da-fA-F:]+)/\\d+";
                    Pattern pattern = Pattern.compile(ipPattern);
                    Matcher matcher = pattern.matcher(row);
                    if (matcher.find()) {
                        return matcher.group(1);
                    }
                }
            }
        } catch (IOException e) {
            throw new ADBShellExecutionException("Device not available!");
        }
        return "";
    }

    public boolean installAppFromApk(String apkPath) throws ADBException {
        if (apkPath.isEmpty() || !new File(apkPath).exists()) {
            throw new ADBIncorrectPathException("APK file " + apkPath + " does not exist!");
        }
        String command = super.getAdbService().getCommandBaseNoShell(super.getDeviceId()) + " install " + apkPath;
        getAdbService().logInfo("Installing apk " + apkPath + " with command " + command);
        try {
            Process process = Runtime.getRuntime().exec(command);
            ArrayList<String> rows = super.getAdbService().getProcessOutputHandler().getProcessOutput(process);
            for (var i : rows){
                if (i.contains("Success")){
                    return true;
                }
            }
            return false;
        } catch (IOException e) {
            throw new ADBShellExecutionException("Device not available!");
        }
    }
}

package ru.opensource.device;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import ru.opensource.application.AndroidApp;
import ru.opensource.calc.HashCalc;
import ru.opensource.connction.ADBService;
import static ru.opensource.connction.AndroidProperties.ANDROID_API_LEVEL;
import static ru.opensource.connction.AndroidProperties.ANDROID_PRODUCT_BRAND;
import static ru.opensource.connction.AndroidProperties.ANDROID_PRODUCT_DEVICE;
import static ru.opensource.connction.AndroidProperties.ANDROID_VERSION;
import ru.opensource.exception.ADBShellExecutionException;
import ru.opensource.exception.AndroidDeviceNotAvailableException;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

@Data
public class AndroidDevice {

    @NotNull
    private final String deviceId;
    @NotNull
    private final ADBService adbService;

    private final String androidVersion;
    private final String androidSDKVersion;
    private final String brand;
    private final String model;

    private float versionNumeric;
    private ArrayList<AndroidApp> applications;

    public AndroidDevice(@NotNull String deviceId, ADBService adbService) throws AndroidDeviceNotAvailableException, ADBShellExecutionException, IOException, NoSuchAlgorithmException {
        if (!adbService.getAvailableDevices().contains(deviceId)){
            throw new AndroidDeviceNotAvailableException("Android device " + deviceId + "is not available!");
        }
        this.adbService = adbService;
        this.deviceId = deviceId;
        this.androidVersion = getRawPropertyValue(ANDROID_VERSION);
        this.androidSDKVersion = getRawPropertyValue(ANDROID_API_LEVEL);
        this.brand = getRawPropertyValue(ANDROID_PRODUCT_BRAND);
        this.model = getRawPropertyValue(ANDROID_PRODUCT_DEVICE);
        this.applications = new ArrayList<>();
        this.versionNumeric = Float.parseFloat(truncateAndroidVersion());
        collectApplications();
        collectApplicationProperties();
    }

    public String getRawPropertyValue(String property) throws ADBShellExecutionException {
        String command = this.adbService.getAdbPath() + " -s " + this.deviceId  + " shell ";
        if (!property.contains(" ")){
            command += "getprop ";
        }
        command += property;
        this.adbService.getAdbServiceLogger().info("Executing command: " + command);
        try {
            Process process = Runtime.getRuntime().exec(command);
            ArrayList<String> result = this.adbService.getPoh().getProcessOutput(process);
            if (result.size() == 0) return "";
            StringBuilder output = new StringBuilder();
            for (var i : result){
                output.append(i).append('\n');
            }
            if (output.toString().strip().length() == 0){
                return null;
            }
            return output.toString();
        } catch (IOException e) {
            throw new ADBShellExecutionException("Something went wrong during getting of parameter " + property + " ! Check if the device is available!");
        }
    }

    private void collectApplications() throws ADBShellExecutionException {
        String command = this.adbService.getCommandBase(this.deviceId) + " pm list packages -f ";
        this.adbService.getAdbServiceLogger().info("Getting applications list with command: " + command);
        try {
            Process process = Runtime.getRuntime().exec(command);
            ArrayList<String> result = this.adbService.getPoh().getProcessOutput(process);
            for (var application : result){
                String currentApp = application;
                currentApp = currentApp.replace("package:/", "/");
                String[] params = currentApp.split("=");
                AndroidApp app = new AndroidApp();
                app.setPath(params[0]);
                app.setPackageName(params[1]);
                System.out.println(app);
                this.applications.add(app);
            }
        } catch (IOException e) {
            throw new ADBShellExecutionException("Unable to get the applications list! Check if the device is available!");
        }
    }

    private void collectApplicationProperties() throws IOException, NoSuchAlgorithmException {
        for (var application : this.applications){
            String[] hashes = collectApplicationHashes(application.getPath());
            application.setSha1(hashes[0]);
            application.setSha256(hashes[1]);
            application.setSha512(hashes[2]);

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
            sha1 = HashCalc.calculateSha1Hash(tempApkName);
        }
        if (versionNumeric <= 6.0){
            String command = this.adbService.getCommandBaseNoShell(this.deviceId) +   "pull " + applicationPath + ' ' + tempApkName;
            this.adbService.getAdbServiceLogger().info("Grabbing apk file using command: " + command);
            ArrayList<String> none = this.adbService.getPoh().getProcessOutput(Runtime.getRuntime().exec(command));
            sha256 = HashCalc.calculateSha256Hash(tempApkName);
            sha512= HashCalc.calculateSha512Hash(tempApkName);
            if (sha1.length() == 0 ){
                sha1 = HashCalc.calculateSha1Hash(tempApkName);
            }
        }
        return new String[]{sha1, sha256, sha512};
    }

    private String truncateAndroidVersion() {
        int firstDotIndex = this.androidVersion.indexOf('.');
        if (firstDotIndex != -1) {
            int secondDotIndex = this.androidVersion.indexOf('.', firstDotIndex + 1);
            if (secondDotIndex != -1) {
                return this.androidVersion.substring(0, secondDotIndex + 1);
            }
        }
        return androidVersion;
    }

    private String collectApplicationHashAuto(String hashFunction, String applicationPath) throws IOException {
       return this.adbService.getPoh().getProcessOutput(Runtime.getRuntime().exec(this.adbService.getCommandBase(this.deviceId) + ' ' + hashFunction + ' ' + applicationPath)).get(0);
    }

}

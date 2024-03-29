package ru.opensource.adblibrary.device;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import ru.opensource.adblibrary.connection.ADBService;
import ru.opensource.adblibrary.properties.AndroidDeviceProperties;
import ru.opensource.adblibrary.exceptions.ADBShellExecutionException;
import java.io.IOException;
import java.util.List;

@Data
public class AndroidDeviceInfo {

    @NotNull
    private final String deviceId;

    @NotNull
    private final ADBService adbService;

    private String androidVersion;
    private String androidSDKVersion;
    private String brand;
    private String model;

    public AndroidDeviceInfo(@NotNull String deviceId, @NotNull ADBService adbService) {
        this.deviceId = deviceId;
        this.adbService = adbService;
        initializeProperties();
    }

    private void initializeProperties() {
        try {
            this.androidVersion = getRawPropertyValue(AndroidDeviceProperties.ANDROID_VERSION);
            this.androidSDKVersion = getRawPropertyValue(AndroidDeviceProperties.ANDROID_API_LEVEL);
            this.brand = getRawPropertyValue(AndroidDeviceProperties.ANDROID_PRODUCT_BRAND);
            this.model = getRawPropertyValue(AndroidDeviceProperties.ANDROID_PRODUCT_DEVICE);
        } catch (ADBShellExecutionException e) {
            handleShellExecutionException(e);
        }
    }

    private void handleShellExecutionException(ADBShellExecutionException e) {
        e.printStackTrace();
        this.androidVersion = "";
        this.androidSDKVersion = "";
        this.brand = "";
        this.model = "";
    }

    public String getRawPropertyValue(String property) throws ADBShellExecutionException {
        String command = String.format("%s -s %s shell %s%s",
                adbService.getAdbPath(), deviceId, property.contains(" ") ? "" : "getprop ", property);
        adbService.logInfo("Executing command: " + command);
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(command.split("\\s+"));
            Process process = processBuilder.start();
            StringBuilder output = new StringBuilder();
            try{
                List<String> result = adbService.getProcessOutputHandler().getProcessOutput(process);
                if (!result.isEmpty()) {
                    for (String line : result) {
                        output.append(line).append('\n');
                    }
                    return output.toString().strip();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } catch (IOException e) {
            throw new ADBShellExecutionException("Something went wrong during getting of parameter " + property +
                    " ! Check if the device is available!");
        }
        return "";
    }
}
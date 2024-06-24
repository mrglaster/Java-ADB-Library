package ru.enplus.adblibrary.device;

import lombok.Data;
import org.jetbrains.annotations.NotNull;
import ru.enplus.adblibrary.connection.ADBService;
import ru.enplus.adblibrary.properties.AndroidDeviceProperties;
import ru.enplus.adblibrary.exceptions.ADBShellExecutionException;
import java.io.IOException;
import java.util.List;

/**
 * Represents the basic information of an Android device, including properties like version, brand, and model.
 * It interacts with the device using ADB (Android Debug Bridge) to retrieve these properties.
 */
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

    /**
     * Constructs an AndroidDeviceInfo object for the specified device ID and ADB service,
     * and initializes the device properties.
     *
     * @param deviceId the ID of the Android device
     * @param adbService the ADB service used to interact with the Android device
     */
    public AndroidDeviceInfo(@NotNull String deviceId, @NotNull ADBService adbService) {
        this.deviceId = deviceId;
        this.adbService = adbService;
        initializeProperties();
    }

    /**
     * Initializes the properties of the Android device by fetching their values via ADB.
     */
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

    /**
     * Handles exceptions that occur during ADB shell command execution.
     *
     * @param e the exception to handle
     */
    private void handleShellExecutionException(ADBShellExecutionException e) {
        e.printStackTrace();
        this.androidVersion = "";
        this.androidSDKVersion = "";
        this.brand = "";
        this.model = "";
    }

    /**
     * Retrieves the raw property value from the Android device using ADB.
     *
     * @param property the property to retrieve
     * @return the raw value of the property
     * @throws ADBShellExecutionException if there is an error during the property retrieval
     */
    public String getRawPropertyValue(String property) throws ADBShellExecutionException {
        String command = String.format("%s -s %s shell %s%s",
                adbService.getAdbPath(), deviceId, property.contains(" ") ? "" : "getprop ", property);
        adbService.logInfo("Executing command: " + command);
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(command.split("\\s+"));
            Process process = processBuilder.start();
            StringBuilder output = new StringBuilder();
            try {
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

package ru.enplus.adblibrary.providers;

import lombok.Getter;
import lombok.Setter;
import ru.enplus.adblibrary.connection.ADBService;
import ru.enplus.adblibrary.shell.CommandGenerator;

/**
 * Provides data and command generation capabilities for an Android device using ADB (Android Debug Bridge).
 * This class encapsulates an ADB service and a device ID, and initializes a CommandGenerator for the specified device.
 */
@Getter
@Setter
public class AndroidDataProvider {

    /**
     * The ADB service used to interact with the Android device.
     */
    private final ADBService adbService;

    /**
     * The ID of the Android device.
     */
    private final String deviceId;

    /**
     * The CommandGenerator used to generate ADB commands for the specified device.
     */
    private CommandGenerator commandGenerator;

    /**
     * Constructs an AndroidDataProvider with the specified ADB service and device ID.
     * Initializes the CommandGenerator for the specified device.
     *
     * @param adbService the ADB service used to interact with the Android device
     * @param deviceId   the ID of the Android device
     */
    public AndroidDataProvider(ADBService adbService, String deviceId) {
        this.adbService = adbService;
        this.deviceId = deviceId;
        this.commandGenerator = new CommandGenerator(adbService, deviceId);
    }
}

package ru.enplus.adblibrary.connection;

import lombok.Getter;
import ru.enplus.adblibrary.exceptions.ADBIncorrectPathException;
import ru.enplus.adblibrary.exceptions.ADBNotFoundException;
import ru.enplus.adblibrary.util.ProcessOutputHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * Service for interacting with the Android Debug Bridge (ADB).
 */
@Getter
public class ADBService {

    private final String adbPath;
    private final Logger adbServiceLogger = Logger.getLogger("ADBServiceLogger");
    private final ProcessOutputHandler processOutputHandler;

    /**
     * Constructs an ADBService instance using the default ADB installation in the system PATH.
     *
     * @throws ADBNotFoundException if ADB is not found in the system PATH
     */
    public ADBService() throws ADBNotFoundException {
        if (!isAdbInstalled()) {
            String errorMessage = "ADB was not found in PATH! Add folder with binaries to path or use ADBService(String adbPath) constructor instead!";
            throw new ADBNotFoundException(errorMessage);
        }
        adbServiceLogger.info("ADB is installed on this machine");
        this.adbPath = "adb";
        this.processOutputHandler = new ProcessOutputHandler();
    }

    /**
     * Constructs an ADBService instance using the specified ADB path.
     *
     * @param adbPath the path to the ADB executable
     * @throws ADBIncorrectPathException if the specified ADB path is invalid
     */
    public ADBService(String adbPath) throws ADBIncorrectPathException {
        if (!isValidAdbPath(adbPath)) {
            throw new ADBIncorrectPathException("ADB path you provided is incorrect!");
        }
        adbServiceLogger.fine("ADB the user provided is valid");
        this.adbPath = adbPath;
        this.processOutputHandler = new ProcessOutputHandler();
    }

    /**
     * Checks if ADB is installed in the system PATH.
     *
     * @return true if ADB is installed, false otherwise
     */
    private boolean isAdbInstalled() {
        try {
            Process process = Runtime.getRuntime().exec("adb version");
            int exitCode = process.waitFor();
            return exitCode == 0;
        } catch (IOException | InterruptedException e) {
            return false;
        }
    }

    /**
     * Validates the specified ADB path.
     *
     * @param path the path to the ADB executable
     * @return true if the ADB path is valid, false otherwise
     */
    private boolean isValidAdbPath(String path) {
        if (path.isEmpty()) return false;
        try {
            Process process = Runtime.getRuntime().exec(path + " version");
            int exitCode = process.waitFor();
            return exitCode == 0;
        } catch (IOException | InterruptedException e) {
            return false;
        }
    }

    /**
     * Retrieves a list of available devices connected to the ADB server.
     *
     * @return a list of available device IDs
     */
    public ArrayList<String> getAvailableDevices() {
        try {
            String command = adbPath + " devices";
            Process process = Runtime.getRuntime().exec(command);
            ArrayList<String> processOutput = processOutputHandler.getProcessOutput(process);
            ArrayList<String> result = new ArrayList<>();
            for (var i : processOutput) {
                if (!i.startsWith("*") && !i.startsWith("List") && !(i.replaceAll("\\s+", "").isEmpty())) {
                    String processedResult = i;
                    processedResult = processedResult.replace("device", "").strip();
                    result.add(processedResult);
                }
            }
            if (result.isEmpty()) {
                adbServiceLogger.warning("No available devices found!");
            }
            return result;
        } catch (IOException e) {
            adbServiceLogger.warning("No available devices found!");
            return new ArrayList<>();
        }
    }

    /**
     * Constructs the base ADB shell command for a specific device.
     *
     * @param deviceId the ID of the device
     * @return the base ADB shell command
     */
    public String getCommandBase(String deviceId) {
        return getCommandBaseNoShell(deviceId) + " shell ";
    }

    /**
     * Constructs the base ADB command for a specific device without the shell.
     *
     * @param deviceId the ID of the device
     * @return the base ADB command without the shell
     */
    public String getCommandBaseNoShell(String deviceId) {
        return adbPath + " -s " + deviceId + " ";
    }

    /**
     * Logs an informational message.
     *
     * @param message the message to log
     */
    public void logInfo(String message) {
        adbServiceLogger.info(message);
    }
}

package ru.enplus.adblibrary.providers;

import ru.enplus.adblibrary.application.AndroidApplication;
import ru.enplus.adblibrary.util.HashCalc;
import ru.enplus.adblibrary.connection.ADBService;
import ru.enplus.adblibrary.enums.EPermissionsStage;
import ru.enplus.adblibrary.exceptions.*;
import ru.enplus.adblibrary.permissions.ApplicationPermission;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static ru.enplus.adblibrary.enums.EPermissionsStage.*;

/**
 * Provides application-specific data and functionalities for an Android device using ADB (Android Debug Bridge).
 * This class extends the AndroidDataProvider to include methods for retrieving application information,
 * calculating hashes, and collecting permissions.
 */
public class ApplicationDataProvider extends AndroidDataProvider {

    /**
     * Constructs an ApplicationDataProvider with the specified ADB service and device ID.
     *
     * @param adbService the ADB service used to interact with the Android device
     * @param deviceId   the ID of the Android device
     */
    public ApplicationDataProvider(ADBService adbService, String deviceId) {
        super(adbService, deviceId);
    }

    /**
     * Retrieves a list of installed applications on the Android device.
     *
     * @param restriction the maximum number of applications to retrieve; if zero, retrieves all applications
     * @return a list of AndroidApplication objects representing the installed applications
     * @throws ADBShellExecutionException if there is an error executing the ADB command
     */
    public ArrayList<AndroidApplication> getApplicationsList(int restriction) throws ADBShellExecutionException {
        String command = getCommandGenerator().generateApplicationCollectionCommand();
        ArrayList<AndroidApplication> applications = new ArrayList<>();
        final String fileFormat = ".apk";
        getAdbService().logInfo("Getting applications list with command: " + command);
        try {
            Process process = Runtime.getRuntime().exec(command);
            ArrayList<String> result = super.getAdbService().getProcessOutputHandler().getProcessOutput(process);
            int appCounter = 0;
            for (var application : result) {
                String currentApp = application.replace("package:/", "/");
                String[] params = currentApp.split(fileFormat + "=");
                AndroidApplication app = new AndroidApplication();
                app.setPath(params[0] + fileFormat);
                app.setPackageName(params[1]);
                applications.add(app);
                appCounter++;
                if (restriction > 0 && appCounter == restriction) break;
            }
            return applications;
        } catch (IOException e) {
            throw new ADBShellExecutionException("Unable to get the applications list! Check if the device is available!");
        }
    }

    /**
     * Collects the hash of an application automatically using the specified hash function and application path.
     *
     * @param hashFunction    the hash function to use (e.g., "sha1sum", "sha256sum", "sha512sum")
     * @param applicationPath the path of the application to hash
     * @return the hash value as a String
     * @throws IOException if there is an error executing the hash command
     */
    private String collectApplicationHashAuto(String hashFunction, String applicationPath) throws IOException {
        return getAdbService().getProcessOutputHandler().getProcessOutput(Runtime.getRuntime().exec(getCommandGenerator().generateHashCollectCommand(hashFunction, applicationPath))).get(0).split(" ")[0];
    }

    /**
     * Retrieves the SHA-1, SHA-256, and SHA-512 hashes of an application.
     *
     * @param applicationPath the path of the application
     * @param versionNumeric  the numeric version of the application
     * @return an array containing the SHA-1, SHA-256, and SHA-512 hashes
     * @throws IOException                  if there is an error executing the hash command
     * @throws NoSuchAlgorithmException     if the specified hash algorithm is not available
     */
    public String[] getApplicationHashes(String applicationPath, Float versionNumeric) throws IOException, NoSuchAlgorithmException {
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
        if (versionNumeric < 6.0 || sha256.isEmpty()) {
            String command = getCommandGenerator().generatePullApplicationCommand(applicationPath, tempApkName);
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

    /**
     * Fills the permissions of the specified Android application.
     *
     * @param application the Android application to fill permissions for
     * @throws ADBException if there is an error collecting the permissions
     */
    public void fillApplicationPermissions(AndroidApplication application) throws ADBException {
        List<ArrayList<ApplicationPermission>> permissions = getApplicationPermissions(application);
        application.setDangerousPermissions(permissions.get(0));
        application.setInstallPermissions(permissions.get(1));
        application.setRuntimePermissions(permissions.get(2));
        application.setRequestedPermissions(permissions.get(3));
    }

    /**
     * Fills the hashes (SHA-1, SHA-256, SHA-512) of the specified Android application.
     *
     * @param application    the Android application to fill hashes for
     * @param versionNumeric the numeric version of the application
     * @throws IOException              if there is an error executing the hash command
     * @throws NoSuchAlgorithmException if the specified hash algorithm is not available
     */
    public void fillApplicationHashes(AndroidApplication application, Float versionNumeric) throws IOException, NoSuchAlgorithmException {
        String[] hashes = getApplicationHashes(application.getPath(), versionNumeric);
        application.setSha1(hashes[0]);
        application.setSha256(hashes[1]);
        application.setSha512(hashes[2]);
    }

    /**
     * Retrieves the permissions of the specified Android application.
     *
     * @param application the Android application to retrieve permissions for
     * @return a list containing lists of different types of permissions (dangerous, install, runtime, requested)
     * @throws ADBException if there is an error collecting the permissions
     */
    public List<ArrayList<ApplicationPermission>> getApplicationPermissions(AndroidApplication application) throws ADBException {
        String command = getCommandGenerator().generateGetApplicationPermissionsCommand(application.getPackageName());
        try {
            ArrayList<String> collectedStrings = super.getAdbService().getProcessOutputHandler().getProcessOutput(Runtime.getRuntime().exec(command));
            EPermissionsStage currentState = AWAITING;

            ArrayList<ApplicationPermission> dangerousPermissions = new ArrayList<>();
            ArrayList<ApplicationPermission> installPermissions = new ArrayList<>();
            ArrayList<ApplicationPermission> runtimePermissions = new ArrayList<>();
            ArrayList<ApplicationPermission> requestedPermissions = new ArrayList<>();

            for (var row : collectedStrings) {
                if (row.contains("requested permissions")) {
                    currentState = COLLECT_REQUESTED_PERMISSIONS;
                    continue;
                }
                else  if (row.contains("install permissions")) {
                    currentState = COLLECT_INSTALL_PERMISSIONS;
                    continue;
                }
                else if (row.contains("runtime permissions")) {
                    currentState = COLLECT_RUNTIME_PERMISSIONS;
                }

                if (currentState != AWAITING && row.contains("android.permission")) {
                    String currentRow = row.strip();
                    ApplicationPermission currentPermission = new ApplicationPermission();
                    currentPermission.setGranted(currentRow.contains("true"));
                    currentPermission.setPermissionName(currentRow.split(":")[0]);
                    if (currentPermission.isDangerous()) {
                        dangerousPermissions.add(currentPermission);
                    }
                    switch (currentState) {
                        case COLLECT_REQUESTED_PERMISSIONS -> {
                            currentPermission.setGranted(true);
                            requestedPermissions.add(currentPermission);
                        }
                        case COLLECT_INSTALL_PERMISSIONS -> installPermissions.add(currentPermission);
                        case COLLECT_RUNTIME_PERMISSIONS -> runtimePermissions.add(currentPermission);
                    }
                }
            }
            return Arrays.asList(dangerousPermissions, installPermissions, runtimePermissions, requestedPermissions);
        } catch (IOException e) {
            throw new ADBPermissionCollectingException("Something went wrong during package " + application.getPackageName() + " permissions collecting! Check if the device is available!");
        }
    }
}

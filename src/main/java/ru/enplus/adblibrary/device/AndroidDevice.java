package ru.enplus.adblibrary.device;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import ru.enplus.adblibrary.application.AndroidApplication;
import ru.enplus.adblibrary.exceptions.ADBException;
import ru.enplus.adblibrary.exceptions.AndroidDeviceNotAvailableException;
import ru.enplus.adblibrary.connection.ADBService;
import ru.enplus.adblibrary.providers.ApplicationDataProvider;
import ru.enplus.adblibrary.providers.ApplicationManagementProvider;
import ru.enplus.adblibrary.providers.ApplicationPermissionManagementProvider;
import ru.enplus.adblibrary.providers.NetworkDataProvider;
import static ru.enplus.adblibrary.util.AndroidUtils.truncateAndroidVersion;

/**
 * Represents an Android device, providing methods for application management,
 * permission management, and network information retrieval using ADB (Android Debug Bridge).
 */
public class AndroidDevice extends AndroidDeviceInfo {

    private ApplicationDataProvider applicationDataProvider;
    private ApplicationManagementProvider applicationManagementProvider;
    private ApplicationPermissionManagementProvider applicationPermissionManagementProvider;
    private NetworkDataProvider networkDataProvider;
    private final float versionNumeric;

    @Getter
    @Setter
    private ArrayList<AndroidApplication> applications;

    /**
     * Constructs an AndroidDevice with the specified device ID and ADB service, and initializes all providers.
     *
     * @param deviceId the ID of the Android device
     * @param adbService the ADB service used to interact with the Android device
     * @throws ADBException if there is an error during ADB operations
     * @throws IOException if there is an IO error
     * @throws NoSuchAlgorithmException if there is an error with hashing algorithms
     */
    public AndroidDevice(@NotNull String deviceId, ADBService adbService) throws ADBException, IOException, NoSuchAlgorithmException {
        super(deviceId, adbService);
        if (!adbService.getAvailableDevices().contains(deviceId)) {
            throw new AndroidDeviceNotAvailableException("Android device " + deviceId + " is not available!");
        }
        initProviders(adbService, deviceId);
        this.applications = new ArrayList<>();
        this.versionNumeric = Float.parseFloat(truncateAndroidVersion(getAndroidVersion()));
        this.applications = applicationDataProvider.getApplicationsList(-1);
        collectApplicationProperties();
    }

    /**
     * Constructs an AndroidDevice with the specified device ID, ADB service, and a limit on the number of applications retrieved.
     *
     * @param deviceId the ID of the Android device
     * @param adbService the ADB service used to interact with the Android device
     * @param appsListRestriction the maximum number of applications to retrieve
     * @throws ADBException if there is an error during ADB operations
     * @throws IOException if there is an IO error
     * @throws NoSuchAlgorithmException if there is an error with hashing algorithms
     */
    public AndroidDevice(@NotNull String deviceId, ADBService adbService, int appsListRestriction) throws ADBException, IOException, NoSuchAlgorithmException {
        super(deviceId, adbService);
        if (!adbService.getAvailableDevices().contains(deviceId)) {
            throw new AndroidDeviceNotAvailableException("Android device " + deviceId + " is not available!");
        }
        initProviders(adbService, deviceId);
        this.applications = new ArrayList<>();
        this.versionNumeric = Float.parseFloat(truncateAndroidVersion(getAndroidVersion()));
        this.applications = applicationDataProvider.getApplicationsList(appsListRestriction);
        collectApplicationProperties();
    }

    /**
     * Initializes the various data providers used by this Android device.
     *
     * @param adbService the ADB service used to interact with the Android device
     * @param deviceId the ID of the Android device
     */
    void initProviders(ADBService adbService, String deviceId) {
        this.applicationDataProvider = new ApplicationDataProvider(adbService, deviceId);
        this.applicationManagementProvider = new ApplicationManagementProvider(adbService, deviceId);
        this.applicationPermissionManagementProvider = new ApplicationPermissionManagementProvider(adbService, deviceId);
        this.networkDataProvider = new NetworkDataProvider(adbService, deviceId);
    }

    /**
     * Collects properties of applications installed on the device, including permissions and hashes.
     *
     * @throws IOException if there is an IO error
     * @throws NoSuchAlgorithmException if there is an error with hashing algorithms
     * @throws ADBException if there is an error during ADB operations
     */
    private void collectApplicationProperties() throws IOException, NoSuchAlgorithmException, ADBException {
        for (var application : this.applications) {
            applicationDataProvider.fillApplicationPermissions(application);
            applicationDataProvider.fillApplicationHashes(application, versionNumeric);
        }
    }

    /**
     * Uninstalls the application with the specified package name.
     *
     * @param packageName the package name of the application to uninstall
     * @throws ADBException if there is an error during the uninstallation
     */
    public void uninstallApp(String packageName) throws ADBException {
        applicationManagementProvider.uninstallApp(packageName);
    }

    /**
     * Installs an application from the specified APK file path.
     *
     * @param apkPath the path to the APK file
     * @return true if the installation was successful, false otherwise
     * @throws ADBException if there is an error during the installation
     */
    public boolean installAppFromApk(String apkPath) throws ADBException {
        return applicationManagementProvider.installAppFromApk(apkPath);
    }

    /**
     * Grants a permission to an application using its package name.
     *
     * @param applicationPackage the package name of the application
     * @param permission the permission to grant
     * @throws ADBException if there is an error during the permission grant
     */
    public void grantPermissionShellOnly(String applicationPackage, String permission) throws ADBException {
        applicationPermissionManagementProvider.grantPermissionShellOnly(applicationPackage, permission);
    }

    /**
     * Grants a permission to the specified application.
     *
     * @param application the application to grant the permission to
     * @param permission the permission to grant
     * @throws ADBException if there is an error during the permission grant
     */
    public void grantPermissionShellOnly(AndroidApplication application, String permission) throws ADBException {
        applicationPermissionManagementProvider.grantPermissionShellOnly(application, permission);
    }

    /**
     * Revokes a permission from an application using its package name.
     *
     * @param applicationPackage the package name of the application
     * @param permission the permission to revoke
     * @throws ADBException if there is an error during the permission revocation
     */
    public void revokePermissionShellOnly(String applicationPackage, String permission) throws ADBException {
        applicationPermissionManagementProvider.revokePermissionShellOnly(applicationPackage, permission);
    }

    /**
     * Revokes a permission from the specified application.
     *
     * @param application the application to revoke the permission from
     * @param permission the permission to revoke
     * @throws ADBException if there is an error during the permission revocation
     */
    public void revokePermissionShellOnly(AndroidApplication application, String permission) throws ADBException {
        applicationPermissionManagementProvider.revokePermissionShellOnly(application, permission);
    }

    /**
     * Retrieves a list of network interfaces available on the device.
     *
     * @return a list of network interfaces
     * @throws ADBException if there is an error during the retrieval
     */
    public ArrayList<String> getNetworkInterfaces() throws ADBException {
        return networkDataProvider.getNetworkInterfaces();
    }

    /**
     * Retrieves the IP address of a specified network interface and IP version.
     *
     * @param interfaceName the name of the network interface
     * @param ipVersion the IP version ("ipv4" or "ipv6")
     * @return the IP address of the network interface
     * @throws ADBException if there is an error during the retrieval
     */
    public String getInterfaceIpAddress(String interfaceName, String ipVersion) throws ADBException {
        return networkDataProvider.getInterfaceIpAddress(interfaceName, ipVersion);
    }
}

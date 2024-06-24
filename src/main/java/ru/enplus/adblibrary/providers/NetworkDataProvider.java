package ru.enplus.adblibrary.providers;

import ru.enplus.adblibrary.connection.ADBService;
import ru.enplus.adblibrary.exceptions.ADBException;
import ru.enplus.adblibrary.exceptions.ADBShellExecutionException;
import ru.enplus.adblibrary.exceptions.NetworkInterfaceNotFoundException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Provides methods for managing network data on an Android device using ADB (Android Debug Bridge).
 * This class extends the AndroidDataProvider to include methods for retrieving network interfaces and IP addresses.
 */
public class NetworkDataProvider extends AndroidDataProvider {

    private final List<String> supportedIpVersions = Arrays.asList(new String[]{"ipv4", "ipv6"});
    private final Pattern ipv4Pattern;
    private final Pattern ipv6Pattern;

    /**
     * Constructs a NetworkDataProvider with the specified ADB service and device ID.
     *
     * @param adbService the ADB service used to interact with the Android device
     * @param deviceId   the ID of the Android device
     */
    public NetworkDataProvider(ADBService adbService, String deviceId) {
        super(adbService, deviceId);
        String ipv4Expression = "inet\\s(\\d+\\.\\d+\\.\\d+\\.\\d+)/\\d+";
        this.ipv4Pattern = Pattern.compile(ipv4Expression);
        String ipv6Expression = "inet6\\s([\\da-fA-F:]+)/\\d+";
        this.ipv6Pattern = Pattern.compile(ipv6Expression);
    }

    /**
     * Retrieves a list of network interfaces on the Android device.
     *
     * @return a list of network interfaces
     * @throws ADBException if there is an error during the command execution
     */
    public ArrayList<String> getNetworkInterfaces() throws ADBException {
        String command = getCommandGenerator().generateGetNetworkInterfacesCommand();
        ArrayList<String> networkInterfaces = new ArrayList<>();
        getAdbService().logInfo("Collecting network interfaces");
        try {
            Process process = Runtime.getRuntime().exec(command);
            ArrayList<String> ifconfigRows = super.getAdbService().getProcessOutputHandler().getProcessOutput(process);
            for (var row : ifconfigRows) {
                if (row.contains("encap")) {
                    networkInterfaces.add(row.split(" ")[0]);
                }
            }
            return networkInterfaces;
        } catch (IOException e) {
            throw new ADBShellExecutionException("Device not available!");
        }
    }

    /**
     * Retrieves the IP address of the specified network interface.
     *
     * @param interfaceName the name of the network interface
     * @param ipVersion     the IP version ("ipv4" or "ipv6")
     * @return the IP address of the network interface
     * @throws ADBException if there is an error during the command execution or if the network interface is not found
     */
    public String getInterfaceIpAddress(String interfaceName, String ipVersion) throws ADBException {
        if (!getNetworkInterfaces().contains(interfaceName)) {
            throw new NetworkInterfaceNotFoundException("Interface " + interfaceName + " not found!");
        }
        String command = getCommandGenerator().generateNetworkInterfaceIpAddressCommand(interfaceName);
        getAdbService().logInfo("Getting IP for interface " + interfaceName + " with " + command);
        try {
            Process process = Runtime.getRuntime().exec(command);
            ArrayList<String> rows = super.getAdbService().getProcessOutputHandler().getProcessOutput(process);
            for (var row : rows) {
                if (Objects.equals(ipVersion, supportedIpVersions.get(0)) && row.contains("inet")) {
                    Matcher matcher = ipv4Pattern.matcher(row);
                    if (matcher.find()) {
                        return matcher.group(1);
                    }
                } else if (Objects.equals(ipVersion, supportedIpVersions.get(1)) && row.contains("inet6")) {
                    Matcher matcher = ipv6Pattern.matcher(row);
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
}

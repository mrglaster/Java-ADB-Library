package ru.enplus.adblibrary.shell;
import ru.enplus.adblibrary.connection.ADBService;

public class CommandGenerator {

    private final StringBuilder commandBuilder;
    private final ADBService adbService;
    private final String deviceId;

    public CommandGenerator(ADBService adbService, String deviceId){
        this.commandBuilder = new StringBuilder();
        this.adbService = adbService;
        this.deviceId = deviceId;
    }

    public String generateApplicationCollectionCommand(){
        cleanContext();
        commandBuilder.append(adbService.getCommandBase(deviceId));
        commandBuilder.append(" pm list packages -f ");
        return commandBuilder.toString();
    }

    public String generateUninstallApplicationCommand(String packageName){
        cleanContext();
        commandBuilder.append(adbService.getCommandBaseNoShell(deviceId));
        commandBuilder.append("uninstall ");
        commandBuilder.append(packageName);
        return commandBuilder.toString();
    }

    public String generateInstallApplicationCommand(String apkPath){
        cleanContext();
        commandBuilder.append(adbService.getCommandBaseNoShell(deviceId));
        commandBuilder.append("install ");
        commandBuilder.append(apkPath);
        return commandBuilder.toString();
    }

    public String generateGetNetworkInterfacesCommand(){
        cleanContext();
        commandBuilder.append(adbService.getCommandBase(deviceId));
        commandBuilder.append("ifconfig");
        return commandBuilder.toString();
    }


    public String generateNetworkInterfaceIpAddressCommand(String interfaceName){
        cleanContext();
        commandBuilder.append(adbService.getCommandBase(deviceId));
        commandBuilder.append(" ip addr show ");
        commandBuilder.append(interfaceName);
        return commandBuilder.toString();
    }



    public String generatePullApplicationCommand(String applicationPath, String tempApkName){
        cleanContext();
        commandBuilder.append(adbService.getCommandBaseNoShell(deviceId));
        commandBuilder.append(" ");
        commandBuilder.append("pull ");
        commandBuilder.append(applicationPath);
        commandBuilder.append(" ");
        commandBuilder.append(tempApkName);
        return commandBuilder.toString();
    }

    public String generateGetApplicationPermissionsCommand(String applicationPackageName){
        cleanContext();
        commandBuilder.append(adbService.getCommandBase(deviceId));
        commandBuilder.append(" dumpsys package ");
        commandBuilder.append(applicationPackageName);
        return commandBuilder.toString();
    }

    public String generateHashCollectCommand(String hashName, String applicationPath){
        cleanContext();
        commandBuilder.append(adbService.getCommandBase(deviceId));
        commandBuilder.append(' ');
        commandBuilder.append(hashName);
        commandBuilder.append(' ');
        commandBuilder.append(applicationPath);
        return commandBuilder.toString();
    }


    private void cleanContext(){
        this.commandBuilder.setLength(0);
    }




}

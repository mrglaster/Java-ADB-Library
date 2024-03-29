package ru.opensource.adblibrary.connection;
import lombok.Getter;
import ru.opensource.adblibrary.exceptions.ADBIncorrectPathException;
import ru.opensource.adblibrary.exceptions.ADBNotFoundException;
import ru.opensource.adblibrary.processes.ProcessOutputHandler;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;

public class ADBService {

    @Getter
    private final String adbPath;

    @Getter
    private final Logger adbServiceLogger = Logger.getLogger("ADBServiceLogger");

    @Getter
    private final ProcessOutputHandler processOutputHandler;

    public ADBService() throws ADBNotFoundException {
        if (!isAdbInstalled()){
            String errorMessage = "ADB was not found in PATH! Add folder with binaries to path or use ADBService(String adbPath) constructor instead!";
            throw new ADBNotFoundException(errorMessage);
        }
        adbServiceLogger.info("ADB is installed on this machine");
        this.adbPath = "adb";
        this.processOutputHandler = new ProcessOutputHandler();
    }


    public ADBService(String adbPath) throws ADBIncorrectPathException {
        if (!isValidAdbPath(adbPath)){
            throw new ADBIncorrectPathException("ADB path you provided is incorrect!");
        }
        adbServiceLogger.fine("ADB the user provided is valid");
        this.adbPath = adbPath;
        this.processOutputHandler = new ProcessOutputHandler();
    }

    private boolean isAdbInstalled(){
        try {
            Process process = Runtime.getRuntime().exec("adb version");
            int exitCode = process.waitFor();
            return exitCode == 0;
        } catch (IOException | InterruptedException e) {
            return false;
        }
    }

    private boolean isValidAdbPath(String path){
        if (path.length() == 0) return false;
        try {
            Process process = Runtime.getRuntime().exec(path + " version");
            int exitCode = process.waitFor();
            return exitCode == 0;
        } catch (IOException | InterruptedException e) {
            return false;
        }
    }


    public ArrayList<String> getAvailableDevices(){
        try {
            String command = adbPath + " devices";
            Process process = Runtime.getRuntime().exec(command);
            ArrayList<String> processOutput = processOutputHandler.getProcessOutput(process);
            ArrayList<String> result = new ArrayList<>();
            for (var i : processOutput){
                if (!i.startsWith("*") && !i.startsWith("List") && !(i.replaceAll("\\s+","").length() == 0)){
                    String processedResult = i;
                    processedResult = processedResult.replace("device", "").strip();
                    result.add(processedResult);
                }
            }
            if (result.size() == 0){
                adbServiceLogger.warning("No available devices found!");
            }
            return result;
        } catch (IOException e) {
            adbServiceLogger.warning("No available devices found!");
            return new ArrayList<>();
        }
    }
    public String getCommandBase(String deviceId){
        return  getCommandBaseNoShell(deviceId) + " shell ";
    }

    public String getCommandBaseNoShell(String deviceId){
        return adbPath+ " -s " + deviceId + " ";
    }

    public void logInfo(String message){
        adbServiceLogger.info(message);
    }
}

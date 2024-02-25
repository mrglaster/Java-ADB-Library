package ru.opensource.adblibrary.processes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class ProcessOutputHandler {
    public ArrayList<String> getProcessOutput(Process process) throws IOException {
        ArrayList<String> processOutput = new ArrayList<String>();
        InputStream inputStream = process.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while ((line = reader.readLine()) != null) {
            processOutput.add(line);
        }
        reader.close();
        return processOutput;
    }
}

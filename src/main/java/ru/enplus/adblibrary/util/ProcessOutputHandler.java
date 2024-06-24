package ru.enplus.adblibrary.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Utility class to handle the output of a process.
 */
public class ProcessOutputHandler {

    /**
     * Retrieves and processes the output from a given process.
     *
     * @param process The process whose output needs to be captured.
     * @return An ArrayList of strings containing each line of the process's output.
     * @throws IOException If an I/O error occurs while reading the process output stream.
     */
    public ArrayList<String> getProcessOutput(Process process) throws IOException {
        ArrayList<String> processOutput = new ArrayList<>();
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
